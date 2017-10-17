package com.siwasoftware.platform.core.dto.broker;

import org.hibernate.validator.constraints.NotBlank;
import tkt.stilleto.toolkit.common.util.lang.Stringify;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * TODO: need to add all the guards to these
 */
public class TaskSubmission {

    @NotNull
    private final Date actionDate;
    @NotNull
    private final Date triggerDate;
    @NotBlank
    private final String state;
    @NotBlank
    private final String step;
    private final String data;


    public TaskSubmission(final Date actionDate, final Date triggerDate,
                          final String state, final String step, final String data) {

        this.actionDate = actionDate;
        this.triggerDate = triggerDate;
        this.state = state;
        this.step = step;
        this.data = data;

    }

    public String getState() {
        return state;
    }

    public String getStep() {
        return step;
    }

    public String getData() {
        return data;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public Date getTriggerDate() {
        return triggerDate;
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
