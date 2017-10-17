SELECT
  q.id id,
  q.update_count update_count,
  q.update_date update_date,
  q.create_date create_date,
  q.delete_date delete_date,
  q.external_id external_id,
  q.watermark watermark,
  q.queue queue,
  q.action_date action_date,
  q.trigger_date trigger_date,
  q.state state,
  q.step step,
  q.data data,
  q.error_count error_count,
  q.error_message error_message,
  q.processed_date processed_date,
  q.sleep sleep
FROM
  v_task_queue q
  INNER JOIN lock l ON
                    l.type = 'TASK_QUEUE'
                    AND l.watermark = q.watermark
                    AND l.target = q.queue
WHERE
  l.external_id = :lock_id
-- ${paging}



