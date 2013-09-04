package org.openflow.core;

import java.util.Iterator;

import org.openflow.core.TCPHandler.COMPONENT_NAMES;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;

/**
 * @author michal.polkorab
 *
 */
public class PublishingChannelInitializer extends ChannelInitializer<SocketChannel> {

    private DefaultChannelGroup allChannels;
    
    /**
     * default ctor
     */
    public PublishingChannelInitializer() {
        allChannels = new DefaultChannelGroup("netty-receiver", null);
    }
    
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        allChannels.add(ch);
        ch.pipeline().addLast(COMPONENT_NAMES.TLS_DETECTOR.name(), new TLSDetector());
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
    
    

}
