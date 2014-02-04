/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.MultipartRequestInputFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.MultipartRequestInputFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.InstructionRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.InstructionRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NextTableRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NextTableRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.table.features.properties.container.table.feature.properties.NextTableIds;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.table.features.properties.container.table.feature.properties.NextTableIdsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ClearActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.GotoTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Meter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableFeaturesPropType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPhyPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.IpEcn;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.IpProto;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm0Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestTableFeaturesCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.table.features._case.MultipartRequestTableFeaturesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.table.features._case.multipart.request.table.features.TableFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.table.features._case.multipart.request.table.features.TableFeaturesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeatureProperties;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeaturePropertiesBuilder;

/**
 * @author michal.polkorab
 *
 */
public class TableFeaturesTest {
    private static final byte PADDING_IN_MULTIPART_REQUEST_MESSAGE =
            MultipartRequestInputFactoryTest.PADDING_IN_MULTIPART_REQUEST_MESSAGE;

    /**
     * @throws Exception
     * Testing of {@link MultipartRequestInputFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestTableFeaturesMessageFactory() throws Exception {
        MultipartRequestInputBuilder builder = new MultipartRequestInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setType(MultipartType.forValue(12));
        builder.setFlags(new MultipartRequestFlags(true));
        MultipartRequestTableFeaturesCaseBuilder caseBuilder = new MultipartRequestTableFeaturesCaseBuilder();
        MultipartRequestTableFeaturesBuilder featuresBuilder = new MultipartRequestTableFeaturesBuilder();
        List<TableFeatures> tableFeaturesList = new ArrayList<>();
        TableFeaturesBuilder tableFeaturesBuilder = new TableFeaturesBuilder();
        tableFeaturesBuilder.setTableId((short) 8);
        tableFeaturesBuilder.setName("AAAABBBBCCCCDDDDEEEEFFFFGGGG");
        tableFeaturesBuilder.setMetadataMatch(new BigInteger(new byte[] {0x00, 0x01, 0x02, 0x03, 0x01, 0x04, 0x08, 0x01}));
        tableFeaturesBuilder.setMetadataWrite(new BigInteger(new byte[] {0x00, 0x07, 0x01, 0x05, 0x01, 0x00, 0x03, 0x01}));
        tableFeaturesBuilder.setConfig(new TableConfig(true));
        tableFeaturesBuilder.setMaxEntries(65L);
        List<TableFeatureProperties> properties = new ArrayList<>();
        TableFeaturePropertiesBuilder propBuilder = new TableFeaturePropertiesBuilder();
        propBuilder.setType(TableFeaturesPropType.OFPTFPTNEXTTABLES);
        NextTableRelatedTableFeaturePropertyBuilder nextPropBuilder =
                new NextTableRelatedTableFeaturePropertyBuilder();
        List<NextTableIds> nextIds = new ArrayList<>();
        nextIds.add(new NextTableIdsBuilder().setTableId((short) 1).build());
        nextIds.add(new NextTableIdsBuilder().setTableId((short) 2).build());
        nextPropBuilder.setNextTableIds(nextIds);
        propBuilder.addAugmentation(NextTableRelatedTableFeatureProperty.class, nextPropBuilder.build());
        properties.add(propBuilder.build());
        propBuilder = new TableFeaturePropertiesBuilder();
        propBuilder.setType(TableFeaturesPropType.OFPTFPTNEXTTABLESMISS);
        nextPropBuilder = new NextTableRelatedTableFeaturePropertyBuilder();
        nextIds = new ArrayList<>();
        nextIds.add(new NextTableIdsBuilder().setTableId((short) 3).build());
        nextPropBuilder.setNextTableIds(nextIds);
        propBuilder.addAugmentation(NextTableRelatedTableFeatureProperty.class, nextPropBuilder.build());
        properties.add(propBuilder.build());
        propBuilder = new TableFeaturePropertiesBuilder();
        propBuilder.setType(TableFeaturesPropType.OFPTFPTINSTRUCTIONS);
        InstructionRelatedTableFeaturePropertyBuilder insPropBuilder =
                new InstructionRelatedTableFeaturePropertyBuilder();
        List<Instruction> insIds = new ArrayList<>();
        InstructionBuilder insBuilder = new InstructionBuilder();
        insBuilder.setType(WriteActions.class);
        insIds.add(insBuilder.build());
        insBuilder = new InstructionBuilder();
        insBuilder.setType(GotoTable.class);
        insIds.add(insBuilder.build());
        insPropBuilder.setInstruction(insIds);
        propBuilder.addAugmentation(InstructionRelatedTableFeatureProperty.class, insPropBuilder.build());
        properties.add(propBuilder.build());
        propBuilder = new TableFeaturePropertiesBuilder();
        propBuilder.setType(TableFeaturesPropType.OFPTFPTINSTRUCTIONSMISS);
        insPropBuilder = new InstructionRelatedTableFeaturePropertyBuilder();
        insIds = new ArrayList<>();
        insBuilder = new InstructionBuilder();
        insBuilder.setType(WriteMetadata.class);
        insIds.add(insBuilder.build());
        insBuilder = new InstructionBuilder();
        insBuilder.setType(ApplyActions.class);
        insIds.add(insBuilder.build());
        insBuilder = new InstructionBuilder();
        insBuilder.setType(Meter.class);
        insIds.add(insBuilder.build());
        insBuilder = new InstructionBuilder();
        insBuilder.setType(ClearActions.class);
        insIds.add(insBuilder.build());
        insBuilder = new InstructionBuilder();
        insBuilder.setType(GotoTable.class);
        insIds.add(insBuilder.build());
        insPropBuilder.setInstruction(insIds);
        propBuilder.addAugmentation(InstructionRelatedTableFeatureProperty.class, insPropBuilder.build());
        properties.add(propBuilder.build());
        tableFeaturesBuilder.setTableFeatureProperties(properties);
        tableFeaturesList.add(tableFeaturesBuilder.build());
        tableFeaturesBuilder = new TableFeaturesBuilder();
        tableFeaturesBuilder.setTableId((short) 8);
        tableFeaturesBuilder.setName("AAAABBBBCCCCDDDDEEEEFFFFGGGG");
        tableFeaturesBuilder.setMetadataMatch(new BigInteger(new byte[] {0x00, 0x01, 0x02, 0x03, 0x01, 0x04, 0x08, 0x01}));
        tableFeaturesBuilder.setMetadataWrite(new BigInteger(new byte[] {0x00, 0x07, 0x01, 0x05, 0x01, 0x00, 0x03, 0x01}));
        tableFeaturesBuilder.setConfig(new TableConfig(true));
        tableFeaturesBuilder.setMaxEntries(67L);
        properties = new ArrayList<>();
        propBuilder = new TableFeaturePropertiesBuilder();
        propBuilder.setType(TableFeaturesPropType.OFPTFPTMATCH);
        OxmRelatedTableFeaturePropertyBuilder oxmBuilder = new OxmRelatedTableFeaturePropertyBuilder();
        List<MatchEntries> entries = new ArrayList<>();
        MatchEntriesBuilder entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(InPhyPort.class);
        entriesBuilder.setHasMask(false);
        entries.add(entriesBuilder.build());
        entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(Nxm0Class.class);
        entriesBuilder.setOxmMatchField(InPort.class);
        entriesBuilder.setHasMask(false);
        entries.add(entriesBuilder.build());
        oxmBuilder.setMatchEntries(entries);
        propBuilder.addAugmentation(OxmRelatedTableFeatureProperty.class, oxmBuilder.build());
        properties.add(propBuilder.build());
        propBuilder = new TableFeaturePropertiesBuilder();
        propBuilder.setType(TableFeaturesPropType.OFPTFPTAPPLYSETFIELD);
        oxmBuilder = new OxmRelatedTableFeaturePropertyBuilder();
        entries = new ArrayList<>();
        entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(IpProto.class);
        entriesBuilder.setHasMask(false);
        entries.add(entriesBuilder.build());
        entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(Nxm1Class.class);
        entriesBuilder.setOxmMatchField(IpEcn.class);
        entriesBuilder.setHasMask(false);
        entries.add(entriesBuilder.build());
        oxmBuilder.setMatchEntries(entries);
        propBuilder.addAugmentation(OxmRelatedTableFeatureProperty.class, oxmBuilder.build());
        properties.add(propBuilder.build());
        tableFeaturesBuilder.setTableFeatureProperties(properties);
        tableFeaturesList.add(tableFeaturesBuilder.build());
        featuresBuilder.setTableFeatures(tableFeaturesList);
        caseBuilder.setMultipartRequestTableFeatures(featuresBuilder.build());
        builder.setMultipartRequestBody(caseBuilder.build());
        MultipartRequestInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestInputFactory factory = MultipartRequestInputFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);

        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", 12, out.readUnsignedShort());
        Assert.assertEquals("Wrong flags", 1, out.readUnsignedShort());
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong length", 120, out.readUnsignedShort());
        Assert.assertEquals("Wrong table-id", 8, out.readUnsignedByte());
        out.skipBytes(5);
        Assert.assertEquals("Wrong name", "AAAABBBBCCCCDDDDEEEEFFFFGGGG",
                ByteBufUtils.decodeNullTerminatedString(out, 32));
        byte[] metadataMatch = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        out.readBytes(metadataMatch);
        Assert.assertArrayEquals("Wrong metadata-match",
                new byte[] {0x00, 0x01, 0x02, 0x03, 0x01, 0x04, 0x08, 0x01}, metadataMatch);
        byte[] metadataWrite = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        out.readBytes(metadataWrite);
        Assert.assertArrayEquals("Wrong metadata-write",
                new byte[] {0x00, 0x07, 0x01, 0x05, 0x01, 0x00, 0x03, 0x01}, metadataWrite);
        Assert.assertEquals("Wrong config", 8, out.readUnsignedInt());
        Assert.assertEquals("Wrong max-entries", 65, out.readUnsignedInt());
        Assert.assertEquals("Wrong property type", 2, out.readUnsignedShort());
        Assert.assertEquals("Wrong property length", 6, out.readUnsignedShort());
        Assert.assertEquals("Wrong next-table-id", 1, out.readUnsignedByte());
        Assert.assertEquals("Wrong next-table-id", 2, out.readUnsignedByte());
        out.skipBytes(2);
        Assert.assertEquals("Wrong property type", 3, out.readUnsignedShort());
        Assert.assertEquals("Wrong property length", 5, out.readUnsignedShort());
        Assert.assertEquals("Wrong next-table-id", 3, out.readUnsignedByte());
        out.skipBytes(3);
        Assert.assertEquals("Wrong property type", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong property length", 12, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 3, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 1, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertEquals("Wrong property type", 1, out.readUnsignedShort());
        Assert.assertEquals("Wrong property length", 24, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 2, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 6, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 5, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 1, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong length", 96, out.readUnsignedShort());
        Assert.assertEquals("Wrong table-id", 8, out.readUnsignedByte());
        out.skipBytes(5);
        Assert.assertEquals("Wrong name", "AAAABBBBCCCCDDDDEEEEFFFFGGGG",
                ByteBufUtils.decodeNullTerminatedString(out, 32));
        metadataMatch = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        out.readBytes(metadataMatch);
        Assert.assertArrayEquals("Wrong metadata-match",
                new byte[] {0x00, 0x01, 0x02, 0x03, 0x01, 0x04, 0x08, 0x01}, metadataMatch);
        metadataWrite = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        out.readBytes(metadataWrite);
        Assert.assertArrayEquals("Wrong metadata-write",
                new byte[] {0x00, 0x07, 0x01, 0x05, 0x01, 0x00, 0x03, 0x01}, metadataWrite);
        Assert.assertEquals("Wrong config", 8, out.readUnsignedInt());
        Assert.assertEquals("Wrong max-entries", 67, out.readUnsignedInt());
        Assert.assertEquals("Wrong property type", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong property length", 12, out.readUnsignedShort());
        Assert.assertEquals("Wrong match class", 0x8000, out.readUnsignedShort());
        Assert.assertEquals("Wrong match field&mask", 2, out.readUnsignedByte());
        Assert.assertEquals("Wrong match length", 4, out.readUnsignedByte());
        Assert.assertEquals("Wrong match class", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong match field&mask", 0, out.readUnsignedByte());
        Assert.assertEquals("Wrong match length", 4, out.readUnsignedByte());
        out.skipBytes(4);
        Assert.assertEquals("Wrong property type", 14, out.readUnsignedShort());
        Assert.assertEquals("Wrong property length", 12, out.readUnsignedShort());
        Assert.assertEquals("Wrong match class", 0x8000, out.readUnsignedShort());
        Assert.assertEquals("Wrong match field&mask", 20, out.readUnsignedByte());
        Assert.assertEquals("Wrong match length", 1, out.readUnsignedByte());
        Assert.assertEquals("Wrong match class", 1, out.readUnsignedShort());
        Assert.assertEquals("Wrong match field&mask", 18, out.readUnsignedByte());
        Assert.assertEquals("Wrong match length", 1, out.readUnsignedByte());
        out.skipBytes(4);
        Assert.assertTrue("Unread data", out.readableBytes() == 0);
    }

}
