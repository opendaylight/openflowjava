/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.util;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.keys.ExperimenterIdSerializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeatureProperties;

/**
 * @author michal.polkorab
 *
 */
public class ExperimenterSerializerKeyFactoryTest {

    /**
     * Test ExperimenterSerializerKeyFactory key creation
     */
    @Test
    public void test() {
        ExperimenterIdSerializerKey<?> createdKey = ExperimenterSerializerKeyFactory
                .createExperimenterMessageSerializerKey(EncodeConstants.OF10_VERSION_ID, 42L);
        ExperimenterIdSerializerKey<?> comparationKey =
                new ExperimenterIdSerializerKey<>(EncodeConstants.OF10_VERSION_ID, 42L, ExperimenterInput.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterSerializerKeyFactory.createExperimenterMessageSerializerKey(
                EncodeConstants.OF10_VERSION_ID, null);
        comparationKey = new ExperimenterIdSerializerKey<>(EncodeConstants.OF10_VERSION_ID,
                null, ExperimenterInput.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterSerializerKeyFactory.createMeterBandSerializerKey(
                EncodeConstants.OF10_VERSION_ID, 43L);
        comparationKey = new ExperimenterIdSerializerKey<>(EncodeConstants.OF10_VERSION_ID,
                43L, MeterBandExperimenterCase.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterSerializerKeyFactory.createMeterBandSerializerKey(
                EncodeConstants.OF10_VERSION_ID, null);
        comparationKey = new ExperimenterIdSerializerKey<>(EncodeConstants.OF10_VERSION_ID,
                null, MeterBandExperimenterCase.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterSerializerKeyFactory.createMultipartRequestSerializerKey(
                EncodeConstants.OF10_VERSION_ID, 44L);
        comparationKey = new ExperimenterIdSerializerKey<>(EncodeConstants.OF10_VERSION_ID,
                44L, MultipartRequestExperimenterCase.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterSerializerKeyFactory.createMultipartRequestSerializerKey(
                EncodeConstants.OF10_VERSION_ID, null);
        comparationKey = new ExperimenterIdSerializerKey<>(EncodeConstants.OF10_VERSION_ID,
                null, MultipartRequestExperimenterCase.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterSerializerKeyFactory.createMultipartRequestTFSerializerKey(
                EncodeConstants.OF10_VERSION_ID, 45L);
        comparationKey = new ExperimenterIdSerializerKey<>(EncodeConstants.OF10_VERSION_ID,
                45L, TableFeatureProperties.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterSerializerKeyFactory.createMultipartRequestTFSerializerKey(
                EncodeConstants.OF10_VERSION_ID, null);
        comparationKey = new ExperimenterIdSerializerKey<>(EncodeConstants.OF10_VERSION_ID,
                null, TableFeatureProperties.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
    }
}