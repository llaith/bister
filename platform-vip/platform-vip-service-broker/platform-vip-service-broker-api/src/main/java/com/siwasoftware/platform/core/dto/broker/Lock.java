/*
 * @Author Nos Doughty
 */
package com.siwasoftware.platform.core.dto.broker;


import tkt.stilleto.toolkit.common.util.exception.ApplicationException;
import tkt.stilleto.toolkit.common.util.lang.Guard;
import tkt.stilleto.toolkit.db.core.dao.simple.SimpleEntity;

import java.util.Date;


public class Lock extends SimpleEntity {

    public enum LockType {
        EVENT_SOURCE, TASK_QUEUE, WATCH_GROUP
    }

    private Integer sessionId;
    private Date lockDate;
    private Integer lockTtl;
    private String type;
    private String watermark;
    private String target;
    private Integer persistent;

    /**
     * Constructs a new Object
     */
    public Lock() {
        super();
    }

    /**
     * Constructs a new Object
     */
    public Lock(final Integer sessionId, final Date lockDate, final Integer lockTtl, final LockType type, final String watermark, final String target) {
        this(sessionId, lockDate, lockTtl, type, watermark, target, false);
    }

    /**
     * Constructs a new Object
     */
    public Lock(final Integer sessionId, final Date lockDate, final Integer lockTtl, final LockType type, final String watermark, final String target, final boolean persistent) {
        super();
        this.sessionId = sessionId;
        this.lockDate = lockDate;
        this.lockTtl = lockTtl;
        this.type = type.name();
        this.watermark = watermark;
        this.target = target;
        this.persistent = persistent ? 1 : 0;
    }

    public Lock verify(final String watermark, final String target) {

        if (!(this.watermark.equals(watermark) && this.target.equals(target))) throw new ApplicationException(
                "Lock does not match target.",
                String.format(
                        "Lock: %s does not match target with %s/%s",
                        this,
                        watermark,
                        target));

        return this;
    }


    /**
     * Gets the value for getSessionId
     *
     * @return the return value for
     */
    public Integer getSessionId() {
        return this.sessionId;
    }

    /**
     * Sets the value for setSessionId
     *
     * @param sessionId the param value for
     */
    public void setSessionId(final Integer sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets the value for getLockDate
     *
     * @return the return value for
     */
    public Date getLockDate() {
        return this.lockDate;
    }

    /**
     * Sets the value for setLockDate
     *
     * @param lockDate the param value for
     */
    public void setLockDate(final Date lockDate) {
        this.lockDate = lockDate;
    }

    /**
     * Gets the value for getLockTtl
     *
     * @return the return value for
     */
    public Integer getLockTtl() {
        return this.lockTtl;
    }

    /**
     * Sets the value for setLockTtl
     *
     * @param lockTtl the param value for
     */
    public void setLockTtl(final Integer lockTtl) {
        this.lockTtl = lockTtl;
    }

    /**
     * Gets the value for getType
     *
     * @return the return value for
     */
    public LockType getType() {
        return Guard.notNull(LockType.valueOf(this.type));
    }

    /**
     * Sets the value for setType
     *
     * @param type the param value for
     */
    public void setType(final LockType type) {
        this.type = type.name();
    }

    /**
     * Gets the value for getWatermark
     *
     * @return the return value for
     */
    public String getWatermark() {
        return this.watermark;
    }

    /**
     * Sets the value for setWatermark
     *
     * @param watermark the param value for
     */
    public void setWatermark(final String watermark) {
        this.watermark = watermark;
    }

    /**
     * Gets the value for getTarget
     *
     * @return the return value for
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * Sets the value for setTarget
     *
     * @param target the param value for
     */
    public void setTarget(final String target) {
        this.target = target;
    }

    public boolean getPersistent() {
        return persistent != null && persistent.equals(1);
    }

    public void setPersistent(final boolean persistent) {
        this.persistent = persistent ? 1 : 0;
    }

}
