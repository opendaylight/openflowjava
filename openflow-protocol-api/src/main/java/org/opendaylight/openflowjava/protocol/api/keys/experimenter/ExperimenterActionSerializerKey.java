/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.keys.experimenter;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ActionBase;

/**
 * @author michal.polkorab
 */
public class ExperimenterActionSerializerKey extends MessageTypeKey<Action> 
        implements ExperimenterSerializerKey {

    private Class<? extends ActionBase> actionType;
    private Long experimenterId;

    /**
     * @param msgVersion protocol wire version
     * @param experimenterId experimenter / vendor ID
     */
    public ExperimenterActionSerializerKey(short msgVersion, Long experimenterId) {
        super(msgVersion, Action.class);
        this.actionType = Experimenter.class;
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
        ExperimenterActionSerializerKey other = (ExperimenterActionSerializerKey) obj;
        if (actionType == null) {
            if (other.actionType != null)
                return false;
        } else if (!actionType.equals(other.actionType))
            return false;
        if (experimenterId == null) {
            if (other.experimenterId != null)
                return false;
        } else if (!experimenterId.equals(other.experimenterId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " action type: " + actionType.getName() + " experimenterID: " + experimenterId;
    }
}