/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.api.extensibility;


/**
 * @author michal.polkorab
 *
 */
public interface DeserializerRegistry {

    /**
     * Initializes deserializers
     */
    public void init();

    /**
     * @param key used for deserializer lookup
     * @return deserializer found
     */
    public <SERIALIZER_TYPE extends OFGeneralDeserializer>
            SERIALIZER_TYPE getDeserializer(MessageCodeKey key);

    /**
     * @param key used to registry lookup
     * @param deserializer deserializer instance
     */
    public void registerDeserializer(MessageCodeKey key, OFGeneralDeserializer deserializer);
}
