/*
 * @Author Nos Doughty
 */
package com.siwasoftware.platform.core.dto.broker;

import tkt.stilleto.toolkit.db.core.dao.simple.SimpleEntity;

import java.util.Date;


public class Session extends SimpleEntity {

    private String processRef;

    private String referrer;

    private String cookie;

    private Date connectDate;

    private Integer connectTtl;

    private Date disconnectDate;

    private String disconnectReason;

    /**
     * Constructs a new Object
     */
    public Session() {
        super();
    }

    /**
     * Constructs a new Object
     */
    public Session(final String processRef, final String referrer, final String cookie, final Date connectDate, final Integer connectTtl, final Date disconnectDate, final String disconnectReason) {
        super();
        this.processRef = processRef;
        this.referrer = referrer;
        this.cookie = cookie;
        this.connectDate = connectDate;
        this.connectTtl = connectTtl;
        this.disconnectDate = disconnectDate;
        this.disconnectReason = disconnectReason;
    }

    /**
     * Gets the value for getProcessRef
     *
     * @return the return value for
     */
    public String getProcessRef() {
        return this.processRef;
    }

    /**
     * Sets the value for setProcessRef
     *
     * @param processRef the param value for
     */
    public void setProcessRef(final String processRef) {
        this.processRef = processRef;
    }

    /**
     * Gets the value for getReferrer
     *
     * @return the return value for
     */
    public String getReferrer() {
        return this.referrer;
    }

    /**
     * Sets the value for setReferrer
     *
     * @param referrer the param value for
     */
    public void setReferrer(final String referrer) {
        this.referrer = referrer;
    }

    /**
     * Gets the value for getCookie
     *
     * @return the return value for
     */
    public String getCookie() {
        return this.cookie;
    }

    /**
     * Sets the value for setCookie
     *
     * @param cookie the param value for
     */
    public void setCookie(final String cookie) {
        this.cookie = cookie;
    }

    /**
     * Gets the value for getConnectDate
     *
     * @return the return value for
     */
    public Date getConnectDate() {
        return this.connectDate;
    }

    /**
     * Sets the value for setConnectDate
     *
     * @param connectDate the param value for
     */
    public void setConnectDate(final Date connectDate) {
        this.connectDate = connectDate;
    }

    /**
     * Gets the value for getConnectTtl
     *
     * @return the return value for
     */
    public Integer getConnectTtl() {
        return this.connectTtl;
    }

    /**
     * Sets the value for setConnectTtl
     *
     * @param connectTtl the param value for
     */
    public void setConnectTtl(final Integer connectTtl) {
        this.connectTtl = connectTtl;
    }

    /**
     * Gets the value for getDisconnectDate
     *
     * @return the return value for
     */
    public Date getDisconnectDate() {
        return this.disconnectDate;
    }

    /**
     * Sets the value for setDisconnectDate
     *
     * @param disconnectDate the param value for
     */
    public void setDisconnectDate(final Date disconnectDate) {
        this.disconnectDate = disconnectDate;
    }

    /**
     * Gets the value for getDisconnectReason
     *
     * @return the return value for
     */
    public String getDisconnectReason() {
        return this.disconnectReason;
    }

    /**
     * Sets the value for setDisconnectReason
     *
     * @param disconnectReason the param value for
     */
    public void setDisconnectReason(final String disconnectReason) {
        this.disconnectReason = disconnectReason;
    }

}
