/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization;

import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.NxmTcpFlagSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmArpOpSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmArpShaSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmArpSpaSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmArpThaSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmArpTpaSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmEthDstSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmEthSrcSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmEthTypeSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIcmpv4CodeSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIcmpv4TypeSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIcmpv6CodeSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIcmpv6TypeSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmInPhyPortSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmInPortSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIpDscpSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIpEcnSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIpProtoSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIpv4DstSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIpv4SrcSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIpv6DstSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIpv6ExtHdrSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIpv6FlabelSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIpv6NdSllSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIpv6NdTargetSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIpv6NdTllSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmIpv6SrcSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmMetadataSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmMplsBosSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmMplsLabelSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmMplsTcSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmPbbIsidSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmSctpDstSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmSctpSrcSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmTcpDstSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmTcpSrcSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmTunnelIdSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmTunnelIpv4DstSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmTunnelIpv4SrcSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmUdpDstSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmUdpSrcSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmVlanPcpSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmVlanVidSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.OF13MatchEntriesRegistryHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpOp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpSha;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpSpa;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpTha;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpTpa;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.EthDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.EthSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.EthType;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.PbbIsid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.SctpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.SctpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TcpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TcpFlag;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TcpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TunnelId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TunnelIpv4Dst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TunnelIpv4Src;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.UdpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.UdpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.VlanPcp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.VlanVid;

/**
 * Initializes serializer registry with match entry serializers
 * @author michal.polkorab
 */
public class MatchEntriesInitializer {

    /**
     * Registers match entry serializers into provided registry
     * @param serializerRegistry registry to be initialized with match entry serializers
     */
    public static void registerMatchEntrySerializers(SerializerRegistry serializerRegistry) {
        // register OF v1.3 OpenflowBasicClass match entry serializers
        Class<OpenflowBasicClass> oxmClass = OpenflowBasicClass.class;
        OF13MatchEntriesRegistryHelper<OpenflowBasicClass> registryHelper =
                new OF13MatchEntriesRegistryHelper<>(EncodeConstants.OF13_VERSION_ID, oxmClass, serializerRegistry);
        registryHelper.registerSerializer(InPort.class, new OxmInPortSerializer());
        registryHelper.registerSerializer(InPhyPort.class, new OxmInPhyPortSerializer());
        registryHelper.registerSerializer(Metadata.class, new OxmMetadataSerializer());
        registryHelper.registerSerializer(EthDst.class, new OxmEthDstSerializer());
        registryHelper.registerSerializer(EthSrc.class, new OxmEthSrcSerializer());
        registryHelper.registerSerializer(EthType.class, new OxmEthTypeSerializer());
        registryHelper.registerSerializer(VlanVid.class, new OxmVlanVidSerializer());
        registryHelper.registerSerializer(VlanPcp.class, new OxmVlanPcpSerializer());
        registryHelper.registerSerializer(IpDscp.class, new OxmIpDscpSerializer());
        registryHelper.registerSerializer(IpEcn.class, new OxmIpEcnSerializer());
        registryHelper.registerSerializer(IpProto.class, new OxmIpProtoSerializer());
        registryHelper.registerSerializer(Ipv4Src.class, new OxmIpv4SrcSerializer());
        registryHelper.registerSerializer(Ipv4Dst.class, new OxmIpv4DstSerializer());
        registryHelper.registerSerializer(TcpSrc.class, new OxmTcpSrcSerializer());
        registryHelper.registerSerializer(TcpDst.class, new OxmTcpDstSerializer());
        registryHelper.registerSerializer(UdpSrc.class, new OxmUdpSrcSerializer());
        registryHelper.registerSerializer(UdpDst.class, new OxmUdpDstSerializer());
        registryHelper.registerSerializer(SctpSrc.class, new OxmSctpSrcSerializer());
        registryHelper.registerSerializer(SctpDst.class, new OxmSctpDstSerializer());
        registryHelper.registerSerializer(Icmpv4Type.class, new OxmIcmpv4TypeSerializer());
        registryHelper.registerSerializer(Icmpv4Code.class, new OxmIcmpv4CodeSerializer());
        registryHelper.registerSerializer(ArpOp.class, new OxmArpOpSerializer());
        registryHelper.registerSerializer(ArpSpa.class, new OxmArpSpaSerializer());
        registryHelper.registerSerializer(ArpTpa.class, new OxmArpTpaSerializer());
        registryHelper.registerSerializer(ArpSha.class, new OxmArpShaSerializer());
        registryHelper.registerSerializer(ArpTha.class, new OxmArpThaSerializer());
        registryHelper.registerSerializer(Ipv6Src.class, new OxmIpv6SrcSerializer());
        registryHelper.registerSerializer(Ipv6Dst.class, new OxmIpv6DstSerializer());
        registryHelper.registerSerializer(Ipv6Flabel.class, new OxmIpv6FlabelSerializer());
        registryHelper.registerSerializer(Icmpv6Type.class, new OxmIcmpv6TypeSerializer());
        registryHelper.registerSerializer(Icmpv6Code.class, new OxmIcmpv6CodeSerializer());
        registryHelper.registerSerializer(Ipv6NdTarget.class, new OxmIpv6NdTargetSerializer());
        registryHelper.registerSerializer(Ipv6NdSll.class, new OxmIpv6NdSllSerializer());
        registryHelper.registerSerializer(Ipv6NdTll.class, new OxmIpv6NdTllSerializer());
        registryHelper.registerSerializer(MplsLabel.class, new OxmMplsLabelSerializer());
        registryHelper.registerSerializer(MplsTc.class, new OxmMplsTcSerializer());
        registryHelper.registerSerializer(MplsBos.class, new OxmMplsBosSerializer());
        registryHelper.registerSerializer(PbbIsid.class, new OxmPbbIsidSerializer());
        registryHelper.registerSerializer(TunnelId.class, new OxmTunnelIdSerializer());
        registryHelper.registerSerializer(Ipv6Exthdr.class, new OxmIpv6ExtHdrSerializer());

        // register OF v1.3 NXM1Class match entry serializer
        Class<Nxm1Class> nxmClass = Nxm1Class.class;
        OF13MatchEntriesRegistryHelper<Nxm1Class> nxmRegistryHelper =
            new OF13MatchEntriesRegistryHelper<>(EncodeConstants.OF13_VERSION_ID, nxmClass, serializerRegistry);
        nxmRegistryHelper.registerSerializer(TunnelIpv4Src.class, new OxmTunnelIpv4SrcSerializer());
        nxmRegistryHelper.registerSerializer(TunnelIpv4Dst.class, new OxmTunnelIpv4DstSerializer());
        nxmRegistryHelper.registerSerializer(TcpFlag.class, new NxmTcpFlagSerializer());

    }
}
