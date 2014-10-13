/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.keys;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.CopyTtlIn;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.CopyTtlOut;

/**
 * @author michal.polkorab
 *
 */
public class ActionSerializerKeyTest {

    /**
     * Test ActionSerializerKey equals and hashCode
     */
    @Test
    public void test() {
        ActionSerializerKey<CopyTtlIn> key1 =
                new ActionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, CopyTtlIn.class, 42L);
        ActionSerializerKey<?> key2 =
                new ActionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, CopyTtlIn.class, 42L);
        Assert.assertTrue("Wrong equals", key1.equals(key2));
        Assert.assertTrue("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new ActionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, CopyTtlIn.class, null);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new ActionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, null, null);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new ActionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, CopyTtlOut.class, 42L);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new ActionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, CopyTtlIn.class, 55L);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new ActionSerializerKey<>(EncodeConstants.OF13_VERSION_ID, CopyTtlIn.class, 55L);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
    }
    
    /**
     * Test ActionDeserializerKey equals - additional test
     */
    @Test
    public void testEquals(){
    	 ActionSerializerKey<CopyTtlIn> key1 = new ActionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, null, 42L);
         ActionSerializerKey<?> key2 = new ActionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, CopyTtlIn.class, 42L);
         
         Assert.assertTrue("Wrong equal to identical object.", key1.equals(key1));
         
         Assert.assertFalse("Wrong equal by actionType", key1.equals(key2));
         
         key1 = new ActionSerializerKey<>(EncodeConstants.OF10_VERSION_ID,  CopyTtlIn.class, null);
         Assert.assertFalse("Wrong equal by experimenterId", key1.equals(key2));
    }
}