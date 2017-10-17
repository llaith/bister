INSERT INTO task (version_id, update_count, update_date, watermark, queue, action_date, trigger_date, state, step, data, error_count, sleep)
VALUES (1, 1, now(), 'bridge1', 'queue1', now(), now(), 'state1', 'step1', 'data1', 0, 0);
COMMIT;

INSERT INTO task (version_id, update_count, update_date, watermark, queue, action_date, trigger_date, state, step, data, error_count, sleep)
VALUES (2, 1, now(), 'bridge1', 'queue2', now(), now(), 'state1', 'step1', 'data1', 0, 0);
COMMIT;

INSERT INTO task (version_id, update_count, update_date, watermark, queue, action_date, trigger_date, state, step, data, error_count, sleep)
VALUES (3, 1, now(), 'bridge1', 'queue3', now(), now(), 'state1', 'step1', 'data1', 0, 0);
COMMIT;

INSERT INTO task (version_id, update_count, update_date, watermark, queue, action_date, trigger_date, state, step, data, error_count, sleep)
VALUES (4, 1, now(), 'bridge1', 'queue1', now(), now(), 'state1', 'step1', 'data1', 0, 0);
COMMIT;

INSERT INTO task (version_id, update_count, update_date, watermark, queue, action_date, trigger_date, state, step, data, error_count, sleep)
VALUES (5, 1, now(), 'bridge1', 'queue3', now(), now(), 'state1', 'step1', 'data1', 0, 0);
COMMIT;

INSERT INTO session (id, process_ref, referrer, cookie, connect_date, connect_ttl)
VALUES (1, 'instance1', 'referrer1', 'cookie1', now(), 300);
COMMIT;

INSERT INTO lock (session_id, lock_date, lock_pid, lock_ttl, type, target, record)
VALUES (1, now(), 'lock1', 300, 'TASK_QUEUE', 'queue1', NULL);
COMMIT;
