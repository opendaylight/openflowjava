/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpOp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpSha;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpSpa;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpTha;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpTpa;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.EthDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.EthSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.EthType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ExperimenterClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Icmpv4Code;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Icmpv4Type;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Icmpv6Code;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Icmpv6Type;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPhyPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.IpDscp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.IpEcn;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.IpProto;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv4Dst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv4Src;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Dst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Exthdr;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Flabel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6NdSll;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6NdTarget;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6NdTll;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Src;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Metadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MplsBos;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MplsLabel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MplsTc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm0Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.PbbIsid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.SctpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.SctpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TcpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TcpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TunnelId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.UdpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.UdpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.VlanPcp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.VlanVid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.MatchEntriesBuilder;

/**
 * Encodes match ids (oxm_ids) needed in Multipart-TableFeatures messages
 * @author michal.polkorab
 */
public abstract class MatchIdsDeserializer {

    /** Decodes oxm ids
     * @param in input ByteBuf
     * @param matchLength match entries length
     * @return list of match ids
     */
    public static List<MatchEntries> createOxmIds(ByteBuf in, int matchLength) {
        List<MatchEntries> matchEntriesList = new ArrayList<>();
        int currLength = 0;
        while(currLength < matchLength) {
            MatchEntriesBuilder matchEntriesBuilder = new MatchEntriesBuilder();
            switch (in.readUnsignedShort()) {
            case 0x0000:
                        matchEntriesBuilder.setOxmClass(Nxm0Class.class);
                        break;
            case 0x0001:
                        matchEntriesBuilder.setOxmClass(Nxm1Class.class);
                        break;
            case 0x8000:
                        matchEntriesBuilder.setOxmClass(OpenflowBasicClass.class);
                        break;
            case 0xFFFF:
                        matchEntriesBuilder.setOxmClass(ExperimenterClass.class);
                        break;
            default:
                        break;
            }

            int fieldAndMask = in.readUnsignedByte();
            boolean hasMask = (fieldAndMask & 1) != 0;
            matchEntriesBuilder.setHasMask(hasMask);
            int matchField =  fieldAndMask >> 1;
            in.skipBytes(EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
            currLength += EncodeConstants.SIZE_OF_SHORT_IN_BYTES +
                    (2 * EncodeConstants.SIZE_OF_BYTE_IN_BYTES);

            switch(matchField) {
            case 0:
                matchEntriesBuilder.setOxmMatchField(InPort.class);
                break;
            case 1:
                matchEntriesBuilder.setOxmMatchField(InPhyPort.class);
                break;
            case 2:
                matchEntriesBuilder.setOxmMatchField(Metadata.class);
                break;
            case 3:
                matchEntriesBuilder.setOxmMatchField(EthDst.class);
                break;
            case 4:
                matchEntriesBuilder.setOxmMatchField(EthSrc.class);
                break;
            case 5:
                matchEntriesBuilder.setOxmMatchField(EthType.class);
                break;
            case 6:
                matchEntriesBuilder.setOxmMatchField(VlanVid.class);
                break;
            case 7:
                matchEntriesBuilder.setOxmMatchField(VlanPcp.class);
                break;
            case 8:
                matchEntriesBuilder.setOxmMatchField(IpDscp.class);
                break;
            case 9:
                matchEntriesBuilder.setOxmMatchField(IpEcn.class);
                break;
            case 10:
                matchEntriesBuilder.setOxmMatchField(IpProto.class);
                break;
            case 11:
                matchEntriesBuilder.setOxmMatchField(Ipv4Src.class);
                break;
            case 12:
                matchEntriesBuilder.setOxmMatchField(Ipv4Dst.class);
                break;
            case 13:
                matchEntriesBuilder.setOxmMatchField(TcpSrc.class);
                break;
            case 14:
                matchEntriesBuilder.setOxmMatchField(TcpDst.class);
                break;
            case 15:
                matchEntriesBuilder.setOxmMatchField(UdpSrc.class);
                break;
            case 16:
                matchEntriesBuilder.setOxmMatchField(UdpDst.class);
                break;
            case 17:
                matchEntriesBuilder.setOxmMatchField(SctpSrc.class);
                break;
            case 18:
                matchEntriesBuilder.setOxmMatchField(SctpDst.class);
                break;
            case 19:
                matchEntriesBuilder.setOxmMatchField(Icmpv4Type.class);
                break;
            case 20:
                matchEntriesBuilder.setOxmMatchField(Icmpv4Code.class);
                break;
            case 21:
                matchEntriesBuilder.setOxmMatchField(ArpOp.class);
                break;
            case 22:
                matchEntriesBuilder.setOxmMatchField(ArpSpa.class);
                break;
            case 23:
                matchEntriesBuilder.setOxmMatchField(ArpTpa.class);
                break;
            case 24:
                matchEntriesBuilder.setOxmMatchField(ArpSha.class);
                break;
            case 25:
                matchEntriesBuilder.setOxmMatchField(ArpTha.class);
                break;
            case 26:
                matchEntriesBuilder.setOxmMatchField(Ipv6Src.class);
                break;
            case 27:
                matchEntriesBuilder.setOxmMatchField(Ipv6Dst.class);
                break;
            case 28:
                matchEntriesBuilder.setOxmMatchField(Ipv6Flabel.class);
                break;
            case 29:
                matchEntriesBuilder.setOxmMatchField(Icmpv6Type.class);
                break;
            case 30:
                matchEntriesBuilder.setOxmMatchField(Icmpv6Code.class);
                break;
            case 31:
                matchEntriesBuilder.setOxmMatchField(Ipv6NdTarget.class);
                break;
            case 32:
                matchEntriesBuilder.setOxmMatchField(Ipv6NdSll.class);
                break;
            case 33:
                matchEntriesBuilder.setOxmMatchField(Ipv6NdTll.class);
                break;
            case 34:
                matchEntriesBuilder.setOxmMatchField(MplsLabel.class);
                break;
            case 35:
                matchEntriesBuilder.setOxmMatchField(MplsTc.class);
                break;
            case 36:
                matchEntriesBuilder.setOxmMatchField(MplsBos.class);
                break;
            case 37:
                matchEntriesBuilder.setOxmMatchField(PbbIsid.class);
                break;
            case 38:
                matchEntriesBuilder.setOxmMatchField(TunnelId.class);
                break;
            case 39:
                matchEntriesBuilder.setOxmMatchField(Ipv6Exthdr.class);
                break;
            default:
                break;
            }
          matchEntriesList.add(matchEntriesBuilder.build());
        }
        return matchEntriesList;
    }
}
