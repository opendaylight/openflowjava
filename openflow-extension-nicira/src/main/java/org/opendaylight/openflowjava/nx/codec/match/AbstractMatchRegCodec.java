package org.opendaylight.openflowjava.nx.codec.match;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.match.rev140421.AugNxMatch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.match.rev140421.AugNxMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.match.rev140421.nx.match.reg.grouping.MatchRegBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Clazz;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

public abstract class AbstractMatchRegCodec extends AbstractMatchCodec {

    private static final int VALUE_LENGTH = 4;

    @Override
    public MatchEntries deserialize(ByteBuf message) {
        MatchEntriesBuilder matchEntriesBuilder = deserializeHeader(message);
        AugNxMatchBuilder augNxMatchBuilder = new AugNxMatchBuilder();
        augNxMatchBuilder.setMatchReg(new MatchRegBuilder().setValue(message.readUnsignedInt()).build());
        matchEntriesBuilder.setHasMask(matchEntriesBuilder.isHasMask());
        matchEntriesBuilder.setOxmClass(matchEntriesBuilder.getOxmClass());
        matchEntriesBuilder.setOxmMatchField(matchEntriesBuilder.getOxmMatchField());
        matchEntriesBuilder.addAugmentation(AugNxMatch.class, augNxMatchBuilder.build());
        return matchEntriesBuilder.build();
    }

    @Override
    public void serialize(MatchEntries input, ByteBuf outBuffer) {
        serializeHeader(input, outBuffer);
        Long value = input.getAugmentation(AugNxMatch.class).getMatchReg().getValue();
        outBuffer.writeInt(value.intValue());
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
    public Class<? extends Clazz> getOxmClass() {
        return NXM_1_CLASS;
    }

}
