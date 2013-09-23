/**
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

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
