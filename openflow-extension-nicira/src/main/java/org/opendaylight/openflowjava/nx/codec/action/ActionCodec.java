package org.opendaylight.openflowjava.nx.codec.action;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.nx.NiciraConstants;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterIdActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ExperimenterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.OfjAugNxAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.OfjAugNxActionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionCodec implements OFSerializer<Action>, OFDeserializer<Action> {

    private static final Logger LOG = LoggerFactory.getLogger(ActionCodec.class);

    public static final ExperimenterActionSerializerKey SERIALIZER_KEY = new ExperimenterActionSerializerKey(EncodeConstants.OF13_VERSION_ID, NiciraConstants.NX_VENDOR_ID); 
    public static final ExperimenterActionDeserializerKey DESERIALIZER_KEY = new ExperimenterActionDeserializerKey(EncodeConstants.OF13_VERSION_ID, NiciraConstants.NX_VENDOR_ID);
    private static final RegMoveCodec regMoveCodec = new RegMoveCodec();
    private static final RegLoadCodec regLoadCodec = new RegLoadCodec();
    
    @Override
    public void serialize(Action input, ByteBuf outBuffer) {
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        OfjAugNxAction augNxAction = input.getAugmentation(OfjAugNxAction.class);
        if (augNxAction == null) {
            LOG.info("Action {} does not have any serializer.", input.getClass());
            return;
        }
        if (augNxAction.getActionRegMove() != null) {
            writeMsgLengthVendorIdSubtypeToBuffer(RegMoveCodec.LENGTH, RegMoveCodec.SUBTYPE, outBuffer);
            regMoveCodec.serialize(augNxAction.getActionRegMove(), outBuffer);
        } else if (augNxAction.getActionRegLoad() != null) {
            writeMsgLengthVendorIdSubtypeToBuffer(RegLoadCodec.LENGTH, RegLoadCodec.SUBTYPE, outBuffer);
            regLoadCodec.serialize(augNxAction.getActionRegLoad(), outBuffer);
        } else {
            LOG.info("Action {} does not have any serializer.", input.getClass());
        }
    }

    private final static void writeMsgLengthVendorIdSubtypeToBuffer(int msgLength, int subtype, ByteBuf outBuffer) {
        outBuffer.writeShort(msgLength);
        outBuffer.writeInt(NiciraConstants.NX_VENDOR_ID.intValue());
        outBuffer.writeShort(subtype);
    }

    @Override
    public Action deserialize(ByteBuf message) {
        // size of experimenter type
        message.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        // size of length
        message.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        ExperimenterIdActionBuilder expIdBuilder = new ExperimenterIdActionBuilder();
        expIdBuilder.setExperimenter(new ExperimenterId(message.readUnsignedInt()));
        int subtype = message.readUnsignedShort();
        OfjAugNxActionBuilder augNxActionBuilder = new OfjAugNxActionBuilder();
        switch (subtype) {
        case RegMoveCodec.SUBTYPE:
            augNxActionBuilder.setActionRegMove(regMoveCodec.deserialize(message));
            break;
        case RegLoadCodec.SUBTYPE:
            augNxActionBuilder.setActionRegLoad(regLoadCodec.deserialize(message));
            break;
        default:
            LOG.info("Action {} does not have any deserializer.", subtype);
            return null;
        }
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Experimenter.class);
        actionBuilder.addAugmentation(ExperimenterIdAction.class, expIdBuilder.build());
        actionBuilder.addAugmentation(OfjAugNxAction.class, augNxActionBuilder.build());
        return actionBuilder.build();
    }

}
