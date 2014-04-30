/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
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
                return new EnhancedMessageCodeKey(getVersion(), oxmClass,
                        oxmField, MatchEntries.class);
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
                return new MessageCodeKey(getVersion(), type, Action.class);
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
                return new MessageCodeKey(getVersion(), type, Instruction.class);
            }
        };
    }
}
