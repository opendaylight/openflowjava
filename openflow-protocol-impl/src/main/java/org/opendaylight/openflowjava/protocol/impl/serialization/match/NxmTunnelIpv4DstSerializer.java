/*
 * Copyright (C) 2014 Red Hat, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.OxmMatchConstants;

/**
 * @author brent.salisbury
 *
 */
public class NxmTunnelIpv4DstSerializer extends AbstractOxmIpv4AddressSerializer {

    @Override
    protected int getOxmClassCode() {
        return OxmMatchConstants.NXM_1_CLASS;
    }

    @Override
    protected int getOxmFieldCode() {
        return OxmMatchConstants.NXM_NX_TUN_IPV4_DST;
    }

    @Override
    protected int getValueLength() {
        return EncodeConstants.SIZE_OF_INT_IN_BYTES;
    }

}