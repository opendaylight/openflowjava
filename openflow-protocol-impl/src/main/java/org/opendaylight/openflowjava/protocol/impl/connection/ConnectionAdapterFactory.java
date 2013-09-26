/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.connection;

import io.netty.channel.socket.SocketChannel;

/**
 * @author mirehak
 * @author michal.polkorab
 */
public abstract class ConnectionAdapterFactory {

    /**
     * @param ch
     * @return connection adapter tcp-implementation
     */
    public static ConnectionFacade createConnectionAdapter(SocketChannel ch) {
        ConnectionAdapterImpl connectionAdapter = new ConnectionAdapterImpl();
        connectionAdapter.setChannel(ch);
        return connectionAdapter;
    }

}
