/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageTypeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * Creates KeyMakers
 * @author michal.polkorab
 */
public abstract class EnhancedTypeKeyMakerFactory {

    /**
     * @param version openflow wire version that shall be used
     *  in lookup key
     * @return lookup key
     */
    public static EnhancedTypeKeyMaker<MatchEntries> createMatchEntriesKeyMaker(short version) {
        return new AbstractEnhancedTypeKeyMaker<MatchEntries>(version) {
            @Override
            public EnhancedMessageTypeKey<?, ?> make(MatchEntries entry) {
                return new EnhancedMessageTypeKey<>(
                        getVersion(), entry.getOxmClass(), entry.getOxmMatchField());
            }
        };
    }

    /**
     * @param version openflow wire version that shall be used
     *  in lookup key
     * @return lookup key
     */
    public static EnhancedTypeKeyMaker<Action> createActionKeyMaker(short version) {
        return new AbstractEnhancedTypeKeyMaker<Action>(version) {
            @Override
            public EnhancedMessageTypeKey<?, ?> make(Action entry) {
                return new EnhancedMessageTypeKey<>(getVersion(), Action.class, entry.getType());
            }
        };
    }

    /**
     * @param version openflow wire version that shall be used
     *  in lookup key
     * @return lookup key
     */
    public static EnhancedTypeKeyMaker<Instruction> createInstructionKeyMaker(short version) {
        return new AbstractEnhancedTypeKeyMaker<Instruction>(version) {
            @Override
            public EnhancedMessageTypeKey<?, ?> make(Instruction entry) {
                return new EnhancedMessageTypeKey<>(getVersion(), Instruction.class, entry.getType());
            }
        };
    }
}
