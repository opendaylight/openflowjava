/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import org.opendaylight.openflowjava.protocol.api.extensibility.keys.ActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.keys.InstructionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.keys.MatchEntrySerializerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ExperimenterClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * Creates KeyMakers
 * @author michal.polkorab
 */
public abstract class TypeKeyMakerFactory {

    /**
     * @param version openflow wire version that shall be used
     *  in lookup key
     * @return lookup key
     */
    public static TypeKeyMaker<MatchEntries> createMatchEntriesKeyMaker(short version) {
        return new AbstractTypeKeyMaker<MatchEntries>(version) {
            @Override
            public MatchEntrySerializerKey<?, ?> make(MatchEntries entry) {
                MatchEntrySerializerKey<?, ?> key;
                key = new MatchEntrySerializerKey<>(getVersion(), entry.getOxmClass(),
                        entry.getOxmMatchField());
                if (entry.getOxmClass().equals(ExperimenterClass.class)) {
                    key.setExperimenterId(entry.getAugmentation(ExperimenterMatchEntry.class)
                            .getExperimenter());
                    return key;
                }
                key.setExperimenterId(null);
                return key;
            }
        };
    }

    /**
     * @param version openflow wire version that shall be used
     *  in lookup key
     * @return lookup key
     */
    public static TypeKeyMaker<Action> createActionKeyMaker(short version) {
        return new AbstractTypeKeyMaker<Action>(version) {
            @Override
            public ActionSerializerKey<?> make(Action entry) {
                if (entry.getType().equals(Experimenter.class)) {
                    return new ActionSerializerKey<>(getVersion(), entry.getType(), entry.getAugmentation(ExperimenterAction.class).getExperimenter());
                }
                return new ActionSerializerKey<>(getVersion(), entry.getType(), null);
            }
        };
    }

    /**
     * @param version openflow wire version that shall be used
     *  in lookup key
     * @return lookup key
     */
    public static TypeKeyMaker<Instruction> createInstructionKeyMaker(short version) {
        return new AbstractTypeKeyMaker<Instruction>(version) {
            @Override
            public InstructionSerializerKey<?> make(Instruction entry) {
                if (entry.getType().equals(org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common
                        .instruction.rev130731.Experimenter.class)) {
                    return new InstructionSerializerKey<>(getVersion(), entry.getType(), entry.getAugmentation(ExperimenterInstruction.class).getExperimenter());
                }
                return new InstructionSerializerKey<>(getVersion(), entry.getType(), null);
            }
        };
    }
}
