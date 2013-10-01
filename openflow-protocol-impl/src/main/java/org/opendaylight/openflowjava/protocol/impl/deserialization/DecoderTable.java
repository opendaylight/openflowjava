/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.core.OFVersionDetector;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.BarrierReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.EchoReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.EchoRequestMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.ErrorMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.ExperimenterMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.FeaturesReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.FlowRemovedMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.GetConfigReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.MultipartReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.PacketInMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.PortStatusMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.QueueGetConfigReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.RoleReplyMessageFactory;

/**
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
    
    private static final short OF13 = OFVersionDetector.OF13_VERSION_ID;
    private Map<MessageTypeCodeKey, OFDeserializer<?>> table;
    private static DecoderTable instance;
    
    
    private DecoderTable() {
        // do nothing
    }
    
    /**
     * @return singleton instance
     */
    public static DecoderTable getInstance() {
        if (instance == null) {
            synchronized (DecoderTable.class) {
                instance = new DecoderTable();
                instance.init();
            }
        }
        return instance;
    }
    
    /**
     * Decoder table provisioning
     */
    public void init() {
        table = new HashMap<>();
        table.put(new MessageTypeCodeKey(OF13, (short) 0), HelloMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 1), ErrorMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 2), EchoRequestMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 3), EchoReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 4), ExperimenterMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 6), FeaturesReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 8), GetConfigReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 10), FlowRemovedMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 11), PacketInMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 12), PortStatusMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 19), MultipartReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 21), BarrierReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 23), QueueGetConfigReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 25), RoleReplyMessageFactory.getInstance());
    }
    
    /**
     * @param msgTypeKey
     * @return decoder for given message types
     */
    public OFDeserializer<?> getDecoder(MessageTypeCodeKey msgTypeKey) {
        return table.get(msgTypeKey);
    }

}
