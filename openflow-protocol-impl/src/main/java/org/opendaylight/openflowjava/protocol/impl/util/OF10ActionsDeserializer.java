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

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DlAddressAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DlAddressActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IpAddressAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IpAddressActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTosAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTosActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanPcpAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanPcpActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Enqueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yangtools.yang.binding.Augmentation;

import com.google.common.base.Joiner;

/**
 * Deserializes ofp_action (OpenFlow v1.0) structures
 * @author michal.polkorab
 */
public abstract class OF10ActionsDeserializer {
    
    private static final byte PADDING_IN_SET_VLAN_VID_ACTION = 2;
    private static final byte PADDING_IN_SET_VLAN_PCP_ACTION = 3;
    private static final byte PADDING_IN_STRIP_VLAN_ACTION = 4;
    private static final byte PADDING_IN_SET_DL_ACTION = 6;
    private static final byte PADDING_IN_NW_TOS_ACTION = 3;
    private static final byte PADDING_IN_TP_ACTION = 2;
    private static final byte PADDING_IN_ENQUEUE_ACTION = 6;

    /**
     * Creates list of actions (OpenFlow v1.0) from ofp_action structures
     * @param input input ByteBuf
     * @return ActionsList list of actions
     */
    public static List<Action> createActionsList(ByteBuf input) {
        List<Action> actions = new ArrayList<>();
        while (input.readableBytes() > 0) {
            int type = input.readUnsignedShort();
            input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
            actions.add(decodeAction(input, type));
        } 
        return actions;
    }

    /**
     * Creates list of actions (OpenFlow v1.0) from ofp_action structures, which are not the last structure
     * in message
     * @param input input ByteBuf
     * @param actionsLength length of actions
     * @return ActionsList list of actions
     */
    public static List<Action> createActionsList(ByteBuf input, int actionsLength) {
        List<Action> actions = new ArrayList<>();
        int currentLength = 0;
        while (currentLength < actionsLength) {
            int type = input.readUnsignedShort();
            currentLength += input.readUnsignedShort();
            actions.add(decodeAction(input, type));
        } 
        return actions;
    }

    private static Action decodeAction(ByteBuf input, int type) {
        ActionBuilder actionsBuilder = new ActionBuilder();
        Action actionsList = null;
        switch(type) {
        case 0:
            actionsList = createOutputAction(input, actionsBuilder);
            break;
        case 1:
            actionsList = createSetVlanVidAction(input, actionsBuilder);
            break;
        case 2:
            actionsList = createVlanPcpAction(input, actionsBuilder);
            break;
        case 3:
            actionsList = createStripVlanAction(input, actionsBuilder);
            break;
        case 4:
            actionsList = createSetDlSrcAction(input, actionsBuilder);
            break;
        case 5:
            actionsList = createSetDlDstAction(input, actionsBuilder);
            break;
        case 6:
            actionsList = createSetNwSrcAction(input, actionsBuilder);
            break;
        case 7:
            actionsList = createSetNwDstAction(input, actionsBuilder);
            break;
        case 8:
            actionsList = createSetNwTosAction(input, actionsBuilder);
            break;
        case 9:
            actionsList = createSetTpSrcAction(input, actionsBuilder);
            break;
        case 10:
            actionsList = createSetTpDstAction(input, actionsBuilder);
            break;
        case 11:
            actionsList = createEnqueueAction(input, actionsBuilder);
            break;
        case 0xFFFF:
            actionsList = createExperimenterAction(input, actionsBuilder);
            break;
        default:
            break;
        }
        return actionsList;
    }

    /**
     * @param in input ByteBuf
     * @param builder 
     * @return ActionList
     */
    public static Action createOutputAction(ByteBuf in, ActionBuilder builder) {
        builder.setType(Output.class);
        createPortAugmentation(in, builder);
        MaxLengthActionBuilder maxLen = new MaxLengthActionBuilder();
        maxLen.setMaxLength(in.readUnsignedShort());
        builder.addAugmentation(MaxLengthAction.class, maxLen.build());
        return builder.build();
    }

    private static Action createSetVlanVidAction(ByteBuf input, ActionBuilder builder) {
        builder.setType(SetVlanVid.class);
        VlanVidActionBuilder vlanBuilder = new VlanVidActionBuilder();
        vlanBuilder.setVlanVid(input.readUnsignedShort());
        input.skipBytes(PADDING_IN_SET_VLAN_VID_ACTION);
        builder.addAugmentation(VlanVidAction.class, vlanBuilder.build());
        return builder.build();
    }

    private static Action createVlanPcpAction(ByteBuf input, ActionBuilder builder) {
        builder.setType(SetVlanPcp.class);
        VlanPcpActionBuilder vlanBuilder = new VlanPcpActionBuilder();
        vlanBuilder.setVlanPcp(input.readUnsignedByte());
        input.skipBytes(PADDING_IN_SET_VLAN_PCP_ACTION);
        builder.addAugmentation(VlanPcpAction.class, vlanBuilder.build());
        return builder.build();
    }

    private static Action createStripVlanAction(ByteBuf input, ActionBuilder builder) {
        builder.setType(StripVlan.class);
        input.skipBytes(PADDING_IN_STRIP_VLAN_ACTION);
        return builder.build();
    }

    private static Action createSetDlSrcAction(ByteBuf input, ActionBuilder builder) {
        builder.setType(SetDlSrc.class);
        builder.addAugmentation(DlAddressAction.class, createDlAugmentationAndPad(input));
        return builder.build();
    }

    private static Action createSetDlDstAction(ByteBuf input, ActionBuilder builder) {
        builder.setType(SetDlDst.class);
        builder.addAugmentation(DlAddressAction.class, createDlAugmentationAndPad(input));
        return builder.build();
    }
    
    private static DlAddressAction createDlAugmentationAndPad(ByteBuf input) {
        DlAddressActionBuilder dlBuilder = new DlAddressActionBuilder();
        byte[] address = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
        input.readBytes(address);
        dlBuilder.setDlAddress(new MacAddress(ByteBufUtils.macAddressToString(address)));
        input.skipBytes(PADDING_IN_SET_DL_ACTION);
        return dlBuilder.build();
    }

    private static Action createSetNwSrcAction(ByteBuf input, ActionBuilder builder) {
        builder.setType(SetNwSrc.class);
        builder.addAugmentation(IpAddressAction.class, createNwAddressAugmentationAndPad(input));
        return builder.build();
    }

    private static Action createSetNwDstAction(ByteBuf input, ActionBuilder builder) {
        builder.setType(SetNwDst.class);
        builder.addAugmentation(IpAddressAction.class, createNwAddressAugmentationAndPad(input));
        return builder.build();
    }
    
    private static Augmentation<Action> createNwAddressAugmentationAndPad(ByteBuf input) {
        IpAddressActionBuilder ipBuilder = new IpAddressActionBuilder();
        List<String> groups = new ArrayList<>();
        for (int i = 0; i < EncodeConstants.GROUPS_IN_IPV4_ADDRESS; i++) {
            groups.add(Short.toString(input.readUnsignedByte()));
        }
        Joiner joiner = Joiner.on(".");
        ipBuilder.setIpAddress(new Ipv4Address(joiner.join(groups)));
        return ipBuilder.build();
    }

    private static Action createSetNwTosAction(ByteBuf input, ActionBuilder builder) {
        builder.setType(SetNwTos.class);
        NwTosActionBuilder tosBuilder = new NwTosActionBuilder();
        tosBuilder.setNwTos(input.readUnsignedByte());
        builder.addAugmentation(NwTosAction.class, tosBuilder.build());
        input.skipBytes(PADDING_IN_NW_TOS_ACTION);
        return builder.build();
    }

    private static Action createSetTpSrcAction(ByteBuf input, ActionBuilder builder) {
        builder.setType(SetTpSrc.class);
        createPortAugmentation(input, builder);
        input.skipBytes(PADDING_IN_TP_ACTION);
        return builder.build();
    }

    private static Action createSetTpDstAction(ByteBuf input, ActionBuilder builder) {
        builder.setType(SetTpDst.class);
        createPortAugmentation(input, builder);
        input.skipBytes(PADDING_IN_TP_ACTION);
        return builder.build();
    }
    
    private static void createPortAugmentation(ByteBuf input, ActionBuilder actionBuilder) {
        PortActionBuilder portBuilder = new PortActionBuilder();
        portBuilder.setPort(new PortNumber((long) input.readUnsignedShort()));
        actionBuilder.addAugmentation(PortAction.class, portBuilder.build());
    }

    private static Action createEnqueueAction(ByteBuf input, ActionBuilder builder) {
        builder.setType(Enqueue.class);
        createPortAugmentation(input, builder);
        input.skipBytes(PADDING_IN_ENQUEUE_ACTION);
        QueueIdActionBuilder queueBuilder = new QueueIdActionBuilder();
        queueBuilder.setQueueId(input.readUnsignedInt());
        builder.addAugmentation(QueueIdAction.class, queueBuilder.build());
        return builder.build();
    }

    private static Action createExperimenterAction(ByteBuf input, ActionBuilder builder) {
        builder.setType(Experimenter.class);
        ExperimenterActionBuilder expBuilder = new ExperimenterActionBuilder();
        expBuilder.setExperimenter(input.readUnsignedInt());
        builder.addAugmentation(ExperimenterAction.class, expBuilder.build());
        return builder.build();
    }

}
