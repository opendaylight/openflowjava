/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.keys.ActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.ExperimenterActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.ExperimenterInstructionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.InstructionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntrySerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev150225.ExperimenterIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev150225.ExperimenterIdActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev150225.ExperimenterIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev150225.ExperimenterIdInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.ExperimenterActionSubType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Output;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.GotoTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ExperimenterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.ExperimenterClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.InPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.OpenflowBasicClass;

/**
 * @author michal.polkorab
 *
 */
public class TypeKeyMakerFactoryTest {

    /**
     * Tests {@link TypeKeyMakerFactory#createActionKeyMaker(short)}
     */
    @Test
    public void testActionKeyMaker() {
        TypeKeyMaker<Action> keyMaker = TypeKeyMakerFactory.createActionKeyMaker(EncodeConstants.OF13_VERSION_ID);
        Assert.assertNotNull("Null keyMaker", keyMaker);

        ActionBuilder builder = new ActionBuilder();
        builder.setType(Output.class);
        Action action = builder.build();
        MessageTypeKey<?> key = keyMaker.make(action);

        Assert.assertNotNull("Null key", key);
        Assert.assertEquals("Wrong key", new ActionSerializerKey<>(EncodeConstants.OF13_VERSION_ID,
                        Output.class, null), key);
    }

    /**
     * Tests {@link TypeKeyMakerFactory#createActionKeyMaker(short)}
     */
    @Test
    public void testExperimenterActionKeyMaker() {
        TypeKeyMaker<Action> keyMaker = TypeKeyMakerFactory.createActionKeyMaker(EncodeConstants.OF13_VERSION_ID);
        Assert.assertNotNull("Null keyMaker", keyMaker);

        ActionBuilder builder = new ActionBuilder();
        builder.setType(Experimenter.class);
        ExperimenterIdActionBuilder expIdBuilder = new ExperimenterIdActionBuilder();
        expIdBuilder.setExperimenter(new ExperimenterId(42L));
        expIdBuilder.setSubType(ActionSubtypeClass.class);
        builder.addAugmentation(ExperimenterIdAction.class, expIdBuilder.build());
        Action action = builder.build();
        MessageTypeKey<?> key = keyMaker.make(action);

        Assert.assertNotNull("Null key", key);
        Assert.assertEquals("Wrong key", new ExperimenterActionSerializerKey(EncodeConstants.OF13_VERSION_ID, 42L,
                ActionSubtypeClass.class), key);
    }

    /**
     * Tests {@link TypeKeyMakerFactory#createInstructionKeyMaker(short)}
     */
    @Test
    public void testInstructionKeyMaker() {
        TypeKeyMaker<Instruction> keyMaker = TypeKeyMakerFactory.createInstructionKeyMaker(EncodeConstants.OF13_VERSION_ID);
        Assert.assertNotNull("Null keyMaker", keyMaker);

        InstructionBuilder builder = new InstructionBuilder();
        builder.setType(GotoTable.class);
        Instruction instruction = builder.build();
        MessageTypeKey<?> key = keyMaker.make(instruction);

        Assert.assertNotNull("Null key", key);
        Assert.assertEquals("Wrong key", new InstructionSerializerKey<>(EncodeConstants.OF13_VERSION_ID,
                        GotoTable.class, null), key);
    }

    /**
     * Tests {@link TypeKeyMakerFactory#createInstructionKeyMaker(short)}
     */
    @Test
    public void testExperimenterInstructionKeyMaker() {
        TypeKeyMaker<Instruction> keyMaker = TypeKeyMakerFactory.createInstructionKeyMaker(EncodeConstants.OF13_VERSION_ID);
        Assert.assertNotNull("Null keyMaker", keyMaker);

        InstructionBuilder builder = new InstructionBuilder();
        builder.setType(org.opendaylight.yang.gen.v1.urn.opendaylight.openflow
                .common.instruction.rev130731.Experimenter.class);
        ExperimenterIdInstructionBuilder expIdBuilder = new ExperimenterIdInstructionBuilder();
        expIdBuilder.setExperimenter(new ExperimenterId(42L));
        builder.addAugmentation(ExperimenterIdInstruction.class, expIdBuilder.build());
        Instruction instruction = builder.build();
        MessageTypeKey<?> key = keyMaker.make(instruction);

        Assert.assertNotNull("Null key", key);
        Assert.assertEquals("Wrong key", new ExperimenterInstructionSerializerKey(EncodeConstants.OF13_VERSION_ID,
                        42L), key);
    }

    /**
     * Tests {@link TypeKeyMakerFactory#createMatchEntriesKeyMaker(short)}
     */
    @Test
    public void testMatchEntriesKeyMaker() {
        TypeKeyMaker<MatchEntries> keyMaker = TypeKeyMakerFactory.createMatchEntriesKeyMaker(EncodeConstants.OF13_VERSION_ID);
        Assert.assertNotNull("Null keyMaker", keyMaker);

        MatchEntriesBuilder builder = new MatchEntriesBuilder();
        builder.setOxmClass(OpenflowBasicClass.class);
        builder.setOxmMatchField(InPort.class);
        builder.setHasMask(true);
        MatchEntries entry = builder.build();
        MessageTypeKey<?> key = keyMaker.make(entry);

        Assert.assertNotNull("Null key", key);
        MatchEntrySerializerKey<?, ?> comparationKey = new MatchEntrySerializerKey<>(EncodeConstants.OF13_VERSION_ID,
                OpenflowBasicClass.class, InPort.class);
        Assert.assertEquals("Wrong key", comparationKey, key);
    }

    /**
     * Tests {@link TypeKeyMakerFactory#createMatchEntriesKeyMaker(short)}
     */
    @Test
    public void testExperimenterMatchEntriesKeyMaker() {
        TypeKeyMaker<MatchEntries> keyMaker = TypeKeyMakerFactory.createMatchEntriesKeyMaker(EncodeConstants.OF13_VERSION_ID);
        Assert.assertNotNull("Null keyMaker", keyMaker);

        MatchEntriesBuilder builder = new MatchEntriesBuilder();
        builder.setOxmClass(ExperimenterClass.class);
        builder.setOxmMatchField(OxmMatchFieldClass.class);
        builder.setHasMask(true);
        ExperimenterIdMatchEntryBuilder expIdBuilder = new ExperimenterIdMatchEntryBuilder();
        expIdBuilder.setExperimenter(new ExperimenterId(42L));
        builder.addAugmentation(ExperimenterIdMatchEntry.class, expIdBuilder.build());
        MatchEntries entry = builder.build();
        MessageTypeKey<?> key = keyMaker.make(entry);

        Assert.assertNotNull("Null key", key);
        MatchEntrySerializerKey<?, ?> comparationKey = new MatchEntrySerializerKey<>(EncodeConstants.OF13_VERSION_ID,
                ExperimenterClass.class, OxmMatchFieldClass.class);
        comparationKey.setExperimenterId(42L);
        Assert.assertEquals("Wrong key", comparationKey, key);
    }

    private class ActionSubtypeClass extends ExperimenterActionSubType {
        // only for testing purposes
    }

    private class OxmMatchFieldClass extends MatchField {
        // only for testing purposes
    }
}