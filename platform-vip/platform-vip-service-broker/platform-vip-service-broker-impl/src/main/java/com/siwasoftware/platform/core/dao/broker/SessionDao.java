package com.siwasoftware.platform.core.dao.broker;


import com.codahale.metrics.MetricRegistry;
import com.siwasoftware.platform.core.dto.broker.Session;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.LongColumnMapper;
import tkt.stilleto.daokit.adapter.jdbi.dao.JdbiDaoActionFactory;
import tkt.stilleto.toolkit.common.results.ResultObject;
import tkt.stilleto.toolkit.db.core.dao.EntityDaoStatements;
import tkt.stilleto.toolkit.db.core.dao.simple.SimpleDao;
import tkt.stilleto.toolkit.db.core.statement.StatementBuilder;

import static tkt.stilleto.daokit.adapter.jdbi.dao.JdbiDaoActionFactory.PARAMETER_PREFIX;
import static tkt.stilleto.toolkit.common.util.lang.Guard.notNull;
import static tkt.stilleto.toolkit.db.core.statement.StatementBuilder.fromClasspath;

/**
 *
 */
public class SessionDao extends SimpleDao<Handle,Session> {

    public static class Statements {

        private final StatementBuilder verifySessionStatement = fromClasspath("sql/broker/session/session_verify.sql");


    }

    private final Statements statements;

    public SessionDao(final Statements statements, final MetricRegistry metrics, final Handle connection, String schema) {

        super(
                schema,
                "session",
                Session.class,
                EntityDaoStatements.newPostgresStatements(),
                new JdbiDaoActionFactory().build(Session.class, LongColumnMapper.WRAPPER),
                PARAMETER_PREFIX,
                metrics,
                connection);

        this.statements = notNull(statements);

    }

    public ResultObject<Session> verifySession(final String externalId) {

        return this.statements.verifySessionStatement
                .composeWithPrefix(PARAMETER_PREFIX)
                .setParameter("external_id", externalId)
                .usingConnection(this.connection)
                .executeSelect(this.actions.select);

    }

}
