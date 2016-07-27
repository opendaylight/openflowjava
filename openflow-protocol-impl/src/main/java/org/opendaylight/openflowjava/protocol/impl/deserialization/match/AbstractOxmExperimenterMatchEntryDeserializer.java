/*
 * Copyright (c) 2016 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.deserialization.match;

import io.netty.buffer.ByteBuf;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entries.grouping.MatchEntry;

/**
 * Created by Anil Vishnoi (avishnoi@Brocade.com) on 7/26/16.
 */
public abstract class AbstractOxmExperimenterMatchEntryDeserializer extends AbstractOxmMatchEntryDeserializer{

    @Override
    public MatchEntry deserializeHeader(ByteBuf input) {
        MatchEntry matchEntry = super.deserializeHeader(input);

        //Skip the experimenter Id
        input.skipBytes(EncodeConstants.SIZE_OF_INT_IN_BYTES);
        return matchEntry;
    }

}
