/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.lib.serialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 *
 */
public class SerializationFactory {

    /**
     * Transforms POJO message into ByteBuf
     * @param version version used for encoding received message
     * @param out ByteBuf for storing and sending transformed message
     * @param message POJO message
     */
    public static <E extends DataObject> void messageToBuffer(short version, ByteBuf out, E message) {
        @SuppressWarnings("unchecked")
        MessageTypeKey<E> msgTypeKey = new MessageTypeKey<E>(version, (Class<E>) message.getClass());
        OfSerializer<E> encoder = EncoderTable.getInstance().getEncoder(msgTypeKey);
        encoder.messageToBuffer(version, out, message);
        /* 
           Type   Message
            0: HELLO
            1: ERROR
            2: ECHO_REQUEST
            3: ECHO_REPLY
            4: EXPERIMENTER
            5: FEATURES_REQUEST
            6: FEATURES_REPLY
            7: GET_CONFIG_REQUEST
            8: GET_CONFIG_REPLY
            9: SET_CONFIG
            10: PACKET_IN
            11: FLOW_REMOVED
            12: PORT_STATUS
            13: PACKET_OUT
            14: FLOW_MOD
            15: GROUP_MOD
            16: PORT_MOD
            17: TABLE_MOD
            18: MULTIPART_REQUEST
            19: MULTIPART_REPLY
            20: BARRIER_REQUEST
            21: BARRIER_REPLY
            22: QUEUE_GET_CONFIG_REQUEST
            23: QUEUE_GET_CONFIG_REPLY
            24: ROLE_REQUEST
            25: ROLE_REPLY    
            26: GET_ASYNC_REQUEST
            27: GET_ASYNC_REPLY
            28: SET_ASYNC
            29: METER_MOD
        */
    }
}
