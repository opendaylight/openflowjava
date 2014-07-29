package org.opendaylight.openflowjava.nx.codec.action;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.nx.NiciraConstants;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.NxmNxActionRegMove;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.OfjAugNxAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.ofj.nx.action.reg.move.grouping.ActionRegMove;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.ofj.nx.action.reg.move.grouping.ActionRegMoveBuilder;

public class RegMoveCodec extends AbstractActionSerializer implements OFDeserializer<ActionRegMove> {

    public static final ExperimenterActionSerializerKey SERIALIZER_KEY = new ExperimenterActionSerializerKey(
            EncodeConstants.OF13_VERSION_ID, NiciraConstants.NX_VENDOR_ID, NxmNxActionRegMove.class);
    public static final int LENGTH = 24;
    public static final byte SUBTYPE = 6; // NXAST_REG_MOVE

    @Override
    public void serialize(Action input, ByteBuf outBuffer) {
        ActionRegMove actionRegMove = input.getAugmentation(OfjAugNxAction.class).getActionRegMove();
        serializeHeader(LENGTH, SUBTYPE, outBuffer);
        outBuffer.writeShort(actionRegMove.getNBits());
        outBuffer.writeShort(actionRegMove.getSrcOfs());
        outBuffer.writeShort(actionRegMove.getDstOfs());
        outBuffer.writeInt(actionRegMove.getSrc().intValue());
        outBuffer.writeInt(actionRegMove.getDst().intValue());
    }

    @Override
    public ActionRegMove deserialize(ByteBuf message) {
        ActionRegMoveBuilder actionRegMoveBuilder = new ActionRegMoveBuilder();
        actionRegMoveBuilder.setNBits(message.readUnsignedShort());
        actionRegMoveBuilder.setSrcOfs(message.readUnsignedShort());
        actionRegMoveBuilder.setDstOfs(message.readUnsignedShort());
        actionRegMoveBuilder.setSrc(message.readUnsignedInt());
        actionRegMoveBuilder.setDst(message.readUnsignedInt());
        return actionRegMoveBuilder.build();
    }

}
