/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.keys;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ActionBase;

/**
 * @author michal.polkorab
 * @param <TYPE> action type
 */
public class ActionSerializerKey<TYPE extends ActionBase> extends MessageTypeKey<Action> {

    private Class<TYPE> actionType;
    private Long experimenterId;

    /**
     * @param msgVersion protocol wire version
     * @param actionType type of action
     * @param experimenterId experimenter / vendor ID
     */
    public ActionSerializerKey(short msgVersion, Class<TYPE> actionType,
            Long experimenterId) {
        super(msgVersion, Action.class);
        this.actionType = actionType;
        this.experimenterId = experimenterId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((actionType == null) ? 0 : actionType.hashCode());
        result = prime * result + ((experimenterId == null) ? 0 : experimenterId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ActionSerializerKey<?> other = (ActionSerializerKey<?>) obj;
        if (actionType == null) {
            if (other.actionType != null) {
                return false;
            }
        } else if (!actionType.equals(other.actionType)) {
            return false;
        }
        if (experimenterId == null) {
            if (other.experimenterId != null) {
                return false;
            }
        } else if (!experimenterId.equals(other.experimenterId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " action type: " + actionType.getName() + " experimenterID: " + experimenterId;
    }
}