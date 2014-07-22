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
     * Registers deserializer.
     * Throws IllegalStateException when there is
     * a deserializer already registered under given key.
     * 
     * If the deserializer implements {@link DeserializerRegistryInjector} interface,
     * the deserializer is injected with DeserializerRegistry instance.
     * 
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    public void registerDeserializer(MessageCodeKey key,
            OFGeneralDeserializer deserializer);

    /**
     * Unregisters deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     * @return true if deserializer was removed,
     *  false if no deserializer was found under specified key
     */
    public boolean unregisterDeserializer(MessageCodeKey key);
}
