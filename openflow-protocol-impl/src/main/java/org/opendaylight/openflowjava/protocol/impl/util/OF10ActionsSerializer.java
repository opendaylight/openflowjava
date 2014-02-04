/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DlAddressAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IpAddressAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTosAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanPcpAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidAction;
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

/**
 * Serializes ofp_action (OpenFlow v1.0) structures
 * @author michal.polkorab
 */
public abstract class OF10ActionsSerializer {
    
    private static final byte OUTPUT_CODE = 0;
    private static final byte SET_VLAN_VID_CODE = 1;
    private static final byte SET_VLAN_PCP_CODE = 2;
    private static final byte STRIP_VLAN_CODE = 3;
    private static final byte SET_DL_SRC_CODE = 4;
    private static final byte SET_DL_DST_CODE = 5;
    private static final byte SET_NW_SRC_CODE = 6;
    private static final byte SET_NW_DST_CODE = 7;
    private static final byte SET_NW_TOS_CODE = 8;
    private static final byte SET_TP_SRC_CODE = 9;
    private static final byte SET_TP_DST_CODE = 10;
    private static final byte ENQUEUE_CODE = 11;
    private static final int EXPERIMENTER_CODE = 65535; // 0xFFFF
    private static final byte GENERIC_ACTION_LENGTH = 8;
    private static final byte PADDING_IN_GENERIC_ACTION = 4;
    private static final byte OUTPUT_LENGTH = 8;
    private static final byte SET_VLAN_VID_LENGTH = 8;
    private static final byte PADDING_IN_SET_VLAN_VID_ACTION = 2;
    private static final byte SET_VLAN_PCP_LENGTH = 8;
    private static final byte PADDING_IN_SET_VLAN_PCP_ACTION = 3;
    private static final byte DL_ADDRESS_ACTION_LENGTH = 16;
    private static final byte PADDING_IN_DL_ADDRESS_ACTION = 6;
    private static final byte SET_NW_TOS_LENGTH = 8;
    private static final byte PADDING_IN_SET_NW_TOS_ACTION = 3;
    private static final byte IP_ADDRESS_ACTION_LENGTH = 8;
    private static final byte TP_PORT_ACTION_LENGTH = 8;
    private static final byte PADDING_IN_TP_PORT_ACTION = 2;
    private static final byte ENQUEUE_LENGTH = 16;
    private static final byte PADDING_IN_ENQUEUE_ACTION = 6;
    private static final byte EXPERIMENTER_LENGTH = 8;

    
    /**
     * Encodes ofp_action (OpenFlow v1.0) structures
     * @param out output ByteBuf that actions will be written into
     * @param actions actions to be encoded
     */
    public static void encodeActionsV10(ByteBuf out, List<Action> actions) {
        if (actions == null) {
            return;
        }
        for (Action action : actions) {
            if (action.getType().equals(Output.class)) {
                encodeOutputAction(action, out);
            } else if (action.getType().equals(SetVlanVid.class)) {
                encodeSetVlanVidAction(action, out);
            } else if (action.getType().equals(SetVlanPcp.class)) {
                encodeSetVlanPcpAction(action, out);
            } else if (action.getType().equals(StripVlan.class)) {
                encodeGenericAction(STRIP_VLAN_CODE, out);
            } else if (action.getType().equals(SetDlSrc.class)) {
                encodeDlAddressAction(action, out, SET_DL_SRC_CODE);
            } else if (action.getType().equals(SetDlDst.class)) {
                encodeDlAddressAction(action, out, SET_DL_DST_CODE);
            } else if (action.getType().equals(SetNwSrc.class)) {
                encodeIpAddressAction(action, out, SET_NW_SRC_CODE);
            } else if (action.getType().equals(SetNwDst.class)) {
                encodeIpAddressAction(action, out, SET_NW_DST_CODE);
            } else if (action.getType().equals(SetNwTos.class)) {
                encodeNwTosAction(action, out);
            } else if (action.getType().equals(SetTpSrc.class)) {
                encodeTpPortAction(action, out, SET_TP_SRC_CODE);
            } else if (action.getType().equals(SetTpDst.class)) {
                encodeTpPortAction(action, out, SET_TP_DST_CODE);
            } else if (action.getType().equals(Enqueue.class)) {
                encodeEnqueueAction(action, out);
            } else if (action.getType().equals(Experimenter.class)) {
                encodeExperimenterAction(action, out);
            }
        }
    }
    
    private static void encodeGenericAction(byte code, ByteBuf out) {
        out.writeShort(code);
        out.writeShort(GENERIC_ACTION_LENGTH);
        ByteBufUtils.padBuffer(PADDING_IN_GENERIC_ACTION, out);
    }
    
    private static void encodeOutputAction(Action action, ByteBuf out) {
        out.writeShort(OUTPUT_CODE);
        out.writeShort(OUTPUT_LENGTH);
        PortAction port = action.getAugmentation(PortAction.class);
        out.writeShort(port.getPort().getValue().intValue());
        MaxLengthAction maxlength = action.getAugmentation(MaxLengthAction.class);
        out.writeShort(maxlength.getMaxLength());
    }
    
    private static void encodeSetVlanVidAction(Action action, ByteBuf out) {
        out.writeShort(SET_VLAN_VID_CODE);
        out.writeShort(SET_VLAN_VID_LENGTH);
        out.writeShort(action.getAugmentation(VlanVidAction.class).getVlanVid());
        ByteBufUtils.padBuffer(PADDING_IN_SET_VLAN_VID_ACTION, out);
    }
    
    private static void encodeSetVlanPcpAction(Action action, ByteBuf out) {
        out.writeShort(SET_VLAN_PCP_CODE);
        out.writeShort(SET_VLAN_PCP_LENGTH);
        out.writeByte(action.getAugmentation(VlanPcpAction.class).getVlanPcp());
        ByteBufUtils.padBuffer(PADDING_IN_SET_VLAN_PCP_ACTION, out);
    }
    
    private static void encodeDlAddressAction(Action action, ByteBuf out, byte code) {
        out.writeShort(code);
        out.writeShort(DL_ADDRESS_ACTION_LENGTH);
        out.writeBytes(ByteBufUtils.macAddressToBytes(action.getAugmentation(DlAddressAction.class)
                .getDlAddress().getValue()));
        ByteBufUtils.padBuffer(PADDING_IN_DL_ADDRESS_ACTION, out);
    }
    
    private static void encodeNwTosAction(Action action, ByteBuf out) {
        out.writeShort(SET_NW_TOS_CODE);
        out.writeShort(SET_NW_TOS_LENGTH);
        out.writeByte(action.getAugmentation(NwTosAction.class).getNwTos());
        ByteBufUtils.padBuffer(PADDING_IN_SET_NW_TOS_ACTION, out);
    }
    
    private static void encodeIpAddressAction(Action action, ByteBuf out, byte code) {
        out.writeShort(code);
        out.writeShort(IP_ADDRESS_ACTION_LENGTH);
        String[] addressGroups = action.
                getAugmentation(IpAddressAction.class).getIpAddress().getValue().split("\\.");
        for (int i = 0; i < addressGroups.length; i++) {
            out.writeByte(Integer.parseInt(addressGroups[i]));
        }
    }
    
    private static void encodeTpPortAction(Action action, ByteBuf out, byte code) {
        out.writeShort(code);
        out.writeShort(TP_PORT_ACTION_LENGTH);
        PortAction port = action.getAugmentation(PortAction.class);
        out.writeShort(port.getPort().getValue().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_TP_PORT_ACTION, out);
    }
    
    private static void encodeEnqueueAction(Action action, ByteBuf out) {
        out.writeShort(ENQUEUE_CODE);
        out.writeShort(ENQUEUE_LENGTH);
        PortAction port = action.getAugmentation(PortAction.class);
        out.writeShort(port.getPort().getValue().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_ENQUEUE_ACTION, out);
        QueueIdAction queueId = action.getAugmentation(QueueIdAction.class);
        out.writeInt(queueId.getQueueId().intValue());
    }
    
    private static void encodeExperimenterAction(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(EXPERIMENTER_CODE);
        outBuffer.writeShort(EXPERIMENTER_LENGTH);
        ExperimenterAction experimenter = action.getAugmentation(ExperimenterAction.class);
        outBuffer.writeInt(experimenter.getExperimenter().intValue());
    }
    
    /**
     * Computes length of actions
     * @param actions
     * @return length of actions (OpenFlow v1.0)
     */
    public static int computeActionsLength(List<Action> actions) {
        int length = 0;
        if (actions != null) {
            for (Action action : actions) {
                if (action.getType().equals(Output.class)) {
                    length += 8;
                } else if (action.getType().equals(SetVlanVid.class)) {
                    length += 8;
                } else if (action.getType().equals(SetVlanPcp.class)) {
                    length += 8;
                } else if (action.getType().equals(StripVlan.class)) {
                    length += 8;
                } else if (action.getType().equals(SetDlSrc.class)) {
                    length += 16;
                } else if (action.getType().equals(SetDlDst.class)) {
                    length += 16;
                } else if (action.getType().equals(SetNwSrc.class)) {
                    length += 8;
                } else if (action.getType().equals(SetNwDst.class)) {
                    length += 8;
                } else if (action.getType().equals(SetNwTos.class)) {
                    length += 8;
                } else if (action.getType().equals(SetTpSrc.class)) {
                    length += 8;
                } else if (action.getType().equals(SetTpDst.class)) {
                    length += 8;
                } else if (action.getType().equals(Enqueue.class)) {
                    length += 16;
                } else if (action.getType().equals(Experimenter.class)) {
                    length += 8;
                }
            }
        }
        return length;
    }

}
