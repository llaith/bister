package com.siwasoftware.platform.core.dto.broker;

import tkt.stilleto.toolkit.common.util.lang.Stringify;

import java.util.Date;

/**
 *
 */
public class WatchUpdate {

    private String externalId;
    private String state;
    private String data;
    private Date lastModifiedDate; // this is not the same as the update date, it's used for a targets last modified date!
    private Integer linkId;
    private String linkReason;
    private Integer linkRootId;
    private Integer updateTaskId;


    public WatchUpdate(
            final String externalId, final String state, final String data, final Date lastModifiedDate,
            final Integer linkId, final String linkReason, final Integer linkRootId, final Integer updateTaskId) {

        this.externalId = externalId;
        this.state = state;
        this.data = data;
        this.lastModifiedDate = lastModifiedDate;
        this.linkId = linkId;
        this.linkReason = linkReason;
        this.linkRootId = linkRootId;
        this.updateTaskId = updateTaskId;

    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(final String externalId) {
        this.externalId = externalId;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getData() {
        return data;
    }

    public void setData(final String data) {
        this.data = data;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Integer getLinkId() {
        return linkId;
    }

    public void setLinkId(final Integer linkId) {
        this.linkId = linkId;
    }

    public String getLinkReason() {
        return linkReason;
    }

    public void setLinkReason(final String linkReason) {
        this.linkReason = linkReason;
    }

    public Integer getLinkRootId() {
        return linkRootId;
    }

    public void setLinkRootId(final Integer linkRootId) {
        this.linkRootId = linkRootId;
    }

    public Integer getUpdateTaskId() {
        return updateTaskId;
    }

    public void setUpdateTaskId(final Integer updateTaskId) {
        this.updateTaskId = updateTaskId;
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
