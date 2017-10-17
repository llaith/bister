package com.siwasoftware.platform.core.util;

import com.siwasoftware.platform.core.dto.broker.Watch;
import tkt.stilleto.toolkit.common.depends.Dependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Structure which allows the watches to be treated as a tree. Note this should be moved into the 'client' lib.
 */
public class WatchGroup {

    private final List<Dependency<Watch>> dependencies = new ArrayList<>();

    public WatchGroup(final Collection<Watch> watches) {

        this.dependencies.addAll(this.calcDependencies(watches));

    }

    private List<Dependency<Watch>> calcDependencies(final Collection<Watch> watches) {

        for (final Watch entity : watches) {

            //

        }

        throw new UnsupportedOperationException("TODO");

    }

    final List<Watch> roots() {
        throw new UnsupportedOperationException("TODO");
    }

    final List<Watch> findChildren(final Watch watch) {
        throw new UnsupportedOperationException("TODO");
    }

}
