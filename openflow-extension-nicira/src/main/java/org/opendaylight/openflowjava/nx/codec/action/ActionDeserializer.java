package org.opendaylight.openflowjava.nx.codec.action;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.nx.NiciraConstants;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterIdActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ExperimenterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.NxmNxRegLoad;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.NxmNxRegMove;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.OfjAugNxAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.OfjAugNxActionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionDeserializer implements OFDeserializer<Action> {

    private static final Logger LOG = LoggerFactory.getLogger(ActionDeserializer.class);

    public static final ExperimenterActionDeserializerKey DESERIALIZER_KEY = new ExperimenterActionDeserializerKey(
            EncodeConstants.OF13_VERSION_ID, NiciraConstants.NX_VENDOR_ID);

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
            augNxActionBuilder.setActionRegMove(NiciraActionCodecs.REG_MOVE_CODEC.deserialize(message));
            expIdBuilder.setSubType(NxmNxRegMove.class);
            break;
        case RegLoadCodec.SUBTYPE:
            augNxActionBuilder.setActionRegLoad(NiciraActionCodecs.REG_LOAD_CODEC.deserialize(message));
            expIdBuilder.setSubType(NxmNxRegLoad.class);
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
