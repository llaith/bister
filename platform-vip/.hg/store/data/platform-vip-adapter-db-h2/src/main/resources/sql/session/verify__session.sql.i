         N   M       ??????????	?e IֹH7?X?            uSELECT id
FROM session
WHERE
  id = :sessionId
  AND disconnect_date IS NULL
