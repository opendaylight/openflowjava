package org.opendaylight.openflowjava.protocol.impl.util;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFVersionAssignable;

/**
 * Simple implementation providing mechanism to assign and read version and additionally notifies itself upon version assignment
 */
public abstract class VersatileFactory implements OFVersionAssignable {
    private Short version;

    @Override
    public void assignVersion(short version) {
        if (this.version == null) {
            this.version = version;
            onVersionAssigned();
        } else {
            throw new IllegalStateException("Version already assigned: " + this.version);
        }
    }

    protected void onVersionAssigned() {
        //NOOP
    }

    protected Short getVersion() {
        return version;
    }
}
