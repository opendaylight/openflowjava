package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;

import java.util.Iterator;

import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.impl.connection.ConnectionFacade;
import org.opendaylight.openflowjava.protocol.impl.connection.ConnectionAdapterFactory;
import org.opendaylight.openflowjava.protocol.impl.core.TcpHandler.COMPONENT_NAMES;

/**
 * @author michal.polkorab
 *
 */
public class PublishingChannelInitializer extends ChannelInitializer<SocketChannel> {

    private DefaultChannelGroup allChannels;
    private SwitchConnectionHandler switchConnectionHandler;
    
    /**
     * default ctor
     */
    public PublishingChannelInitializer() {
        allChannels = new DefaultChannelGroup("netty-receiver", null);
    }
    
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // TODO - call switchConnectionHandler accept first
        allChannels.add(ch);
        ConnectionFacade connectionAdapter = null;
        if (switchConnectionHandler != null) {
            connectionAdapter = ConnectionAdapterFactory.createConnectionAdapter(ch);
            switchConnectionHandler.onSwitchConnected(connectionAdapter);
        }
        ch.pipeline().addLast(COMPONENT_NAMES.TLS_DETECTOR.name(), new TlsDetector());
        ch.pipeline().addLast(COMPONENT_NAMES.DELEGATING_INBOUND_HANDLER.name(), new DelegatingInboundHandler(connectionAdapter));
    }
    
    /**
     * @return iterator through active connections
     */
    public Iterator<Channel> getConnectionIterator() {
        return allChannels.iterator();
    }

    /**
     * @return amount of active channels
     */
    public int size() {
        return allChannels.size();
    }
    
    /**
     * @param switchListener the switchListener to set
     */
    public void setSwitchConnectionHandler(SwitchConnectionHandler switchListener) {
        this.switchConnectionHandler = switchListener;
    }
}
