/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.nx;

import java.util.List;

import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.nx.deserialization.NxActionResubmitDeserializer;
import org.opendaylight.openflowjava.protocol.nx.serialization.NxActionResubmitSerializer;
import org.opendaylight.openflowjava.protocol.spi.connection.SwitchConnectionProvider;

/**
 * @author michal.polkorab
 *
 */
public class NxResubmitActionRegistrator implements AutoCloseable {

    /** Nicira experimenter ID */
    public static final Long NICIRA_EXPERIMENTER_ID = 0x00002320L;

    /**
     * @param providers providesr that shall be filled with serializers
     */
    public void registerNxResubmitSerializer(List<SwitchConnectionProvider> providers) {
        if (providers != null) {
            for (SwitchConnectionProvider provider : providers) {
                provider.registerActionSerializer(new ExperimenterActionSerializerKey(
                        EncodeConstants.OF13_VERSION_ID, NICIRA_EXPERIMENTER_ID),
                        new NxActionResubmitSerializer());
            }
        }
    }

    /**
     * @param providers providers that shall be filled with deserializers
     */
    public void registerNxResubmitDeserializer(List<SwitchConnectionProvider> providers) {
        if (providers != null) {
            for (SwitchConnectionProvider provider : providers) {
                provider.registerActionDeserializer(new ExperimenterActionDeserializerKey(
                        EncodeConstants.OF13_VERSION_ID, NICIRA_EXPERIMENTER_ID),
                        new NxActionResubmitDeserializer());
            }
        }
    }

    @Override
    public void close() throws Exception {
        // no need to close - registrator only registers (de)serializers and quits
    }
}
