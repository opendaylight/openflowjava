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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteActions;

/**
 * @author michal.polkorab
 *
 */
public class InstructionSerializerKeyTest {

    /**
     * Test InstructionSerializerKey equals and hashCode
     */
    @Test
    public void test() {
        InstructionSerializerKey<?> key1 =
                new InstructionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, ApplyActions.class, 42L);
                InstructionSerializerKey<?> key2 =
                new InstructionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, ApplyActions.class, 42L);
        Assert.assertTrue("Wrong equals", key1.equals(key2));
        Assert.assertTrue("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new InstructionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, ApplyActions.class, null);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new InstructionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, null, 42L);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new InstructionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, WriteActions.class, 42L);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new InstructionSerializerKey<>(EncodeConstants.OF10_VERSION_ID, ApplyActions.class, 55L);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new InstructionSerializerKey<>(EncodeConstants.OF13_VERSION_ID, ApplyActions.class, 42L);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
    }
}