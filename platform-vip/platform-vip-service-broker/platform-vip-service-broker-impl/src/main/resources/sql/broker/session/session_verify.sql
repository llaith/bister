SELECT
  id,
  update_count,
  update_date,
  create_date,
  delete_date,
  external_id,
  process_id,
  process_ref,
  referrer,
  cookie,
  connect_date,
  connect_ttl,
  disconnect_date,
  disconnect_reason
FROM session
WHERE
  external_id = :external_id
  AND disconnect_date IS NULL
