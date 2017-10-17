package com.siwasoftware.platform.core.service.broker;

import com.siwasoftware.platform.core.dao.broker.LockDao;
import com.siwasoftware.platform.core.dao.broker.TaskDao;
import com.siwasoftware.platform.core.dto.audit.AuditEntry.AuditAction;
import com.siwasoftware.platform.core.dto.broker.Lock;
import com.siwasoftware.platform.core.dto.broker.Task;
import com.siwasoftware.platform.core.dto.broker.TaskQueueInfo;
import com.siwasoftware.platform.core.dto.broker.TaskStatus;
import com.siwasoftware.platform.core.dto.broker.TaskSubmission;
import com.siwasoftware.platform.core.dto.broker.TaskUpdate;
import com.siwasoftware.platform.core.dto.broker.Session;
import com.siwasoftware.platform.core.service.audit.AuditService;
import com.siwasoftware.platform.core.util.UuidUtil;
import com.siwasoftware.platform.core.util.ValidationUtil;
import org.llaith.toolkit.adapter.db.jdbi.dao.DaoManager;
import org.llaith.toolkit.adapter.db.jdbi.dao.DaoSession;
import org.llaith.toolkit.common.etc.Configs;
import org.llaith.toolkit.common.etc.Services;
import org.llaith.toolkit.common.guard.Guard;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validator;
import java.util.Date;
import java.util.List;

/**
 * Task -> TaskHandle (on add)
 * => queue.auditTrail()
 * -> TaskStatus (on lock)
 * -> TaskUpdate
 * => release()
 * -> TaskAudit
 * <p>
 * <p>
 * Generally these don't need optimistic locking because they have manual pessimistic locking (the Lock entity)
 */
public class TaskServiceImpl implements TaskService {

    public final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    public static class ServiceConfig {

        public int lockTtl = 300; // TODO: check the request ttl against this.
        // also note, when the ttl is up, DO NOT clear it, just send a warning mail!
        // clear locks should be a manual process - change the release_old_tasks.sql to be by id and controlled by
        // the dashboard. This means the ttl is not based per worker!
    }

    private final ServiceConfig config;

    private final Services services;
    private final DaoManager daoManager;

    private Validator validator; // manual validation mode

    public TaskServiceImpl(
            final Configs configs,
            final Services services,
            final DaoManager daoManager) {

        this.config = Guard.notNull(configs.configFor(ServiceConfig.class));

        this.daoManager = daoManager;

        this.services = services;

    }

    public TaskServiceImpl setManualValidation(final Validator validator) {
        this.validator = validator;

        return this;
    }

    @Override
    public TaskQueueInfo getQueueInfo(final String sessionUid, final String watermark, final String queue) {

        return this.daoManager.inTransaction((Handle connection, TransactionStatus status) -> {

            // check the session
            this.services
                    .serviceFor(SessionService.class)
                    .verifySession(sessionUid);

            // return queue locks
            return this.daoManager
                    .sessionFor(connection)
                    .with(TaskDao.class)
                    .listTaskQueue(watermark, queue)
                    .getResult();

        });

    }

    @Override
    public List<TaskQueueInfo> listQueueInfo(final String sessionUid, final String watermark, final int limit) {

        return this.daoManager.inTransaction((Handle connection, TransactionStatus status) -> {

            // get a dao session
            final DaoSession daos = this.daoManager.sessionFor(connection);

            // check the session
            this.services
                    .serviceFor(SessionService.class)
                    .verifySession(sessionUid);

            // return queue locks
            return daos
                    .with(TaskDao.class)
                    .listTaskQueues(watermark, limit)
                    .getResults();


        });

    }

    @Override
    public List<Lock> listQueueLocks(
            final String sessionUid, final Date lastDate, final Integer lastId, final int limit) {

        return this.daoManager.inTransaction((Handle connection, TransactionStatus status) -> {

            // get a dao session
            final DaoSession daos = this.daoManager.sessionFor(connection);

            // check the session
            final Session session = this.services
                    .serviceFor(SessionService.class)
                    .verifySession(sessionUid);

            // return queue locks
            return daos
                    .with(LockDao.class)
                    .listLocks(session.getProcessId(), lastDate, lastId, limit)
                    .getResults();


        });
    }

    @Override
    public String lockNextQueue(final String sessionUid, final String watermark) {

        return this.daoManager.inTransaction((Handle connection, TransactionStatus status) -> {

            // get a dao session
            final DaoSession daos = this.daoManager.sessionFor(connection);

            // check the session
            final Session session = this.services
                    .serviceFor(SessionService.class)
                    .verifySession(sessionUid);

            // else we check for the specific queue
            final String externalId = UuidUtil.externalId();

            // lock the next available queue
            final Integer id = daos
                    .with(TaskDao.class)
                    .lockNextQueue(
                            session.getId(),
                            externalId,
                            watermark,
                            300,
                            true)
                    .getResult();

            // return null if no rows, else the external id
            return id == null ? null : externalId;

        });

    }

    @Override
    public String lockRequestedQueue(final String sessionUid, final String watermark, final String requestedQueue) {

        return this.daoManager.inTransaction((Handle connection, TransactionStatus status) -> {

            // get a dao session
            final DaoSession daos = this.daoManager.sessionFor(connection);

            // check the session
            final Session session = this.services
                    .serviceFor(SessionService.class)
                    .verifySession(sessionUid);

            // else we check for the specific queue
            final String externalId = UuidUtil.externalId();

            // lock the requested queue
            daos.with(TaskDao.class)
                .lockRequestedQueue(
                        session.getId(),
                        externalId,
                        watermark,
                        requestedQueue,
                        300,
                        true)
                .ifNoResultThrowError("Lock conflict, requestedQueue is already locked!");

            return externalId;

        });

    }

    @Override
    public void releaseQueueLock(final String sessionUid, final String lockUid) {

        this.daoManager.inTransaction((Handle connection, TransactionStatus status) -> {

            // get a dao session
            final DaoSession daos = this.daoManager.sessionFor(connection);

            // check the session
            final Session session = this.services
                    .serviceFor(SessionService.class)
                    .verifySession(sessionUid);

            // verify/load the lock
            final Lock lock = daos
                    .with(LockDao.class)
                    .resolveLock(Lock.LockType.TASK_QUEUE, lockUid, session.getProcessId())
                    .ifNoResultThrowError("Lock is expired.")
                    .getResult();

            // delete the lock
            daos.with(LockDao.class)
                .delete(lock.getId())
                .ifNoUpdatesThrowError("Could not delete lock.");

            // return nothing
            return null;

        });

    }

    @Override
    public String submitTask(final String sessionUid, final String lockUid, final TaskSubmission init) {

        return this.daoManager.inTransaction((Handle connection, TransactionStatus status) -> {

            if (this.validator != null) {
                ValidationUtil.throwIfFails(this.validator.validate(init));
            }

            // get a dao session
            final DaoSession daos = this.daoManager.sessionFor(connection);

            // check the session
            final Session session = this.services
                    .serviceFor(SessionService.class)
                    .verifySession(sessionUid);

            // check the lock
            final Lock lock = daos
                    .with(LockDao.class)
                    .resolveLock(Lock.LockType.TASK_QUEUE, lockUid, session.getProcessId())
                    .ifNoResultThrowError("Cannot retrieve queue lock for task submission.")
                    .getResult();

            // create a task object
            final Task task = daos
                    .with(TaskDao.class)
                    .createAndReturn(new Task(
                            lock.getWatermark(),
                            lock.getTarget(),
                            init.getActionDate(),
                            init.getTriggerDate(),
                            init.getState(),
                            init.getStep(),
                            init.getData(),
                            new Date(),
                            0))
                    .getResult();

            // save audit entry
            this.services
                    .serviceFor(AuditService.class)
                    .auditEntity(lock.getSessionId(),
                                 AuditAction.CREATE,
                                 task.getUpdateDate(),
                                 task);

            // return id to caller
            return task.getExternalId();

        });

    }

    @Override
    public TaskStatus taskStatus(final String sessionUid, final String taskUid) {

        // DOES NOT REQUIRE LOCK OR AUDITING! :)
        return this.daoManager.inTransaction((Handle connection, TransactionStatus status) -> {

            // check the session
            this.services
                    .serviceFor(SessionService.class)
                    .verifySession(sessionUid);

            // get the status
            return this.daoManager
                    .sessionFor(connection)
                    .with(TaskDao.class)
                    .taskStatus(taskUid)
                    .ifNoResultThrowError("Cannot find task to query status.")
                    .getResult();

        });

    }

    @Override
    public Task nextTask(final String sessionUid, final String lockUid) {

        return this.daoManager.inTransaction((Handle connection, TransactionStatus status) -> {

            // get a dao session
            final DaoSession daos = this.daoManager.sessionFor(connection);

            // check the session
            final Session session = this.services
                    .serviceFor(SessionService.class)
                    .verifySession(sessionUid);

            // verify lock
            final Lock lock = daos
                    .with(LockDao.class)
                    .resolveLock(Lock.LockType.TASK_QUEUE, lockUid, session.getProcessId())
                    .ifNoResultThrowError("Lock is expired.")
                    .getResult();

            // retrieve the info
            final Task task = daos
                    .with(TaskDao.class)
                    .nextTask(lockUid)
                    .getResult();

            if (task != null) {

                // audit the access
                this.services
                        .serviceFor(AuditService.class)
                        .auditEntity(lock.getSessionId(),
                                     AuditAction.READ,
                                     new Date(),
                                     task);

            }

            return task;

        });

    }

    @Override
    public Task updateTask(final String sessionUid, final String lockUid, final TaskUpdate update) {

        return this.daoManager.inTransaction((Handle connection, TransactionStatus status) -> {

            // get a dao session
            final DaoSession daos = this.daoManager.sessionFor(connection);

            // check the session
            final Session session = this.services
                    .serviceFor(SessionService.class)
                    .verifySession(sessionUid);

            // verify we still have lock
            final Lock lock = daos
                    .with(LockDao.class)
                    .resolveLock(Lock.LockType.TASK_QUEUE, lockUid, session.getProcessId())
                    .ifNoResultThrowError("Lock is expired.")
                    .getResult();

            // reload for auditing (a bit ouch)
            final Task task = daos
                    .with(TaskDao.class)
                    .readByExternalId(update.getExternalId())
                    .ifNoResultThrowError("Cannot reload task for update with id: " + update.getExternalId())
                    .getResult();

            // check for lock failure
            lock.verify(task.getWatermark(), task.getQueue());

            // update and save the task
            daos
                    .with(TaskDao.class)
                    .update(task.fromUpdate(update))
                    .ifNoUpdatesThrowError("Cannot save updated task for: " + update.getExternalId());

            // save the audit version
            this.services
                    .serviceFor(AuditService.class)
                    .auditEntity(lock.getSessionId(),
                                 AuditAction.UPDATE,
                                 task.getUpdateDate(),
                                 task);

            return task;

        });

    }

    @Override
    public void deleteTask(final String sessionUid, final String lockUid, final String taskUid, final String reason) {

        this.daoManager.inTransaction((Handle connection, TransactionStatus status) -> {

            // get a dao session
            final DaoSession daos = this.daoManager.sessionFor(connection);

            // check the session
            final Session session = this.services
                    .serviceFor(SessionService.class)
                    .verifySession(sessionUid);

            // verify we still have lock
            final Lock lock = daos
                    .with(LockDao.class)
                    .resolveLock(Lock.LockType.TASK_QUEUE, lockUid, session.getProcessId())
                    .ifNoResultThrowError("Lock is expired.")
                    .getResult();

            // load the task in question
            final Task task = daos
                    .with(TaskDao.class)
                    .readByExternalId(taskUid)
                    .ifNoResultThrowError("Cannot load task with id: " + taskUid)
                    .getResult();

            // verify the lock matches
            lock.verify(task.getWatermark(), task.getQueue());

            // do the soft delete
            daos
                    .with(TaskDao.class)
                    .softDelete(task)
                    .ifNoUpdatesThrowError("Cannot soft-delete task for: " + task.getId());

            // audit
            this.services
                    .serviceFor(AuditService.class)
                    .auditEntity(lock.getSessionId(),
                                 AuditAction.UPDATE,
                                 task.getUpdateDate(),
                                 task);

            // void return
            return null;

        });

    }


    @Override
    public int deleteQueue(final String sessionUid, final String lockUid, final String reason) {

        return this.daoManager.inTransaction((Handle connection, TransactionStatus status) -> {

            int count = 0;

            // get a dao session
            final DaoSession daos = this.daoManager.sessionFor(connection);

            // check the session
            final Session session = this.services
                    .serviceFor(SessionService.class)
                    .verifySession(sessionUid);

            // verify we still have lock
            final Lock lock = daos
                    .with(LockDao.class)
                    .resolveLock(Lock.LockType.TASK_QUEUE, lockUid, session.getProcessId())
                    .ifNoResultThrowError("Lock is expired.")
                    .getResult();

            // repeat until no more
            Integer lastSeenId = 0;
            while (true) {

                final List<Task> results = daos
                        .with(TaskDao.class)
                        .listPending(lock, lastSeenId, 1000)
                        .getResults();

                if (results.isEmpty()) break;

                for (final Task task : results) {

                    // check lock
                    lock.verify(task.getWatermark(), task.getQueue());

                    // soft delete task
                    daos
                            .with(TaskDao.class)
                            .softDelete(task)
                            .ifNoUpdatesThrowError("Cannot save updated task for: " + task.getId());

                    // audit
                    this.services
                            .serviceFor(AuditService.class)
                            .auditEntity(lock.getSessionId(),
                                         AuditAction.UPDATE,
                                         task.getUpdateDate(),
                                         task);

                    // record last id
                    lastSeenId = task.getId();

                    count++;
                }

            }

            return count;

        });

    }

    @Override
    public int cleanQueue(final String sessionUid, final String lockUid, final Date before) {

        // important that before is < not <=. When you want to delete everything, then < now() will get
        // it because you'll have been the last one with the lock and that time is the buffer. However,
        // if you want to delete only closed ones, then you can get the next() and use that date for the
        // 'before' in the call to this, and that will be SAFE.

        return this.daoManager.inTransaction(
                (Handle connection, TransactionStatus status) -> {

                    int count = 0;

                    // get a dao session
                    final DaoSession daos = this.daoManager.sessionFor(connection);

                    // check the session
                    final Session session = this.services
                            .serviceFor(SessionService.class)
                            .verifySession(sessionUid);

                    // verify we still have lock
                    final Lock lock = daos
                            .with(LockDao.class)
                            .resolveLock(Lock.LockType.TASK_QUEUE, lockUid, session.getProcessId())
                            .ifNoResultThrowError("Lock is expired.")
                            .getResult();

                    // repeat until no more
                    Integer lastSeenId = 0;
                    while (true) {

                        final List<Task> results = daos
                                .with(TaskDao.class)
                                .listBefore(lock, before, lastSeenId, 1000)
                                .getResults();

                        if (results.isEmpty()) break;

                        for (final Task task : results) {

                            // verify lock
                            lock.verify(task.getWatermark(), task.getQueue());

                            // delete task
                            daos
                                    .with(TaskDao.class)
                                    .delete(task.getId())
                                    .ifNoUpdatesThrowError("Cannot delete task for: " + task.getId());

                            // audit
                            this.services
                                    .serviceFor(AuditService.class)
                                    .auditEntity(lock.getSessionId(),
                                                 AuditAction.DELETE,
                                                 task.getUpdateDate(),
                                                 task);

                            // record last id
                            lastSeenId = task.getId();

                            count++;
                        }

                    }

                    return count;

                });


    }

}



