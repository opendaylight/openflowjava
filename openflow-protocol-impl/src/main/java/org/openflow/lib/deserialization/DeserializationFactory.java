/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.lib.deserialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 *
 * @author michal.polkorab
 */
public abstract class DeserializationFactory {

    /**
     * Transforms ByteBuf into correct POJO message
     * @param rawMessage 
     * @param version version decoded from OpenFlow protocol message
     * @return correct POJO as DataObject
     */
    public static DataObject bufferToMessage(ByteBuf rawMessage, short version) {
        DataObject dataObject = null;
        short type = rawMessage.readUnsignedByte();
        
        // TODO - check if no change happened, so that skipping length would cause problems
        rawMessage.skipBytes(Short.SIZE / Byte.SIZE);

        MessageTypeCodeKey msgTypeCodeKey = new MessageTypeCodeKey(version, type);
        OfDeserializer<?> decoder = DecoderTable.getInstance().getDecoder(msgTypeCodeKey);
        dataObject = decoder.bufferToMessage(rawMessage, version);
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
        return dataObject;
    }
}
