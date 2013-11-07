/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.action.header.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;

/**
 * Class for easy deserialization of actions
 * 
 * @author timotej.kubas
 * @author michal.polkorab
 */
public abstract class ActionsDeserializer {
    final static byte PAD_ACTION_LENGTH = 2;
    private static ActionBuilder actionBuilder = new ActionBuilder();
    private static ActionsListBuilder actionsListBuilder = new ActionsListBuilder();
    private static List<ActionsList> actionsList = new ArrayList<>();
    
    /**
     * @param input input ByteBuf
     * @param bucketsLength length of buckets
     * @return ActionsList
     */
    public static List<ActionsList> createActionsList(ByteBuf input, int bucketsLength) {
        final byte BUCKET_HEADER_LENGTH = 16;
        int bucketsCurrentLength = BUCKET_HEADER_LENGTH;
        int actionsLength = 0;
            
            while (bucketsCurrentLength < bucketsLength) {
                switch(input.readUnsignedShort()) {
                case 0: actionsLength = input.readUnsignedShort(); //outputActionLength
                        actionsList.add(ActionsDeserializer.createOutputAction(input));
                        break;
                case 11: 
                         actionsLength = input.readUnsignedShort();//empty header length
                         actionsList.add(ActionsDeserializer.createCopyTtlOutAction(input));
                         break;
                         
                case 12: 
                         actionsLength = input.readUnsignedShort();//empty header length
                         actionsList.add(ActionsDeserializer.createCopyTtlInAction(input));
                         break;
                
                case 15: 
                         actionsLength = input.readUnsignedShort();//empty header length
                         actionsList.add(ActionsDeserializer.createSetMplsTtlAction(input));
                         break;
                         
                case 16:                              
                         actionsLength = input.readUnsignedShort();//empty header length
                         actionsList.add(ActionsDeserializer.createDecMplsTtlOutAction(input));
                         break;
                         
                case 17: 
                         actionsLength = input.readUnsignedShort();
                         actionsList.add(ActionsDeserializer.createPushVlanAction(input));
                         break;
                         
                case 18:                              
                         actionsLength = input.readUnsignedShort();//empty header length
                         actionsList.add(ActionsDeserializer.createPopVlanAction(input));
                         break;
                         
                case 19: 
                         actionsLength = input.readUnsignedShort();//8
                         actionsList.add(ActionsDeserializer.createPushMplsAction(input));
                         break;
                         
                case 20: 
                         actionsLength = input.readUnsignedShort();//8
                         actionsList.add(ActionsDeserializer.createPopMplsAction(input));
                         break;
                         
                case 21: 
                         actionsLength = input.readUnsignedShort();
                         actionsList.add(ActionsDeserializer.createSetQueueAction(input));
                         break;
                         
                case 22: 
                        actionsLength = input.readUnsignedShort();//8
                        actionsList.add(ActionsDeserializer.createGroupAction(input));
                        break;
                        
                case 23: 
                         actionsLength = input.readUnsignedShort();//8
                         actionsList.add(ActionsDeserializer.createSetNwTtlAction(input));
                         break;
                        
                case 24:                              
                        actionsLength = input.readUnsignedShort();//empty header length
                        actionsList.add(ActionsDeserializer.createDecNwTtlAction(input));
                        break;
                        
                case 25:
                        actionsLength = input.readUnsignedShort();//8
                        //TODO field
                        actionsList.add(ActionsDeserializer.createSetFieldAction(input, actionsLength));
                        break; 
                case 26: 
                         actionsLength = input.readUnsignedShort();//8
                         actionsList.add(ActionsDeserializer.createPushPbbAction(input));
                         break;
                         
                case 27:                              
                        actionsLength = input.readUnsignedShort();//empty header length
                        actionsList.add(ActionsDeserializer.createPopPbbAction(input));
                        break;
                        
                case 0xFFFF: 
                        actionsLength = input.readUnsignedShort();
                        actionsList.add(ActionsDeserializer.createExperimenterAction(input));
                        break;
                default: 
                         break;
                }
                bucketsCurrentLength += actionsLength;
            } 
        return actionsList;
    }
    
    
    /**
     * @param action input action that contains empty header
     * @param in input ByteBuf 
     * @return Action
     */
    private static ActionsList createEmptyHeader(Class<? extends org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.Action> action, ByteBuf in) {
        final byte PADDING_IN_ACTIONS_HEADER = 4;
        
        actionBuilder.setType(action);
        in.skipBytes(PADDING_IN_ACTIONS_HEADER);
        actionsListBuilder.setAction(actionBuilder.build());
        return actionsListBuilder.build();
    }
    
    /**
     * @param in input ByteBuf
     * @return Action
     */
    public static ActionsList createCopyTtlInAction(ByteBuf in) {
        return createEmptyHeader(CopyTtlIn.class, in);
    }
    
    /**
     * @param in input ByteBuf
     * @return Action
     */
    public static ActionsList createCopyTtlOutAction(ByteBuf in) {
        return createEmptyHeader(CopyTtlOut.class, in);
    }
    
    /**
     * @param in input ByteBuf
     * @return Action
     */
    public static ActionsList createDecMplsTtlOutAction(ByteBuf in) {
        return createEmptyHeader(DecMplsTtl.class, in);
    }
    
    /**
     * @param in input ByteBuf
     * @return Action
     */
    public static ActionsList createPopVlanAction(ByteBuf in) {
        return createEmptyHeader(PopVlan.class, in);
    }
    
    /**
     * @param in input ByteBuf
     * @return Action
     */
    public static ActionsList createDecNwTtlAction(ByteBuf in) {
        return createEmptyHeader(DecNwTtl.class, in);
    }
    
    /**
     * @param in input ByteBuf
     * @return Action
     */
    public static ActionsList createPopPbbAction(ByteBuf in) {
        return createEmptyHeader(PopPbb.class, in);
    }
    
    /**
     * @param in input ByteBuf
     * @return ActionList
     */
    public static ActionsList createOutputAction(ByteBuf in) {
        final byte PADDING_IN_OUTPUT_ACTIONS_HEADER = 6;
        
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
    
    /**
     * @param in input ByteBuf
     * @return ActionList
     */
    public static ActionsList createSetMplsTtlAction(ByteBuf in) {
        final byte PADDING_IN_SET_MPLS_TTL_ACTIONS_HEADER = 3;
        
        actionBuilder.setType(SetMplsTtl.class);
        MplsTtlActionBuilder mplsTtl = new MplsTtlActionBuilder();
        mplsTtl.setMplsTtl(in.readUnsignedByte());
        actionBuilder.addAugmentation(MplsTtlAction.class, mplsTtl.build());
        actionsListBuilder.setAction(actionBuilder.build());
        in.skipBytes(PADDING_IN_SET_MPLS_TTL_ACTIONS_HEADER);
        
        return actionsListBuilder.build();
    }
    
    /**
     * @param action input action that contains push
     * @param in input ByteBuf
     * @return ActionList
     */
    private static ActionsList createPushAction(Class<? extends org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.Action> action, ByteBuf in) {
        final byte PADDING_IN_PUSH_VLAN_ACTIONS_HEADER = 2;
        
        actionBuilder.setType(action);
        EthertypeActionBuilder etherType = new EthertypeActionBuilder();
        etherType.setEthertype(new EtherType(in.readUnsignedShort()));
        actionBuilder.addAugmentation(EthertypeAction.class, etherType.build());
        actionsListBuilder.setAction(actionBuilder.build());
        in.skipBytes(PADDING_IN_PUSH_VLAN_ACTIONS_HEADER);
        
        return actionsListBuilder.build();
    }
    
    /**
     * @param in input ByteBuf
     * @return Action
     */
    public static ActionsList createPushVlanAction(ByteBuf in) {
        return createPushAction(PushVlan.class, in);
    }
    
    /**
     * @param in input ByteBuf
     * @return Action
     */
    public static ActionsList createPushMplsAction(ByteBuf in) {
        return createPushAction(PushMpls.class, in);
    }
    
    /**
     * @param in input ByteBuf
     * @return Action
     */
    public static ActionsList createPopMplsAction(ByteBuf in) {
        return createPushAction(PopMpls.class, in);
    }
    
    /**
     * @param in input ByteBuf
     * @return Action
     */
    public static ActionsList createPushPbbAction(ByteBuf in) {
        return createPushAction(PushPbb.class, in);
    }
    
    /**
     * @param in input ByteBuf
     * @return ActionList
     */
    public static ActionsList createSetQueueAction(ByteBuf in) {
        actionBuilder.setType(SetQueue.class);
        QueueIdActionBuilder queueId = new QueueIdActionBuilder();
        queueId.setQueueId(in.readUnsignedInt());
        actionBuilder.addAugmentation(QueueIdAction.class, queueId.build());
        actionsListBuilder.setAction(actionBuilder.build());
        
        return actionsListBuilder.build();
    }
    
    /**
     * @param in input ByteBuf
     * @return ActionList
     */
    public static ActionsList createGroupAction(ByteBuf in) {
        actionBuilder.setType(Group.class);
        GroupIdActionBuilder group = new GroupIdActionBuilder();
        group.setGroupId(in.readUnsignedInt());
        actionBuilder.addAugmentation(GroupIdAction.class, group.build());
        actionsListBuilder.setAction(actionBuilder.build());
        
        return actionsListBuilder.build();
    }
    
    /**
     * @param in input ByteBuf
     * @return ActionList
     */
    public static ActionsList createExperimenterAction(ByteBuf in) {
        actionBuilder.setType(Experimenter.class);
        ExperimenterActionBuilder experimenter = new ExperimenterActionBuilder();
        experimenter.setExperimenter(in.readUnsignedInt());
        actionBuilder.addAugmentation(ExperimenterAction.class, experimenter.build());
        actionsListBuilder.setAction(actionBuilder.build());
        
        return actionsListBuilder.build();
    }
    
    /**
     * @param in input ByteBuf
     * @return ActionList
     */
    public static ActionsList createSetNwTtlAction(ByteBuf in) {
        final byte PADDING_IN_NW_TTL_ACTIONS_HEADER = 3;
        
        actionBuilder.setType(SetNwTtl.class);
        NwTtlActionBuilder nwTtl = new NwTtlActionBuilder();
        nwTtl.setNwTtl(in.readUnsignedByte());
        actionBuilder.addAugmentation(NwTtlAction.class, nwTtl.build());
        actionsListBuilder.setAction(actionBuilder.build());
        in.skipBytes(PADDING_IN_NW_TTL_ACTIONS_HEADER);
        
        return actionsListBuilder.build();
    }
    
    /**
     * @param in input ByteBuf
     * @param actionLength length of action
     * @return ActionList
     */
    public static ActionsList createSetFieldAction(ByteBuf in, int actionLength) {
        actionBuilder.setType(SetField.class);
        OxmFieldsActionBuilder matchEntries = new OxmFieldsActionBuilder();
        matchEntries.setMatchEntries(MatchEntriesDeserializer.createMatchEntries(in, actionLength - 4));
        actionBuilder.addAugmentation(OxmFieldsAction.class, matchEntries.build());
        actionsListBuilder.setAction(actionBuilder.build());
        
        return actionsListBuilder.build();
    }
}
