/*
 * Copyright (c) 2016 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.experimenter;

import io.netty.buffer.ByteBuf;
import java.util.List;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.keys.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.bundle.properties.BundleProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.experimenter.input.experimenter.data.of.choice.BundleAddMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.experimenter.input.experimenter.data.of.choice.bundle.add.message.Message;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.experimenter.input.experimenter.data.of.choice.bundle.add.message.message.FlowModCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.experimenter.input.experimenter.data.of.choice.bundle.add.message.message.GroupModCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.experimenter.input.experimenter.data.of.choice.bundle.add.message.message.PortModCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.experimenter.core.ExperimenterDataOfChoice;
import org.opendaylight.yangtools.yang.binding.DataContainer;

/**
 * Translates BundleAddMessage messages (OpenFlow v1.3 extension #230).
 */
public class BundleAddMessageFactory extends AbstractBundleMessageFactory {

    @Override
    public void serialize(ExperimenterDataOfChoice input, ByteBuf outBuffer) {
        BundleAddMessage msg = (BundleAddMessage) input;
        outBuffer.writeInt(msg.getBundleId().getValue().intValue());
        outBuffer.writeZero(2 * EncodeConstants.PADDING);
        writeBundleFlags(msg.getFlags(), outBuffer);

        int msgStart = outBuffer.writerIndex();
        Message innerMsg = msg.getMessage();
        if (innerMsg instanceof FlowModCase) {
            serializeInnerMessage(innerMsg, outBuffer, FlowModInput.class);
        } else if (innerMsg instanceof GroupModCase) {
            serializeInnerMessage(innerMsg, outBuffer, GroupModInput.class);
        } else if (innerMsg instanceof PortModCase) {
            serializeInnerMessage(innerMsg, outBuffer, PortModInput.class);
        }
        int msgLength = outBuffer.writerIndex() - msgStart;

        List<BundleProperty> bundleProperties = msg.getBundleProperty();
        if (bundleProperties != null && !bundleProperties.isEmpty()) {
            outBuffer.writeZero(paddingNeeded(msgLength));
            writeBundleProperties(msg.getBundleProperty(), outBuffer);
        }
    }

    private <T extends DataContainer> void serializeInnerMessage(final Message innerMessage, final ByteBuf outBuffer,
                                                                 final Class<T> clazz) {
        OFSerializer<T> serializer = serializerRegistry.getSerializer(
                new MessageTypeKey<>(EncodeConstants.OF13_VERSION_ID, clazz));
        serializer.serialize((T)innerMessage, outBuffer);
    }

    private static int paddingNeeded(final int length) {
        int paddingRemainder = length % EncodeConstants.PADDING;
        return (paddingRemainder != 0) ? (EncodeConstants.PADDING - paddingRemainder) : 0;
    }

}
