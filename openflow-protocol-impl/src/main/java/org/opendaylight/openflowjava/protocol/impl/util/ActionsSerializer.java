/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.opendaylight.openflowjava.protocol.impl.serialization.factories.EncodeConstants;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.actions.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.MatchEntries;

/**
 * Serializes ofp_actions (OpenFlow v1.3)
 * @author michal.polkorab
 * @author timotej.kubas
 */
public abstract class ActionsSerializer {

    /**
     * Encodes actions to ByteBuf
     * @param actionsList list of actions to be encoded
     * @param outBuffer output ByteBuf
     */
    public static void encodeActions(List<ActionsList> actionsList, ByteBuf outBuffer) {
        if (actionsList == null) {
            return;
        }
        for (ActionsList list : actionsList) {
            Action action = list.getAction();
            if (action.getType().equals(Output.class)) {
                encodeOutputAction(action, outBuffer);
            } else if (action.getType().equals(CopyTtlOut.class)) {
                encodeCopyTtlOutAction(outBuffer);
            } else if (action.getType().equals(CopyTtlIn.class)) {
                encodeCopyTtlInAction(outBuffer);
            } else if (action.getType().equals(SetMplsTtl.class)) {
                encodeSetMplsTtltAction(action, outBuffer);
            } else if (action.getType().equals(DecMplsTtl.class)) {
                encodeDecMplsTtlAction(outBuffer);
            } else if (action.getType().equals(PushVlan.class)) {
                encodePushVlanAction(action, outBuffer);
            } else if (action.getType().equals(PopVlan.class)) {
                encodePopVlanAction(outBuffer);
            } else if (action.getType().equals(PushMpls.class)) {
                encodePushMplsAction(action, outBuffer);
            } else if (action.getType().equals(PopMpls.class)) {
                encodePopMplsAction(action, outBuffer);
            } else if (action.getType().equals(SetQueue.class)) {
                encodeSetQueueAction(action, outBuffer);
            } else if (action.getType().equals(Group.class)) {
                encodeGroupAction(action, outBuffer);
            } else if (action.getType().equals(SetNwTtl.class)) {
                encodeSetNwTtlAction(action, outBuffer);
            } else if (action.getType().equals(DecNwTtl.class)) {
                encodeDecNwTtlAction(outBuffer);
            } else if (action.getType().equals(SetField.class)) {
                encodeSetFieldAction(action, outBuffer);
            } else if (action.getType().equals(PushPbb.class)) {
                encodePushPbbAction(action, outBuffer);
            } else if (action.getType().equals(PopPbb.class)) {
                encodePopPbbAction(outBuffer);
            } else if (action.getType().equals(Experimenter.class)) {
                encodeExperimenterAction(action, outBuffer);
            } 
        }
    }

    private static void encodeOutputAction(Action action, ByteBuf outBuffer) {
        final byte OUTPUT_CODE = 0;
        final byte OUTPUT_LENGTH = 16;
        final byte OUTPUT_PADDING = 6;
        outBuffer.writeShort(OUTPUT_CODE);
        outBuffer.writeShort(OUTPUT_LENGTH);
        PortAction port = action.getAugmentation(PortAction.class);
        outBuffer.writeInt(port.getPort().getValue().intValue());
        MaxLengthAction maxlength = action.getAugmentation(MaxLengthAction.class);
        outBuffer.writeShort(maxlength.getMaxLength());
        ByteBufUtils.padBuffer(OUTPUT_PADDING, outBuffer);
    }

    private static void encodeCopyTtlOutAction(ByteBuf outBuffer) {
        final byte COPY_TTL_OUT_CODE = 11;
        outBuffer.writeShort(COPY_TTL_OUT_CODE);
        encodeRestOfActionHeader(outBuffer);
    }
    
    private static void encodeCopyTtlInAction(ByteBuf outBuffer) {
        final byte COPY_TTL_IN_CODE = 12;
        outBuffer.writeShort(COPY_TTL_IN_CODE);
        encodeRestOfActionHeader(outBuffer);
    }
    
    private static void encodeSetMplsTtltAction(Action action, ByteBuf outBuffer) {
        final byte SET_MPLS_TTL_CODE = 15;
        final byte SET_MPLS_TTL_LENGTH = 8;
        final byte SET_MPLS_TTL_PADDING = 3;
        outBuffer.writeShort(SET_MPLS_TTL_CODE);
        outBuffer.writeShort(SET_MPLS_TTL_LENGTH);
        MplsTtlAction mplsTtl = action.getAugmentation(MplsTtlAction.class);
        outBuffer.writeByte(mplsTtl.getMplsTtl());
        ByteBufUtils.padBuffer(SET_MPLS_TTL_PADDING, outBuffer);
    }
    
    private static void encodeDecMplsTtlAction(ByteBuf outBuffer) {
        final byte DEC_MPLS_TTL_CODE = 16;
        outBuffer.writeShort(DEC_MPLS_TTL_CODE);
        encodeRestOfActionHeader(outBuffer);
    }
    
    private static void encodePushVlanAction(Action action, ByteBuf outBuffer) {
        final byte PUSH_VLAN_CODE = 17;
        outBuffer.writeShort(PUSH_VLAN_CODE);
        encodeCommonEthertype(action, outBuffer);
    }

    private static void encodePopVlanAction(ByteBuf outBuffer) {
        final byte POP_VLAN_CODE = 18;
        outBuffer.writeShort(POP_VLAN_CODE);
        encodeRestOfActionHeader(outBuffer);
    }
    
    private static void encodePushMplsAction(Action action, ByteBuf outBuffer) {
        final byte PUSH_MPLS_CODE = 19;
        outBuffer.writeShort(PUSH_MPLS_CODE);
        encodeCommonEthertype(action, outBuffer);
    }
    
    private static void encodePopMplsAction(Action action, ByteBuf outBuffer) {
        final byte POP_MPLS_CODE = 20;
        outBuffer.writeShort(POP_MPLS_CODE);
        encodeCommonEthertype(action, outBuffer);
    }
    
    private static void encodeSetQueueAction(Action action, ByteBuf outBuffer) {
        final byte SET_QUEUE_CODE = 21;
        final byte SET_QUEUE_LENGTH = 8;
        outBuffer.writeShort(SET_QUEUE_CODE);
        outBuffer.writeShort(SET_QUEUE_LENGTH);
        QueueIdAction queueId = action.getAugmentation(QueueIdAction.class);
        outBuffer.writeInt(queueId.getQueueId().intValue());
    }

    private static void encodeGroupAction(Action action, ByteBuf outBuffer) {
        final byte GROUP_CODE = 22;
        final byte GROUP_LENGTH = 8;
        outBuffer.writeShort(GROUP_CODE);
        outBuffer.writeShort(GROUP_LENGTH);
        GroupIdAction groupId = action.getAugmentation(GroupIdAction.class);
        outBuffer.writeInt(groupId.getGroupId().intValue());
    }
    
    private static void encodeSetNwTtlAction(Action action, ByteBuf outBuffer) {
        final byte SET_NW_TTL_CODE = 23;
        final byte SET_NW_TTL_LENGTH = 8;
        final byte SET_NW_TTL_PADDING = 3;
        outBuffer.writeShort(SET_NW_TTL_CODE);
        outBuffer.writeShort(SET_NW_TTL_LENGTH);
        NwTtlAction nwTtl = action.getAugmentation(NwTtlAction.class);
        outBuffer.writeByte(nwTtl.getNwTtl());
        ByteBufUtils.padBuffer(SET_NW_TTL_PADDING, outBuffer);
    }
    
    private static void encodeDecNwTtlAction(ByteBuf outBuffer) {
        final byte DEC_NW_TTL_CODE = 24;
        outBuffer.writeShort(DEC_NW_TTL_CODE);
        encodeRestOfActionHeader(outBuffer);
    }
    
    private static void encodeSetFieldAction(Action action, ByteBuf outBuffer) {
        final int SET_FIELD_CODE = 25;
        final byte SET_FIELD_HEADER_LENGTH = 4; // only type and length
        OxmFieldsAction oxmField = action.getAugmentation(OxmFieldsAction.class);
        int length = MatchSerializer.computeMatchEntriesLength(oxmField.getMatchEntries()) + SET_FIELD_HEADER_LENGTH;
        outBuffer.writeShort(SET_FIELD_CODE);
        int paddingRemainder = length % EncodeConstants.PADDING;
        if (paddingRemainder != 0) {
            length += EncodeConstants.PADDING - paddingRemainder;
        }
        outBuffer.writeShort(length);
    }
    
    private static void encodePushPbbAction(Action action, ByteBuf outBuffer) {
        final byte PUSH_PBB_CODE = 26;
        outBuffer.writeShort(PUSH_PBB_CODE);
        encodeCommonEthertype(action, outBuffer);
    }
    
    private static void encodePopPbbAction(ByteBuf outBuffer) {
        final byte POP_PBB_CODE = 27;
        outBuffer.writeShort(POP_PBB_CODE);
        encodeRestOfActionHeader(outBuffer);
    }

    private static void encodeExperimenterAction(Action action, ByteBuf outBuffer) {
        final int EXPERIMENTER_CODE = 65535; // 0xFFFF
        final byte EXPERIMENTER_LENGTH = 8;
        outBuffer.writeShort(EXPERIMENTER_CODE);
        outBuffer.writeShort(EXPERIMENTER_LENGTH);
        ExperimenterAction experimenter = action.getAugmentation(ExperimenterAction.class);
        outBuffer.writeInt(experimenter.getExperimenter().intValue());
    }
    
    private static void encodeRestOfActionHeader(ByteBuf outBuffer) {
        final byte ACTION_HEADER_LENGTH = 8;
        final byte PADDING_IN_ACTION_HEADER = 4;
        outBuffer.writeShort(ACTION_HEADER_LENGTH);
        ByteBufUtils.padBuffer(PADDING_IN_ACTION_HEADER, outBuffer);
    }
    
    private static void encodeCommonEthertype(Action action, ByteBuf outBuffer) {
        final byte LENGTH_OF_ETHERTYPE_ACTION = 8;
        final byte ETHERTYPE_ACTION_PADDING = 2;
        outBuffer.writeShort(LENGTH_OF_ETHERTYPE_ACTION);
        EthertypeAction ethertype = action.getAugmentation(EthertypeAction.class);
        outBuffer.writeShort(ethertype.getEthertype().getValue());
        ByteBufUtils.padBuffer(ETHERTYPE_ACTION_PADDING, outBuffer);
    }
    
    /**
     * Computes length of actions
     * @param actionsList list of actions
     * @return actions length
     */
    public static int computeLengthOfActions(List<ActionsList> actionsList) {
        final byte OUTPUT_LENGTH = 16;
        final byte LENGTH_OF_OTHER_ACTIONS = 8;
        final byte ACTION_HEADER_LENGTH = 4;
        int lengthOfActions = 0;
        if (actionsList != null) {
            for (ActionsList list : actionsList) {
                Action action = list.getAction();
                if (action.getType().equals(Output.class)) {
                    lengthOfActions += OUTPUT_LENGTH;
                } else if (action.getType().equals(SetField.class)){
                    List<MatchEntries> entries = action.getAugmentation(OxmFieldsAction.class).getMatchEntries();
                    int actionLength = ACTION_HEADER_LENGTH + MatchSerializer.computeMatchEntriesLength(entries);
                    lengthOfActions += actionLength;
                    int paddingRemainder = actionLength % EncodeConstants.PADDING;
                    if ((paddingRemainder) != 0) {
                        lengthOfActions += EncodeConstants.PADDING - paddingRemainder;
                    }
                } else {
                    lengthOfActions += LENGTH_OF_OTHER_ACTIONS;
                }
            }
        }
        return lengthOfActions;
    }
}
