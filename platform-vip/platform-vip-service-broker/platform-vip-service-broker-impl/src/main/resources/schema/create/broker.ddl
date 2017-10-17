------------------------------------------------------------------------------------------------------------------------
-- process session.
CREATE TABLE IF NOT EXISTS session (

  id                INTEGER IDENTITY NOT NULL,
  update_count      INTEGER      NOT NULL, --
  update_date       TIMESTAMP    NOT NULL,
  create_date       TIMESTAMP    NOT NULL,
  delete_date       TIMESTAMP    NULL,
  external_id       VARCHAR(36)  NOT NULL,

  process_ref       VARCHAR(250) NOT NULL, -- perhaps rename to instance_ref?
  referrer          VARCHAR(250) NOT NULL, -- ip and other identifying features.
  cookie            CLOB         NULL, -- all security information available to help track.

  connect_date      TIMESTAMP    NOT NULL,
  connect_ttl       INTEGER      NOT NULL, -- cheat with this, instead of touching, just max(lock_date) > now() - login_ttl :)
  disconnect_date   TIMESTAMP    NULL,
  disconnect_reason VARCHAR(50)  NULL

);

CREATE UNIQUE INDEX ux_session_extkey
  ON session (external_id);

------------------------------------------------------------------------------------------------------------------------
-- source queue is lock if and only there is a lock_pid with a value of not_null if it's missing a row, it's unlocked.
-- table is unversioned and transient. The source must be locked before and after any addition or deletion to the queue.
-- common usage is to delete events from general sources (queues) and add associated ones to more specific ones as they
-- are processed so that the effects of locks are minimised.
-- May ned manual clean-up because no cascading fks for flexibility.
CREATE TABLE IF NOT EXISTS lock (

  id           INTEGER IDENTITY NOT NULL,
  update_count INTEGER      NOT NULL, --
  update_date  TIMESTAMP    NOT NULL,
  create_date  TIMESTAMP    NOT NULL,
  delete_date  TIMESTAMP    NULL,
  external_id  VARCHAR(36)  NOT NULL,

  session_id   INTEGER      NOT NULL, -- the 'source' feed

  type         VARCHAR(250) NOT NULL, -- will be event, task, watch
  watermark    VARCHAR(250) NOT NULL,
  target       VARCHAR(250) NOT NULL, -- will be event_source, task_queue, or watch_group

  lock_date    TIMESTAMP    NULL, -- when being processed the lock
  lock_ttl     INTEGER      NULL, -- the lock's ttl

  persistent   SMALLINT     NOT NULL, -- will be event_source, task_queue, or watch_group

  FOREIGN KEY (session_id) REFERENCES session (id)

);

CREATE UNIQUE INDEX ux_lock_extkey
  ON lock (external_id);

CREATE INDEX ix_lock_ordered
  ON lock (session_id, lock_date, type, watermark, target);

------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
-- Event queue. Each event is immutable, but can be deleted (the history update_count remains). Supports put, putif, delete.
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS event (

  id           INTEGER IDENTITY NOT NULL,
  update_count INTEGER      NOT NULL, --
  update_date  TIMESTAMP    NOT NULL,
  create_date  TIMESTAMP    NOT NULL,
  delete_date  TIMESTAMP    NULL,
  external_id  VARCHAR(36)  NOT NULL,

  -- immutable
  watermark    VARCHAR(50)  NOT NULL, -- these events are specific to a specific system.
  source       VARCHAR(250) NOT NULL, -- freeform field for a source. possibly a side of the linkage or other known source
  type         VARCHAR(50)  NOT NULL, -- the event type
  action_date  TIMESTAMP    NOT NULL, -- the date the source told us it was for. We order by it.
  trigger_date TIMESTAMP    NOT NULL, -- In case 2 actions come at the same time, we can increment this one (we won't touch the original date). We order by this secondarily
  unique_ref   VARCHAR(250) NULL, -- if not null, will be use to reject the insertion (or max(trigger_date) where ref == ref).
  data         CLOB         NULL, -- the immutable data

);

CREATE UNIQUE INDEX ux_event_extkey
  ON event (external_id);
CREATE UNIQUE INDEX ux_event_unique_ref
  ON event (watermark, unique_ref);

CREATE INDEX ix_event_ordered
  ON event (watermark, trigger_date, type, source);

------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
-- Task queue. Rows are mutable and version_counted.
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS task (

  id             INTEGER IDENTITY NOT NULL,
  update_count   INTEGER     NOT NULL, --
  update_date    TIMESTAMP   NOT NULL,
  create_date    TIMESTAMP   NOT NULL,
  delete_date    TIMESTAMP   NULL,
  external_id    VARCHAR(36) NOT NULL,

  -- immutable
  watermark      VARCHAR(50) NOT NULL, -- the 'source' feed
  queue          VARCHAR(50) NOT NULL, -- the queue: input, output, whatever
  action_date    TIMESTAMP   NOT NULL, -- the date the source told us it was for. We order by it.
  trigger_date   TIMESTAMP   NOT NULL, -- In case 2 actions come at the same time, we can increment this one (we won't touch the original date). We order by this secondarily

  -- mutable
  state          VARCHAR(50) NOT NULL, -- the state change requested (the file to process it)
  step           VARCHAR(50) NOT NULL, -- savepoint
  data           CLOB, -- the normal mutable payload

  -- mutable controls
  error_count    INTEGER     NOT NULL,
  error_message  CLOB,
  processed_date TIMESTAMP   NOT NULL, -- goes with the sleep
  sleep          INTEGER     NOT NULL,

);

CREATE UNIQUE INDEX ux_task_extkey
  ON task (external_id);

CREATE INDEX ix_task_ordered
  ON task (watermark, queue, trigger_date);

------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
-- Watch table. Rows are mutable and version_counted.
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS watch (

  id                 INTEGER IDENTITY NOT NULL,
  update_count       INTEGER      NOT NULL, --
  update_date        TIMESTAMP    NOT NULL,
  create_date        TIMESTAMP    NOT NULL,
  delete_date        TIMESTAMP    NULL,
  external_id        VARCHAR(36)  NOT NULL,

  -- immutable
  watermark          VARCHAR(50)  NOT NULL, -- the 'source' feed
  type               VARCHAR(50)  NOT NULL, -- watch type: ref, mark, counter, linkage, etc

  -- the source will probably be us, eg task entity
  -- immutable
  namespace          VARCHAR(250) NOT NULL, -- or catalog
  owner              VARCHAR(250) NOT NULL, -- or table
  record             VARCHAR(250) NOT NULL, -- or id column value

  -- mutable fields
  state              VARCHAR(50)  NOT NULL, -- state, usually name of logic file with should_accept(event) function
  data               CLOB,
  last_modified_date TIMESTAMP    NOT NULL,

  -- links
  link_id            INTEGER      NULL, -- IMPORTANT: an entity like this is *dependent* and should have no events!
  link_reason        CHAR(50)     NULL, -- and the reason for that link (linkage, coalesced, etc)
  link_root_id       INTEGER      NULL, -- in order to keep them together, the root is passed down (not really used as an id though).

  update_task_id     INTEGER      NULL, -- (Parent)

  FOREIGN KEY (link_id) REFERENCES watch (id),
  FOREIGN KEY (link_root_id) REFERENCES watch (id),
  FOREIGN KEY (update_task_id) REFERENCES task (id),

);

CREATE UNIQUE INDEX ux_watch_extkey
  ON watch (external_id);

CREATE INDEX ix_watch_ordered
  ON watch (watermark, type, namespace, owner, record, update_count);

------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
-- Task Views.
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
-- WARNING: do not touch this or trim any cols out or anything, everything is needed!
CREATE VIEW IF NOT EXISTS v_task_queue AS
SELECT
  t.id             id,
  t.update_count   update_count,
  t.update_date    update_date,
  t.create_date    create_date,
  t.delete_date    delete_date,
  t.external_id    external_id,
  t.watermark      watermark,
  t.queue          queue,
  t.action_date    action_date,
  t.trigger_date   trigger_date,
  t.state          state,
  t.step           step,
  t.data           data,
  t.error_count    error_count,
  t.error_message  error_message,
  t.processed_date processed_date,
  t.sleep          sleep
FROM (
       SELECT
         MIN(id) minid,
         trigger_date,
         watermark,
         queue
       FROM (
         SELECT
           t1.id           id,
           t1.trigger_date trigger_date,
           t1.watermark    watermark,
           t1.queue        queue
         FROM
           task t1
           LEFT OUTER JOIN task t2 ON
                                     t2.watermark = t1.watermark
                                     AND t2.queue = t1.queue
                                     AND t2.trigger_date < t1.trigger_date
                                     AND t2.delete_date IS NULL
         WHERE
           t2.id IS NULL
           AND t1.delete_date IS NULL
         ORDER BY
           t1.trigger_date
       )
       GROUP BY
         watermark,
         queue,
         trigger_date
     ) a INNER JOIN task t ON t.id = a.minid
ORDER BY
  t.trigger_date


