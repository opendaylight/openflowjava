/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.keys.experimenter;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.ExperimenterActionSubType;

/**
 * @author michal.polkorab
 *
 */
public class ExperimenterActionSerializerKeyTest {

    /**
     * Test ExperimenterActionSerializerKey equals and hashCode
     */
    @Test
    public void test() {
        ExperimenterActionSerializerKey key1 =
                new ExperimenterActionSerializerKey(EncodeConstants.OF10_VERSION_ID, 42L, TestSubType.class);
        ExperimenterActionSerializerKey key2 =
                new ExperimenterActionSerializerKey(EncodeConstants.OF10_VERSION_ID, 42L, TestSubType.class);
        Assert.assertTrue("Wrong equals", key1.equals(key2));
        Assert.assertTrue("Wrong hashcode", key1.hashCode() == key2.hashCode());
        key2 = new ExperimenterActionSerializerKey(EncodeConstants.OF13_VERSION_ID, 42L, TestSubType.class);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashcode", key1.hashCode() == key2.hashCode());
        key2 = new ExperimenterActionSerializerKey(EncodeConstants.OF10_VERSION_ID, null, TestSubType.class);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashcode", key1.hashCode() == key2.hashCode());
        key2 = new ExperimenterActionSerializerKey(EncodeConstants.OF10_VERSION_ID, 55L, TestSubType.class);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashcode", key1.hashCode() == key2.hashCode());
        key2 = new ExperimenterActionSerializerKey(EncodeConstants.OF10_VERSION_ID, 55L, null);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashcode", key1.hashCode() == key2.hashCode());
        key2 = new ExperimenterActionSerializerKey(EncodeConstants.OF10_VERSION_ID, 55L, TestSubType2.class);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashcode", key1.hashCode() == key2.hashCode());
    }

    private static class TestSubType extends ExperimenterActionSubType {
        // empty class - only used in test for comparation
    }

    private static class TestSubType2 extends ExperimenterActionSubType {
        // empty class - only used in test for comparation
    }
}