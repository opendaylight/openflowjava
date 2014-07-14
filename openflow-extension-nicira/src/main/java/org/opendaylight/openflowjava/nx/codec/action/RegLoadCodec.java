package org.opendaylight.openflowjava.nx.codec.action;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.action.rev140421.ofj.nx.action.reg.load.grouping.ActionRegLoad;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.action.rev140421.ofj.nx.action.reg.load.grouping.ActionRegLoadBuilder;

public class RegLoadCodec implements OFSerializer<ActionRegLoad>, OFDeserializer<ActionRegLoad> {

    public static final int LENGTH = 24;
    public static final byte SUBTYPE = 7; // NXAST_REG_LOAD

    @Override
    public void serialize(ActionRegLoad input, ByteBuf outBuffer) {
        outBuffer.writeShort(input.getOfsNbits());
        outBuffer.writeInt(input.getDst().intValue());
        outBuffer.writeLong(input.getValue().longValue());
    }

    @Override
    public ActionRegLoad deserialize(ByteBuf message) {
        ActionRegLoadBuilder actionRegLoadBuilder = new ActionRegLoadBuilder();
        actionRegLoadBuilder.setOfsNbits(message.readUnsignedShort());
        actionRegLoadBuilder.setDst(message.readUnsignedInt());
        actionRegLoadBuilder.setValue(BigInteger.valueOf(message.readLong()));
        return actionRegLoadBuilder.build();
    }

}
