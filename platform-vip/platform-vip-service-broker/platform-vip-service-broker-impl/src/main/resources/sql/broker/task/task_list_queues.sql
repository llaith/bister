SELECT
  queue,
  count(queue) count
FROM
  task
WHERE
  watermark = :watermark
  AND delete_date IS NULL
  AND queue = :queue -- @@SNIP-LINE|QUEUE_NAME@@
GROUP BY
  queue
