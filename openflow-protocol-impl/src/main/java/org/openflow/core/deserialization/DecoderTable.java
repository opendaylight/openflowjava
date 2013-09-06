/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.core.deserialization;

import java.util.HashMap;
import java.util.Map;

import org.openflow.core.OFVersionDetector;
import org.openflow.factories.EchoMessageFactory;
import org.openflow.factories.HelloMessageFactory;

/**
 * @author michal.polkorab
 *
 */
public class DecoderTable {
    
    private static final short OF13 = OFVersionDetector.OF13_VERSION_ID;
    private Map<MessageTypeKey, OfDeserializer<?>> table;
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
     * decoder table provisioning
     */
    public void init() {
        table = new HashMap<>();
        table.put(new MessageTypeKey(OF13, (short) 0), HelloMessageFactory.getInstance());
        table.put(new MessageTypeKey(OF13, (short) 2), EchoMessageFactory.getInstance());
    }
    
    /**
     * @param msgTypeKey
     * @return decoder for given message types
     */
    public OfDeserializer<?> getDecoder(MessageTypeKey msgTypeKey) {
        return table.get(msgTypeKey);
    }

}
