package org.opendaylight.openflowjava.nx;

import java.util.List;

import org.opendaylight.openflowjava.nx.codec.action.ActionDeserializer;
import org.opendaylight.openflowjava.nx.codec.action.NiciraActionCodecs;
import org.opendaylight.openflowjava.nx.codec.action.OutputRegCodec;
import org.opendaylight.openflowjava.nx.codec.action.RegLoadCodec;
import org.opendaylight.openflowjava.nx.codec.action.RegMoveCodec;
import org.opendaylight.openflowjava.nx.codec.match.ArpOpCodec;
import org.opendaylight.openflowjava.nx.codec.match.ArpShaCodec;
import org.opendaylight.openflowjava.nx.codec.match.ArpSpaCodec;
import org.opendaylight.openflowjava.nx.codec.match.ArpThaCodec;
import org.opendaylight.openflowjava.nx.codec.match.ArpTpaCodec;
import org.opendaylight.openflowjava.nx.codec.match.EthDstCodec;
import org.opendaylight.openflowjava.nx.codec.match.EthSrcCodec;
import org.opendaylight.openflowjava.nx.codec.match.EthTypeCodec;
import org.opendaylight.openflowjava.nx.codec.match.Reg0Codec;
import org.opendaylight.openflowjava.nx.codec.match.Reg1Codec;
import org.opendaylight.openflowjava.nx.codec.match.Reg2Codec;
import org.opendaylight.openflowjava.nx.codec.match.Reg3Codec;
import org.opendaylight.openflowjava.nx.codec.match.Reg4Codec;
import org.opendaylight.openflowjava.nx.codec.match.Reg5Codec;
import org.opendaylight.openflowjava.nx.codec.match.Reg6Codec;
import org.opendaylight.openflowjava.nx.codec.match.Reg7Codec;
import org.opendaylight.openflowjava.nx.codec.match.TunIdCodec;
import org.opendaylight.openflowjava.nx.codec.match.TunIpv4DstCodec;
import org.opendaylight.openflowjava.nx.codec.match.TunIpv4SrcCodec;
import org.opendaylight.openflowjava.protocol.spi.connection.SwitchConnectionProvider;

import com.google.common.base.Preconditions;

public class NiciraExtensionsRegistrator implements AutoCloseable {

    private final List<SwitchConnectionProvider> providers;

    /**
     * @param providers cannot be null
     */
    public NiciraExtensionsRegistrator(List<SwitchConnectionProvider> providers) {
        Preconditions.checkNotNull(providers);
        this.providers = providers;
    }

    public void registerNiciraExtensions() {
        for (SwitchConnectionProvider provider : providers) {
            provider.registerActionDeserializer(ActionDeserializer.DESERIALIZER_KEY, NiciraActionCodecs.ACTION_DESERIALIZER);
            provider.registerActionSerializer(RegLoadCodec.SERIALIZER_KEY, NiciraActionCodecs.REG_LOAD_CODEC);
            provider.registerActionSerializer(RegMoveCodec.SERIALIZER_KEY, NiciraActionCodecs.REG_MOVE_CODEC);
            provider.registerActionSerializer(OutputRegCodec.SERIALIZER_KEY, NiciraActionCodecs.OUTPUT_REG_CODEC);
            provider.registerMatchEntrySerializer(Reg0Codec.SERIALIZER_KEY, NiciraMatchCodecs.REG0_CODEC);
            provider.registerMatchEntryDeserializer(Reg0Codec.DESERIALIZER_KEY, NiciraMatchCodecs.REG0_CODEC);
            provider.registerMatchEntrySerializer(Reg1Codec.SERIALIZER_KEY, NiciraMatchCodecs.REG1_CODEC);
            provider.registerMatchEntryDeserializer(Reg1Codec.DESERIALIZER_KEY, NiciraMatchCodecs.REG1_CODEC);
            provider.registerMatchEntrySerializer(Reg2Codec.SERIALIZER_KEY, NiciraMatchCodecs.REG2_CODEC);
            provider.registerMatchEntryDeserializer(Reg2Codec.DESERIALIZER_KEY, NiciraMatchCodecs.REG2_CODEC);
            provider.registerMatchEntrySerializer(Reg3Codec.SERIALIZER_KEY, NiciraMatchCodecs.REG3_CODEC);
            provider.registerMatchEntryDeserializer(Reg3Codec.DESERIALIZER_KEY, NiciraMatchCodecs.REG3_CODEC);
            provider.registerMatchEntrySerializer(Reg4Codec.SERIALIZER_KEY, NiciraMatchCodecs.REG4_CODEC);
            provider.registerMatchEntryDeserializer(Reg4Codec.DESERIALIZER_KEY, NiciraMatchCodecs.REG4_CODEC);
            provider.registerMatchEntrySerializer(Reg5Codec.SERIALIZER_KEY, NiciraMatchCodecs.REG5_CODEC);
            provider.registerMatchEntryDeserializer(Reg5Codec.DESERIALIZER_KEY, NiciraMatchCodecs.REG5_CODEC);
            provider.registerMatchEntrySerializer(Reg6Codec.SERIALIZER_KEY, NiciraMatchCodecs.REG6_CODEC);
            provider.registerMatchEntryDeserializer(Reg6Codec.DESERIALIZER_KEY, NiciraMatchCodecs.REG6_CODEC);
            provider.registerMatchEntrySerializer(Reg7Codec.SERIALIZER_KEY, NiciraMatchCodecs.REG7_CODEC);
            provider.registerMatchEntryDeserializer(Reg7Codec.DESERIALIZER_KEY, NiciraMatchCodecs.REG7_CODEC);
            provider.registerMatchEntrySerializer(TunIdCodec.SERIALIZER_KEY, NiciraMatchCodecs.TUN_ID_CODEC);
            provider.registerMatchEntryDeserializer(TunIdCodec.DESERIALIZER_KEY, NiciraMatchCodecs.TUN_ID_CODEC);
            provider.registerMatchEntrySerializer(ArpOpCodec.SERIALIZER_KEY, NiciraMatchCodecs.ARP_OP_CODEC);
            provider.registerMatchEntryDeserializer(ArpOpCodec.DESERIALIZER_KEY, NiciraMatchCodecs.ARP_OP_CODEC);
            provider.registerMatchEntrySerializer(ArpShaCodec.SERIALIZER_KEY, NiciraMatchCodecs.ARP_SHA_CODEC);
            provider.registerMatchEntryDeserializer(ArpShaCodec.DESERIALIZER_KEY, NiciraMatchCodecs.ARP_SHA_CODEC);
            provider.registerMatchEntrySerializer(ArpSpaCodec.SERIALIZER_KEY, NiciraMatchCodecs.ARP_SPA_CODEC);
            provider.registerMatchEntryDeserializer(ArpSpaCodec.DESERIALIZER_KEY, NiciraMatchCodecs.ARP_SPA_CODEC);
            provider.registerMatchEntrySerializer(ArpThaCodec.SERIALIZER_KEY, NiciraMatchCodecs.ARP_THA_CODEC);
            provider.registerMatchEntryDeserializer(ArpThaCodec.DESERIALIZER_KEY, NiciraMatchCodecs.ARP_THA_CODEC);
            provider.registerMatchEntrySerializer(ArpTpaCodec.SERIALIZER_KEY, NiciraMatchCodecs.ARP_TPA_CODEC);
            provider.registerMatchEntryDeserializer(ArpTpaCodec.DESERIALIZER_KEY, NiciraMatchCodecs.ARP_TPA_CODEC);
            provider.registerMatchEntrySerializer(EthDstCodec.SERIALIZER_KEY, NiciraMatchCodecs.ETH_DST_CODEC);
            provider.registerMatchEntryDeserializer(EthDstCodec.DESERIALIZER_KEY, NiciraMatchCodecs.ETH_DST_CODEC);
            provider.registerMatchEntrySerializer(EthSrcCodec.SERIALIZER_KEY, NiciraMatchCodecs.ETH_SRC_CODEC);
            provider.registerMatchEntryDeserializer(EthSrcCodec.DESERIALIZER_KEY, NiciraMatchCodecs.ETH_SRC_CODEC);
            provider.registerMatchEntrySerializer(TunIpv4DstCodec.SERIALIZER_KEY, NiciraMatchCodecs.TUN_IPV4_DST_CODEC);
            provider.registerMatchEntryDeserializer(TunIpv4DstCodec.DESERIALIZER_KEY, NiciraMatchCodecs.TUN_IPV4_DST_CODEC);
            provider.registerMatchEntrySerializer(TunIpv4SrcCodec.SERIALIZER_KEY, NiciraMatchCodecs.TUN_IPV4_SRC_CODEC);
            provider.registerMatchEntryDeserializer(TunIpv4SrcCodec.DESERIALIZER_KEY, NiciraMatchCodecs.TUN_IPV4_SRC_CODEC);
            provider.registerMatchEntrySerializer(EthTypeCodec.SERIALIZER_KEY, NiciraMatchCodecs.ETH_TYPE_CODEC);
            provider.registerMatchEntryDeserializer(EthTypeCodec.DESERIALIZER_KEY, NiciraMatchCodecs.ETH_TYPE_CODEC);
        }
    }

    public void unregisterExtensions() {
        for (SwitchConnectionProvider provider : providers) {
            provider.unregisterDeserializer(ActionDeserializer.DESERIALIZER_KEY);
            provider.unregisterSerializer(RegLoadCodec.SERIALIZER_KEY);
            provider.unregisterSerializer(RegMoveCodec.SERIALIZER_KEY);
            provider.unregisterSerializer(OutputRegCodec.SERIALIZER_KEY);
            provider.unregisterSerializer(Reg0Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(Reg0Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(Reg1Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(Reg1Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(Reg2Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(Reg2Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(Reg3Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(Reg3Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(Reg4Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(Reg4Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(Reg5Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(Reg5Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(Reg6Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(Reg6Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(Reg7Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(Reg7Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(TunIdCodec.SERIALIZER_KEY);
            provider.unregisterDeserializer(TunIdCodec.DESERIALIZER_KEY);
            provider.unregisterSerializer(ArpOpCodec.SERIALIZER_KEY);
            provider.unregisterDeserializer(ArpOpCodec.DESERIALIZER_KEY);
            provider.unregisterSerializer(ArpShaCodec.SERIALIZER_KEY);
            provider.unregisterDeserializer(ArpShaCodec.DESERIALIZER_KEY);
            provider.unregisterSerializer(ArpSpaCodec.SERIALIZER_KEY);
            provider.unregisterDeserializer(ArpSpaCodec.DESERIALIZER_KEY);
            provider.unregisterSerializer(ArpThaCodec.SERIALIZER_KEY);
            provider.unregisterDeserializer(ArpThaCodec.DESERIALIZER_KEY);
            provider.unregisterSerializer(ArpTpaCodec.SERIALIZER_KEY);
            provider.unregisterDeserializer(ArpTpaCodec.DESERIALIZER_KEY);
            provider.unregisterSerializer(EthDstCodec.SERIALIZER_KEY);
            provider.unregisterDeserializer(EthDstCodec.DESERIALIZER_KEY);
            provider.unregisterSerializer(EthSrcCodec.SERIALIZER_KEY);
            provider.unregisterDeserializer(EthSrcCodec.DESERIALIZER_KEY);
            provider.unregisterSerializer(TunIpv4DstCodec.SERIALIZER_KEY);
            provider.unregisterDeserializer(TunIpv4DstCodec.DESERIALIZER_KEY);
            provider.unregisterSerializer(TunIpv4SrcCodec.SERIALIZER_KEY);
            provider.unregisterDeserializer(TunIpv4SrcCodec.DESERIALIZER_KEY);
            provider.unregisterSerializer(EthTypeCodec.SERIALIZER_KEY);
            provider.unregisterDeserializer(EthTypeCodec.DESERIALIZER_KEY);
        }
    }

    @Override
    public void close() throws Exception {
        unregisterExtensions();
    }

}
