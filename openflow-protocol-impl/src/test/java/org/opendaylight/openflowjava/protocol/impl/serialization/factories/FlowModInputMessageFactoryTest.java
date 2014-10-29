/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.keys.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EcnMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EcnMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortNumberMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortNumberMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TableIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TableIdInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Output;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.GotoTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModCommand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPhyPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.IpEcn;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OxmMatchType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.grouping.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInputBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class FlowModInputMessageFactoryTest {
    private static final byte PADDING_IN_FLOW_MOD_MESSAGE = 2;
    private SerializerRegistry registry;
    private OFSerializer<FlowModInput> flowModFactory;

    /**
     * Initializes serializer registry and stores correct factory in field
     */
    @Before
    public void startUp() {
        registry = new SerializerRegistryImpl();
        registry.init();
        flowModFactory = registry.getSerializer(new MessageTypeKey<>(
                EncodeConstants.OF13_VERSION_ID, FlowModInput.class));
    }

    /**
     * @throws Exception 
     * Testing of {@link FlowModInputMessageFactory} for correct translation from POJO
     */
    @Test
    public void testFlowModInputMessageFactory() throws Exception {
        FlowModInputBuilder builder = new FlowModInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        byte[] cookie = new byte[]{(byte) 0xFF, 0x01, 0x04, 0x01, 0x06, 0x00, 0x07, 0x01};
        builder.setCookie(new BigInteger(1, cookie));
        byte[] cookieMask = new byte[]{(byte) 0xFF, 0x05, 0x00, 0x00, 0x09, 0x30, 0x00, 0x30};
        builder.setCookieMask(new BigInteger(1, cookieMask));
        builder.setTableId(new TableId(65L));
        builder.setCommand(FlowModCommand.forValue(2));
        builder.setIdleTimeout(12);
        builder.setHardTimeout(0);
        builder.setPriority(126);
        builder.setBufferId(2L);
        builder.setOutPort(new PortNumber(4422L));
        builder.setOutGroup(98L);
        builder.setFlags(new FlowModFlags(true, false, true, false, true));
        MatchBuilder matchBuilder = new MatchBuilder();
        matchBuilder.setType(OxmMatchType.class);
        List<MatchEntries> entries = new ArrayList<>();
        MatchEntriesBuilder entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(InPhyPort.class);
        entriesBuilder.setHasMask(false);
        PortNumberMatchEntryBuilder portNumberBuilder = new PortNumberMatchEntryBuilder();
        portNumberBuilder.setPortNumber(new PortNumber(42L));
        entriesBuilder.addAugmentation(PortNumberMatchEntry.class, portNumberBuilder.build());
        entries.add(entriesBuilder.build());
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(IpEcn.class);
        entriesBuilder.setHasMask(false);
        EcnMatchEntryBuilder ecnBuilder = new EcnMatchEntryBuilder();
        ecnBuilder.setEcn((short) 4);
        entriesBuilder.addAugmentation(EcnMatchEntry.class, ecnBuilder.build());
        entries.add(entriesBuilder.build());
        matchBuilder.setMatchEntries(entries);
        builder.setMatch(matchBuilder.build());
        List<Instruction> instructions = new ArrayList<>();
        InstructionBuilder insBuilder = new InstructionBuilder();
        insBuilder.setType(GotoTable.class);
        TableIdInstructionBuilder idBuilder = new TableIdInstructionBuilder();
        idBuilder.setTableId((short) 43);
        insBuilder.addAugmentation(TableIdInstruction.class, idBuilder.build());
        instructions.add(insBuilder.build());
        insBuilder.setType(WriteMetadata.class);
        MetadataInstructionBuilder metaBuilder = new MetadataInstructionBuilder();
        metaBuilder.setMetadata(cookie);
        metaBuilder.setMetadataMask(cookieMask);
        insBuilder.addAugmentation(MetadataInstruction.class, metaBuilder.build());
        instructions.add(insBuilder.build());
        insBuilder = new InstructionBuilder();
        insBuilder.setType(ApplyActions.class);
        List<Action> actions = new ArrayList<>();
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Output.class);
        PortActionBuilder port = new PortActionBuilder();
        port.setPort(new PortNumber(42L));
        actionBuilder.addAugmentation(PortAction.class, port.build());
        MaxLengthActionBuilder maxLen = new MaxLengthActionBuilder();
        maxLen.setMaxLength(52);
        actionBuilder.addAugmentation(MaxLengthAction.class, maxLen.build());
        actions.add(actionBuilder.build());
        ActionsInstructionBuilder actionInstructionBuilder = new ActionsInstructionBuilder();
        actionInstructionBuilder.setAction(actions);
        insBuilder.addAugmentation(ActionsInstruction.class, actionInstructionBuilder.build());
        instructions.add(insBuilder.build());
        builder.setInstruction(instructions);
        FlowModInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        flowModFactory.serialize(message, out);

        BufferHelper.checkHeaderV13(out,(byte) 14, 128);
        cookie = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        out.readBytes(cookie);
        Assert.assertEquals("Wrong cookie", message.getCookie(), new BigInteger(1, cookie));
        cookieMask = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        out.readBytes(cookieMask);
        Assert.assertEquals("Wrong cookieMask", message.getCookieMask(), new BigInteger(1,  cookieMask));
        Assert.assertEquals("Wrong tableId", message.getTableId().getValue().intValue(), out.readUnsignedByte());
        Assert.assertEquals("Wrong command", message.getCommand().getIntValue(), out.readUnsignedByte());
        Assert.assertEquals("Wrong idleTimeOut", message.getIdleTimeout().intValue(), out.readShort());
        Assert.assertEquals("Wrong hardTimeOut", message.getHardTimeout().intValue(), out.readShort());
        Assert.assertEquals("Wrong priority", message.getPriority().intValue(), out.readUnsignedShort());
        Assert.assertEquals("Wrong bufferId", message.getBufferId().intValue(), out.readUnsignedInt());
        Assert.assertEquals("Wrong outPort", message.getOutPort().getValue().intValue(), out.readUnsignedInt());
        Assert.assertEquals("Wrong outGroup", message.getOutGroup().intValue(), out.readUnsignedInt());
        Assert.assertEquals("Wrong flags", message.getFlags(), createFlowModFlagsFromBitmap(out.readUnsignedShort()));
        out.skipBytes(PADDING_IN_FLOW_MOD_MESSAGE);
        Assert.assertEquals("Wrong match type", 1, out.readUnsignedShort());
        out.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        Assert.assertEquals("Wrong oxm class", 0x8000, out.readUnsignedShort());
        short fieldAndMask = out.readUnsignedByte();
        Assert.assertEquals("Wrong oxm hasMask", 0, fieldAndMask & 1);
        Assert.assertEquals("Wrong oxm field", 1, fieldAndMask >> 1);
        out.skipBytes(EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        Assert.assertEquals("Wrong oxm value", 42, out.readUnsignedInt());
        Assert.assertEquals("Wrong oxm class", 0x8000, out.readUnsignedShort());
        fieldAndMask = out.readUnsignedByte();
        Assert.assertEquals("Wrong oxm hasMask", 0, fieldAndMask & 1);
        Assert.assertEquals("Wrong oxm field", 9, fieldAndMask >> 1);
        out.skipBytes(EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        Assert.assertEquals("Wrong oxm value", 4, out.readUnsignedByte());
        out.skipBytes(7);
        Assert.assertEquals("Wrong instruction type", 1, out.readUnsignedShort());
        out.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        Assert.assertEquals("Wrong instruction value", 43, out.readUnsignedByte());
        out.skipBytes(3);
        Assert.assertEquals("Wrong instruction type", 2, out.readUnsignedShort());
        out.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        out.skipBytes(EncodeConstants.SIZE_OF_INT_IN_BYTES);
        byte[] cookieRead = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        out.readBytes(cookieRead);
        byte[] cookieMaskRead = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        out.readBytes(cookieMaskRead);
        Assert.assertArrayEquals("Wrong metadata", cookie, cookieRead);
        Assert.assertArrayEquals("Wrong metadata mask", cookieMask, cookieMaskRead);
        Assert.assertEquals("Wrong instruction type", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 24, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertEquals("Wrong action type", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 16, out.readUnsignedShort());
        Assert.assertEquals("Wrong port", 42, out.readUnsignedInt());
        Assert.assertEquals("Wrong max-length", 52, out.readUnsignedShort());
        out.skipBytes(6);
        Assert.assertTrue("Unread data", out.readableBytes() == 0);
    }
    
    private static FlowModFlags createFlowModFlagsFromBitmap(int input){
        final Boolean _oFPFFSENDFLOWREM = (input & (1 << 0)) > 0;
        final Boolean _oFPFFCHECKOVERLAP = (input & (1 << 1)) > 0;
        final Boolean _oFPFFRESETCOUNTS = (input & (1 << 2)) > 0; 
        final Boolean _oFPFFNOPKTCOUNTS = (input & (1 << 3)) > 0;
        final Boolean _oFPFFNOBYTCOUNTS = (input & (1 << 4)) > 0;
        return new FlowModFlags(_oFPFFCHECKOVERLAP, _oFPFFNOBYTCOUNTS, _oFPFFNOPKTCOUNTS, _oFPFFRESETCOUNTS, _oFPFFSENDFLOWREM);
    }

}
