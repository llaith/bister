SELECT
  lock.id           id,
  lock.update_count update_count,
  lock.update_date  update_date,
  lock.create_date  create_date,
  lock.delete_date  delete_date,
  lock.external_id  external_id,
  lock.session_id   session_id,
  lock.lock_date    lock_date,
  lock.lock_ttl     lock_ttl,
  lock.type         type,
  lock.watermark    watermark,
  lock.target       target,
  lock.persistent   persistent
FROM
  lock
  INNER JOIN session ON lock.session_id = session.id
WHERE
  session.process_ref = :process_ref -- without caring about expired sessions we get the process for the current session
  AND lock.delete_date IS NULL
  AND lock.lock_date >= :last_date -- keyset pagination is faster and easier to be cross-platform
  AND ((lock.lock_date > :last_date) OR (lock.lock_date = :last_date AND lock.id > :last_id))
ORDER BY
  lock_date,
  lock.ID -- must have orderby for keyset pagination to work!
  -- ${paging} we add keyset paging to this at runtime 
