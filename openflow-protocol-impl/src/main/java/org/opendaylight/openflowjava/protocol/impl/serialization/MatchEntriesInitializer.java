/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization;

import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.impl.deserialization.EnhancedMessageTypeKey;
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
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmUdpDstSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmUdpSrcSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmVlanPcpSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.OxmVlanVidSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
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

/**
 * Initializes serializer registry with match entry serializers
 * @author michal.polkorab
 */
public class MatchEntriesInitializer {

    /**
     * Registers match entry serializers into provided registry
     * @param serializerRegistry registry to be initialized with match entry serializers
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void registerMatchEntrySerializers(SerializerRegistry serializerRegistry) {
        // register OF v1.3 OpenflowBasicClass match entry serializers
        short version = EncodeConstants.OF13_VERSION_ID;
        Class<?> oxmClass = OpenflowBasicClass.class;
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, InPort.class),
                new OxmInPortSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, InPhyPort.class),
                new OxmInPhyPortSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Metadata.class),
                new OxmMetadataSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, EthDst.class),
                new OxmEthDstSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, EthSrc.class),
                new OxmEthSrcSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, EthType.class),
                new OxmEthTypeSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, VlanVid.class),
                new OxmVlanVidSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, VlanPcp.class),
                new OxmVlanPcpSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, IpDscp.class),
                new OxmIpDscpSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, IpEcn.class),
                new OxmIpEcnSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, IpProto.class),
                new OxmIpProtoSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Ipv4Src.class),
                new OxmIpv4SrcSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Ipv4Dst.class),
                new OxmIpv4DstSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, TcpSrc.class),
                new OxmTcpSrcSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, TcpDst.class),
                new OxmTcpDstSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, UdpSrc.class),
                new OxmUdpSrcSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, UdpDst.class),
                new OxmUdpDstSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, SctpSrc.class),
                new OxmSctpSrcSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, SctpDst.class),
                new OxmSctpDstSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Icmpv4Type.class),
                new OxmIcmpv4TypeSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Icmpv4Code.class),
                new OxmIcmpv4CodeSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, ArpOp.class),
                new OxmArpOpSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, ArpSpa.class),
                new OxmArpSpaSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, ArpTpa.class),
                new OxmArpTpaSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, ArpSha.class),
                new OxmArpShaSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, ArpTha.class),
                new OxmArpThaSerializer());
        
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Ipv6Src.class),
                new OxmIpv6SrcSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Ipv6Dst.class),
                new OxmIpv6DstSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Ipv6Flabel.class),
                new OxmIpv6FlabelSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Icmpv6Type.class),
                new OxmIcmpv6TypeSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Icmpv6Code.class),
                new OxmIcmpv6CodeSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Ipv6NdTarget.class),
                new OxmIpv6NdTargetSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Ipv6NdSll.class),
                new OxmIpv6NdSllSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Ipv6NdTll.class),
                new OxmIpv6NdTllSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, MplsLabel.class),
                new OxmMplsLabelSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, MplsTc.class),
                new OxmMplsTcSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, MplsBos.class),
                new OxmMplsBosSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, PbbIsid.class),
                new OxmPbbIsidSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, TunnelId.class),
                new OxmTunnelIdSerializer());
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey(version, oxmClass, Ipv6Exthdr.class),
                new OxmIpv6ExtHdrSerializer());
    }
}
