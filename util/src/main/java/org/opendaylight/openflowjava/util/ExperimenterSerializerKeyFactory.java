/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.util;

import org.opendaylight.openflowjava.protocol.api.keys.ExperimenterIdSerializerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeatureProperties;

/**
 * @author michal.polkorab
 *
 */
public abstract class ExperimenterSerializerKeyFactory {

    /**
     * @param msgVersion openflow wire version
     * @param experimenterId experimenter / vendor ID
     * @return key instance
     */
    public static ExperimenterIdSerializerKey<ExperimenterInput> createExperimenterMessageSerializerKey(
            short msgVersion, Long experimenterId) {
        return new ExperimenterIdSerializerKey<>(msgVersion, experimenterId, ExperimenterInput.class);
    }

    /**
     * @param msgVersion openflow wire version
     * @param experimenterId experimenter / vendor ID
     * @return key instance
     */
    public static ExperimenterIdSerializerKey<MultipartRequestExperimenterCase> createMultipartRequestSerializerKey(
            short msgVersion, Long experimenterId) {
        return new ExperimenterIdSerializerKey<>(msgVersion, experimenterId, MultipartRequestExperimenterCase.class);
    }

    /**
     * @param msgVersion openflow wire version
     * @param experimenterId experimenter / vendor ID
     * @return key instance
     */
    public static ExperimenterIdSerializerKey<TableFeatureProperties> createMultipartRequestTFSerializerKey(
            short msgVersion, Long experimenterId) {
        return new ExperimenterIdSerializerKey<>(msgVersion, experimenterId, TableFeatureProperties.class);
    }

    /**
     * @param msgVersion openflow wire version
     * @param experimenterId experimenter / vendor ID
     * @return key instance
     */
    public static ExperimenterIdSerializerKey<MeterBandExperimenterCase> createMeterBandSerializerKey(
            short msgVersion, Long experimenterId) {
        return new ExperimenterIdSerializerKey<>(msgVersion, experimenterId, MeterBandExperimenterCase.class);
    }
}