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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;

/**
 * @author michal.polkorab
 *
 */
public final class ExperimenterErrorDeserializerKey extends MessageCodeKey 
        implements ExperimenterDeserializerKey {

    private Long experimenterId;

    /**
     * @param version protocol wire version
     * @param experimenterId experimenter / vendor ID
     */
    public ExperimenterErrorDeserializerKey(short version, Long experimenterId) {
        super(version, EncodeConstants.EXPERIMENTER_VALUE, ErrorMessage.class);
        this.experimenterId = experimenterId;
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((experimenterId == null) ? 0 : experimenterId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof ExperimenterErrorDeserializerKey))
            return false;
        ExperimenterErrorDeserializerKey other = (ExperimenterErrorDeserializerKey) obj;
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