         0   /       �������������Ђ�5��j�����>            uDELETE
FROM lock
WHERE session_id = :sessionId
     0     A   F       �    �����6l�-��"Ν�	N�U��>                  /   5WHERE
  session_id = :sessionId
  AND persist <> 'y'
     q     H   G        ����2;��/��$h���G6�d��            uDELETE
FROM lock
WHERE
  session_id = :sessionId
  AND persistent <> 1
