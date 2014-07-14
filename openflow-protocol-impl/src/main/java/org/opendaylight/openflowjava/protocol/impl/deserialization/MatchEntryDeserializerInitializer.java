/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.deserialization;

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.NxmTcpFlagDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.NxmTunnelIpv4DstDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.NxmTunnelIpv4SrcDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmArpOpDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmArpShaDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmArpSpaDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmArpThaDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmArpTpaDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmEthDstDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmEthSrcDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmEthTypeDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIcmpv4CodeDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIcmpv4TypeDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIcmpv6CodeDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIcmpv6TypeDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmInPhyPortDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmInPortDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIpDscpDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIpEcnDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIpProtoDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIpv4DstDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIpv4SrcDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIpv6DstDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIpv6ExtHdrDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIpv6FlabelDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIpv6NdSllDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIpv6NdTargetDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIpv6NdTllDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmIpv6SrcDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmMetadataDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmMplsBosDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmMplsLabelDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmMplsTcDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmPbbIsidDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmSctpDstDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmSctpSrcDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmTcpDstDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmTcpSrcDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmTunnelIdDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmUdpDstDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmUdpSrcDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmVlanPcpDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.match.OxmVlanVidDeserializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.MatchEntryDeserializerRegistryHelper;
import org.opendaylight.openflowjava.protocol.api.util.OxmMatchConstants;

/**
 * @author michal.polkorab
 *
 */
public class MatchEntryDeserializerInitializer {

    /**
     * Registers match entry deserializers
     * @param registry registry to be filled with deserializers
     */
    public static void registerMatchEntryDeserializers(DeserializerRegistry registry) {
        // register OpenflowBasicClass match entry deserializers
        MatchEntryDeserializerRegistryHelper helper =
                new MatchEntryDeserializerRegistryHelper(EncodeConstants.OF13_VERSION_ID,
                        OxmMatchConstants.OPENFLOW_BASIC_CLASS, registry);
        helper.register(OxmMatchConstants.IN_PORT, null, new OxmInPortDeserializer());
        helper.register(OxmMatchConstants.IN_PHY_PORT, null, new OxmInPhyPortDeserializer());
        helper.register(OxmMatchConstants.METADATA, null, new OxmMetadataDeserializer());
        helper.register(OxmMatchConstants.ETH_DST, null, new OxmEthDstDeserializer());
        helper.register(OxmMatchConstants.ETH_SRC, null, new OxmEthSrcDeserializer());
        helper.register(OxmMatchConstants.ETH_TYPE, null, new OxmEthTypeDeserializer());
        helper.register(OxmMatchConstants.VLAN_VID, null, new OxmVlanVidDeserializer());
        helper.register(OxmMatchConstants.VLAN_PCP, null, new OxmVlanPcpDeserializer());
        helper.register(OxmMatchConstants.IP_DSCP, null, new OxmIpDscpDeserializer());
        helper.register(OxmMatchConstants.IP_ECN, null, new OxmIpEcnDeserializer());
        helper.register(OxmMatchConstants.IP_PROTO, null, new OxmIpProtoDeserializer());
        helper.register(OxmMatchConstants.IPV4_SRC, null, new OxmIpv4SrcDeserializer());
        helper.register(OxmMatchConstants.IPV4_DST, null, new OxmIpv4DstDeserializer());
        helper.register(OxmMatchConstants.TCP_SRC, null, new OxmTcpSrcDeserializer());
        helper.register(OxmMatchConstants.TCP_DST, null, new OxmTcpDstDeserializer());
        helper.register(OxmMatchConstants.UDP_SRC, null, new OxmUdpSrcDeserializer());
        helper.register(OxmMatchConstants.UDP_DST, null, new OxmUdpDstDeserializer());
        helper.register(OxmMatchConstants.SCTP_SRC, null, new OxmSctpSrcDeserializer());
        helper.register(OxmMatchConstants.SCTP_DST, null, new OxmSctpDstDeserializer());
        helper.register(OxmMatchConstants.ICMPV4_TYPE, null, new OxmIcmpv4TypeDeserializer());
        helper.register(OxmMatchConstants.ICMPV4_CODE, null, new OxmIcmpv4CodeDeserializer());
        helper.register(OxmMatchConstants.ARP_OP, null, new OxmArpOpDeserializer());
        helper.register(OxmMatchConstants.ARP_SPA, null, new OxmArpSpaDeserializer());
        helper.register(OxmMatchConstants.ARP_TPA, null, new OxmArpTpaDeserializer());
        helper.register(OxmMatchConstants.ARP_SHA, null, new OxmArpShaDeserializer());
        helper.register(OxmMatchConstants.ARP_THA, null, new OxmArpThaDeserializer());
        helper.register(OxmMatchConstants.IPV6_SRC, null, new OxmIpv6SrcDeserializer());
        helper.register(OxmMatchConstants.IPV6_DST, null, new OxmIpv6DstDeserializer());
        helper.register(OxmMatchConstants.IPV6_FLABEL, null, new OxmIpv6FlabelDeserializer());
        helper.register(OxmMatchConstants.ICMPV6_TYPE, null, new OxmIcmpv6TypeDeserializer());
        helper.register(OxmMatchConstants.ICMPV6_CODE, null, new OxmIcmpv6CodeDeserializer());
        helper.register(OxmMatchConstants.IPV6_ND_TARGET, null, new OxmIpv6NdTargetDeserializer());
        helper.register(OxmMatchConstants.IPV6_ND_SLL, null, new OxmIpv6NdSllDeserializer());
        helper.register(OxmMatchConstants.IPV6_ND_TLL, null, new OxmIpv6NdTllDeserializer());
        helper.register(OxmMatchConstants.MPLS_LABEL, null, new OxmMplsLabelDeserializer());
        helper.register(OxmMatchConstants.MPLS_TC, null, new OxmMplsTcDeserializer());
        helper.register(OxmMatchConstants.MPLS_BOS, null, new OxmMplsBosDeserializer());
        helper.register(OxmMatchConstants.PBB_ISID, null, new OxmPbbIsidDeserializer());
        helper.register(OxmMatchConstants.TUNNEL_ID, null, new OxmTunnelIdDeserializer());
        helper.register(OxmMatchConstants.IPV6_EXTHDR, null, new OxmIpv6ExtHdrDeserializer());

        // Register NXM1Class match entry deserializers
        MatchEntryDeserializerRegistryHelper nxm1helper =
                new MatchEntryDeserializerRegistryHelper(EncodeConstants.OF13_VERSION_ID,
                        OxmMatchConstants.NXM_1_CLASS, registry);
        nxm1helper.register(OxmMatchConstants.NXM_NX_TUN_IPV4_SRC, null, new NxmTunnelIpv4SrcDeserializer());
        nxm1helper.register(OxmMatchConstants.NXM_NX_TUN_IPV4_DST, null, new NxmTunnelIpv4DstDeserializer());
        nxm1helper.register(OxmMatchConstants.NXM_NX_TCP_FLAG, null, new NxmTcpFlagDeserializer());

    }
}
