package org.openflow.lib;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.DefaultChannelGroup;

import java.util.Iterator;

import org.openflow.lib.TcpHandler.COMPONENT_NAMES;

/**
 * @author michal.polkorab
 *
 */
public class PublishingChannelInitializer extends ChannelInitializer<Channel> {

    private DefaultChannelGroup allChannels;
    
    /**
     * default ctor
     */
    public PublishingChannelInitializer() {
        allChannels = new DefaultChannelGroup("netty-receiver", null);
    }
    
    @Override
    protected void initChannel(Channel ch) throws Exception {
        allChannels.add(ch);
        ch.pipeline().addLast(COMPONENT_NAMES.TLS_DETECTOR.name(), new TlsDetector());
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
