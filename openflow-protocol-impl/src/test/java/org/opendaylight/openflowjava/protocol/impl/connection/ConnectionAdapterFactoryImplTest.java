package org.opendaylight.openflowjava.protocol.impl.connection;

import static org.mockito.Mockito.when;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;

import java.net.InetSocketAddress;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConnectionAdapterFactoryImplTest {

    @Mock ChannelPipeline channnelPipe;
    @Mock Channel channel; 
    @Mock ChannelFuture channelFuture;
    @Mock InetSocketAddress address;
    
    @Before
    public void startUp(){
        MockitoAnnotations.initMocks(this);
        when(channel.pipeline()).thenReturn(channnelPipe);
        when(channel.disconnect()).thenReturn(channelFuture);
    }
    
    @Test
    public void test(){
        ConnectionAdapterFactoryImpl caf = new ConnectionAdapterFactoryImpl();
        ConnectionFacade conn = caf.createConnectionFacade(channel, address);
        
        // otestovat isAlive
    }
}
