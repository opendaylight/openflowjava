/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.keys.ActionSerializerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ActionBase;
import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 * @param <CLAZZ> {@link Action}.class
 */
public class ActionSerializerRegistryHelper<CLAZZ extends DataObject> {

    private short version;
    private Class<CLAZZ> objectType;
    private SerializerRegistry serializerRegistry;

    /**
     * @param version Openflow wire version
     * @param objectType
     * @param serializerRegistry
     */
    public ActionSerializerRegistryHelper(short version, Class<CLAZZ> objectType,
            SerializerRegistry serializerRegistry) {
        this.version = version;
        this.objectType = objectType;
        this.serializerRegistry = serializerRegistry;
    }

    /**
     * Registers given serializer
     * @param actionType
     * @param experimenterId 
     * @param serializer
     */
    public <TYPE extends ActionBase> void registerSerializer(Class<TYPE> actionType,
            Long experimenterId, OFGeneralSerializer serializer) {
        serializerRegistry.registerSerializer(new ActionSerializerKey<>(version,
                actionType, experimenterId), serializer);
    }
}