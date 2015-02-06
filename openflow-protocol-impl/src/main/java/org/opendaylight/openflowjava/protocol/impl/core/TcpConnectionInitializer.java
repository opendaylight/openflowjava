package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.opendaylight.openflowjava.protocol.api.connection.ThreadConfiguration;
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

    @Override
    public void run() {
        // initiateConnection(...) does the job 
    }

    /**
     * Shuts down {@link TcpConnectionInitializer}
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

    /**
     * Initiates connection towards device
     */
    @Override
    public void initiateConnection(String host, int port) {
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
     * @param channelInitializer
     */
    public void setChannelInitializer(TcpChannelInitializer channelInitializer) {
        this.channelInitializer = channelInitializer;
    }
}
