/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerTable;
import org.opendaylight.openflowjava.protocol.impl.deserialization.EnhancedMessageTypeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthertypeAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.GroupIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MplsTtlAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTtlAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmFieldsAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.CopyTtlIn;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.CopyTtlOut;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.DecMplsTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.DecNwTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Group;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Output;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopMpls;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopPbb;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushMpls;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushPbb;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetMplsTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * Serializes ofp_actions (OpenFlow v1.3)
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class OF13ActionsSerializer implements HeaderSerializer<Action> {

    private static final byte OUTPUT_CODE = 0;
    private static final byte COPY_TTL_OUT_CODE = 11;
    private static final byte COPY_TTL_IN_CODE = 12;
    private static final byte SET_MPLS_TTL_CODE = 15;
    private static final byte DEC_MPLS_TTL_CODE = 16;
    private static final byte PUSH_VLAN_CODE = 17;
    private static final byte POP_VLAN_CODE = 18;
    private static final byte PUSH_MPLS_CODE = 19;
    private static final byte POP_MPLS_CODE = 20;
    private static final byte SET_QUEUE_CODE = 21;
    private static final byte GROUP_CODE = 22;
    private static final byte SET_NW_TTL_CODE = 23;
    private static final byte DEC_NW_TTL_CODE = 24;
    private static final int SET_FIELD_CODE = 25;
    private static final byte PUSH_PBB_CODE = 26;
    private static final byte POP_PBB_CODE = 27;
    private static final int EXPERIMENTER_CODE = 65535; // 0xFFFF
    private static final byte OUTPUT_LENGTH = 16;
    private static final byte SET_MPLS_TTL_LENGTH = 8;
    private static final byte SET_QUEUE_LENGTH = 8;
    private static final byte GROUP_LENGTH = 8;
    private static final byte SET_NW_TTL_LENGTH = 8;
    private static final byte EXPERIMENTER_ACTION_HEADER_LENGTH = 8;
    private static final byte ACTION_HEADER_LENGTH = 8;
    private static final byte LENGTH_OF_ETHERTYPE_ACTION = 8;
    private static final byte OUTPUT_PADDING = 6;
    private static final byte SET_MPLS_TTL_PADDING = 3;
    private static final byte SET_NW_TTL_PADDING = 3;
    private static final byte PADDING_IN_ACTION_HEADER = 4;
    private static final byte ETHERTYPE_ACTION_PADDING = 2;
    private static final byte ACTION_IDS_LENGTH = 4;
    private SerializerTable serializerTable;

    @Override
    public void serialize(Action object, ByteBuf outBuffer) {
        if (object.getType().isAssignableFrom(Output.class)) {
            encodeOutputAction(object, outBuffer);
        } else if (object.getType().isAssignableFrom(CopyTtlOut.class)) {
            encodeCopyTtlOutAction(outBuffer);
        } else if (object.getType().isAssignableFrom(CopyTtlIn.class)) {
            encodeCopyTtlInAction(outBuffer);
        } else if (object.getType().isAssignableFrom(SetMplsTtl.class)) {
            encodeSetMplsTtltAction(object, outBuffer);
        } else if (object.getType().isAssignableFrom(DecMplsTtl.class)) {
            encodeDecMplsTtlAction(outBuffer);
        } else if (object.getType().isAssignableFrom(PushVlan.class)) {
            encodePushVlanAction(object, outBuffer);
        } else if (object.getType().isAssignableFrom(PopVlan.class)) {
            encodePopVlanAction(outBuffer);
        } else if (object.getType().isAssignableFrom(PushMpls.class)) {
            encodePushMplsAction(object, outBuffer);
        } else if (object.getType().isAssignableFrom(PopMpls.class)) {
            encodePopMplsAction(object, outBuffer);
        } else if (object.getType().isAssignableFrom(SetQueue.class)) {
            encodeSetQueueAction(object, outBuffer);
        } else if (object.getType().isAssignableFrom(Group.class)) {
            encodeGroupAction(object, outBuffer);
        } else if (object.getType().isAssignableFrom(SetNwTtl.class)) {
            encodeSetNwTtlAction(object, outBuffer);
        } else if (object.getType().isAssignableFrom(DecNwTtl.class)) {
            encodeDecNwTtlAction(outBuffer);
        } else if (object.getType().isAssignableFrom(SetField.class)) {
            encodeSetFieldAction(object, outBuffer);
        } else if (object.getType().isAssignableFrom(PushPbb.class)) {
            encodePushPbbAction(object, outBuffer);
        } else if (object.getType().isAssignableFrom(PopPbb.class)) {
            encodePopPbbAction(outBuffer);
        } else if (object.getType().isAssignableFrom(Experimenter.class)) {
            encodeExperimenterAction(object, outBuffer);
        }
    }

    private static void writeTypeAndLength(ByteBuf out, int type, int length) {
        out.writeShort(type);
        out.writeShort(length);
    }

    private static void encodeOutputAction(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(OUTPUT_CODE);
        outBuffer.writeShort(OUTPUT_LENGTH);
        PortAction port = action.getAugmentation(PortAction.class);
        outBuffer.writeInt(port.getPort().getValue().intValue());
        MaxLengthAction maxlength = action.getAugmentation(MaxLengthAction.class);
        outBuffer.writeShort(maxlength.getMaxLength());
        ByteBufUtils.padBuffer(OUTPUT_PADDING, outBuffer);
    }

    private static void encodeCopyTtlOutAction(ByteBuf outBuffer) {
        outBuffer.writeShort(COPY_TTL_OUT_CODE);
        encodeRestOfActionHeader(outBuffer);
    }
    
    private static void encodeCopyTtlInAction(ByteBuf outBuffer) {
        outBuffer.writeShort(COPY_TTL_IN_CODE);
        encodeRestOfActionHeader(outBuffer);
    }
    
    private static void encodeSetMplsTtltAction(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(SET_MPLS_TTL_CODE);
        outBuffer.writeShort(SET_MPLS_TTL_LENGTH);
        MplsTtlAction mplsTtl = action.getAugmentation(MplsTtlAction.class);
        outBuffer.writeByte(mplsTtl.getMplsTtl());
        ByteBufUtils.padBuffer(SET_MPLS_TTL_PADDING, outBuffer);
    }
    
    private static void encodeDecMplsTtlAction(ByteBuf outBuffer) {
        outBuffer.writeShort(DEC_MPLS_TTL_CODE);
        encodeRestOfActionHeader(outBuffer);
    }
    
    private static void encodePushVlanAction(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(PUSH_VLAN_CODE);
        encodeCommonEthertype(action, outBuffer);
    }

    private static void encodePopVlanAction(ByteBuf outBuffer) {
        outBuffer.writeShort(POP_VLAN_CODE);
        encodeRestOfActionHeader(outBuffer);
    }
    
    private static void encodePushMplsAction(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(PUSH_MPLS_CODE);
        encodeCommonEthertype(action, outBuffer);
    }
    
    private static void encodePopMplsAction(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(POP_MPLS_CODE);
        encodeCommonEthertype(action, outBuffer);
    }
    
    private static void encodeSetQueueAction(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(SET_QUEUE_CODE);
        outBuffer.writeShort(SET_QUEUE_LENGTH);
        QueueIdAction queueId = action.getAugmentation(QueueIdAction.class);
        outBuffer.writeInt(queueId.getQueueId().intValue());
    }

    private static void encodeGroupAction(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(GROUP_CODE);
        outBuffer.writeShort(GROUP_LENGTH);
        GroupIdAction groupId = action.getAugmentation(GroupIdAction.class);
        outBuffer.writeInt(groupId.getGroupId().intValue());
    }
    
    private static void encodeSetNwTtlAction(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(SET_NW_TTL_CODE);
        outBuffer.writeShort(SET_NW_TTL_LENGTH);
        NwTtlAction nwTtl = action.getAugmentation(NwTtlAction.class);
        outBuffer.writeByte(nwTtl.getNwTtl());
        ByteBufUtils.padBuffer(SET_NW_TTL_PADDING, outBuffer);
    }
    
    private static void encodeDecNwTtlAction(ByteBuf outBuffer) {
        outBuffer.writeShort(DEC_NW_TTL_CODE);
        encodeRestOfActionHeader(outBuffer);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void encodeSetFieldAction(Action action, ByteBuf outBuffer) {
        OxmFieldsAction oxmField = action.getAugmentation(OxmFieldsAction.class);
        int setFieldStartIndex = outBuffer.writerIndex();
        outBuffer.writeShort(SET_FIELD_CODE);
        int setFieldLengthIndex = outBuffer.writerIndex();
        outBuffer.writeShort(EncodeConstants.EMPTY_LENGTH);
        MatchEntries entry = oxmField.getMatchEntries().get(0);
        OFSerializer serializer = serializerTable.getSerializer(new EnhancedMessageTypeKey(
                EncodeConstants.OF13_VERSION_ID, entry.getOxmClass(), entry.getOxmMatchField()));
        serializer.serialize(entry, outBuffer);
        int paddingRemainder = (outBuffer.writerIndex() - setFieldStartIndex) % EncodeConstants.PADDING;
        if (paddingRemainder != 0) {
            ByteBufUtils.padBuffer(EncodeConstants.PADDING - paddingRemainder, outBuffer);
        }
        outBuffer.setShort(setFieldLengthIndex, outBuffer.writerIndex() - setFieldStartIndex);
    }
    
    private static void encodePushPbbAction(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(PUSH_PBB_CODE);
        encodeCommonEthertype(action, outBuffer);
    }
    
    private static void encodePopPbbAction(ByteBuf outBuffer) {
        outBuffer.writeShort(POP_PBB_CODE);
        encodeRestOfActionHeader(outBuffer);
    }

    private static void encodeExperimenterAction(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(EXPERIMENTER_CODE);
        ExperimenterAction experimenter = action.getAugmentation(ExperimenterAction.class);
        if (experimenter.getData() != null) {
            outBuffer.writeShort(EXPERIMENTER_ACTION_HEADER_LENGTH + experimenter.getData().length);
            outBuffer.writeInt(experimenter.getExperimenter().intValue());
            outBuffer.writeBytes(experimenter.getData());
        } else {
            outBuffer.writeShort(EXPERIMENTER_ACTION_HEADER_LENGTH);
            outBuffer.writeInt(experimenter.getExperimenter().intValue());
        }
    }
    
    private static void encodeRestOfActionHeader(ByteBuf outBuffer) {
        outBuffer.writeShort(ACTION_HEADER_LENGTH);
        ByteBufUtils.padBuffer(PADDING_IN_ACTION_HEADER, outBuffer);
    }
    
    private static void encodeCommonEthertype(Action action, ByteBuf outBuffer) {
        EthertypeAction ethertype = action.getAugmentation(EthertypeAction.class);
        outBuffer.writeShort(LENGTH_OF_ETHERTYPE_ACTION);
        outBuffer.writeShort(ethertype.getEthertype().getValue());
        ByteBufUtils.padBuffer(ETHERTYPE_ACTION_PADDING, outBuffer);
    }

    @Override
    public void injectSerializerTable(SerializerTable table) {
        this.serializerTable = table;
    }

    @Override
    public void serializeHeader(Action object, ByteBuf outBuffer) {
        if (object.getType().isAssignableFrom(Output.class)) {
            writeTypeAndLength(outBuffer, OUTPUT_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(CopyTtlOut.class)) {
            writeTypeAndLength(outBuffer, COPY_TTL_OUT_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(CopyTtlIn.class)) {
            writeTypeAndLength(outBuffer, COPY_TTL_IN_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(SetMplsTtl.class)) {
            writeTypeAndLength(outBuffer, SET_MPLS_TTL_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(DecMplsTtl.class)) {
            writeTypeAndLength(outBuffer, DEC_MPLS_TTL_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(PushVlan.class)) {
            writeTypeAndLength(outBuffer, PUSH_VLAN_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(PopVlan.class)) {
            writeTypeAndLength(outBuffer, POP_VLAN_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(PushMpls.class)) {
            writeTypeAndLength(outBuffer, PUSH_MPLS_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(PopMpls.class)) {
            writeTypeAndLength(outBuffer, POP_MPLS_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(SetQueue.class)) {
            writeTypeAndLength(outBuffer, SET_QUEUE_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(Group.class)) {
            writeTypeAndLength(outBuffer, GROUP_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(SetNwTtl.class)) {
            writeTypeAndLength(outBuffer, SET_NW_TTL_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(DecNwTtl.class)) {
            writeTypeAndLength(outBuffer, DEC_NW_TTL_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(SetField.class)) {
            writeTypeAndLength(outBuffer, SET_FIELD_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(PushPbb.class)) {
            writeTypeAndLength(outBuffer, PUSH_PBB_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(PopPbb.class)) {
            writeTypeAndLength(outBuffer, POP_PBB_CODE, ACTION_IDS_LENGTH);
        } else if (object.getType().isAssignableFrom(Experimenter.class)) {
            writeTypeAndLength(outBuffer, EXPERIMENTER_CODE, EncodeConstants.EXPERIMENTER_IDS_LENGTH);
            ExperimenterAction experimenter = object.getAugmentation(ExperimenterAction.class);
            outBuffer.writeInt(experimenter.getExperimenter().intValue());
        }
    }

}
