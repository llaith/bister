DELETE FROM task
WHERE
    watermark = :watermark
    AND queue = :queue
