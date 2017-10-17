package com.siwasoftware.platform.core.dto.broker;


import tkt.stilleto.toolkit.common.util.lang.Stringify;

/**
 *
 */
public class SessionSubmission {

    private final String processRef; // instance

    private final String referrer; // ip

    private final String cookie; // json data


    public SessionSubmission(final String processRef, final String referrer, final String cookie) {

        this.processRef = processRef;
        this.referrer = referrer;
        this.cookie = cookie;

    }

    public String getProcessRef() {
        return processRef;
    }

    public String getReferrer() {
        return referrer;
    }

    public String getCookie() {
        return cookie;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public String toString() {
        return Stringify.toString(this);
    }

}
