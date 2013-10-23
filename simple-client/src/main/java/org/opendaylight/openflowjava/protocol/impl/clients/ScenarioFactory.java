/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.clients;

import java.util.Stack;

import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;

/**
 * Class for providing prepared handshake scenario
 * 
 * @author michal.polkorab
 */
public class ScenarioFactory {

    /**
     * Creates stack with handshake needed messages.
     * <ol> XID of messages:
     *   <li> hello sent - 00000001
     *   <li> hello waiting - 00000002
     *   <li> featuresrequest waiting - 00000003
     *   <li> featuresreply sent - 00000003
     * </ol>
     * @return stack filled with Handshake messages
     */
    public static Stack<ClientEvent> createHandshakeScenario() {
        Stack<ClientEvent> stack = new Stack<>();
        stack.add(0, new SendEvent(ByteBufUtils.hexStringToBytes("04 00 00 08 00 00 00 01")));
        stack.add(0, new WaitForMessageEvent(ByteBufUtils.hexStringToBytes("04 00 00 08 00 00 00 02")));
        stack.add(0, new WaitForMessageEvent(ByteBufUtils.hexStringToBytes("04 05 00 08 00 00 00 03")));
        stack.add(0, new SendEvent(ByteBufUtils.hexStringToBytes("04 06 00 20 00 00 00 03 "
                + "00 01 02 03 04 05 06 07 00 01 02 03 01 01 00 00 00 01 02 03 00 01 02 03")));
        return stack;
    }

}
