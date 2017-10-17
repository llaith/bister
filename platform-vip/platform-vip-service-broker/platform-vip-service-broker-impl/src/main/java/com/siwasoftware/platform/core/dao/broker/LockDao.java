package com.siwasoftware.platform.core.dao.broker;


import com.codahale.metrics.MetricRegistry;
import com.siwasoftware.platform.core.dto.broker.Lock;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.LongColumnMapper;
import tkt.stilleto.daokit.adapter.jdbi.dao.JdbiDaoActionFactory;
import tkt.stilleto.toolkit.common.results.ResultCount;
import tkt.stilleto.toolkit.common.results.ResultList;
import tkt.stilleto.toolkit.common.results.ResultObject;
import tkt.stilleto.toolkit.db.core.dao.EntityDaoStatements;
import tkt.stilleto.toolkit.db.core.dao.simple.SimpleDao;
import tkt.stilleto.toolkit.db.core.statement.StatementBuilder;
import tkt.stilleto.toolkit.db.core.statement.builder.PagingBuilder;

import java.util.Date;

import static tkt.stilleto.daokit.adapter.jdbi.dao.JdbiDaoActionFactory.PARAMETER_PREFIX;
import static tkt.stilleto.toolkit.common.util.lang.Guard.notNull;
import static tkt.stilleto.toolkit.db.core.statement.StatementBuilder.fromClasspath;

/**
 *
 */
public class LockDao extends SimpleDao<Handle,Lock> {

    public static class Statements {

        final StatementBuilder resolveLockStatement = fromClasspath("sql/broker/lock/lock_resolve.sql");
        final StatementBuilder clearLockStatement = fromClasspath("sql/broker/lock/lock_delete_all.sql");
        final StatementBuilder listLocksStatement = fromClasspath("sql/broker/lock/lock_list_locks.sql");
        final StatementBuilder debugLockStatement = fromClasspath("select * from lock where session_id = :session_id");

    }

    private final Statements statements;

    private static final PagingBuilder.PagingSupport pager = PagingBuilder.PagingSupport.POSTGRES;

    public LockDao(final Statements statements, final MetricRegistry metrics, final Handle connection, String schema) {

        super(
                schema,
                "lock",
                Lock.class,
                EntityDaoStatements.newPostgresStatements(),
                new JdbiDaoActionFactory().build(Lock.class, LongColumnMapper.WRAPPER),
                PARAMETER_PREFIX,
                metrics,
                connection);

        this.statements = notNull(statements);

    }

    public Lock verifyLock(
            final Lock.LockType lockType, final String lockId, final Integer processRef,
            final String watermark, final String target) {

        return this
                .resolveLock(lockType, lockId, processRef)
                .expectNotNull("Lock expired")
                .expectResultElseError("Lock expired")
                .verify(watermark, target);

    }

    public ResultObject<Lock> resolveLock(final Lock.LockType lockType, final String lockId, final Integer processRef) {

        return this.statements.resolveLockStatement
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("lock_type", lockType.name())
                .setParameter("lock_id", lockId)
                .setParameter("process_ref", processRef)
                .usingConnection(this.connection)
                .executeSelect(this.actions.select);

    }

    public ResultList<Lock> listLocks(final Integer processRef, final Date lastDate, final int lastId, final int limit) {

        // not completely safe to give out ids as you can't ever load by them!
        // note: date,id approach is here: http://use-the-index-luke.com/no-offset

        return this.statements.listLocksStatement
                .resolveAsPaging("paging", pager, b -> b.addLimit(limit))
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("process_ref", processRef)
                .setParameter("last_date", lastDate)
                .setParameter("last_id", lastId)
                .usingConnection(this.connection)
                .executeQuery(this.actions.query);

    }

    public ResultCount deleteAllLocks(final Integer sessionId) {

        return this.statements.clearLockStatement
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("session_id", sessionId)
                .usingConnection(this.connection)
                .executeUpdate(this.actions.update);

    }

    public ResultList<Lock> debugListLocks(final Integer session_id) {

        return this.statements.debugLockStatement
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("session_id", session_id)
                .usingConnection(this.connection)
                .executeQuery(this.actions.query);

    }

}
