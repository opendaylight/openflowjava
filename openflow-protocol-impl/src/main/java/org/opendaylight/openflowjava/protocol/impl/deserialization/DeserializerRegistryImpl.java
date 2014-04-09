/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.ActionsDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.InstructionsDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.MatchDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.OF10ActionsDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.OF10MatchDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.grouping.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.v10.grouping.MatchV10;

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
public class DeserializerRegistryImpl implements DeserializerRegistry {
    
    private Map<MessageCodeKey, OFGeneralDeserializer> registry;

    /**
     * Decoder table provisioning
     */
    @Override
    public void init() {
        registry = new HashMap<>();
        // register message deserializers
        MessageDerializerInitializer.registerMessageDeserializers(this);

        // register common structure deserializers
        registerDeserializer(new MessageCodeKey(EncodeConstants.OF10_VERSION_ID,
                EncodeConstants.EMPTY_VALUE, MatchV10.class), new OF10MatchDeserializer());
        registerDeserializer(new MessageCodeKey(EncodeConstants.OF10_VERSION_ID,
                EncodeConstants.EMPTY_VALUE, Action.class), new OF10ActionsDeserializer());
        registerDeserializer(new MessageCodeKey(EncodeConstants.OF13_VERSION_ID,
                EncodeConstants.EMPTY_VALUE, Match.class), new MatchDeserializer());
        registerDeserializer(new MessageCodeKey(EncodeConstants.OF13_VERSION_ID,
                EncodeConstants.EMPTY_VALUE, Action.class), new ActionsDeserializer());
        registerDeserializer(new MessageCodeKey(EncodeConstants.OF13_VERSION_ID,
                EncodeConstants.EMPTY_VALUE, Instruction.class), new InstructionsDeserializer());

        // register match entry deserializers
        MatchEntryDeserializerInitializer.registerMatchEntryDeserializers(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <DESERIALIZER_TYPE extends OFGeneralDeserializer> DESERIALIZER_TYPE getDeserializer(
            MessageCodeKey key) {
        OFGeneralDeserializer deserializer = registry.get(key);
        if (deserializer == null) {
            if (key instanceof EnhancedMessageCodeKey) {
                EnhancedMessageCodeKey enhancedKey =  (EnhancedMessageCodeKey) key;
                throw new NullPointerException("Deserializer for key: " + enhancedKey.toString()
                        + " was not found");
            }
            throw new NullPointerException("Deserializer for key: " + key.toString()
                    + " was not found");
        }
        return (DESERIALIZER_TYPE) deserializer;
    }

    @Override
    public void registerDeserializer(MessageCodeKey key,
            OFGeneralDeserializer deserializer) {
        if ((key == null) || (deserializer == null)) {
            throw new NullPointerException("MessageCodeKey or Deserializer is null");
        }
        if (deserializer instanceof DeserializerRegistryInjector) {
            ((DeserializerRegistryInjector) deserializer).injectDeserializerRegistry(this);
        }
        registry.put(key, deserializer);
    }

}
