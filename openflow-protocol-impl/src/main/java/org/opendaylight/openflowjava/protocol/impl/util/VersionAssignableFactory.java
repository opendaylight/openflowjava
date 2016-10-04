/**
 * Copyright (c) 2016 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import javax.annotation.Nonnull;
import org.opendaylight.openflowjava.protocol.api.extensibility.VersionAssignable;

/**
 * Simple implementation providing mechanism to assign and read OF version.
 */
public abstract class VersionAssignableFactory implements VersionAssignable {
    private Short version;

    @Override
    public void assignVersion(@Nonnull final short version) {
        if (this.version == null) {
            this.version = version;
        } else {
            throw new IllegalStateException("Version already assigned: " + this.version);
        }
    }

    protected Short getVersion() {
        return this.version;
    }
}