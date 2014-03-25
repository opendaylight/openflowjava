/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.api.extensibility;

import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

/**
 * @author michal.polkorab
 *
 */
public class DeserializerKey {

    private final int version;
    private final Object value;
    private final InstanceIdentifier<DataObject> identifier;

    /**
     * Constructor
     * @param version Openflow protocol wire version
     * @param value read value
     * @param identifier identifies parent object (object the call is coming from)
     */
    public DeserializerKey(int version, Object value, InstanceIdentifier<DataObject> identifier){
        this.version = version;
        this.value = value;
        this.identifier = identifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + version;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeserializerKey other = (DeserializerKey) obj;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        if (version != other.version)
            return false;
        return true;
    }

}
