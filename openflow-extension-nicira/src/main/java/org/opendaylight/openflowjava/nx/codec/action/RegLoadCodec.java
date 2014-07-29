package org.opendaylight.openflowjava.nx.codec.action;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.opendaylight.openflowjava.nx.NiciraConstants;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.NxmNxRegLoad;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.OfjAugNxAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.ofj.nx.action.reg.load.grouping.ActionRegLoad;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.ofj.nx.action.reg.load.grouping.ActionRegLoadBuilder;

public class RegLoadCodec extends AbstractActionSerializer implements OFDeserializer<ActionRegLoad> {

    public static final ExperimenterActionSerializerKey SERIALIZER_KEY = new ExperimenterActionSerializerKey(
            EncodeConstants.OF13_VERSION_ID, NiciraConstants.NX_VENDOR_ID, NxmNxRegLoad.class);
    public static final int LENGTH = 24;
    public static final byte SUBTYPE = 7; // NXAST_REG_LOAD

    @Override
    public void serialize(Action input, ByteBuf outBuffer) {
        ActionRegLoad actionRegLoad = input.getAugmentation(OfjAugNxAction.class).getActionRegLoad();
        serializeHeader(LENGTH, SUBTYPE, outBuffer);
        outBuffer.writeShort(actionRegLoad.getOfsNbits());
        outBuffer.writeInt(actionRegLoad.getDst().intValue());
        outBuffer.writeLong(actionRegLoad.getValue().longValue());
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
