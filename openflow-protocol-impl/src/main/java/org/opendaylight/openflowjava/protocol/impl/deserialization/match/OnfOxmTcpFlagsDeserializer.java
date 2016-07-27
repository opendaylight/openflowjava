/*
 * Copyright (c) 2016 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.deserialization.match;

import io.netty.buffer.ByteBuf;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.ExperimenterClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.OxmClassBase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.TcpFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entries.grouping.MatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entries.grouping.MatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.TcpFlagsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.tcp.flags._case.TcpFlagsBuilder;

/**
 * Created by Anil Vishnoi (avishnoi@Brocade.com) on 7/26/16.
 */
public class OnfOxmTcpFlagsDeserializer extends AbstractOxmExperimenterMatchEntryDeserializer
        implements OFDeserializer<MatchEntry> {

    @Override
    public MatchEntry deserialize(ByteBuf input) {
        MatchEntryBuilder matchEntryBuilder = new MatchEntryBuilder(super.deserializeHeader(input));
        addTcpFlagsValue(input, matchEntryBuilder);
        return matchEntryBuilder.build();

    }

    private static void addTcpFlagsValue(ByteBuf input, MatchEntryBuilder matchEntryBuilder) {
        TcpFlagsCaseBuilder tcpFlagsCaseBuilder = new TcpFlagsCaseBuilder();
        TcpFlagsBuilder tcpFlagsBuilder = new TcpFlagsBuilder();
        tcpFlagsBuilder.setFlags(input.readUnsignedShort());
        if (matchEntryBuilder.isHasMask()) {
            byte[] maskBuf = new byte[2];
            input.readBytes(maskBuf);
            tcpFlagsBuilder.setMask(maskBuf);
        }
        tcpFlagsCaseBuilder.setTcpFlags(tcpFlagsBuilder.build());
        matchEntryBuilder.setMatchEntryValue(tcpFlagsCaseBuilder.build());
    }
    /**
     * @return oxm_field class
     */
    @Override
    protected Class<? extends MatchField> getOxmField() {
        return TcpFlags.class;
    }

    /**
     * @return oxm_class class
     */
    @Override
    protected Class<? extends OxmClassBase> getOxmClass() {
        return ExperimenterClass.class;
    }
}
