/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.connection;

import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author mirehak
 *
 */
public interface MessageConsumer {

    /**
     * @param message to process
     */
    public void consume(DataObject message);

}
