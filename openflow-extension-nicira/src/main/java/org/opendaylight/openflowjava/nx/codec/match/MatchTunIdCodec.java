package org.opendaylight.openflowjava.nx.codec.match;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.opendaylight.openflowjava.protocol.api.keys.MatchEntryDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntrySerializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.api.util.OxmMatchConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.match.rev140421.OfjAugNxMatch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.match.rev140421.OfjAugNxMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.match.rev140421.OfjNxMatchTunId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.match.rev140421.ofj.nx.match.tun.id.grouping.MatchTunIdBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Clazz;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

public class MatchTunIdCodec extends AbstractMatchCodec {

    private static final int VALUE_LENGTH = 8;
    private static final int NXM_FIELD_CODE = 16;
    public static final MatchEntrySerializerKey<Nxm1Class, OfjNxMatchTunId> SERIALIZER_KEY = new MatchEntrySerializerKey<>(
            EncodeConstants.OF13_VERSION_ID, Nxm1Class.class, OfjNxMatchTunId.class);
    public static final MatchEntryDeserializerKey DESERIALIZER_KEY = new MatchEntryDeserializerKey(
            EncodeConstants.OF13_VERSION_ID, OxmMatchConstants.NXM_1_CLASS, NXM_FIELD_CODE);

    @Override
    public void serialize(MatchEntries input, ByteBuf outBuffer) {
        serializeHeader(input, outBuffer);
        BigInteger value = input.getAugmentation(OfjAugNxMatch.class).getMatchTunId().getValue();
        outBuffer.writeLong(value.longValue());
    }

    @Override
    public MatchEntries deserialize(ByteBuf message) {
        MatchEntriesBuilder matchEntriesBuilder = deserializeHeader(message);
        OfjAugNxMatchBuilder augNxMatchBuilder = new OfjAugNxMatchBuilder();
        augNxMatchBuilder.setMatchTunId(new MatchTunIdBuilder().setValue(BigInteger.valueOf(message.readLong()))
                .build());
        matchEntriesBuilder.addAugmentation(OfjAugNxMatch.class, augNxMatchBuilder.build());
        return matchEntriesBuilder.build();
    }

    @Override
    public int getNxmFieldCode() {
        return NXM_FIELD_CODE;
    }

    @Override
    public int getOxmClassCode() {
        return OxmMatchConstants.NXM_1_CLASS;
    }

    @Override
    public int getValueLength() {
        return VALUE_LENGTH;
    }

    @Override
    public Class<? extends MatchField> getNxmField() {
        return OfjNxMatchTunId.class;
    }

    @Override
    public Class<? extends Clazz> getOxmClass() {
        return Nxm1Class.class;
    }

}
