/*
 * Copyright (c) 2015 NetIDE Consortium and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.keys.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.keys.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev150203.action.grouping.action.choice.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev150203.action.grouping.action.choice.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev150203.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev150203.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instruction.grouping.instruction.choice.ApplyActionsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instruction.grouping.instruction.choice.GotoTableCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instruction.grouping.instruction.choice.WriteMetadataCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instruction.grouping.instruction.choice._goto.table._case.GotoTableBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instruction.grouping.instruction.choice.apply.actions._case.ApplyActionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instruction.grouping.instruction.choice.write.metadata._case.WriteMetadataBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModCommand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.InPhyPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.IpEcn;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.OxmMatchType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entries.grouping.MatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entries.grouping.MatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.InPhyPortCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.IpEcnCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.in.phy.port._case.InPhyPortBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.ip.ecn._case.IpEcnBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.grouping.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInputBuilder;

/**
 * @author giuseppex.petralia@intel.com
 *
 */
public class FlowModInputMessageFactoryTest {
    private FlowModInputMessageFactory flowFactory;

    /**
     * Initializes deserializer registry and lookups correct deserializer
     */
    @Before
    public void startUp() {
        DeserializerRegistry registry = new DeserializerRegistryImpl();
        registry.init();
        flowFactory = registry
                .getDeserializer(new MessageCodeKey(EncodeConstants.OF13_VERSION_ID, 14, FlowModInput.class));
    }

    @Test
    public void test() throws Exception {
        FlowModInput expectedMessage = createMessage();
        SerializerRegistry registry = new SerializerRegistryImpl();
        registry.init();
        OFSerializer<FlowModInput> serializer = registry
                .getSerializer(new MessageTypeKey<>(EncodeConstants.OF13_VERSION_ID, FlowModInput.class));
        ByteBuf originalBuffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(expectedMessage, originalBuffer);

        // TODO: Skipping first 4 bytes due to the way deserializer is
        // implemented
        // Skipping version, type and length from OF header
        originalBuffer.skipBytes(4);
        FlowModInput deserializedMessage = BufferHelper.deserialize(flowFactory, originalBuffer);
        Assert.assertEquals("Wrong version", expectedMessage.getVersion(), deserializedMessage.getVersion());
        Assert.assertEquals("Wrong XId", expectedMessage.getXid(), deserializedMessage.getXid());
        Assert.assertEquals("Wrong cookie", expectedMessage.getCookie(), deserializedMessage.getCookie());
        Assert.assertEquals("Wrong cookie mask", expectedMessage.getCookieMask(), deserializedMessage.getCookieMask());
        Assert.assertEquals("Wrong table id", expectedMessage.getTableId().getValue(),
                deserializedMessage.getTableId().getValue());
        Assert.assertEquals("Wrong command", expectedMessage.getCommand().getIntValue(),
                deserializedMessage.getCommand().getIntValue());
        Assert.assertEquals("Wrong idle timeout", expectedMessage.getIdleTimeout(),
                deserializedMessage.getIdleTimeout());
        Assert.assertEquals("Wrong hard timeout", expectedMessage.getHardTimeout(),
                deserializedMessage.getHardTimeout());
        Assert.assertEquals("Wrong priority", expectedMessage.getPriority(), deserializedMessage.getPriority());
        Assert.assertEquals("Wrong buffer id ", expectedMessage.getBufferId(), deserializedMessage.getBufferId());
        Assert.assertEquals("Wrong out port", expectedMessage.getOutPort().getValue(),
                deserializedMessage.getOutPort().getValue());
        Assert.assertEquals("Wrong out group", expectedMessage.getOutGroup(), deserializedMessage.getOutGroup());
        Assert.assertEquals("Wrong number of flags", expectedMessage.getFlags().getValue().length,
                deserializedMessage.getFlags().getValue().length);
        for (int i = 0; i < expectedMessage.getFlags().getValue().length; i++) {
            Assert.assertEquals("Wrong flag", expectedMessage.getFlags().getValue()[i],
                    deserializedMessage.getFlags().getValue()[i]);
        }
        Assert.assertEquals("Wrong match", expectedMessage.getMatch(), deserializedMessage.getMatch());
        Assert.assertEquals("Wrong number of instructions", expectedMessage.getInstruction().size(),
                deserializedMessage.getInstruction().size());
        int i = 0;
        for (Instruction ins : expectedMessage.getInstruction()) {
            Assert.assertEquals("Wrong instruction", ins, deserializedMessage.getInstruction().get(i));
            i++;
        }
    }

    private FlowModInput createMessage() throws Exception {
        FlowModInputBuilder builder = new FlowModInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        byte[] cookie = new byte[] { (byte) 0xFF, 0x01, 0x04, 0x01, 0x06, 0x00, 0x07, 0x01 };
        builder.setCookie(new BigInteger(1, cookie));
        byte[] cookieMask = new byte[] { (byte) 0xFF, 0x05, 0x00, 0x00, 0x09, 0x30, 0x00, 0x30 };
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
        List<MatchEntry> entries = new ArrayList<>();
        MatchEntryBuilder entriesBuilder = new MatchEntryBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(InPhyPort.class);
        entriesBuilder.setHasMask(false);
        InPhyPortCaseBuilder inPhyPortCaseBuilder = new InPhyPortCaseBuilder();
        InPhyPortBuilder inPhyPortBuilder = new InPhyPortBuilder();
        inPhyPortBuilder.setPortNumber(new PortNumber(42L));
        inPhyPortCaseBuilder.setInPhyPort(inPhyPortBuilder.build());
        entriesBuilder.setMatchEntryValue(inPhyPortCaseBuilder.build());
        entries.add(entriesBuilder.build());
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(IpEcn.class);
        entriesBuilder.setHasMask(false);
        IpEcnCaseBuilder ipEcnCaseBuilder = new IpEcnCaseBuilder();
        IpEcnBuilder ipEcnBuilder = new IpEcnBuilder();
        ipEcnBuilder.setEcn((short) 4);
        ipEcnCaseBuilder.setIpEcn(ipEcnBuilder.build());
        entriesBuilder.setMatchEntryValue(ipEcnCaseBuilder.build());
        entries.add(entriesBuilder.build());
        matchBuilder.setMatchEntry(entries);
        builder.setMatch(matchBuilder.build());
        List<Instruction> instructions = new ArrayList<>();
        InstructionBuilder insBuilder = new InstructionBuilder();
        GotoTableCaseBuilder goToCaseBuilder = new GotoTableCaseBuilder();
        GotoTableBuilder instructionBuilder = new GotoTableBuilder();
        instructionBuilder.setTableId((short) 43);
        goToCaseBuilder.setGotoTable(instructionBuilder.build());
        insBuilder.setInstructionChoice(goToCaseBuilder.build());
        instructions.add(insBuilder.build());
        WriteMetadataCaseBuilder metadataCaseBuilder = new WriteMetadataCaseBuilder();
        WriteMetadataBuilder metadataBuilder = new WriteMetadataBuilder();
        metadataBuilder.setMetadata(cookie);
        metadataBuilder.setMetadataMask(cookieMask);
        metadataCaseBuilder.setWriteMetadata(metadataBuilder.build());
        insBuilder.setInstructionChoice(metadataCaseBuilder.build());
        instructions.add(insBuilder.build());
        insBuilder = new InstructionBuilder();
        ApplyActionsCaseBuilder applyActionsCaseBuilder = new ApplyActionsCaseBuilder();
        ApplyActionsBuilder actionsBuilder = new ApplyActionsBuilder();
        List<Action> actions = new ArrayList<>();
        ActionBuilder actionBuilder = new ActionBuilder();
        OutputActionCaseBuilder caseBuilder = new OutputActionCaseBuilder();
        OutputActionBuilder outputBuilder = new OutputActionBuilder();
        outputBuilder.setPort(new PortNumber(42L));
        outputBuilder.setMaxLength(52);
        caseBuilder.setOutputAction(outputBuilder.build());
        actionBuilder.setActionChoice(caseBuilder.build());
        actions.add(actionBuilder.build());
        actionsBuilder.setAction(actions);
        applyActionsCaseBuilder.setApplyActions(actionsBuilder.build());
        insBuilder.setInstructionChoice(applyActionsCaseBuilder.build());
        instructions.add(insBuilder.build());
        builder.setInstruction(instructions);
        return builder.build();
    }
}
