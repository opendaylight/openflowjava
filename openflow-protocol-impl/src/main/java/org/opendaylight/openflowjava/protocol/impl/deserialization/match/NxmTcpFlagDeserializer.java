

package org.opendaylight.openflowjava.protocol.impl.deserialization.match;

import io.netty.buffer.ByteBuf;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TcpFlagMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TcpFlagMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Clazz;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TcpFlag;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

/**
 *
 */
public class NxmTcpFlagDeserializer extends AbstractOxmMatchEntryDeserializer
        implements OFDeserializer<MatchEntries> {

    @Override
    public MatchEntries deserialize(ByteBuf input) {
        MatchEntriesBuilder builder = processHeader(getOxmClass(), getOxmField(), input);
        addTcpFlagAugmentation(input, builder);
        if (builder.isHasMask()) {
            OxmMaskDeserializer.addMaskAugmentation(builder, input, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        }
        return builder.build();
    }

    private static void addTcpFlagAugmentation(ByteBuf input, MatchEntriesBuilder builder) {
        TcpFlagMatchEntryBuilder tcpFlagMatchEntryBuilder = new TcpFlagMatchEntryBuilder();
        tcpFlagMatchEntryBuilder.setTcpFlag(input.readUnsignedShort());
        builder.addAugmentation(TcpFlagMatchEntry.class, tcpFlagMatchEntryBuilder.build());
    }

    @Override
    protected Class<? extends MatchField> getOxmField() {
        return TcpFlag.class;
    }

    @Override
    protected Class<? extends Clazz> getOxmClass() {
        return Nxm1Class.class;
    }
}