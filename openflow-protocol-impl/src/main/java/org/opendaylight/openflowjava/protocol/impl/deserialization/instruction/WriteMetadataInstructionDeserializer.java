/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.instruction;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.InstructionConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.InstructionBuilder;

/**
 * @author michal.polkorab
 *
 */
public class WriteMetadataInstructionDeserializer extends AbstractInstructionDeserializer {

    @Override
    public Instruction deserialize(ByteBuf input) {
        InstructionBuilder builder = new InstructionBuilder();
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setType(WriteMetadata.class);
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        input.skipBytes(InstructionConstants.PADDING_IN_WRITE_METADATA);
        MetadataInstructionBuilder metadataBuilder = new MetadataInstructionBuilder();
        byte[] metadata = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        input.readBytes(metadata);
        metadataBuilder.setMetadata(metadata);
        byte[] metadata_mask = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        input.readBytes(metadata_mask);
        metadataBuilder.setMetadataMask(metadata_mask);
        builder.addAugmentation(MetadataInstruction.class, metadataBuilder.build());
        return builder.build();
    }
}
