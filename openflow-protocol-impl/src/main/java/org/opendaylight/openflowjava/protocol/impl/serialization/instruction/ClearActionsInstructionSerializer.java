/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.instruction;

import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.impl.util.InstructionConstants;

/**
 * @author michal.polkorab
 *
 */
public class ClearActionsInstructionSerializer extends AbstractActionInstructionSerializer
        implements SerializerRegistryInjector {

    private SerializerRegistry registry;

    @Override
    protected SerializerRegistry getRegistry() {
        return registry;
    }

    @Override
    protected int getType() {
        return InstructionConstants.CLEAR_ACTIONS_TYPE;
    }

    @Override
    public void injectSerializerRegistry(SerializerRegistry serializerRegistry) {
        registry = serializerRegistry;
    }

}
