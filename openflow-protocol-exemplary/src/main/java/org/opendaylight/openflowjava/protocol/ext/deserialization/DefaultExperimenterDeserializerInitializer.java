/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.deserialization;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.ext.util.ExtConstants;
import org.opendaylight.openflowjava.protocol.spi.connection.SwitchConnectionProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;

/**
 * @author michal.polkorab
 *
 */
public class DefaultExperimenterDeserializerInitializer {

    /**
     * Registers default experimenter deserializers
     * @param provider provider to be filled with deserializers
     */
    public static void registerDeserializers(SwitchConnectionProvider provider) {
        provider.registerDeserializer(new MessageCodeKey(ExtConstants.OF13_VERSION_ID,
                ExtConstants.EXPERIMENTER_VALUE, Action.class), new NxActionResubmitDeserializer());
    }
}
