/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthertypeAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthertypeActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.GroupIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.GroupIdActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MplsTtlAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MplsTtlActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTtlAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTtlActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmFieldsAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmFieldsActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdActionBuilder;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ActionBase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;

/**
 * Deserializes ofp_actions (OpenFlow v1.3)
 * @author timotej.kubas
 * @author michal.polkorab
 */
public abstract class ActionsDeserializer {
    
    private static final byte ACTION_HEADER_LENGTH = 4;
    private static final byte PADDING_IN_ACTIONS_HEADER = 4;
    private static final byte PADDING_IN_OUTPUT_ACTIONS_HEADER = 6;
    private static final byte PADDING_IN_SET_MPLS_TTL_ACTIONS_HEADER = 3;
    private static final byte PADDING_IN_PUSH_VLAN_ACTIONS_HEADER = 2;
    private static final byte PADDING_IN_NW_TTL_ACTIONS_HEADER = 3;
    
    /**
     * Creates list of actions (OpenFlow v1.3)
     * @param input input ByteBuf
     * @param actionsLength length of buckets
     * @return ActionsList
     */
    public static List<Action> createActions(ByteBuf input, int actionsLength) {
        List<Action> actions = new ArrayList<>();
        int length = 0;
        while (length < actionsLength) {
            int type = input.readUnsignedShort();
            int currentActionLength = input.readUnsignedShort();
            ActionBuilder actionBuilder = new ActionBuilder();
            switch(type) {
            case 0:
                actions.add(createOutputAction(input, actionBuilder));
                break;
            case 11:
                actions.add(createCopyTtlOutAction(input, actionBuilder));
                break;
            case 12:
                actions.add(createCopyTtlInAction(input, actionBuilder));
                break;
            case 15:
                actions.add(createSetMplsTtlAction(input, actionBuilder));
                break;
            case 16:
                actions.add(createDecMplsTtlOutAction(input, actionBuilder));
                break;
            case 17:
                actions.add(createPushVlanAction(input, actionBuilder));
                break;
            case 18:
                actions.add(createPopVlanAction(input, actionBuilder));
                break;
            case 19:
                actions.add(createPushMplsAction(input, actionBuilder));
                break;
            case 20:
                actions.add(createPopMplsAction(input, actionBuilder));
                break;
            case 21:
                actions.add(createSetQueueAction(input, actionBuilder));
                break;
            case 22:
                actions.add(createGroupAction(input, actionBuilder));
                break;
            case 23:
                actions.add(createSetNwTtlAction(input, actionBuilder));
                break;
            case 24:
                actions.add(createDecNwTtlAction(input, actionBuilder));
                break;
            case 25:
                actions.add(createSetFieldAction(input, actionBuilder, currentActionLength));
                break;
            case 26:
                actions.add(createPushPbbAction(input, actionBuilder));
                break;
            case 27:
                actions.add(createPopPbbAction(input, actionBuilder));
                break;
            case 0xFFFF:
                actions.add(createExperimenterAction(input, actionBuilder));
                break;
            default:
                break;
            }
            length += currentActionLength;
        } 
        return actions;
    }

    /**
     * Creates action ids - actions without values (OpenFlow v1.3)
     * @param input input ByteBuf
     * @param actionsLength length of actions
     * @return ActionsList
     */
    public static List<Action> createActionIds(ByteBuf input, int actionsLength) {
        List<Action> actionsList = new ArrayList<>();
        int length = 0;
        ActionBuilder builder;
        while (length < actionsLength) {
            builder = new ActionBuilder();
            int type = input.readUnsignedShort();
            int currentActionLength = input.readUnsignedShort();
            switch(type) {
            case 0:
                builder.setType(Output.class);
                break;
            case 11:
                builder.setType(CopyTtlOut.class);
                break;
            case 12:
                builder.setType(CopyTtlIn.class);
                break;
            case 15:
                builder.setType(SetMplsTtl.class);
                break;
            case 16:
                builder.setType(DecMplsTtl.class);
                break;
            case 17:
                builder.setType(PushVlan.class);
                break;
            case 18:
                builder.setType(PopVlan.class);
                break;
            case 19:
                builder.setType(PushMpls.class);
                break;
            case 20:
                builder.setType(PopMpls.class);
                break;
            case 21:
                builder.setType(SetQueue.class);
                break;
            case 22:
                builder.setType(Group.class);
                break;
            case 23:
                builder.setType(SetNwTtl.class);
                break;
            case 24:
                builder.setType(DecNwTtl.class);
                break;
            case 25:
                builder.setType(SetField.class);
                break; 
            case 26:
                builder.setType(PushPbb.class);
                break;
            case 27:
                builder.setType(PopPbb.class);
                break;
            case 0xFFFF:
                builder.setType(Experimenter.class);
                ExperimenterActionBuilder experimenter = new ExperimenterActionBuilder();
                experimenter.setExperimenter(input.readUnsignedInt());
                builder.addAugmentation(ExperimenterAction.class, experimenter.build());
                break;
            default: 
                break;
            }
            actionsList.add(builder.build());
            length += currentActionLength;
        } 
        return actionsList;
    }

    private static Action createEmptyHeader(Class<? extends ActionBase> action,
            ByteBuf in, ActionBuilder actionBuilder) {
        actionBuilder.setType(action);
        in.skipBytes(PADDING_IN_ACTIONS_HEADER);
        return actionBuilder.build();
    }
    
    private static Action createCopyTtlInAction(ByteBuf in, ActionBuilder actionBuilder) {
        return createEmptyHeader(CopyTtlIn.class, in, actionBuilder);
    }

    private static Action createCopyTtlOutAction(ByteBuf in, ActionBuilder actionBuilder) {
        return createEmptyHeader(CopyTtlOut.class, in, actionBuilder);
    }
    
    private static Action createDecMplsTtlOutAction(ByteBuf in, ActionBuilder actionBuilder) {
        return createEmptyHeader(DecMplsTtl.class, in, actionBuilder);
    }
    
    private static Action createPopVlanAction(ByteBuf in, ActionBuilder actionBuilder) {
        return createEmptyHeader(PopVlan.class, in, actionBuilder);
    }
    
    private static Action createDecNwTtlAction(ByteBuf in, ActionBuilder actionBuilder) {
        return createEmptyHeader(DecNwTtl.class, in, actionBuilder);
    }
    
    private static Action createPopPbbAction(ByteBuf in, ActionBuilder actionBuilder) {
        return createEmptyHeader(PopPbb.class, in, actionBuilder);
    }
    
    private static Action createOutputAction(ByteBuf in, ActionBuilder actionBuilder) {
        actionBuilder.setType(Output.class);
        PortActionBuilder port = new PortActionBuilder();
        port.setPort(new PortNumber(in.readUnsignedInt()));
        actionBuilder.addAugmentation(PortAction.class, port.build());
        MaxLengthActionBuilder maxLen = new MaxLengthActionBuilder();
        maxLen.setMaxLength(in.readUnsignedShort());
        actionBuilder.addAugmentation(MaxLengthAction.class, maxLen.build());
        in.skipBytes(PADDING_IN_OUTPUT_ACTIONS_HEADER);
        return actionBuilder.build();
    }
    
    private static Action createSetMplsTtlAction(ByteBuf in, ActionBuilder actionBuilder) {
        actionBuilder.setType(SetMplsTtl.class);
        MplsTtlActionBuilder mplsTtl = new MplsTtlActionBuilder();
        mplsTtl.setMplsTtl(in.readUnsignedByte());
        actionBuilder.addAugmentation(MplsTtlAction.class, mplsTtl.build());
        in.skipBytes(PADDING_IN_SET_MPLS_TTL_ACTIONS_HEADER);
        return actionBuilder.build();
    }
    
    private static Action createPushAction(Class<? extends ActionBase> action,
            ByteBuf in, ActionBuilder actionBuilder) {
        actionBuilder.setType(action);
        EthertypeActionBuilder etherType = new EthertypeActionBuilder();
        etherType.setEthertype(new EtherType(in.readUnsignedShort()));
        actionBuilder.addAugmentation(EthertypeAction.class, etherType.build());
        in.skipBytes(PADDING_IN_PUSH_VLAN_ACTIONS_HEADER);
        return actionBuilder.build();
    }
    
    private static Action createPushVlanAction(ByteBuf in, ActionBuilder actionBuilder) {
        return createPushAction(PushVlan.class, in, actionBuilder);
    }
    
    private static Action createPushMplsAction(ByteBuf in, ActionBuilder actionBuilder) {
        return createPushAction(PushMpls.class, in, actionBuilder);
    }
    
    private static Action createPopMplsAction(ByteBuf in, ActionBuilder actionBuilder) {
        return createPushAction(PopMpls.class, in, actionBuilder);
    }
    
    private static Action createPushPbbAction(ByteBuf in, ActionBuilder actionBuilder) {
        return createPushAction(PushPbb.class, in, actionBuilder);
    }
    
    private static Action createSetQueueAction(ByteBuf in, ActionBuilder actionBuilder) {
        actionBuilder.setType(SetQueue.class);
        QueueIdActionBuilder queueId = new QueueIdActionBuilder();
        queueId.setQueueId(in.readUnsignedInt());
        actionBuilder.addAugmentation(QueueIdAction.class, queueId.build());
        return actionBuilder.build();
    }
    
    private static Action createGroupAction(ByteBuf in, ActionBuilder actionBuilder) {
        actionBuilder.setType(Group.class);
        GroupIdActionBuilder group = new GroupIdActionBuilder();
        group.setGroupId(in.readUnsignedInt());
        actionBuilder.addAugmentation(GroupIdAction.class, group.build());
        return actionBuilder.build();
    }
    
    private static Action createExperimenterAction(ByteBuf in, ActionBuilder actionBuilder) {
        actionBuilder.setType(Experimenter.class);
        ExperimenterActionBuilder experimenter = new ExperimenterActionBuilder();
        experimenter.setExperimenter(in.readUnsignedInt());
        actionBuilder.addAugmentation(ExperimenterAction.class, experimenter.build());
        return actionBuilder.build();
    }
    
    private static Action createSetNwTtlAction(ByteBuf in, ActionBuilder actionBuilder) {
        actionBuilder.setType(SetNwTtl.class);
        NwTtlActionBuilder nwTtl = new NwTtlActionBuilder();
        nwTtl.setNwTtl(in.readUnsignedByte());
        actionBuilder.addAugmentation(NwTtlAction.class, nwTtl.build());
        in.skipBytes(PADDING_IN_NW_TTL_ACTIONS_HEADER);
        return actionBuilder.build();
    }
    
    private static Action createSetFieldAction(ByteBuf in, ActionBuilder actionBuilder, int actionLength) {
        actionBuilder.setType(SetField.class);
        OxmFieldsActionBuilder matchEntries = new OxmFieldsActionBuilder();
        matchEntries.setMatchEntries(MatchDeserializer.createMatchEntry(in, actionLength  - ACTION_HEADER_LENGTH));
        actionBuilder.addAugmentation(OxmFieldsAction.class, matchEntries.build());
        return actionBuilder.build();
    }
}
