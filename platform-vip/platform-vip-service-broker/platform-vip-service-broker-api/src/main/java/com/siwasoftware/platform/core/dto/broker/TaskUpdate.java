package com.siwasoftware.platform.core.dto.broker;

import org.hibernate.validator.constraints.NotBlank;
import tkt.stilleto.toolkit.common.util.lang.Guard;
import tkt.stilleto.toolkit.common.util.lang.Stringify;

import javax.validation.constraints.NotNull;

/**
 *
 */
public class TaskUpdate {

    @NotBlank
    private String externalId;

    @NotBlank
    private String state;
    @NotBlank
    private String step;
    private String data;

    @NotNull
    private Integer sleep = 0;

    @NotNull
    private Integer errorCount = 0;
    private String errorMessage;


    public TaskUpdate() {
        super();
    }

    public TaskUpdate(final String externalId,
                      final String state, final String step, final String data) {

        this.externalId = externalId;
        this.state = state;
        this.step = step;
        this.data = data;

    }

    public TaskUpdate sleep(Integer sleep) {

        this.sleep = sleep;

        return this;

    }

    public TaskUpdate update(final String state, final String step, final String data) {

        this.state = Guard.notNull(state);
        this.step = Guard.notNull(step);
        this.data = data; // can be null

        return this;

    }

    public TaskUpdate error(final String errorMessage) {

        this.errorCount++;

        this.errorMessage = Guard.notNull(errorMessage);

        return this;

    }

    public String getExternalId() {
        return externalId;
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

    public int getSleep() {
        return sleep;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public String getErrorMessage() {
        return errorMessage;
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
