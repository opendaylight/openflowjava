package org.opendaylight.openflowjava.nx;

import java.util.List;

import org.opendaylight.openflowjava.nx.codec.action.ActionCodec;
import org.opendaylight.openflowjava.nx.codec.match.MatchReg0Codec;
import org.opendaylight.openflowjava.nx.codec.match.MatchReg1Codec;
import org.opendaylight.openflowjava.nx.codec.match.MatchReg2Codec;
import org.opendaylight.openflowjava.nx.codec.match.MatchReg3Codec;
import org.opendaylight.openflowjava.nx.codec.match.MatchReg4Codec;
import org.opendaylight.openflowjava.nx.codec.match.MatchReg5Codec;
import org.opendaylight.openflowjava.nx.codec.match.MatchReg6Codec;
import org.opendaylight.openflowjava.nx.codec.match.MatchReg7Codec;
import org.opendaylight.openflowjava.nx.codec.match.MatchTunIdCodec;
import org.opendaylight.openflowjava.protocol.spi.connection.SwitchConnectionProvider;

import com.google.common.base.Preconditions;

public class NiciraExtensionsRegistrator implements AutoCloseable {

    private static final ActionCodec ACTION_CODEC = new ActionCodec();
    private static final MatchReg0Codec MATCH_REG0_CODEC = new MatchReg0Codec();
    private static final MatchReg1Codec MATCH_REG1_CODEC = new MatchReg1Codec();
    private static final MatchReg2Codec MATCH_REG2_CODEC = new MatchReg2Codec();
    private static final MatchReg3Codec MATCH_REG3_CODEC = new MatchReg3Codec();
    private static final MatchReg4Codec MATCH_REG4_CODEC = new MatchReg4Codec();
    private static final MatchReg5Codec MATCH_REG5_CODEC = new MatchReg5Codec();
    private static final MatchReg6Codec MATCH_REG6_CODEC = new MatchReg6Codec();
    private static final MatchReg7Codec MATCH_REG7_CODEC = new MatchReg7Codec();
    private static final MatchTunIdCodec MATCH_TUN_ID_CODEC = new MatchTunIdCodec();
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
            provider.registerActionDeserializer(ActionCodec.DESERIALIZER_KEY, ACTION_CODEC);
            provider.registerActionSerializer(ActionCodec.SERIALIZER_KEY, ACTION_CODEC);
            provider.registerMatchEntrySerializer(MatchReg0Codec.SERIALIZER_KEY, MATCH_REG0_CODEC);
            provider.registerMatchEntryDeserializer(MatchReg0Codec.DESERIALIZER_KEY, MATCH_REG0_CODEC);
            provider.registerMatchEntrySerializer(MatchReg1Codec.SERIALIZER_KEY, MATCH_REG1_CODEC);
            provider.registerMatchEntryDeserializer(MatchReg1Codec.DESERIALIZER_KEY, MATCH_REG1_CODEC);
            provider.registerMatchEntrySerializer(MatchReg2Codec.SERIALIZER_KEY, MATCH_REG2_CODEC);
            provider.registerMatchEntryDeserializer(MatchReg2Codec.DESERIALIZER_KEY, MATCH_REG2_CODEC);
            provider.registerMatchEntrySerializer(MatchReg3Codec.SERIALIZER_KEY, MATCH_REG3_CODEC);
            provider.registerMatchEntryDeserializer(MatchReg3Codec.DESERIALIZER_KEY, MATCH_REG3_CODEC);
            provider.registerMatchEntrySerializer(MatchReg4Codec.SERIALIZER_KEY, MATCH_REG4_CODEC);
            provider.registerMatchEntryDeserializer(MatchReg4Codec.DESERIALIZER_KEY, MATCH_REG4_CODEC);
            provider.registerMatchEntrySerializer(MatchReg5Codec.SERIALIZER_KEY, MATCH_REG5_CODEC);
            provider.registerMatchEntryDeserializer(MatchReg5Codec.DESERIALIZER_KEY, MATCH_REG5_CODEC);
            provider.registerMatchEntrySerializer(MatchReg6Codec.SERIALIZER_KEY, MATCH_REG6_CODEC);
            provider.registerMatchEntryDeserializer(MatchReg6Codec.DESERIALIZER_KEY, MATCH_REG6_CODEC);
            provider.registerMatchEntrySerializer(MatchReg7Codec.SERIALIZER_KEY, MATCH_REG7_CODEC);
            provider.registerMatchEntryDeserializer(MatchReg7Codec.DESERIALIZER_KEY, MATCH_REG7_CODEC);
            provider.registerMatchEntrySerializer(MatchTunIdCodec.SERIALIZER_KEY, MATCH_TUN_ID_CODEC);
            provider.registerMatchEntryDeserializer(MatchTunIdCodec.DESERIALIZER_KEY, MATCH_TUN_ID_CODEC);
        }
    }

    public void unregisterExtensions() {
        for (SwitchConnectionProvider provider : providers) {
            provider.unregisterSerializer(ActionCodec.SERIALIZER_KEY);
            provider.unregisterDeserializer(ActionCodec.DESERIALIZER_KEY);
            provider.unregisterSerializer(MatchReg0Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(MatchReg0Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(MatchReg1Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(MatchReg1Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(MatchReg2Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(MatchReg2Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(MatchReg3Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(MatchReg3Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(MatchReg4Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(MatchReg4Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(MatchReg5Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(MatchReg5Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(MatchReg6Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(MatchReg6Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(MatchReg7Codec.SERIALIZER_KEY);
            provider.unregisterDeserializer(MatchReg7Codec.DESERIALIZER_KEY);
            provider.unregisterSerializer(MatchTunIdCodec.SERIALIZER_KEY);
            provider.unregisterDeserializer(MatchTunIdCodec.DESERIALIZER_KEY);
        }
    }

    @Override
    public void close() throws Exception {
        unregisterExtensions();
    }

}
