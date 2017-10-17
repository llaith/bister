SELECT
  external_id,
  watermark,
  queue,
  action_date,
  trigger_date,
  state state,
  step step,
  error_count,
  error_message,
  processed_date,
  update_date,
  sleep
FROM
  task
WHERE
  external_id = :external_id
