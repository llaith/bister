DELETE
FROM lock
WHERE
  session_id = :session_id
  AND persistent <> 1
