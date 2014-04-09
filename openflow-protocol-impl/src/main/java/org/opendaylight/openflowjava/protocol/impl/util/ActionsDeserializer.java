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

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthertypeAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthertypeActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * Deserializes ofp_actions (OpenFlow v1.3)
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class ActionsDeserializer implements OFDeserializer<Action>,
        HeaderDeserializer<Action>, DeserializerRegistryInjector {
    
    private static final byte PADDING_IN_ACTIONS_HEADER = 4;
    private static final byte PADDING_IN_OUTPUT_ACTIONS_HEADER = 6;
    private static final byte PADDING_IN_SET_MPLS_TTL_ACTIONS_HEADER = 3;
    private static final byte PADDING_IN_PUSH_VLAN_ACTIONS_HEADER = 2;
    private static final byte PADDING_IN_NW_TTL_ACTIONS_HEADER = 3;
    private DeserializerRegistry registry;

    @Override
    public Action deserialize(ByteBuf input) {
        Action action = null;
        ActionBuilder actionBuilder = new ActionBuilder();
        int type = input.getUnsignedShort(input.readerIndex());
        switch(type) {
        case 0:
            action = createOutputAction(input, actionBuilder);
            break;
        case 11:
            action = createCopyTtlOutAction(input, actionBuilder);
            break;
        case 12:
            action = createCopyTtlInAction(input, actionBuilder);
            break;
        case 15:
            action = createSetMplsTtlAction(input, actionBuilder);
            break;
        case 16:
            action = createDecMplsTtlOutAction(input, actionBuilder);
            break;
        case 17:
            action = createPushVlanAction(input, actionBuilder);
            break;
        case 18:
            action = createPopVlanAction(input, actionBuilder);
            break;
        case 19:
            action = createPushMplsAction(input, actionBuilder);
            break;
        case 20:
            action = createPopMplsAction(input, actionBuilder);
            break;
        case 21:
            action = createSetQueueAction(input, actionBuilder);
            break;
        case 22:
            action = createGroupAction(input, actionBuilder);
            break;
        case 23:
            action = createSetNwTtlAction(input, actionBuilder);
            break;
        case 24:
            action = createDecNwTtlAction(input, actionBuilder);
            break;
        case 25:
            action = createSetFieldAction(input, actionBuilder);
            break;
        case 26:
            action = createPushPbbAction(input, actionBuilder);
            break;
        case 27:
            action = createPopPbbAction(input, actionBuilder);
            break;
        case 0xFFFF:
            OFDeserializer<ExperimenterAction> expDeserializer = registry.getDeserializer(
                    new MessageCodeKey(EncodeConstants.OF13_VERSION_ID, 0xFFFF, ExperimenterAction.class));
            ExperimenterAction expAction = expDeserializer.deserialize(input);
            actionBuilder.addAugmentation(ExperimenterAction.class, expAction);
            break;
        default:
            break;
        }
        return action;
    }

    @Override
    public Action deserializeHeader(ByteBuf input) {
        ActionBuilder builder;
        builder = new ActionBuilder();
        int type = input.readUnsignedShort();
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
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
            HeaderDeserializer<ExperimenterAction> expDeserializer = registry.getDeserializer(
                    new MessageCodeKey(EncodeConstants.OF13_VERSION_ID, 0xFFFF, ExperimenterAction.class));
            ExperimenterAction expAction = expDeserializer.deserializeHeader(input);
            builder.setType(Experimenter.class);
            builder.addAugmentation(ExperimenterAction.class, expAction);
            break;
        default: 
            break;
        }
        return builder.build();
    }

    private static Action createEmptyHeader(Class<? extends ActionBase> action,
            ByteBuf in, ActionBuilder actionBuilder) {
        actionBuilder.setType(action);
        in.skipBytes(2 * EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
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
        in.skipBytes(2 * EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
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
        in.skipBytes(2 * EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        MplsTtlActionBuilder mplsTtl = new MplsTtlActionBuilder();
        mplsTtl.setMplsTtl(in.readUnsignedByte());
        actionBuilder.addAugmentation(MplsTtlAction.class, mplsTtl.build());
        in.skipBytes(PADDING_IN_SET_MPLS_TTL_ACTIONS_HEADER);
        return actionBuilder.build();
    }
    
    private static Action createPushAction(Class<? extends ActionBase> action,
            ByteBuf in, ActionBuilder actionBuilder) {
        actionBuilder.setType(action);
        in.skipBytes(2 * EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
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
        in.skipBytes(2 * EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        QueueIdActionBuilder queueId = new QueueIdActionBuilder();
        queueId.setQueueId(in.readUnsignedInt());
        actionBuilder.addAugmentation(QueueIdAction.class, queueId.build());
        return actionBuilder.build();
    }
    
    private static Action createGroupAction(ByteBuf in, ActionBuilder actionBuilder) {
        actionBuilder.setType(Group.class);
        in.skipBytes(2 * EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        GroupIdActionBuilder group = new GroupIdActionBuilder();
        group.setGroupId(in.readUnsignedInt());
        actionBuilder.addAugmentation(GroupIdAction.class, group.build());
        return actionBuilder.build();
    }

    private static Action createSetNwTtlAction(ByteBuf in, ActionBuilder actionBuilder) {
        actionBuilder.setType(SetNwTtl.class);
        in.skipBytes(2 * EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        NwTtlActionBuilder nwTtl = new NwTtlActionBuilder();
        nwTtl.setNwTtl(in.readUnsignedByte());
        actionBuilder.addAugmentation(NwTtlAction.class, nwTtl.build());
        in.skipBytes(PADDING_IN_NW_TTL_ACTIONS_HEADER);
        return actionBuilder.build();
    }
    
    private Action createSetFieldAction(ByteBuf in, ActionBuilder actionBuilder) {
        actionBuilder.setType(SetField.class);
        int startIndex = in.readerIndex();
        in.skipBytes(2 * EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        OxmFieldsActionBuilder matchEntries = new OxmFieldsActionBuilder();
        int oxmClass = in.getUnsignedShort(in.readerIndex());
        // get oxm_field & hasMask byte and extract the field value
        int oxmField = in.getUnsignedByte(in.readerIndex()
                + EncodeConstants.SIZE_OF_SHORT_IN_BYTES) >>> 1;
        OFDeserializer<MatchEntries> matchDeserializer = registry.getDeserializer(
                new EnhancedMessageCodeKey(EncodeConstants.OF13_VERSION_ID, oxmClass,
                        oxmField, MatchEntries.class));
        List<MatchEntries> entry = new ArrayList<>();
        entry.add(matchDeserializer.deserialize(in));
        matchEntries.setMatchEntries(entry);
        actionBuilder.addAugmentation(OxmFieldsAction.class, matchEntries.build());
        int paddingRemainder = (in.readerIndex() - startIndex) % EncodeConstants.PADDING;
        if (paddingRemainder != 0) {
            in.skipBytes(EncodeConstants.PADDING - paddingRemainder);
        }
        return actionBuilder.build();
    }

    @Override
    public void injectDeserializerRegistry(DeserializerRegistry deserializerRegistry) {
        registry = deserializerRegistry;
    }
}
