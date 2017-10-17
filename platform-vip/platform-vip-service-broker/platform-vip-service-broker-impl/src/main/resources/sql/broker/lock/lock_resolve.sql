SELECT
  l.id           id,
  l.update_count update_count,
  l.update_date  update_date,
  l.create_date  create_date,
  l.delete_date  delete_date,
  l.external_id  external_id,
  l.session_id   session_id,
  l.lock_date    lock_date,
  l.lock_ttl     lock_ttl,
  l.type         type,
  l.watermark    watermark,
  l.target       target,
  l.persistent   persistent
FROM lock l
  INNER JOIN session s ON l.session_id = s.id
WHERE
  l.external_id = :lock_id
  AND l.type = :lock_type
  AND s.process_ref = :process_ref
