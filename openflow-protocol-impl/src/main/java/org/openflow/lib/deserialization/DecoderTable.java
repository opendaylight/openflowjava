/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.lib.deserialization;

import java.util.HashMap;
import java.util.Map;

import org.openflow.deserialization.factories.EchoReplyMessageFactory;
import org.openflow.deserialization.factories.EchoRequestMessageFactory;
import org.openflow.deserialization.factories.FeaturesReplyMessageFactory;
import org.openflow.deserialization.factories.HelloMessageFactory;
import org.openflow.lib.OfVersionDetector;

/**
 * @author michal.polkorab
 *
 */
public class DecoderTable {
    
    private static final short OF13 = OfVersionDetector.OF13_VERSION_ID;
    private Map<MessageTypeCodeKey, OfDeserializer<?>> table;
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
        table.put(new MessageTypeCodeKey(OF13, (short) 2), EchoRequestMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 3), EchoReplyMessageFactory.getInstance());
        table.put(new MessageTypeCodeKey(OF13, (short) 6), FeaturesReplyMessageFactory.getInstance());
    }
    
    /**
     * @param msgTypeKey
     * @return decoder for given message types
     */
    public OfDeserializer<?> getDecoder(MessageTypeCodeKey msgTypeKey) {
        return table.get(msgTypeKey);
    }

}
