package com.siwasoftware.platform.core.dto.broker;

import tkt.stilleto.toolkit.db.core.dao.simple.SimpleEntity;

import java.util.Date;

/**
 * TODO: the DAO associated with this needs special functions for the root!
 *
 */
public class Watch extends SimpleEntity {

    private String watermark;
    private String type;
    private String namespace;
    private String owner;
    private String record;
    private String state;
    private String data;
    private Date lastModifiedDate; // this is not the same as the update date, it's used for a targets last modified date!
    private Integer linkId;
    private String linkReason;
    private Integer linkRootId;
    private Integer updateTaskId;

    public Watch() {
        super();
    }

    public String getWatermark() {
        return watermark;
    }

    public void setWatermark(final String watermark) {
        this.watermark = watermark;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(final String record) {
        this.record = record;
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

}
