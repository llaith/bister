package com.siwasoftware.platform.core.dao.broker;

import com.codahale.metrics.MetricRegistry;
import com.siwasoftware.platform.core.dto.broker.Lock;
import com.siwasoftware.platform.core.dto.broker.Task;
import com.siwasoftware.platform.core.dto.broker.TaskQueueInfo;
import com.siwasoftware.platform.core.dto.broker.TaskStatus;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.LongColumnMapper;
import tkt.stilleto.daokit.adapter.jdbi.dao.JdbiDaoActionFactory;
import tkt.stilleto.toolkit.common.results.ResultCount;
import tkt.stilleto.toolkit.common.results.ResultList;
import tkt.stilleto.toolkit.common.results.ResultObject;
import tkt.stilleto.toolkit.common.util.lang.Guard;
import tkt.stilleto.toolkit.common.util.text.TextLineSnipper;
import tkt.stilleto.toolkit.db.core.dao.EntityDaoActions;
import tkt.stilleto.toolkit.db.core.dao.EntityDaoStatements;
import tkt.stilleto.toolkit.db.core.dao.simple.SimpleDao;
import tkt.stilleto.toolkit.db.core.statement.StatementBuilder;
import tkt.stilleto.toolkit.db.core.statement.builder.PagingBuilder;

import java.util.Date;

import static tkt.stilleto.daokit.adapter.jdbi.dao.JdbiDaoActionFactory.PARAMETER_PREFIX;
import static tkt.stilleto.toolkit.common.util.lang.FileUtil.readFromClasspath;
import static tkt.stilleto.toolkit.common.util.lang.Guard.notNull;
import static tkt.stilleto.toolkit.db.core.statement.StatementBuilder.from;
import static tkt.stilleto.toolkit.db.core.statement.StatementBuilder.fromClasspath;

/**
 *
 */
public class TaskDao extends SimpleDao<Handle,Task> {

    public static class Statements {

        public final StatementBuilder taskStatusStatement =
                fromClasspath("sql/broker/task/task_status.sql");

        public final StatementBuilder lockNextQueueStatement =
                fromClasspath("sql/broker/task/task_lock_next_queue.sql");

        public final StatementBuilder lockRequestedQueueStatement =
                fromClasspath("sql/broker/task/task_lock_requested_queue.sql");

        public final StatementBuilder nextTaskStatement =
                fromClasspath("sql/broker/task/task_next_task.sql");

        public final StatementBuilder cleanQueueStatement =
                fromClasspath("sql/broker/task/task_clean_queue.sql");

        public final StatementBuilder closeQueueStatement =
                fromClasspath("sql/broker/task/task_close_queue.sql");

        public final StatementBuilder listBeforeStatement =
                fromClasspath("sql/broker/task/task_list_before.sql");

        public final StatementBuilder listQueueStatement =
                fromClasspath("sql/broker/task/task_list_queues.sql");

        public final StatementBuilder listQueuesStatement =
                from(new TextLineSnipper()
                             .deleteTag("QUEUE_NAME")
                             .scanAndReturnString(readFromClasspath("sql/broker/task/task_list_queues.sql")));

        public final StatementBuilder listPendingStatement = fromClasspath("sql/broker/task/task_list_pending.sql");

    }

    private static final JdbiDaoActionFactory factory = new JdbiDaoActionFactory();

    private static final EntityDaoActions<Handle,TaskStatus,Long> taskStatusActions =
            factory.build(TaskStatus.class, LongColumnMapper.WRAPPER);

    private static final EntityDaoActions<Handle,TaskQueueInfo,Long> taskQueueInfoActions =
            factory.build(TaskQueueInfo.class, LongColumnMapper.WRAPPER);

    private static final EntityDaoActions<Handle,Task,Long> taskActions =
            factory.build(Task.class, LongColumnMapper.WRAPPER);

    private static final PagingBuilder.PagingSupport pager = PagingBuilder.PagingSupport.POSTGRES;

    private final Statements statements;

    public TaskDao(final Statements statements, final MetricRegistry metrics, final Handle connection, String schema) {

        super(
                schema,
                "task",
                Task.class,
                EntityDaoStatements.newPostgresStatements(),
                taskActions,
                PARAMETER_PREFIX,
                metrics,
                connection);

        this.statements = notNull(statements);

    }

    public ResultObject<TaskStatus> taskStatus(final String externalId) {

        return this.statements.taskStatusStatement
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("external_id", externalId)
                .usingConnection(this.connection)
                .executeSelect(taskStatusActions.select);

    }

    public ResultObject<Long> lockNextQueue(
            final Integer sessionId, final String externalId,
            final String watermark,
            final int ttl, final boolean persistent) {

        return this.statements.lockNextQueueStatement
                .resolveAsPaging("paging", pager, b -> b.addLimit(1))
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("session_id", sessionId)
                .setParameter("external_id", externalId)
                .setParameter("now", new Date())
                .setParameter("watermark", watermark)
                .setParameter("ttl", ttl)
                .setParameter("persistent", persistent ? 1 : 0)
                .usingConnection(this.connection)
                .executeSelect(this.actions.insertAndReturn);

    }

    public ResultObject<Long> lockRequestedQueue(
            final Integer sessionId, final String externalId,
            final String watermark, final String queue,
            final int ttl, final boolean persistent) {

        return this.statements.lockRequestedQueueStatement
                .resolveAsPaging("paging", pager, b -> b.addLimit(1))
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("session_id", sessionId)
                .setParameter("external_id", externalId)
                .setParameter("now", new Date())
                .setParameter("watermark", watermark)
                .setParameter("queue", queue)
                .setParameter("ttl", ttl)
                .setParameter("persistent", persistent ? 1 : 0)
                .usingConnection(this.connection)
                .executeSelect(this.actions.insertAndReturn);

    }

    public ResultObject<Task> nextTask(final String lockId) {

        return this.statements.nextTaskStatement
                .resolveAsPaging("paging", pager, b -> b.addLimit(1))
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("lock_id", lockId)
                .usingConnection(this.connection)
                .executeSelect(this.actions.select);

    }

    public ResultCount cleanQueue(final String watermark, final String queue) {

        return this.statements.cleanQueueStatement
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("watermark", watermark)
                .setParameter("queue", queue)
                .usingConnection(this.connection)
                .executeUpdate(this.actions.update);

    }

    public ResultCount closeQueue(final String watermark, final String queue) {

        return this.statements.closeQueueStatement
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("watermark", watermark)
                .setParameter("queue", queue)
                .usingConnection(this.connection)
                .executeUpdate(this.actions.update);

    }

    public ResultList<Task> listBefore(final Lock lock, final Date before, Integer lastSeenId, final int limit) {

        return this.statements.listBeforeStatement
                .resolveAsPaging("paging", pager, b -> b.addLimitAndOffset(limit, lastSeenId))
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("watermark", lock.getWatermark())
                .setParameter("queue", lock.getTarget())
                .setParameter("before", before)
                .setParameter("last_seen_id", Guard.notNull(lastSeenId))
                .usingConnection(this.connection)
                .executeQuery(this.actions.query);

    }

    public ResultObject<TaskQueueInfo> listTaskQueue(final String watermark, final String queue) {

        return this.statements.listQueuesStatement
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("watermark", watermark)
                .setParameter("queue", queue)
                .usingConnection(this.connection)
                .executeSelect(taskQueueInfoActions.select);

    }

    public ResultList<TaskQueueInfo> listTaskQueues(final String watermark, final int limit) {

        return this.statements.listQueuesStatement
                .resolveAsPaging("paging", pager, b -> b.addLimit(limit))
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("watermark", watermark)
                .usingConnection(this.connection)
                .executeQuery(taskQueueInfoActions.query);

    }

    public ResultList<Task> listPending(final Lock lock, Integer lastSeenId, final int limit) {

        return this.statements.listPendingStatement
                .resolveAsPaging("paging", pager, b -> b.addLimit(limit))
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("watermark", lock.getWatermark())
                .setParameter("queue", lock.getTarget())
                .setParameter("lastSeenId", Guard.notNull(lastSeenId))
                .usingConnection(this.connection)
                .executeQuery(this.actions.query);

    }

}
