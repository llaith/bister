package com.siwasoftware.platform.core.dto.broker;

import tkt.stilleto.toolkit.common.util.lang.Stringify;

/**
 *
 */
public class TaskQueueInfo {

    private String queue;
    private int count;

    public TaskQueueInfo() {
        super();
    }

    public TaskQueueInfo(final String queue, final int count) {
        this.queue = queue;
        this.count = count;
    }

    public String getQueue() {
        return queue;
    }

    public int getCount() {
        return count;
    }

    public void setQueue(final String queue) {
        this.queue = queue;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    @Override
    final public int hashCode() {
        return super.hashCode();
    }

    @Override
    final public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    final public String toString() {
        return Stringify.toString(this);
    }

}
