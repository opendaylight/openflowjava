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

import com.google.common.net.InetAddresses;

/**
 * Parent for Ipv6 address based match entry serializers
 * @author michal.polkorab
 */
public abstract class AbstractOxmIpv6AddressSerializer extends AbstractOxmMatchEntrySerializer {

    protected void writeIpv6Address(String textAddress, final ByteBuf outBuffer) {
        if (InetAddresses.isInetAddress(textAddress)) {
            byte[] binaryAddress = InetAddresses.forString(textAddress).getAddress();
            outBuffer.writeBytes(binaryAddress);
        } else {
            throw new IllegalArgumentException("Invalid ipv6 address received: " + textAddress);
        }
    }

}
