/**
 * Copyright (c) 2016 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.extensibility;

import javax.annotation.Nonnull;

/**
 * Prescribes OF version assigning.
 */
public interface VersionAssignable {

    /**
     * Assign particular OF version.
     * @param version version to be assigned
     */
    void assignVersion(@Nonnull final short version);
}