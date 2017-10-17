package com.siwasoftware.platform.core.service.broker;

import com.siwasoftware.platform.core.dto.broker.Session;
import com.siwasoftware.platform.core.dto.broker.SessionSubmission;

/**
 *
 */
public interface SessionService {

    String connectSession(SessionSubmission init);

    Session verifySession(String sessionUid);

    void disconnectSession(String sessionUid);

}
