/*
 * Copyright (c) 2016 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization.match.ext;

import io.netty.buffer.ByteBuf;
import org.opendaylight.openflowjava.protocol.impl.serialization.match.AbstractOxmMatchEntrySerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev150225.oxm.container.match.entry.value.ExperimenterIdCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entries.grouping.MatchEntry;

/**
 * Created by Anil Vishnoi (avishnoi@Brocade.com) on 7/25/16.
 */
public abstract class AbstractOxmExperimenterMatchEntrySerializer extends AbstractOxmMatchEntrySerializer {

    protected ExperimenterIdCase serializeExperimenterId(MatchEntry matchEntry, ByteBuf out) {
        ExperimenterIdCase expCase = (ExperimenterIdCase) matchEntry.getMatchEntryValue();
        out.writeInt(expCase.getExperimenter().getExperimenter().getValue().intValue());
        return expCase;
    }

    /**
     * @return Experimenter match entry ID
     */
    protected abstract long getExperimenterId();
}

