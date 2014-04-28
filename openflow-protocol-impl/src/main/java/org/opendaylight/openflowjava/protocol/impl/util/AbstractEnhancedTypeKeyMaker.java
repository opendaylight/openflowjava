/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageTypeKey;

/**
 * @author michal.polkorab
 * @param <T> type the key maker is based on 
 */
public abstract class AbstractEnhancedTypeKeyMaker<T> implements EnhancedTypeKeyMaker<T> {

    private short version;

    /**
     * @param version openflow wire version
     */
    public AbstractEnhancedTypeKeyMaker(short version) {
        this.version = version;
        
    }

    /**
     * @return the version
     */
    public short getVersion() {
        return version;
    }

    /**
     * @param entry
     * @return
     */
    @Override
    public abstract EnhancedMessageTypeKey<?,?> make(T entry);

}
