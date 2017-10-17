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
    q.watermark,
    q.queue,
    :now,
    :ttl,
    :session_id,
    :persistent
  FROM v_task_queue q
    LEFT JOIN lock l ON l.watermark = q.watermark AND l.target = q.queue
  WHERE
    l.id IS NULL -- no lock for that row
    AND q.watermark = :watermark
-- ${paging}
