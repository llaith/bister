package com.siwasoftware.platform.core.dto.broker;

import tkt.stilleto.toolkit.common.util.lang.Stringify;

import java.util.Date;

/**
 *
 */
public class WatchSubmission {

    private final String watermark;
    private final String type;
    private final String namespace;
    private final String owner;
    private final String record;
    private final String state;
    private final String data;
    private final Date lastModifiedDate; // this is not the same as the update date, it's used for a targets last modified date!
    private final Integer linkId;
    private final String linkReason;
    private final Integer linkRootId;
    private final Integer updateTaskId;


    public WatchSubmission(
            final String watermark, final String type, final String namespace, final String owner, final String record,
            final String state,
            final String data, final Date lastModifiedDate, final Integer linkId, final String linkReason,
            final Integer linkRootId,
            final Integer updateTaskId) {

        this.watermark = watermark;
        this.type = type;
        this.namespace = namespace;
        this.owner = owner;
        this.record = record;
        this.state = state;
        this.data = data;
        this.lastModifiedDate = lastModifiedDate;
        this.linkId = linkId;
        this.linkReason = linkReason;
        this.linkRootId = linkRootId;
        this.updateTaskId = updateTaskId;

    }

    public String getWatermark() {
        return watermark;
    }

    public String getType() {
        return type;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getOwner() {
        return owner;
    }

    public String getRecord() {
        return record;
    }

    public String getState() {
        return state;
    }

    public String getData() {
        return data;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Integer getLinkId() {
        return linkId;
    }

    public String getLinkReason() {
        return linkReason;
    }

    public Integer getLinkRootId() {
        return linkRootId;
    }

    public Integer getUpdateTaskId() {
        return updateTaskId;
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
