DELETE FROM task
WHERE
    watermark = :watermark
    AND queue = :queue
    AND delete_date IS NOT NULL

