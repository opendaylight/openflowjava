package org.opendaylight.openflowjava.nx.codec.action;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.ofj.nx.action.reg.move.grouping.ActionRegMove;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.ofj.nx.action.reg.move.grouping.ActionRegMoveBuilder;

public class RegMoveCodec implements OFSerializer<ActionRegMove>, OFDeserializer<ActionRegMove> {

    public static final int LENGTH = 24;
    public static final byte SUBTYPE = 6; // NXAST_REG_MOVE

    @Override
    public void serialize(ActionRegMove input, ByteBuf outBuffer) {
        outBuffer.writeShort(input.getNBits());
        outBuffer.writeShort(input.getSrcOfs());
        outBuffer.writeShort(input.getDstOfs());
        outBuffer.writeInt(input.getSrc().intValue());
        outBuffer.writeInt(input.getDst().intValue());
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
