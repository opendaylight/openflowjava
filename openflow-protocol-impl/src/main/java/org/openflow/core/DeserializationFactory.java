/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.core;

import io.netty.buffer.ByteBuf;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michal.polkorab
 */
public class DeserializationFactory {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DeserializationFactory.class);

    public DeserializationFactory(ByteBuf bb) {
        short type = bb.readUnsignedByte();

        switch (type) {
            // HELLO
            case 0: {
                LOGGER.info("OFPT_HELLO received");
                
                byte[] hello = new byte[]{0x04, 0x0, 0x0, 0x08, 0x0, 0x0, 0x0, 0x01};
                //out.writeBytes(hello);
                break;
            }
            // ERROR
            case 1:
                LOGGER.info("OFPT_ERROR received");
                break;
            // ECHO_REQUEST
            case 2: {
                LOGGER.info("OFPT_ECHO_REQUEST received");
                byte[] echoReply = new byte[]{0x04, 0x03, 0x00, 0x08};
//                out.writeBytes(echoReply);
//                out.writeInt((int) xid);
                // TODO - append original data field
                break;
            }
            // ECHO_REPLY
            case 3:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // EXPERIMENTER
            case 4:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // FEATURES_REQUEST
            case 5:
                LOGGER.info("OFPT_FEATURES_REQUEST received");
                break;
            // FEATURES_REPLY
            case 6:
                LOGGER.info("OFPT_FEATURES_REPLY received");
                break;
            // GET_CONFIG_REQUEST
            case 7:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // GET_CONFIG_REPLY
            case 8:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // SET_CONFIG
            case 9:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // PACKET_IN
            case 10:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // FLOW_REMOVED
            case 11:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // PORT_STATUS
            case 12:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // PACKET_OUT
            case 13:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // FLOW_MOD
            case 14:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // GROUP_MOD
            case 15:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // PORT_MOD
            case 16:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // TABLE_MOD
            case 17:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // MULTIPART_REQUEST
            case 18:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // MULTIPART_REPLY
            case 19:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // BARRIER_REQUEST
            case 20:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // BARRIER_REPLY
            case 21:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // QUEUE_GET_CONFIG_REQUEST
            case 22:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // QUEUE_GET_CONFIG_REPLY
            case 23:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // ROLE_REQUEST
            case 24:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // ROLE_REPLY    
            case 25:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // GET_ASYNC_REQUEST
            case 26:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // GET_ASYNC_REPLY
            case 27:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // SET_ASYNC
            case 28:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            // METER_MOD
            case 29:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;

            default:
                LOGGER.info("Received message type: " + type);
                break;
        }
    }
}
