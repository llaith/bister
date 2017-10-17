INSERT INTO lock (
  external_id, update_count, update_date, create_date, delete_date,
  type, watermark, target,
  lock_date, lock_ttl,
  session_id, persistent
)
  SELECT
    :external_id,
    0,
    :now,
    :now,
    NULL,
    'TASK_QUEUE',
    :watermark,
    :queue,
    :now,
    :ttl,
    :session_id,
    :persistent
  WHERE NOT EXISTS(SELECT 1
                   FROM lock
                   WHERE watermark = :watermark AND target = :queue)
