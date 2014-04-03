/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.api.extensibility;

import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * Stores and handles serializers 
 * @author michal.polkorab
 *
 */
public interface SerializerRegistry {

    /**
     * Serializer registry provisioning
     */
    public void init();

    /**
     * @param msgTypeKey lookup key
     * @return serializer or NullPointerException if no serializer was found
     */
    public <E extends DataObject> OFSerializer<E> getSerializer(MessageTypeKey<E> msgTypeKey);

    /**
     * Registers serializer under key
     * @param msgTypeKey lookup key
     * @param serializer serializer implementation
     */
    public <E extends DataObject> void registerSerializer(MessageTypeKey<E> msgTypeKey, OFSerializer<E> serializer);
}
