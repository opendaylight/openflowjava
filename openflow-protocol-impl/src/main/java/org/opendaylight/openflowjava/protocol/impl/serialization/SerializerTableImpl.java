/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerTable;
import org.opendaylight.openflowjava.protocol.impl.deserialization.EnhancedMessageTypeKey;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.BarrierInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.EchoInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.EchoReplyInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.FlowModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetAsyncRequestMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetConfigInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetFeaturesInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetQueueConfigInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GroupModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.HelloInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.MeterModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.MultipartRequestInputFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10BarrierInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10FlowModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10HelloInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10PacketOutInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10PortModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10QueueGetConfigInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10StatsRequestInputFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.PacketOutInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.PortModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.RoleRequestInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.SetAsyncInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.SetConfigMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.TableModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.OF10ActionsSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.OF10MatchSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.OF13ActionsSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.OF13InstructionsSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.OF13MatchSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.grouping.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.v10.grouping.MatchV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MeterModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.TableModInput;
import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * Stores and provides correct encoders for received messages
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class SerializerTableImpl implements SerializerTable {

    private static final short OF10 = EncodeConstants.OF10_VERSION_ID;
    private static final short OF13 = EncodeConstants.OF13_VERSION_ID;
    private Map<MessageTypeKey<?>, OFSerializer<?>> table;


    @Override
    public void init() {
        table = new HashMap<>();
        // Openflow message type serializers
        table.put(new MessageTypeKey<>(OF10, BarrierInput.class),new OF10BarrierInputMessageFactory());
        table.put(new MessageTypeKey<>(OF10, EchoInput.class), new EchoInputMessageFactory());
        table.put(new MessageTypeKey<>(OF10, EchoReplyInput.class), new EchoReplyInputMessageFactory());
        OFSerializer<FlowModInput> of10FlowModInputMessageFactory = new OF10FlowModInputMessageFactory();
        of10FlowModInputMessageFactory.injectSerializerTable(this);
        table.put(new MessageTypeKey<>(OF10, FlowModInput.class), of10FlowModInputMessageFactory);
        table.put(new MessageTypeKey<>(OF10, GetConfigInput.class), new GetConfigInputMessageFactory());
        table.put(new MessageTypeKey<>(OF10, GetFeaturesInput.class), new GetFeaturesInputMessageFactory());
        table.put(new MessageTypeKey<>(OF10, GetQueueConfigInput.class), new OF10QueueGetConfigInputMessageFactory());
        table.put(new MessageTypeKey<>(OF10, HelloInput.class), new OF10HelloInputMessageFactory());
        OFSerializer<MultipartRequestInput> of10StatsRequestInputFactory = new OF10StatsRequestInputFactory();
        of10StatsRequestInputFactory.injectSerializerTable(this);
        table.put(new MessageTypeKey<>(OF10, MultipartRequestInput.class), of10StatsRequestInputFactory);
        OFSerializer<PacketOutInput> of10PacketOutInputMessageFactory = new OF10PacketOutInputMessageFactory();
        of10PacketOutInputMessageFactory.injectSerializerTable(this);
        table.put(new MessageTypeKey<>(OF10, PacketOutInput.class), of10PacketOutInputMessageFactory);
        table.put(new MessageTypeKey<>(OF10, PortModInput.class), new OF10PortModInputMessageFactory());
        table.put(new MessageTypeKey<>(OF10, SetConfigInput.class), new SetConfigMessageFactory());
        table.put(new MessageTypeKey<>(OF13, BarrierInput.class), new BarrierInputMessageFactory());
        table.put(new MessageTypeKey<>(OF13, EchoInput.class), new EchoInputMessageFactory());
        table.put(new MessageTypeKey<>(OF13, EchoReplyInput.class), new EchoReplyInputMessageFactory());
        OFSerializer<FlowModInput> flowModInputMessageFactory = new FlowModInputMessageFactory();
        flowModInputMessageFactory.injectSerializerTable(this);
        table.put(new MessageTypeKey<>(OF13, FlowModInput.class), flowModInputMessageFactory);
        table.put(new MessageTypeKey<>(OF13, GetAsyncInput.class), new GetAsyncRequestMessageFactory());
        table.put(new MessageTypeKey<>(OF13, GetConfigInput.class), new GetConfigInputMessageFactory());
        table.put(new MessageTypeKey<>(OF13, GetFeaturesInput.class), new GetFeaturesInputMessageFactory());
        table.put(new MessageTypeKey<>(OF13, GetQueueConfigInput.class), new GetQueueConfigInputMessageFactory());
        OFSerializer<GroupModInput> groupModInputMessageFactory = new GroupModInputMessageFactory();
        groupModInputMessageFactory.injectSerializerTable(this);
        table.put(new MessageTypeKey<>(OF13, GroupModInput.class), groupModInputMessageFactory);
        table.put(new MessageTypeKey<>(OF13, HelloInput.class), new HelloInputMessageFactory());
        table.put(new MessageTypeKey<>(OF13, MeterModInput.class), new MeterModInputMessageFactory());
        OFSerializer<MultipartRequestInput> multipartRequestInputFactory = new MultipartRequestInputFactory();
        multipartRequestInputFactory.injectSerializerTable(this);
        table.put(new MessageTypeKey<>(OF13, MultipartRequestInput.class), multipartRequestInputFactory);
        OFSerializer<PacketOutInput> packetOutInputMessageFactory = new PacketOutInputMessageFactory();
        packetOutInputMessageFactory.injectSerializerTable(this);
        table.put(new MessageTypeKey<>(OF13, PacketOutInput.class), packetOutInputMessageFactory);
        table.put(new MessageTypeKey<>(OF13, PortModInput.class), new PortModInputMessageFactory());
        table.put(new MessageTypeKey<>(OF13, RoleRequestInput.class), new RoleRequestInputMessageFactory());
        table.put(new MessageTypeKey<>(OF13, SetAsyncInput.class), new SetAsyncInputMessageFactory());
        table.put(new MessageTypeKey<>(OF13, SetConfigInput.class), new SetConfigMessageFactory());
        table.put(new MessageTypeKey<>(OF13, TableModInput.class), new TableModInputMessageFactory());

        // common structure serializers
        table.put(new MessageTypeKey<>(OF10, MatchV10.class), new OF10MatchSerializer());
        OFSerializer<Match> of13MatchSerializer = new OF13MatchSerializer();
        of13MatchSerializer.injectSerializerTable(this);
        table.put(new MessageTypeKey<>(OF13, Match.class), of13MatchSerializer);
        OFSerializer<Action> of10ActionsSerializer = new OF10ActionsSerializer();
        of10ActionsSerializer.injectSerializerTable(this);
        table.put(new MessageTypeKey<>(OF10, Action.class), of10ActionsSerializer);
        OFSerializer<Action> of13ActionsSerializer = new OF13ActionsSerializer();
        of13ActionsSerializer.injectSerializerTable(this);
        table.put(new MessageTypeKey<>(OF13, Action.class), of13ActionsSerializer);
        OFSerializer<Instruction> of13InstructionsSerializer = new OF13InstructionsSerializer();
        of13InstructionsSerializer.injectSerializerTable(this);
        table.put(new MessageTypeKey<>(OF13, Instruction.class), of13InstructionsSerializer);

        // match entry serializers
        MatchEntriesInitializer matchEntriesInitializer = new MatchEntriesInitializer();
        matchEntriesInitializer.registerMatchEntrySerializers(this);
    }

    /**
     * @param msgTypeKey
     * @return encoder for current type of message (msgTypeKey)
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <E extends DataObject> OFSerializer<E> getSerializer(MessageTypeKey<E> msgTypeKey) {
        OFSerializer<E> serializer = (OFSerializer<E>) table.get(msgTypeKey);
        if (serializer == null) {
            if (msgTypeKey instanceof EnhancedMessageTypeKey) {
                EnhancedMessageTypeKey key = (EnhancedMessageTypeKey) msgTypeKey;
                throw new NullPointerException("Serializer for key: version: " + key.getMsgVersion()
                        + " msgType: " + key.getMsgType() + " msgType2: " + key.getMsgType2() + " was not found");
            }
            throw new NullPointerException("Serializer for key: version: " + msgTypeKey.getMsgVersion()
                    + " msgType: " + msgTypeKey.getMsgType() + " was not found");
        }
        return serializer;
    }

    @Override
    public <E extends DataObject> void registerSerializer(
            MessageTypeKey<E> msgTypeKey, OFSerializer<E> serializer) {
        if ((msgTypeKey == null) || (serializer == null)) {
            throw new NullPointerException("MessageTypeKey or Serializer is null");
        }
        table.put(msgTypeKey, serializer);
    }

}
