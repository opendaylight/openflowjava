package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;

import java.util.Iterator;

import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
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
        allChannels.add(ch);
        //TODO - create inBoundHandler
        if (switchConnectionHandler != null) {
            switchConnectionHandler.onSwitchConnected(ConnectionAdapterFactory.createConnectionAdapter(ch));
            //TODO - check OpenflowProtocolListener, set it to inBoundHandler
        }
        ch.pipeline().addLast(COMPONENT_NAMES.TLS_DETECTOR.name(), new TlsDetector());
        //TODO - chain inBoundHandler to pipe
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
