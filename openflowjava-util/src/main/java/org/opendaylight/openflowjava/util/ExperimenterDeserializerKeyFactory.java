/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.util;

import org.opendaylight.openflowjava.protocol.api.keys.ExperimenterIdDeserializerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.property.header.QueueProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeatureProperties;

/**
 * @author michal.polkorab
 *
 */
public abstract class ExperimenterDeserializerKeyFactory {

    private ExperimenterDeserializerKeyFactory() {
        //not called
    }

    /**
     * @param version openflow wire version
     * @param experimenterId experimenter / vendor ID
     * @return key instance
     */
    public static ExperimenterIdDeserializerKey createExperimenterErrorDeserializerKey(
            short version, Long experimenterId) {
        return new ExperimenterIdDeserializerKey(version, experimenterId, ErrorMessage.class);
    }

    /**
     * @param version openflow wire version
     * @param experimenterId experimenter / vendor ID
     * @return key instance
     */
    public static ExperimenterIdDeserializerKey createExperimenterMessageDeserializerKey(
            short version, Long experimenterId) {
        return new ExperimenterIdDeserializerKey(version, experimenterId, ExperimenterMessage.class);
    }

    /**
     * @param version openflow wire version
     * @param experimenterId experimenter / vendor ID
     * @return key instance
     */
    public static ExperimenterIdDeserializerKey createMultipartReplyMessageDeserializerKey(
            short version, Long experimenterId) {
        return new ExperimenterIdDeserializerKey(version, experimenterId, MultipartReplyExperimenterCase.class);
    }

    /**
     * @param version openflow wire version
     * @param experimenterId experimenter / vendor ID
     * @return key instance
     */
    public static ExperimenterIdDeserializerKey createMultipartReplyTFDeserializerKey(
            short version, Long experimenterId) {
        return new ExperimenterIdDeserializerKey(version, experimenterId, TableFeatureProperties.class);
    }

    /**
     * @param version openflow wire version
     * @param experimenterId experimenter / vendor ID
     * @return key instance
     */
    public static ExperimenterIdDeserializerKey createQueuePropertyDeserializerKey(
            short version, Long experimenterId) {
        return new ExperimenterIdDeserializerKey(version, experimenterId, QueueProperty.class);
    }

    /**
     * @param version openflow wire version
     * @param experimenterId experimenter / vendor ID
     * @return key instance
     */
    public static ExperimenterIdDeserializerKey createMeterBandDeserializerKey(
            short version, Long experimenterId) {
        return new ExperimenterIdDeserializerKey(version, experimenterId, MeterBandExperimenterCase.class);
    }
}