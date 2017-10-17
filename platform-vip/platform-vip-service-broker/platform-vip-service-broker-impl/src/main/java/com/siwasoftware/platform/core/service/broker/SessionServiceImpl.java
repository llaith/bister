package com.siwasoftware.platform.core.service.broker;

import com.siwasoftware.platform.core.dao.broker.SessionDao;
import com.siwasoftware.platform.core.dto.broker.Session;
import com.siwasoftware.platform.core.dto.broker.SessionSubmission;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.TransactionStatus;
import tkt.stilleto.toolkit.common.util.etc.Configs;
import tkt.stilleto.toolkit.common.util.etc.Services;
import tkt.stilleto.toolkit.common.util.lang.Guard;

import java.util.Date;

/**
 * Task -> TaskHandle (on add)
 * => queue.auditTrail()
 * -> TaskStatus (on lock)
 * -> TaskUpdate
 * => release()
 * -> TaskAudit
 */
public class SessionServiceImpl implements SessionService {

    public static class ServiceConfig {

        public int ttl = 300;
    }

    private final ServiceConfig config;

    private final Services services;

    private final DaoManager daoManager;

    public SessionServiceImpl(
            final Configs configs,
            final Services services,
            final DaoManager daoManager) {

        this.config = Guard.notNull(configs.configFor(ServiceConfig.class));

        this.services = services;

        this.daoManager = daoManager;

    }

    @Override
    public String connectSession(final SessionSubmission init) {

        final Session session = new Session();
        session.setProcessId(process.getId());
        session.setProcessRef(init.getProcessRef());
        session.setReferrer(init.getReferrer());
        session.setCookie(init.getCookie());
        session.setConnectDate(new Date());
        session.setConnectTtl(this.config.ttl);

        this.daoManager.inTransaction(
                (Handle connection, TransactionStatus status) ->
                        this.daoManager
                                .sessionFor(connection)
                                .with(SessionDao.class)
                                .create(session)
                                .ifNoResultThrowError("Session could not be created."));

        return session.getExternalId();

    }

    @Override
    public Session verifySession(final String sessionUid) {

        return this.daoManager.inTransaction(
                (Handle connection, TransactionStatus status) ->
                        this.daoManager
                                .sessionFor(connection)
                                .with(SessionDao.class)
                                .verifySession(sessionUid)
                                .ifNoResultThrowError("Session has expired.")
                                .getResult());

    }

    @Override
    public void disconnectSession(final String sessionUid) {

        // delibrately no opt-locking on sessions, race is safest.

        final Session session = this.verifySession(sessionUid);

        session.setDisconnectDate(new Date());
        session.setDisconnectReason("disconnect"); // nice normal disconnect

        this.daoManager.inTransaction(
                (Handle connection, TransactionStatus status) -> {

                    final Integer sessionPkey = this.daoManager
                            .sessionFor(connection)
                            .with(SessionDao.class)
                            .resolveExternalId(sessionUid)
                            .ifNoResultThrowError("Session was lost.")
                            .getResult();

                    return this.daoManager
                            .sessionFor(connection)
                            .with(SessionDao.class)
                            .update(session)
                            .ifNoUpdatesThrowError("Session was lost.");

                });

    }

}



