/*
 * @Author Nos Doughty
 */
package com.siwasoftware.platform.core.dto.broker;

import org.hibernate.validator.constraints.NotBlank;
import tkt.stilleto.toolkit.db.core.dao.simple.SimpleEntity;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class Task extends SimpleEntity {

    @NotBlank
    private String watermark;
    @NotBlank
    private String queue;
    @NotNull
    private Date actionDate;
    @NotNull
    private Date triggerDate;
    @NotBlank
    private String state;
    @NotBlank
    private String step;
    private String data;
    @NotNull
    private Date processedDate; // this is NOT the audit one, this is specific to the processing!
    @NotNull
    private Integer sleep;
    @NotNull
    private Integer errorCount = 0;
    private String errorMessage;

    /**
     * Constructs a new Object
     */
    public Task() {
        super();
    }

    /**
     * Constructs a new Object
     */
    public Task(
            final String watermark, final String queue, final Date actionDate, final Date triggerDate, final String state, final String step, final String data,
            final Date processedDate, final Integer sleep) {
        super();
        this.watermark = watermark;
        this.queue = queue;
        this.actionDate = actionDate;
        this.triggerDate = triggerDate;
        this.state = state;
        this.step = step;
        this.data = data;
        this.processedDate = processedDate;
        this.sleep = sleep;
    }

    public Task fromUpdate(final TaskUpdate update) {

        // don't increment updateLevel in here, will break.

        this.state = update.getState();
        this.step = update.getStep();
        this.data = update.getData();
        this.errorCount = update.getErrorCount();
        this.errorMessage = update.getErrorMessage();
        this.sleep = update.getSleep();

        this.updateDate = new Date();

        return this;

    }

    public TaskUpdate toUpdate() {

        return new TaskUpdate(
                this.externalId,
                this.state,
                this.step,
                this.data);

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
     * @param    watermark    the param value for
     */
    public void setWatermark(final String watermark) {
        this.watermark = watermark;
    }

    /**
     * Gets the value for getQueue
     *
     * @return the return value for
     */
    public String getQueue() {
        return this.queue;
    }

    /**
     * Sets the value for setQueue
     *
     * @param    queue    the param value for
     */
    public void setQueue(final String queue) {
        this.queue = queue;
    }

    /**
     * Gets the value for getActionDate
     *
     * @return the return value for
     */
    public Date getActionDate() {
        return this.actionDate;
    }

    /**
     * Sets the value for setActionDate
     *
     * @param    actionDate    the param value for
     */
    public void setActionDate(final Date actionDate) {
        this.actionDate = actionDate;
    }

    /**
     * Gets the value for getTriggerDate
     *
     * @return the return value for
     */
    public Date getTriggerDate() {
        return this.triggerDate;
    }

    /**
     * Sets the value for setTriggerDate
     *
     * @param    triggerDate    the param value for
     */
    public void setTriggerDate(final Date triggerDate) {
        this.triggerDate = triggerDate;
    }

    /**
     * Gets the value for getState
     *
     * @return the return value for
     */
    public String getState() {
        return this.state;
    }

    /**
     * Sets the value for setState
     *
     * @param    state    the param value for
     */
    public void setState(final String state) {
        this.state = state;
    }

    /**
     * Gets the value for getStep
     *
     * @return the return value for
     */
    public String getStep() {
        return this.step;
    }

    /**
     * Sets the value for setStep
     *
     * @param    step    the param value for
     */
    public void setStep(final String step) {
        this.step = step;
    }

    /**
     * Gets the value for getData
     *
     * @return the return value for
     */
    public String getData() {
        return this.data;
    }

    /**
     * Sets the value for setData
     *
     * @param    data    the param value for
     */
    public void setData(final String data) {
        this.data = data;
    }

    /**
     * Gets the value for getErrorCount
     *
     * @return the return value for
     */
    public Integer getErrorCount() {
        return this.errorCount;
    }

    /**
     * Sets the value for setErrorCount
     *
     * @param    errorCount    the param value for
     */
    public void setErrorCount(final Integer errorCount) {
        this.errorCount = errorCount;
    }

    /**
     * Gets the value for getErrorMessage
     *
     * @return the return value for
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Sets the value for setErrorMessage
     *
     * @param    errorMessage    the param value for
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the value for getSleep
     *
     * @return the return value for
     */
    public Integer getSleep() {
        return this.sleep;
    }

    /**
     * Sets the value for setSleep
     *
     * @param    sleep    the param value for
     */
    public void setSleep(final Integer sleep) {
        this.sleep = sleep;
    }

    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(final Date processedDate) {
        this.processedDate = processedDate;
    }

}
