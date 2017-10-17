package com.siwasoftware.platform.core.dto.broker;

import tkt.stilleto.toolkit.db.core.dao.simple.SimpleEntity;

import java.util.Date;

/**
 *
 */
public class Event extends SimpleEntity {

    private String watermark;
    private String source;
    private String type;
    private Date actionDate;
    private Date triggerDate;
    private String uniqueRef;
    private String data;

    public Event() {
        super();
    }

    public String getWatermark() {
        return watermark;
    }

    public void setWatermark(final String watermark) {
        this.watermark = watermark;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(final Date actionDate) {
        this.actionDate = actionDate;
    }

    public Date getTriggerDate() {
        return triggerDate;
    }

    public void setTriggerDate(final Date triggerDate) {
        this.triggerDate = triggerDate;
    }

    public String getUniqueRef() {
        return uniqueRef;
    }

    public void setUniqueRef(final String uniqueRef) {
        this.uniqueRef = uniqueRef;
    }

    public String getData() {
        return data;
    }

    public void setData(final String data) {
        this.data = data;
    }

}
