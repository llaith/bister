SELECT
  id,
  update_count,
  update_date,
  create_date,
  delete_date,
  external_id,
  watermark,
  queue,
  action_date,
  trigger_date,
  state,
  step,
  data,
  error_count,
  error_message,
  processed_date,
  sleep
FROM
  task
WHERE
  watermark = :watermark
  AND queue = :queue
  AND delete_date IS NULL
  AND id < :last_seen_id -- keyset pagination is faster and easier to be cross-platform
ORDER BY
  id -- must have orderby for keyset pagination to work!
-- ${paging}
