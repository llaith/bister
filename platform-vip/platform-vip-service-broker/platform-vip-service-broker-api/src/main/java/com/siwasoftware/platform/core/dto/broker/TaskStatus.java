package com.siwasoftware.platform.core.dto.broker;

import tkt.stilleto.toolkit.common.util.lang.Stringify;

import java.util.Date;

/**
 *
 */
public class TaskStatus {

    // status has no lock, but info does.
    // all read only. No useful payload fields.
    private String externalId;
    private String watermark;
    private String queue;
    private Date actionDate;
    private Date triggerDate;
    private String state;
    private String step;
    private Date processedDate;
    private int sleep;
    private int errorCount;
    private String errorMessage;

    public TaskStatus() {
        super();
    }

    public TaskStatus(final String externalId,
                      final String watermark, final String queue,
                      final Date actionDate, final Date triggerDate,
                      final String state, final String step,
                      final Date processedDate, final int sleep,
                      final int errorCount, final String errorMessage) {

        this.externalId = externalId;
        this.watermark = watermark;
        this.queue = queue;
        this.actionDate = actionDate;
        this.triggerDate = triggerDate;
        this.state = state;
        this.step = step;
        this.processedDate = processedDate;
        this.sleep = sleep;
        this.errorCount = errorCount;
        this.errorMessage = errorMessage;

    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(final String externalId) {
        this.externalId = externalId;
    }

    public String getWatermark() {
        return watermark;
    }

    public void setWatermark(final String watermark) {
        this.watermark = watermark;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(final String queue) {
        this.queue = queue;
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

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getStep() {
        return step;
    }

    public void setStep(final String step) {
        this.step = step;
    }

    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(final Date processedDate) {
        this.processedDate = processedDate;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(final int sleep) {
        this.sleep = sleep;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(final int errorCount) {
        this.errorCount = errorCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
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
