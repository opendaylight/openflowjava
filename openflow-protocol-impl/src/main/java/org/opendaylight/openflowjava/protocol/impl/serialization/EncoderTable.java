/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.core.OFVersionDetector;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.HelloInputMessageFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
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
        table.put(new MessageTypeKey<>(OF13, HelloInput.class), HelloInputMessageFactory.getInstance());
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
