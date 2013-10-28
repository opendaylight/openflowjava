/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action1Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action2;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action2Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action3;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action3Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action5;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action5Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action6;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action6Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action7;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action7Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action8;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action8Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action9;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Action9Builder;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.AnyPortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.bucket.ActionsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.bucket.ActionsListBuilder;

/**
 * Class for easy creation of actions
 * 
 * @author timotej.kubas
 * @author michal.polkorab
 */
public abstract class ActionCreator {
    final static byte PAD_ACTION_LENGTH = 2;
    private static ActionBuilder actionBuilder = new ActionBuilder();
    private static ActionsListBuilder actionsListBuilder = new ActionsListBuilder();
    private static List<ActionsList> actionsList = new ArrayList<>();
/**
 * @param inp input ByteBuf
 * @param bucketsLength length of buckets
 * @return ActionsList
 */
    public static List<ActionsList> createActionsList(ByteBuf inp, int bucketsLength) {
        final byte BUCKET_HEADER_LENGTH = 16;
        int bucketsCurrentLength = BUCKET_HEADER_LENGTH;
        int actionsLength = 0;
            
            while (bucketsCurrentLength < bucketsLength) {
                switch(inp.readUnsignedShort()) {
                case 0: actionsLength = inp.readUnsignedShort(); //outputActionLength
                        actionsList.add(ActionCreator.createOutputAction(inp));
                        bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                        break;
                case 11: 
                         actionsLength = inp.readUnsignedShort();//empty header length
                         actionsList.add(ActionCreator.createCopyTtlOutAction(inp));
                         bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                         break;
                         
                case 12: 
                         actionsLength = inp.readUnsignedShort();//empty header length
                         actionsList.add(ActionCreator.createCopyTtlInAction(inp));
                         bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                         break;
                
                case 15: 
                         actionsLength = inp.readUnsignedShort();//empty header length
                         actionsList.add(ActionCreator.createSetMplsTtlAction(inp));
                         bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                         break;
                         
                case 16:                              
                         actionsLength = inp.readUnsignedShort();//empty header length
                         actionsList.add(ActionCreator.createDecMplsTtlOutAction(inp));
                         bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                         break;
                         
                case 17: 
                         actionsLength = inp.readUnsignedShort();
                         actionsList.add(ActionCreator.createPushVlanAction(inp));
                         bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                         break;
                         
                case 18:                              
                         actionsLength = inp.readUnsignedShort();//empty header length
                         actionsList.add(ActionCreator.createPopVlanAction(inp));
                         bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                         break;
                         
                case 19: 
                         actionsLength = inp.readUnsignedShort();//8
                         actionsList.add(ActionCreator.createPushMplsAction(inp));
                         bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                         break;
                         
                case 20: 
                         actionsLength = inp.readUnsignedShort();//8
                         actionsList.add(ActionCreator.createPopMplsAction(inp));
                         bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                         break;
                         
                case 21: 
                         actionsLength = inp.readUnsignedShort();
                         actionsList.add(ActionCreator.createSetQueueAction(inp));
                         bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                         break;
                         
                case 22: 
                        actionsLength = inp.readUnsignedShort();//8
                        actionsList.add(ActionCreator.createGroupAction(inp));
                        bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                        break;
                        
                case 23: 
                         actionsLength = inp.readUnsignedShort();//8
                         actionsList.add(ActionCreator.createSetNwTtlAction(inp));
                         bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                         break;
                        
                case 24:                              
                        actionsLength = inp.readUnsignedShort();//empty header length
                        actionsList.add(ActionCreator.createDecNwTtlAction(inp));
                        bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                        break;
                        
                case 25:
                        actionsLength = inp.readUnsignedShort();//8
                        //TODO field
                        actionsList.add(ActionCreator.createSetFieldAction(inp, actionsLength));
                        bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                        break; 
                case 26: 
                         actionsLength = inp.readUnsignedShort();//8
                         actionsList.add(ActionCreator.createPushPbbAction(inp));
                         bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                         break;
                         
                case 27:                              
                        actionsLength = inp.readUnsignedShort();//empty header length
                        actionsList.add(ActionCreator.createPopPbbAction(inp));
                        bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                        break;
                        
                case 0xFFFF: 
                        actionsLength = inp.readUnsignedShort();
                        actionsList.add(ActionCreator.createExperimenterAction(inp));
                        bucketsCurrentLength = bucketsCurrentLength + actionsLength;
                        break;
                default: 
                         break;
                }
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
        Action8Builder port = new Action8Builder();
        port.setPort(new AnyPortNumber(new PortNumber(in.readUnsignedInt())));
        actionBuilder.addAugmentation(Action8.class, port.build());
        Action7Builder maxLen = new Action7Builder();
        maxLen.setMaxLength(in.readUnsignedShort());
        actionBuilder.addAugmentation(Action7.class, maxLen.build());
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
        Action2Builder mplsTtl = new Action2Builder();
        mplsTtl.setMplsTtl(in.readUnsignedByte());
        actionBuilder.addAugmentation(Action2.class, mplsTtl.build());
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
        Action6Builder etherType = new Action6Builder();
        etherType.setEthertype(new EtherType(in.readUnsignedShort()));
        actionBuilder.addAugmentation(Action6.class, etherType.build());
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
        Action9Builder queueId = new Action9Builder();
        queueId.setQueueId(in.readUnsignedInt());
        actionBuilder.addAugmentation(Action9.class, queueId.build());
        actionsListBuilder.setAction(actionBuilder.build());
        
        return actionsListBuilder.build();
    }
    
    /**
     * @param in input ByteBuf
     * @return ActionList
     */
    public static ActionsList createGroupAction(ByteBuf in) {
        actionBuilder.setType(Group.class);
        Action1Builder group = new Action1Builder();
        group.setGroupId(in.readUnsignedInt());
        actionBuilder.addAugmentation(Action1.class, group.build());
        actionsListBuilder.setAction(actionBuilder.build());
        
        return actionsListBuilder.build();
    }
    
    /**
     * @param in input ByteBuf
     * @return ActionList
     */
    public static ActionsList createExperimenterAction(ByteBuf in) {
        actionBuilder.setType(Experimenter.class);
        Action3Builder experimenter = new Action3Builder();
        experimenter.setExperimenter(in.readUnsignedInt());
        actionBuilder.addAugmentation(Action3.class, experimenter.build());
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
        Action4Builder nwTtl = new Action4Builder();
        nwTtl.setNwTtl(in.readUnsignedByte());
        actionBuilder.addAugmentation(Action4.class, nwTtl.build());
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
        Action5Builder matchEntries = new Action5Builder();
        matchEntries.setMatchEntries(MatchEntriesCreator.createMatchEntry(in, actionLength - 4));
        actionBuilder.addAugmentation(Action5.class, matchEntries.build());
        actionsListBuilder.setAction(actionBuilder.build());
        
        return actionsListBuilder.build();
    }
}
