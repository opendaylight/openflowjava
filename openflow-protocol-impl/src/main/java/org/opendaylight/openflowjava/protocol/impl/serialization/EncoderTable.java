/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.core.OFVersionDetector;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.BarrierInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.EchoInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.EchoReplyInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.ExperimenterInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.FlowModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetAsyncRequestMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetConfigInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetFeaturesInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetQueueConfigInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GroupModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.HelloInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.MeterModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.MultipartRequestMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.PacketOutInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.PortModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.RoleRequestInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.SetAsyncInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.SetConfigMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.TableModInputMessageFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MeterModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.TableModInput;
import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 *
 */
public class EncoderTable {
    
    private static final short OF13 = OFVersionDetector.OF13_VERSION_ID;
    private static EncoderTable instance;
    private Map<MessageTypeKey<?>, OFSerializer<?>> table;
    

    private EncoderTable() {
        // do nothing
    }
    
    /**
     * @return singleton instance
     */
    public static EncoderTable getInstance() {
        if (instance == null) {
            synchronized (EncoderTable.class) {
                instance = new EncoderTable();
                instance.init();
            }
        }
        return instance;
    }
    
    /**
     * Encoder table provisioning
     */
    public void init() {
        table = new HashMap<>();
        table.put(new MessageTypeKey<>(OF13, BarrierInput.class), BarrierInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, EchoInput.class), EchoInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, EchoReplyInput.class), EchoReplyInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, ExperimenterInput.class), ExperimenterInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, FlowModInput.class), FlowModInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, GetAsyncInput.class), GetAsyncRequestMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, GetConfigInput.class), GetConfigInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, GetFeaturesInput.class), GetFeaturesInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, GetQueueConfigInput.class), GetQueueConfigInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, GroupModInput.class), GroupModInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, HelloInput.class), HelloInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, MeterModInput.class), MeterModInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, MultipartRequestMessage.class), MultipartRequestMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, PacketOutInput.class), PacketOutInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, PortModInput.class), PortModInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, RoleRequestInput.class), RoleRequestInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, SetAsyncInput.class), SetAsyncInputMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, SetConfigInput.class), SetConfigMessageFactory.getInstance());
        table.put(new MessageTypeKey<>(OF13, TableModInput.class), TableModInputMessageFactory.getInstance());
    }
    
    /**
     * @param msgTypeKey
     * @return encoder for current type of message (msgTypeKey)
     */
    @SuppressWarnings("unchecked")
    public <E extends DataObject> OFSerializer<E> getEncoder(MessageTypeKey<E> msgTypeKey) {
        return (OFSerializer<E>) table.get(msgTypeKey);
    }

}
