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
import org.opendaylight.openflowjava.protocol.api.keys.ExperimenterIdDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
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
public class ExperimenterDeserializerKeyFactoryTest {

    /**
     * Test ExperimenterDeserializerKeyFactory key creation
     */
    @Test
    public void test() {
        ExperimenterIdDeserializerKey createdKey = ExperimenterDeserializerKeyFactory
                .createExperimenterErrorDeserializerKey(EncodeConstants.OF10_VERSION_ID, 42L);
        ExperimenterIdDeserializerKey comparationKey =
                new ExperimenterIdDeserializerKey(EncodeConstants.OF10_VERSION_ID, 42L, ErrorMessage.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterDeserializerKeyFactory.createExperimenterErrorDeserializerKey(
                EncodeConstants.OF10_VERSION_ID, null);
        comparationKey = new ExperimenterIdDeserializerKey(EncodeConstants.OF10_VERSION_ID,
                null, ErrorMessage.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterDeserializerKeyFactory.createExperimenterMessageDeserializerKey(
                EncodeConstants.OF10_VERSION_ID, 43L);
        comparationKey = new ExperimenterIdDeserializerKey(EncodeConstants.OF10_VERSION_ID,
                43L, ExperimenterMessage.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterDeserializerKeyFactory.createExperimenterMessageDeserializerKey(
                EncodeConstants.OF10_VERSION_ID, null);
        comparationKey = new ExperimenterIdDeserializerKey(EncodeConstants.OF10_VERSION_ID,
                null, ExperimenterMessage.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterDeserializerKeyFactory.createMeterBandDeserializerKey(
                EncodeConstants.OF10_VERSION_ID, 44L);
        comparationKey = new ExperimenterIdDeserializerKey(EncodeConstants.OF10_VERSION_ID,
                44L, MeterBandExperimenterCase.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterDeserializerKeyFactory.createMeterBandDeserializerKey(
                EncodeConstants.OF10_VERSION_ID, null);
        comparationKey = new ExperimenterIdDeserializerKey(EncodeConstants.OF10_VERSION_ID,
                null, MeterBandExperimenterCase.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterDeserializerKeyFactory.createMultipartReplyMessageDeserializerKey(
                EncodeConstants.OF10_VERSION_ID, 45L);
        comparationKey = new ExperimenterIdDeserializerKey(EncodeConstants.OF10_VERSION_ID,
                45L, MultipartReplyExperimenterCase.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterDeserializerKeyFactory.createMultipartReplyMessageDeserializerKey(
                EncodeConstants.OF10_VERSION_ID, null);
        comparationKey = new ExperimenterIdDeserializerKey(EncodeConstants.OF10_VERSION_ID,
                null, MultipartReplyExperimenterCase.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterDeserializerKeyFactory.createMultipartReplyTFDeserializerKey(
                EncodeConstants.OF10_VERSION_ID, null);
        comparationKey = new ExperimenterIdDeserializerKey(EncodeConstants.OF10_VERSION_ID,
                null, TableFeatureProperties.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterDeserializerKeyFactory.createMultipartReplyTFDeserializerKey(
                EncodeConstants.OF10_VERSION_ID, null);
        comparationKey = new ExperimenterIdDeserializerKey(EncodeConstants.OF10_VERSION_ID,
                null, TableFeatureProperties.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterDeserializerKeyFactory.createQueuePropertyDeserializerKey(
                EncodeConstants.OF10_VERSION_ID, null);
        comparationKey = new ExperimenterIdDeserializerKey(EncodeConstants.OF10_VERSION_ID,
                null, QueueProperty.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
        createdKey = ExperimenterDeserializerKeyFactory.createQueuePropertyDeserializerKey(
                EncodeConstants.OF10_VERSION_ID, null);
        comparationKey = new ExperimenterIdDeserializerKey(EncodeConstants.OF10_VERSION_ID,
                null, QueueProperty.class);
        Assert.assertEquals("Wrong key created", comparationKey, createdKey);
    }
}