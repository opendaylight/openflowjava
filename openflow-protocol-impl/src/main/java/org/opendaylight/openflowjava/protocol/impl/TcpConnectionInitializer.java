package org.opendaylight.openflowjava.protocol.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.opendaylight.openflowjava.protocol.api.connection.ThreadConfiguration;
import org.opendaylight.openflowjava.protocol.impl.core.ConnectionInitializer;
import org.opendaylight.openflowjava.protocol.impl.core.ServerFacade;
import org.opendaylight.openflowjava.protocol.impl.core.TcpChannelInitializer;
import org.opendaylight.openflowjava.protocol.impl.core.TcpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

/**
 * Initializes (TCP) connection to device
 * @author martin.uhlir
 *
 */
public class TcpConnectionInitializer implements ServerFacade,
        ConnectionInitializer {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(TcpConnectionInitializer.class);
    private String host;
    private int port;
    private EventLoopGroup workerGroup;
    private ThreadConfiguration threadConfig;

    private TcpChannelInitializer channelInitializer;

    /**
     * Constructor
     * 
     * @param workerGroup
     *            - shared worker group
     */
    public TcpConnectionInitializer(NioEventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    /**
     * Starting class of {@link ListeningSimpleClient}
     */
    @Override
    public void run() {
        if (workerGroup == null) {
            throw new IllegalStateException("Worker group is null.");
        }

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup).channel(NioSocketChannel.class)
                    .handler(channelInitializer);

            b.connect(host, port).sync();
            
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Shuts down {@link TcpHandler}
     */
    @Override
    public ListenableFuture<Boolean> shutdown() {
        final SettableFuture<Boolean> result = SettableFuture.create();
        workerGroup.shutdownGracefully();
        return result;
    }

    @Override
    public ListenableFuture<Boolean> getIsOnlineFuture() {
        return null;
    }

    @Override
    public void setThreadConfig(ThreadConfiguration threadConfig) {
        this.threadConfig = threadConfig;
    }

    @Override
    public void initiateConnection(String host, int port) {
        this.host = host;
        this.port = port;
        run();
    }

    /**
     * @param channelInitializer
     */
    public void setChannelInitializer(TcpChannelInitializer channelInitializer) {
        this.channelInitializer = channelInitializer;
    }
}
