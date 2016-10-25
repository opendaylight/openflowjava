/*
 * Copyright (c) 2016 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.experimenter;

import io.netty.buffer.ByteBuf;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.experimenter.input.experimenter.data.of.choice.BundleControl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.experimenter.core.ExperimenterDataOfChoice;

/**
 * Translates BundleControl messages (OpenFlow v1.3 extension #230).
 */
public class BundleControlFactory extends AbstractBundleMessageFactory {

    @Override
    public void serialize(ExperimenterDataOfChoice input, ByteBuf outBuffer) {
        BundleControl msg = (BundleControl) input;
        outBuffer.writeInt(msg.getBundleId().getValue().intValue());
        outBuffer.writeShort(msg.getType().getIntValue());
        writeBundleFlags(msg.getFlags(), outBuffer);
        if (msg.getBundleProperty() != null) {
            writeBundleProperties(msg.getBundleProperty(), outBuffer);
        }
    }

}
