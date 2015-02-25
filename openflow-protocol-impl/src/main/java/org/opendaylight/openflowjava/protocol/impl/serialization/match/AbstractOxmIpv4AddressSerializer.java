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

/**
 * Parent for Ipv4 address based match entry serializers
 * @author michal.polkorab
 */
public abstract class AbstractOxmIpv4AddressSerializer extends AbstractOxmMatchEntrySerializer {

    protected static void writeIpv4Address(String address, final ByteBuf out) {
        Iterable<String> addressGroups = ByteBufUtils.DOT_SPLITTER.split(address);
        for (String group : addressGroups) {
            out.writeByte(Short.parseShort(group));
        }
    }

}
