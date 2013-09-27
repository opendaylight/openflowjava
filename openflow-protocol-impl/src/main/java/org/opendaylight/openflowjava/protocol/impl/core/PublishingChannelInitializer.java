package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;

import java.util.Iterator;

import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.impl.connection.ConnectionAdapterFactory;
import org.opendaylight.openflowjava.protocol.impl.connection.ConnectionFacade;
import org.opendaylight.openflowjava.protocol.impl.core.TcpHandler.COMPONENT_NAMES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 *
 */
public class PublishingChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublishingChannelInitializer.class);
    private DefaultChannelGroup allChannels;
    private SwitchConnectionHandler switchConnectionHandler;
    
    /**
     * default ctor
     */
    public PublishingChannelInitializer() {
        allChannels = new DefaultChannelGroup("netty-receiver", null);
    }
    
    @Override
    protected void initChannel(SocketChannel ch) {
        LOGGER.debug("building pipeline");
        // TODO - call switchConnectionHandler accept first
        allChannels.add(ch);
        ConnectionFacade connectionAdapter = null;
        connectionAdapter = ConnectionAdapterFactory.createConnectionAdapter(ch);
        try {
            LOGGER.debug("calling plugin: "+switchConnectionHandler);
            switchConnectionHandler.onSwitchConnected(connectionAdapter);
            connectionAdapter.checkListeners();
            ch.pipeline().addLast(COMPONENT_NAMES.TLS_DETECTOR.name(), new TlsDetector());
            ch.pipeline().addLast(COMPONENT_NAMES.DELEGATING_INBOUND_HANDLER.name(), new DelegatingInboundHandler(connectionAdapter));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            ch.close();
        }
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
     * @param switchConnectionHandler the switchConnectionHandler to set
     */
    public void setSwitchConnectionHandler(SwitchConnectionHandler switchConnectionHandler) {
        this.switchConnectionHandler = switchConnectionHandler;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + "; switchConnectionHandler: "+ switchConnectionHandler;
    }
}
