/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.keys.experimenter;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;

/**
 * @author michal.polkorab
 *
 */
public class ExperimenterActionDeserializerKey extends MessageCodeKey 
        implements ExperimenterDeserializerKey {

    private Long experimenterId;
    /**
     * @param version protocol wire version
     * @param experimenterId experimenter / vendor ID
     */
    public ExperimenterActionDeserializerKey(short version, Long experimenterId) {
        super(version, EncodeConstants.EXPERIMENTER_VALUE, Action.class);
        this.experimenterId = experimenterId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExperimenterActionDeserializerKey other = (ExperimenterActionDeserializerKey) obj;
        if (experimenterId == null) {
            if (other.experimenterId != null)
                return false;
        } else if (!experimenterId.equals(other.experimenterId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " experimenterID: " + experimenterId;
    }
}