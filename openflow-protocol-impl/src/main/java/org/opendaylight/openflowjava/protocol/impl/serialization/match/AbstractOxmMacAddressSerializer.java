/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;

/**
 * Parent for MAC address based match entry serializers
 * @author michal.polkorab
 */
public abstract class AbstractOxmMacAddressSerializer extends AbstractOxmMatchEntrySerializer {

    protected void writeMacAddress(MacAddress address, ByteBuf outBuffer) {
        outBuffer.writeBytes(ByteBufUtils.macAddressToBytes(address.getValue())); // 48 b + mask [OF 1.3.2 spec]
    }
}
