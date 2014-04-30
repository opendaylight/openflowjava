/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableFeaturesPropType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeatureProperties;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeaturePropertiesBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF13TableFeatExpSerializerTest {

    /**
     * Testing of {@link OF13TableFeatExpSerializer} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testExperimenter() {
        TableFeaturePropertiesBuilder builder = new TableFeaturePropertiesBuilder();
        builder.setType(TableFeaturesPropType.OFPTFPTEXPERIMENTER);
        ExperimenterRelatedTableFeaturePropertyBuilder propBuilder =
                new ExperimenterRelatedTableFeaturePropertyBuilder();
        propBuilder.setExperimenter(891L);
        propBuilder.setExpType(6546L);
        byte[] data = new byte[]{0, 1, 2, 3};
        propBuilder.setData(data);
        builder.addAugmentation(ExperimenterRelatedTableFeatureProperty.class,
                propBuilder.build());
        TableFeatureProperties property = builder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF13TableFeatExpSerializer serializer = new OF13TableFeatExpSerializer();
        serializer.serialize(property, buffer);

        Assert.assertEquals("Wrong type", 65534, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong length", 16, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong experimenter", 891, buffer.readUnsignedInt());
        Assert.assertEquals("Wrong exp-type", 6546, buffer.readUnsignedInt());
        byte[] expData = new byte[4];
        buffer.readBytes(expData);
        Assert.assertArrayEquals("Wrong experimenter data", data, expData);
        Assert.assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Testing of {@link OF13TableFeatExpSerializer} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testExperimenterWithoutData() {
        TableFeaturePropertiesBuilder builder = new TableFeaturePropertiesBuilder();
        builder.setType(TableFeaturesPropType.OFPTFPTEXPERIMENTER);
        ExperimenterRelatedTableFeaturePropertyBuilder propBuilder =
                new ExperimenterRelatedTableFeaturePropertyBuilder();
        propBuilder.setExperimenter(891L);
        propBuilder.setExpType(6546L);
        builder.addAugmentation(ExperimenterRelatedTableFeatureProperty.class,
                propBuilder.build());
        TableFeatureProperties property = builder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF13TableFeatExpSerializer serializer = new OF13TableFeatExpSerializer();
        serializer.serialize(property, buffer);

        Assert.assertEquals("Wrong type", 65534, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong length", 16, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong experimenter", 891, buffer.readUnsignedInt());
        Assert.assertEquals("Wrong exp-type", 6546, buffer.readUnsignedInt());
        buffer.skipBytes(4);
        Assert.assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Testing of {@link OF13TableFeatExpSerializer} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testExperimenterMiss() {
        TableFeaturePropertiesBuilder builder = new TableFeaturePropertiesBuilder();
        builder.setType(TableFeaturesPropType.OFPTFPTEXPERIMENTERMISS);
        ExperimenterRelatedTableFeaturePropertyBuilder propBuilder =
                new ExperimenterRelatedTableFeaturePropertyBuilder();
        propBuilder.setExperimenter(8L);
        propBuilder.setExpType(6L);
        byte[] data = new byte[]{4, 5, 6, 7};
        propBuilder.setData(data);
        builder.addAugmentation(ExperimenterRelatedTableFeatureProperty.class,
                propBuilder.build());
        TableFeatureProperties property = builder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF13TableFeatExpSerializer serializer = new OF13TableFeatExpSerializer();
        serializer.serialize(property, buffer);

        Assert.assertEquals("Wrong type", 65535, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong length", 16, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong experimenter", 8, buffer.readUnsignedInt());
        Assert.assertEquals("Wrong exp-type", 6, buffer.readUnsignedInt());
        byte[] expData = new byte[4];
        buffer.readBytes(expData);
        Assert.assertArrayEquals("Wrong experimenter data", data, expData);
        Assert.assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Testing of {@link OF13TableFeatExpSerializer} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testExperimenterMissWithoutData() {
        TableFeaturePropertiesBuilder builder = new TableFeaturePropertiesBuilder();
        builder.setType(TableFeaturesPropType.OFPTFPTEXPERIMENTERMISS);
        ExperimenterRelatedTableFeaturePropertyBuilder propBuilder =
                new ExperimenterRelatedTableFeaturePropertyBuilder();
        propBuilder.setExperimenter(8L);
        propBuilder.setExpType(6L);
        builder.addAugmentation(ExperimenterRelatedTableFeatureProperty.class,
                propBuilder.build());
        TableFeatureProperties property = builder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF13TableFeatExpSerializer serializer = new OF13TableFeatExpSerializer();
        serializer.serialize(property, buffer);

        Assert.assertEquals("Wrong type", 65535, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong length", 16, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong experimenter", 8, buffer.readUnsignedInt());
        Assert.assertEquals("Wrong exp-type", 6, buffer.readUnsignedInt());
        buffer.skipBytes(4);
        Assert.assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }
}
