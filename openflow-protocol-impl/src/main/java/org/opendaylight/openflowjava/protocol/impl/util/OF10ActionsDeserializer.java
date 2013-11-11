/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DlAddressAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DlAddressActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IpAddressActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTosAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTosActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanPcpActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Enqueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Output;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetDlDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetDlSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwTos;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetTpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetTpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetVlanPcp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetVlanVid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.StripVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.actions.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.actions.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yangtools.yang.binding.Augmentation;

import com.google.common.base.Joiner;

/**
 * @author michal.polkorab
 *
 */
public class OF10ActionsDeserializer {

    /**
     * @param input input ByteBuf
     * @return ActionsList
     */
    public static List<ActionsList> createActionsList(ByteBuf input) {
        List<ActionsList> actions = new ArrayList<>();
        while (input.readableBytes() > 0) {
            ActionsListBuilder actionsBuilder = new ActionsListBuilder();
            int type = input.readUnsignedShort();
            input.skipBytes(Short.SIZE / Byte.SIZE);
            switch(type) {
            case 0:  
                actions.add(createOutputAction(input, actionsBuilder));
                break;
            case 1: 
                actions.add(createSetVlanVidAction(input, actionsBuilder));
                break;
            case 2: 
                actions.add(createVlanPcpAction(input, actionsBuilder));
                break;
            case 3: 
                actions.add(createStripVlanAction(input, actionsBuilder));
                break;
            case 4:                              
                actions.add(createSetDlSrcAction(input, actionsBuilder));
                break;
            case 5: 
                actions.add(createSetDlDstAction(input, actionsBuilder));
                break;
            case 6:                              
                actions.add(createSetNwSrcAction(input, actionsBuilder));
                break;
            case 7: 
                actions.add(createSetNwDstAction(input, actionsBuilder));
                break;
            case 8: 
                actions.add(createSetNwTosAction(input, actionsBuilder));
                break;
            case 9: 
                actions.add(createSetTpSrcAction(input, actionsBuilder));
                break;
            case 10: 
                actions.add(createSetTpDstAction(input, actionsBuilder));
                break;
            case 11: 
                actions.add(createEnqueueAction(input, actionsBuilder));
                break;
            case 0xFFFF:
                actions.add(createExperimenterAction(input, actionsBuilder));
                break;
            default: 
                break;
            }
        } 
        return actions;
    }

    /**
     * @param in input ByteBuf
     * @param builder 
     * @return ActionList
     */
    public static ActionsList createOutputAction(ByteBuf in, ActionsListBuilder builder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Output.class);
        createPortAugmentation(in, actionBuilder);
        MaxLengthActionBuilder maxLen = new MaxLengthActionBuilder();
        maxLen.setMaxLength(in.readUnsignedShort());
        actionBuilder.addAugmentation(MaxLengthAction.class, maxLen.build());
        builder.setAction(actionBuilder.build());
        return builder.build();
    }

    private static ActionsList createSetVlanVidAction(ByteBuf input, ActionsListBuilder builder) {
        final byte PADDING_IN_SET_VLAN_VID_ACTION = 2;
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetVlanVid.class);
        VlanVidActionBuilder vlanBuilder = new VlanVidActionBuilder();
        vlanBuilder.setVlanVid(input.readUnsignedShort());
        input.skipBytes(PADDING_IN_SET_VLAN_VID_ACTION);
        actionBuilder.addAugmentation(VlanVidAction.class, vlanBuilder.build());
        builder.setAction(actionBuilder.build());
        return builder.build();
    }

    private static ActionsList createVlanPcpAction(ByteBuf input, ActionsListBuilder builder) {
        final byte PADDING_IN_SET_VLAN_PCP_ACTION = 3;
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetVlanPcp.class);
        VlanPcpActionBuilder vlanBuilder = new VlanPcpActionBuilder();
        vlanBuilder.setVlanPcp(input.readUnsignedByte());
        input.skipBytes(PADDING_IN_SET_VLAN_PCP_ACTION);
        actionBuilder.addAugmentation(VlanVidAction.class, vlanBuilder.build());
        builder.setAction(actionBuilder.build());
        return builder.build();
    }

    private static ActionsList createStripVlanAction(ByteBuf input, ActionsListBuilder builder) {
        final byte PADDING_IN_STRIP_VLAN_ACTION = 4;
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(StripVlan.class);
        input.skipBytes(PADDING_IN_STRIP_VLAN_ACTION);
        builder.setAction(actionBuilder.build());
        return builder.build();
    }

    private static ActionsList createSetDlSrcAction(ByteBuf input, ActionsListBuilder builder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetDlSrc.class);
        actionBuilder.addAugmentation(DlAddressAction.class, createDlAugmentationAndPad(input));
        builder.setAction(actionBuilder.build());
        return builder.build();
    }

    private static ActionsList createSetDlDstAction(ByteBuf input, ActionsListBuilder builder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetDlDst.class);
        actionBuilder.addAugmentation(DlAddressAction.class, createDlAugmentationAndPad(input));
        builder.setAction(actionBuilder.build());
        return builder.build();
    }
    
    private static DlAddressAction createDlAugmentationAndPad(ByteBuf input) {
        final byte MAC_ADDRESS_LENGTH = 6;
        final byte PADDING_IN_SET_DL_ACTION = 6;
        DlAddressActionBuilder dlBuilder = new DlAddressActionBuilder();
        short mac = 0;
        StringBuffer macAddress = new StringBuffer();
        for(int i = 0; i < MAC_ADDRESS_LENGTH; i++){
            mac = input.readUnsignedByte();
            macAddress.append(String.format("%02X", mac));
        }
        dlBuilder.setDlAddress(new MacAddress(macAddress.toString()));
        input.skipBytes(PADDING_IN_SET_DL_ACTION);
        return dlBuilder.build();
    }

    private static ActionsList createSetNwSrcAction(ByteBuf input, ActionsListBuilder builder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetNwSrc.class);
        actionBuilder.addAugmentation(DlAddressAction.class, createNwAddressAugmentationAndPad(input));
        builder.setAction(actionBuilder.build());
        return builder.build();
    }

    private static ActionsList createSetNwDstAction(ByteBuf input, ActionsListBuilder builder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetNwDst.class);
        actionBuilder.addAugmentation(DlAddressAction.class, createNwAddressAugmentationAndPad(input));
        builder.setAction(actionBuilder.build());
        return builder.build();
    }
    
    private static Augmentation<Action> createNwAddressAugmentationAndPad(ByteBuf input) {
        final byte GROUPS_IN_IPV4_ADDRESS = 4;
        IpAddressActionBuilder ipBuilder = new IpAddressActionBuilder();
        List<String> groups = new ArrayList<>();
        for (int i = 0; i < GROUPS_IN_IPV4_ADDRESS; i++) {
            groups.add(Short.toString(input.readUnsignedByte()));
        }
        Joiner joiner = Joiner.on(".");
        ipBuilder.setIpAddress(new Ipv4Address(joiner.join(groups)));
        return ipBuilder.build();
    }

    private static ActionsList createSetNwTosAction(ByteBuf input, ActionsListBuilder builder) {
        final byte PADDING_IN_NW_TOS_ACTION = 3;
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetNwTos.class);
        NwTosActionBuilder tosBuilder = new NwTosActionBuilder();
        tosBuilder.setNwTos(input.readUnsignedByte());
        actionBuilder.addAugmentation(NwTosAction.class, tosBuilder.build());
        input.skipBytes(PADDING_IN_NW_TOS_ACTION);
        builder.setAction(actionBuilder.build());
        return builder.build();
    }

    private static ActionsList createSetTpSrcAction(ByteBuf input, ActionsListBuilder builder) {
        final byte PADDING_IN_TP_ACTION = 2;
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetTpSrc.class);
        createPortAugmentation(input, actionBuilder);
        input.skipBytes(PADDING_IN_TP_ACTION);
        builder.setAction(actionBuilder.build());
        return builder.build();
    }

    private static ActionsList createSetTpDstAction(ByteBuf input, ActionsListBuilder builder) {
        final byte PADDING_IN_TP_ACTION = 2;
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetTpDst.class);
        createPortAugmentation(input, actionBuilder);
        input.skipBytes(PADDING_IN_TP_ACTION);
        builder.setAction(actionBuilder.build());
        return builder.build();
    }
    
    private static void createPortAugmentation(ByteBuf input, ActionBuilder actionBuilder) {
        PortActionBuilder portBuilder = new PortActionBuilder();
        portBuilder.setPort(new PortNumber(new Long(input.readUnsignedShort())));
        actionBuilder.addAugmentation(PortAction.class, portBuilder.build());
    }

    private static ActionsList createEnqueueAction(ByteBuf input, ActionsListBuilder builder) {
        final byte PADDING_IN_ENQUEUE_ACTION = 6;
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Enqueue.class);
        createPortAugmentation(input, actionBuilder);
        input.skipBytes(PADDING_IN_ENQUEUE_ACTION);
        QueueIdActionBuilder queueBuilder = new QueueIdActionBuilder();
        queueBuilder.setQueueId(input.readUnsignedInt());
        actionBuilder.addAugmentation(QueueIdAction.class, queueBuilder.build());
        builder.setAction(actionBuilder.build());
        return builder.build();
    }

    private static ActionsList createExperimenterAction(ByteBuf input, ActionsListBuilder builder) {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Enqueue.class);
        ExperimenterActionBuilder expBuilder = new ExperimenterActionBuilder();
        expBuilder.setExperimenter(input.readUnsignedInt());
        actionBuilder.addAugmentation(QueueIdAction.class, expBuilder.build());
        builder.setAction(actionBuilder.build());
        return builder.build();
    }
    
    
}
