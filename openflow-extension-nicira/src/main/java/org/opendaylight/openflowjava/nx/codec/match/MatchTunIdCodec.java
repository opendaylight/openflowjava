package org.opendaylight.openflowjava.nx.codec.match;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.match.rev140421.AugNxMatch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.match.rev140421.AugNxMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.match.rev140421.NxMatchTunId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.match.rev140421.nx.match.tun.id.grouping.MatchTunIdBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Clazz;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

public class MatchTunIdCodec extends AbstractMatchCodec {

    private static final int VALUE_LENGTH = 8;
    private static final int NXM_FIELD_CODE = 16;
    private static final Class<NxMatchTunId> NXM_FIELD_CLASS = NxMatchTunId.class;
    public static final EnhancedMessageTypeKey<Nxm1Class, NxMatchTunId> MESSAGE_TYPE_KEY = new EnhancedMessageTypeKey<>(
            EncodeConstants.OF13_VERSION_ID, AbstractMatchCodec.NXM_1_CLASS, NXM_FIELD_CLASS);
    public static final EnhancedMessageCodeKey MESSAGE_CODE_KEY = new EnhancedMessageCodeKey(
            EncodeConstants.OF13_VERSION_ID, AbstractMatchCodec.NXM_1_CODE, NXM_FIELD_CODE, MatchEntries.class);

    @Override
    public void serialize(MatchEntries input, ByteBuf outBuffer) {
        serializeHeader(input, outBuffer);
        BigInteger value = input.getAugmentation(AugNxMatch.class).getMatchTunId().getValue();
        outBuffer.writeLong(value.longValue());
    }

    @Override
    public MatchEntries deserialize(ByteBuf message) {
        MatchEntriesBuilder matchEntriesBuilder = deserializeHeader(message);
        AugNxMatchBuilder augNxMatchBuilder = new AugNxMatchBuilder();
        augNxMatchBuilder.setMatchTunId(new MatchTunIdBuilder().setValue(BigInteger.valueOf(message.readLong()))
                .build());
        matchEntriesBuilder.addAugmentation(AugNxMatch.class, augNxMatchBuilder.build());
        return matchEntriesBuilder.build();
    }

    @Override
    public int getNxmFieldCode() {
        return NXM_FIELD_CODE;
    }

    @Override
    public int getOxmClassCode() {
        return NXM_1_CODE;
    }

    @Override
    public int getValueLength() {
        return VALUE_LENGTH;
    }

    @Override
    public Class<? extends MatchField> getNxmField() {
        return NXM_FIELD_CLASS;
    }

    @Override
    public Class<? extends Clazz> getOxmClass() {
        return NXM_1_CLASS;
    }

}
