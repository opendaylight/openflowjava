/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.nx;

import java.util.List;

import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.nx.deserialization.NxActionResubmitDeserializer;
import org.opendaylight.openflowjava.protocol.nx.serialization.NxActionResubmitSerializer;
import org.opendaylight.openflowjava.protocol.spi.connection.SwitchConnectionProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.nx.resubmit.action.rev130731.NxResubmit;

/**
 * @author michal.polkorab
 *
 */
public class NxResubmitActionRegistrator implements AutoCloseable {

    /**
     * @param providers providesr that shall be filled with serializers
     */
    public void registerNxResubmitSerializer(List<SwitchConnectionProvider> providers) {
        if (providers != null) {
            for (SwitchConnectionProvider provider : providers) {
                provider.registerSerializer(new EnhancedMessageTypeKey<>(EncodeConstants.OF13_VERSION_ID,
                        Action.class, NxResubmit.class), new NxActionResubmitSerializer());
            }
        }
    }

    /**
     * @param providers providers that shall be filled with deserializers
     */
    public void registerNxResubmitDeserializer(List<SwitchConnectionProvider> providers) {
        if (providers != null) {
            for (SwitchConnectionProvider provider : providers) {
                //TODO - 42 represents vendor id (42 is used because vendor id is unknown from specification)
                provider.registerDeserializer(new EnhancedMessageCodeKey(EncodeConstants.OF13_VERSION_ID,
                        EncodeConstants.EXPERIMENTER_VALUE, 42, Action.class), new NxActionResubmitDeserializer());
            }
        }
    }

    @Override
    public void close() throws Exception {
        // no need to close - registrator only registers (de)serializers and quits
    }
}
