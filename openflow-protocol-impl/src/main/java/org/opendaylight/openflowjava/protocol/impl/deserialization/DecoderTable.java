/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.BarrierReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.EchoReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.EchoRequestMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.ErrorMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.ExperimenterMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.FeaturesReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.FlowRemovedMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.GetAsyncReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.GetConfigReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.MultipartReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10ErrorMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10FeaturesReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10FlowRemovedMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10HelloMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10PacketInMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10PortStatusMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10QueueGetConfigReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10StatsReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10VendorMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.PacketInMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.PortStatusMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.QueueGetConfigReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.RoleReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;

/**
 * Stores and provides correct decoders for received messages
 * @author michal.polkorab
 * 
 *  <pre>         
 *  Type   Message
 *   0: HELLO
 *   1: ERROR
 *   2: ECHO_REQUEST
 *   3: ECHO_REPLY
 *   4: EXPERIMENTER
 *   5: FEATURES_REQUEST
 *   6: FEATURES_REPLY
 *   7: GET_CONFIG_REQUEST
 *   8: GET_CONFIG_REPLY
 *   9: SET_CONFIG
 *   10: PACKET_IN
 *   11: FLOW_REMOVED
 *   12: PORT_STATUS
 *   13: PACKET_OUT
 *   14: FLOW_MOD
 *   15: GROUP_MOD
 *   16: PORT_MOD
 *   17: TABLE_MOD
 *   18: MULTIPART_REQUEST
 *   19: MULTIPART_REPLY
 *   20: BARRIER_REQUEST
 *   21: BARRIER_REPLY
 *   22: QUEUE_GET_CONFIG_REQUEST
 *   23: QUEUE_GET_CONFIG_REPLY
 *   24: ROLE_REQUEST
 *   25: ROLE_REPLY    
 *   26: GET_ASYNC_REQUEST
 *   27: GET_ASYNC_REPLY
 *   28: SET_ASYNC
 *   29: METER_MOD
 *   </pre>
 */
public class DecoderTable {
    
    private static final short OF10 = EncodeConstants.OF10_VERSION_ID;
    private static final short OF13 = EncodeConstants.OF13_VERSION_ID;
    private Map<MessageTypeCodeKey, OFDeserializer<?>> table;
    private static DecoderTable instance;
    
    
    private DecoderTable() {
        // do nothing
    }
    
    /**
     * @return singleton instance
     */
    public static synchronized DecoderTable getInstance() {
        if (instance == null) {
            instance = new DecoderTable();
            instance.init();
        }
        return instance;
    }
    
    /**
     * Decoder table provisioning
     */
    public void init() {
        table = new HashMap<>();
        table.put(new MessageTypeCodeKey(OF10, (short) 0), OF10HelloMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF10, (short) 1), OF10ErrorMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF10, (short) 2), EchoRequestMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF10, (short) 3), EchoReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF10, (short) 4), OF10VendorMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF10, (short) 6), OF10FeaturesReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF10, (short) 8), GetConfigReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF10, (short) 10), OF10PacketInMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF10, (short) 11), OF10FlowRemovedMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF10, (short) 12), OF10PortStatusMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF10, (short) 16), OF10StatsReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF10, (short) 18), BarrierReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF10, (short) 20), OF10QueueGetConfigReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 0), HelloMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 1), ErrorMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 2), EchoRequestMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 3), EchoReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 4), ExperimenterMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 6), FeaturesReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 8), GetConfigReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 10), PacketInMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 11), FlowRemovedMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 12), PortStatusMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 19), MultipartReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 21), BarrierReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 23), QueueGetConfigReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 25), RoleReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 27), GetAsyncReplyMessageFactory.getInstance());
    }
    
    /**
     * @param msgTypeKey
     * @return decoder for given message types
     */
    public OFDeserializer<?> getDecoder(MessageTypeCodeKey msgTypeKey) {
        return table.get(msgTypeKey);
    }

}
