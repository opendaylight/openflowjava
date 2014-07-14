/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.extensibility.keys;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 *
 */
public class MatchEntryDeserializerKey extends MessageCodeKey {

    private int oxmField;
    private Long experimenterId;

    /**
     * @param version protocol wire version
     * @param objectType class of deserialized object (MatchEntries.class)
     * @param oxmClass oxm_class (see specification)
     * @param oxmField oxm_field (see specification)
     */
    public <CLAZZ extends DataObject> MatchEntryDeserializerKey(short version,
            int oxmClass, int oxmField, Class<CLAZZ> objectType) {
        super(version, oxmClass, objectType);
        this.oxmField = oxmField;
    }

    /**
     * @param experimenterId experimenter / vendor ID
     */
    public void setExperimenterId(Long experimenterId) {
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
        MatchEntryDeserializerKey other = (MatchEntryDeserializerKey) obj;
        if (experimenterId == null) {
            if (other.experimenterId != null)
                return false;
        } else if (!experimenterId.equals(other.experimenterId))
            return false;
        if (oxmField != other.oxmField)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " oxm_field: " + oxmField + " experimenterID: " + experimenterId;
    }
}