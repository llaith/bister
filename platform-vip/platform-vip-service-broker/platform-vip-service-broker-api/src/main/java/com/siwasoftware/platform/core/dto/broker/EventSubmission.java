package com.siwasoftware.platform.core.dto.broker;


import tkt.stilleto.toolkit.common.util.lang.Stringify;

import java.util.Date;

/**
 *
 */
public class EventSubmission {

    private final String type;
    private final Date actionDate;
    private final Date triggerDate;
    private final String uniqueRef;
    private final String data;


    public EventSubmission(final String type, final Date actionDate, final Date triggerDate,
                           final String uniqueRef, final String data) {

        this.type = type;
        this.actionDate = actionDate;
        this.triggerDate = triggerDate;
        this.uniqueRef = uniqueRef;
        this.data = data;

    }

    public String getType() {
        return type;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public Date getTriggerDate() {
        return triggerDate;
    }

    public String getUniqueRef() {
        return uniqueRef;
    }

    public String getData() {
        return data;
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

