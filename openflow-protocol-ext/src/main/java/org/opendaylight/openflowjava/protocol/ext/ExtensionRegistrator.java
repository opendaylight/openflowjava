/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext;

import java.util.List;

import org.opendaylight.openflowjava.protocol.ext.deserialization.DefaultExperimenterDeserializerInitializer;
import org.opendaylight.openflowjava.protocol.ext.serialization.DefaultExperimenterSerializerInitializer;
import org.opendaylight.openflowjava.protocol.spi.connection.SwitchConnectionProvider;

/**
 * @author michal.polkorab
 *
 */
public class ExtensionRegistrator implements AutoCloseable {

    /**
     * @param providers providesr that shall be filled with serializers
     */
    public void registerDefaultExperimenterSerializers(List<SwitchConnectionProvider> providers) {
        if (providers != null) {
            for (SwitchConnectionProvider provider : providers) {
                DefaultExperimenterSerializerInitializer.registerSerializers(provider);
            }
        }
    }

    /**
     * @param providers providers that shall be filled with deserializers
     */
    public void registerDefaultExperimenterDeserializers(List<SwitchConnectionProvider> providers) {
        if (providers != null) {
            for (SwitchConnectionProvider provider : providers) {
                DefaultExperimenterDeserializerInitializer.registerDeserializers(provider);
            }
        }
    }

    @Override
    public void close() throws Exception {
        // no need to close - registrator only registers (de)serializers and quits
    }
}
