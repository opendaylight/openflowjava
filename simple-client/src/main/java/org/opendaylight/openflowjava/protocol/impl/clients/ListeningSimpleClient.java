package org.opendaylight.openflowjava.protocol.impl.clients;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.SettableFuture;

/**
 * Simple client for testing purposes
 *
 * @author michal.polkorab
 */
public class ListeningSimpleClient implements OFClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListeningSimpleClient.class);
    private final int port;
    private boolean securedClient = false;
    private EventLoopGroup workerGroup;
    private SettableFuture<Boolean> isOnlineFuture;
    private SettableFuture<Boolean> scenarioDone;
    private ScenarioHandler scenarioHandler;
    
    /**
     * Constructor of the class
     *
     * @param host address of host
     * @param port host listening port
     */
    public ListeningSimpleClient(int port) {
        this.port = port;
        init();
    }

    private void init() {
        isOnlineFuture = SettableFuture.create();
        scenarioDone = SettableFuture.create();
    }

    /**
     * Starting class of {@link ListeningSimpleClient}
     */
    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        SimpleClientInitializer clientInitializer = new SimpleClientInitializer(isOnlineFuture, securedClient);
        clientInitializer.setScenario(scenarioHandler);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(clientInitializer);

            b.bind(port).sync();
            isOnlineFuture.set(true);

            synchronized (scenarioHandler) {
                LOGGER.debug("WAITING FOR SCENARIO");
                while (! scenarioHandler.isScenarioFinished()) {
                    scenarioHandler.wait();
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            LOGGER.debug("listening client shutting down");
            try {
                workerGroup.shutdownGracefully().get();
                bossGroup.shutdownGracefully().get();
                LOGGER.debug("listening client shutdown succesful");
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        scenarioDone.set(true);
    }

    /**
     * @return close future
     */
    public Future<?> disconnect() {
        LOGGER.debug("disconnecting client");
        return workerGroup.shutdownGracefully();
    }

    @Override
    public void setSecuredClient(boolean securedClient) {
        this.securedClient = securedClient;
    }

    @Override
    public SettableFuture<Boolean> getIsOnlineFuture() {
        return isOnlineFuture;
    }

    @Override
    public SettableFuture<Boolean> getScenarioDone() {
        return scenarioDone;
    }

    @Override
    public void setScenarioHandler(ScenarioHandler scenario) {
        this.scenarioHandler = scenario;
    }
}