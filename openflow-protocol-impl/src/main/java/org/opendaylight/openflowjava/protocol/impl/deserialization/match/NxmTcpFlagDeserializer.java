/*
 * Copyright (C) 2014 Red Hat, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.match;

import io.netty.buffer.ByteBuf;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TcpFlagMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Clazz;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TcpFlag;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author brent.salisbury
 *
 */
public class NxmTcpFlagDeserializer extends AbstractOxmMatchEntryDeserializer
        implements OFDeserializer<MatchEntries> {
    private static final Logger logger = LoggerFactory.getLogger(NxmTcpFlagDeserializer.class);

    @Override
    public MatchEntries deserialize(ByteBuf input) {
        MatchEntriesBuilder builder = processHeader(getOxmClass(), getOxmField(), input);
        addTcpFlagAugmentation(input, builder);
        return builder.build();
    }

    private static void addTcpFlagAugmentation(ByteBuf input, MatchEntriesBuilder builder) {
        TcpFlagMatchEntryBuilder tcpFlagMatchEntryBuilder = new TcpFlagMatchEntryBuilder();
        tcpFlagMatchEntryBuilder.setTcpFlag(input.readUnsignedShort());
    }

    @Override
    protected Class<? extends MatchField> getOxmField() {
        return TcpFlag.class;
    }

    @Override
    protected Class<? extends Clazz> getOxmClass() {
        return Nxm1Class.class;
    }
}