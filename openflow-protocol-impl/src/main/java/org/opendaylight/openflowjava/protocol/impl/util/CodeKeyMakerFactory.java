/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.keys.ActionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.keys.InstructionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.keys.MatchEntryDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * @author michal.polkorab
 *
 */
public abstract class CodeKeyMakerFactory {

    /**
     * @param version
     * @return
     */
    public static CodeKeyMaker createMatchEntriesKeyMaker(short version) {
        return new AbstractCodeKeyMaker(version) {
            @Override
            public MessageCodeKey make(ByteBuf input) {
                int oxmClass = input.getUnsignedShort(input.readerIndex());
                int oxmField = input.getUnsignedByte(input.readerIndex()
                        + EncodeConstants.SIZE_OF_SHORT_IN_BYTES) >>> 1;
                MatchEntryDeserializerKey key = new MatchEntryDeserializerKey(getVersion(),
                        oxmClass, oxmField, MatchEntries.class);
                if (oxmClass == EncodeConstants.EXPERIMENTER_VALUE) {
                    long expId = input.getUnsignedInt(input.readerIndex() + EncodeConstants.SIZE_OF_SHORT_IN_BYTES
                            + 2 * EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
                    key.setExperimenterId(expId);
                    return key;
                }
                key.setExperimenterId(null);
                return key;
            }
        };
    }

    /**
     * @param version
     * @return
     */
    public static CodeKeyMaker createActionsKeyMaker(short version) {
        return new AbstractCodeKeyMaker(version) {
            @Override
            public MessageCodeKey make(ByteBuf input) {
                int type = input.getUnsignedShort(input.readerIndex());
                if (type == EncodeConstants.EXPERIMENTER_VALUE) {
                    Long expId = input.getUnsignedInt(input.readerIndex()
                            + 2 * EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
                    return new ActionDeserializerKey(getVersion(), type, Action.class, expId);
                }
                ActionDeserializerKey actionDeserializerKey = new ActionDeserializerKey(getVersion(), type, Action.class, null);
                return actionDeserializerKey;
            }
        };
    }

    /**
     * @param version
     * @return
     */
    public static CodeKeyMaker createInstructionsKeyMaker(short version) {
        return new AbstractCodeKeyMaker(version) {
            @Override
            public MessageCodeKey make(ByteBuf input) {
                int type = input.getUnsignedShort(input.readerIndex());
                if (type == EncodeConstants.EXPERIMENTER_VALUE) {
                    Long expId = input.getUnsignedInt(input.readerIndex()
                            + 2 * EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
                    return new InstructionDeserializerKey(getVersion(), type, Instruction.class, expId);
                }
                return new InstructionDeserializerKey(getVersion(), type, Instruction.class, null);
            }
        };
    }
}
