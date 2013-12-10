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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.actions.list.ActionBuilder;
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
    public static List<ActionsList> createActionsList(ByteBuf input, int actionsLength) {
        ActionsListBuilder actionsListBuilder = new ActionsListBuilder();
        List<ActionsList> actionsList = new ArrayList<>();
        int length = 0;
        while (length < actionsLength) {
            int currentActionLength = 0;
            switch(input.readUnsignedShort()) {
            case 0: currentActionLength = input.readUnsignedShort(); //outputActionLength
            actionsList.add(createOutputAction(input, actionsListBuilder));
            break;
            case 11: 
                currentActionLength = input.readUnsignedShort();//empty header length
                actionsList.add(createCopyTtlOutAction(input, actionsListBuilder));
                break;

            case 12: 
                currentActionLength = input.readUnsignedShort();//empty header length
                actionsList.add(createCopyTtlInAction(input, actionsListBuilder));
                break;

            case 15: 
                currentActionLength = input.readUnsignedShort();//empty header length
                actionsList.add(createSetMplsTtlAction(input, actionsListBuilder));
                break;

            case 16:                              
                currentActionLength = input.readUnsignedShort();//empty header length
                actionsList.add(createDecMplsTtlOutAction(input, actionsListBuilder));
                break;

            case 17: 
                currentActionLength = input.readUnsignedShort();
                actionsList.add(createPushVlanAction(input, actionsListBuilder));
                break;

            case 18:                              
                currentActionLength = input.readUnsignedShort();//empty header length
                actionsList.add(createPopVlanAction(input, actionsListBuilder));
                break;

            case 19: 
                currentActionLength = input.readUnsignedShort();//8
                actionsList.add(createPushMplsAction(input, actionsListBuilder));
                break;

            case 20: 
                currentActionLength = input.readUnsignedShort();//8
                actionsList.add(createPopMplsAction(input, actionsListBuilder));
                break;

            case 21: 
                currentActionLength = input.readUnsignedShort();
                actionsList.add(createSetQueueAction(input, actionsListBuilder));
                break;

            case 22: 
                currentActionLength = input.readUnsignedShort();//8
                actionsList.add(createGroupAction(input, actionsListBuilder));
                break;

            case 23: 
                currentActionLength = input.readUnsignedShort();//8
                actionsList.add(createSetNwTtlAction(input, actionsListBuilder));
                break;

            case 24:                              
                currentActionLength = input.readUnsignedShort();//empty header length
                actionsList.add(createDecNwTtlAction(input, actionsListBuilder));
                break;

            case 25:
                currentActionLength = input.readUnsignedShort();//8
                actionsList.add(createSetFieldAction(input, actionsListBuilder, currentActionLength));
                break; 
            case 26: 
                currentActionLength = input.readUnsignedShort();//8
                actionsList.add(createPushPbbAction(input, actionsListBuilder));
                break;

            case 27:                              
                currentActionLength = input.readUnsignedShort();//empty header length
                actionsList.add(createPopPbbAction(input, actionsListBuilder));
                break;

            case 0xFFFF: 
                currentActionLength = input.readUnsignedShort();
                actionsList.add(createExperimenterAction(input, actionsListBuilder));
                break;
            default: 
                break;
            }
            length += currentActionLength;
        } 
        return actionsList;
    }
    
    private static ActionsList createEmptyHeader(Class<? extends org.opendaylight.yang.gen.v1.
            urn.opendaylight.openflow.common.types.rev130731.Action> action,
            ByteBuf in, ActionsListBuilder actionsListBuilder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(action);
        in.skipBytes(PADDING_IN_ACTIONS_HEADER);
        actionsListBuilder.setAction(actionBuilder.build());
        return actionsListBuilder.build();
    }
    
    private static ActionsList createCopyTtlInAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        return createEmptyHeader(CopyTtlIn.class, in, actionsListBuilder);
    }

    private static ActionsList createCopyTtlOutAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        return createEmptyHeader(CopyTtlOut.class, in, actionsListBuilder);
    }
    
    private static ActionsList createDecMplsTtlOutAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        return createEmptyHeader(DecMplsTtl.class, in, actionsListBuilder);
    }
    
    private static ActionsList createPopVlanAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        return createEmptyHeader(PopVlan.class, in, actionsListBuilder);
    }
    
    private static ActionsList createDecNwTtlAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        return createEmptyHeader(DecNwTtl.class, in, actionsListBuilder);
    }
    
    private static ActionsList createPopPbbAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        return createEmptyHeader(PopPbb.class, in, actionsListBuilder);
    }
    
    private static ActionsList createOutputAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Output.class);
        PortActionBuilder port = new PortActionBuilder();
        port.setPort(new PortNumber(in.readUnsignedInt()));
        actionBuilder.addAugmentation(PortAction.class, port.build());
        MaxLengthActionBuilder maxLen = new MaxLengthActionBuilder();
        maxLen.setMaxLength(in.readUnsignedShort());
        actionBuilder.addAugmentation(MaxLengthAction.class, maxLen.build());
        actionsListBuilder.setAction(actionBuilder.build());
        in.skipBytes(PADDING_IN_OUTPUT_ACTIONS_HEADER);
        return actionsListBuilder.build();
    }
    
    private static ActionsList createSetMplsTtlAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetMplsTtl.class);
        MplsTtlActionBuilder mplsTtl = new MplsTtlActionBuilder();
        mplsTtl.setMplsTtl(in.readUnsignedByte());
        actionBuilder.addAugmentation(MplsTtlAction.class, mplsTtl.build());
        actionsListBuilder.setAction(actionBuilder.build());
        in.skipBytes(PADDING_IN_SET_MPLS_TTL_ACTIONS_HEADER);
        return actionsListBuilder.build();
    }
    
    private static ActionsList createPushAction(Class<? extends org.opendaylight.yang.gen.
            v1.urn.opendaylight.openflow.common.types.rev130731.Action> action,
            ByteBuf in, ActionsListBuilder actionsListBuilder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(action);
        EthertypeActionBuilder etherType = new EthertypeActionBuilder();
        etherType.setEthertype(new EtherType(in.readUnsignedShort()));
        actionBuilder.addAugmentation(EthertypeAction.class, etherType.build());
        actionsListBuilder.setAction(actionBuilder.build());
        in.skipBytes(PADDING_IN_PUSH_VLAN_ACTIONS_HEADER);
        return actionsListBuilder.build();
    }
    
    private static ActionsList createPushVlanAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        return createPushAction(PushVlan.class, in, actionsListBuilder);
    }
    
    private static ActionsList createPushMplsAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        return createPushAction(PushMpls.class, in, actionsListBuilder);
    }
    
    private static ActionsList createPopMplsAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        return createPushAction(PopMpls.class, in, actionsListBuilder);
    }
    
    private static ActionsList createPushPbbAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        return createPushAction(PushPbb.class, in, actionsListBuilder);
    }
    
    private static ActionsList createSetQueueAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetQueue.class);
        QueueIdActionBuilder queueId = new QueueIdActionBuilder();
        queueId.setQueueId(in.readUnsignedInt());
        actionBuilder.addAugmentation(QueueIdAction.class, queueId.build());
        actionsListBuilder.setAction(actionBuilder.build());
        return actionsListBuilder.build();
    }
    
    private static ActionsList createGroupAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Group.class);
        GroupIdActionBuilder group = new GroupIdActionBuilder();
        group.setGroupId(in.readUnsignedInt());
        actionBuilder.addAugmentation(GroupIdAction.class, group.build());
        actionsListBuilder.setAction(actionBuilder.build());
        return actionsListBuilder.build();
    }
    
    private static ActionsList createExperimenterAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Experimenter.class);
        ExperimenterActionBuilder experimenter = new ExperimenterActionBuilder();
        experimenter.setExperimenter(in.readUnsignedInt());
        actionBuilder.addAugmentation(ExperimenterAction.class, experimenter.build());
        actionsListBuilder.setAction(actionBuilder.build());
        return actionsListBuilder.build();
    }
    
    private static ActionsList createSetNwTtlAction(ByteBuf in, ActionsListBuilder actionsListBuilder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetNwTtl.class);
        NwTtlActionBuilder nwTtl = new NwTtlActionBuilder();
        nwTtl.setNwTtl(in.readUnsignedByte());
        actionBuilder.addAugmentation(NwTtlAction.class, nwTtl.build());
        actionsListBuilder.setAction(actionBuilder.build());
        in.skipBytes(PADDING_IN_NW_TTL_ACTIONS_HEADER);
        return actionsListBuilder.build();
    }
    
    private static ActionsList createSetFieldAction(ByteBuf in, ActionsListBuilder actionsListBuilder, int actionLength) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetField.class);
        OxmFieldsActionBuilder matchEntries = new OxmFieldsActionBuilder();
        matchEntries.setMatchEntries(MatchDeserializer.createMatchEntry(in, actionLength  - ACTION_HEADER_LENGTH));
        actionBuilder.addAugmentation(OxmFieldsAction.class, matchEntries.build());
        actionsListBuilder.setAction(actionBuilder.build());
        return actionsListBuilder.build();
    }
}
