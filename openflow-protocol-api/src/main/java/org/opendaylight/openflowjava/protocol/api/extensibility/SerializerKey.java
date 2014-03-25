/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.api.extensibility;

import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 * 
 * Class used for lookup in Serializer collection
 */
public class SerializerKey {

    private final int version;
    private final DataObject object;

    /**
     * Constructor 
     * @param version Openflow protocol wire version
     * @param object Object to be serialized
     */
    public SerializerKey(int version, DataObject object) {
        this.version = version;
        this.object = object;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((object == null) ? 0 : object.hashCode());
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
        SerializerKey other = (SerializerKey) obj;
        if (object == null) {
            if (other.object != null)
                return false;
        } else if (!object.equals(other.object))
            return false;
        if (version != other.version)
            return false;
        return true;
    }

}
