/*
 * Copyright (c) 2016 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.tools;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.opendaylight.openflowjava.protocol.impl.clients.CallableClient;
import org.opendaylight.openflowjava.protocol.impl.clients.ScenarioFactory;
import org.opendaylight.openflowjava.protocol.impl.clients.ScenarioHandler;
import org.slf4j.LoggerFactory;

/**
 * ConnectionTestTool class, utilities for testing device's connect
 * @author Jozef Bacigal
 * Date: 4.3.2016.
 */
public class ConnectionTestTool {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ConnectionTestTool.class);

    /**
     * Helper class to defining command line parameters
     * Parameters:
     * --device-count           : number of devices connection to the controller
     * --controller-ip          : controller IP address
     * --ssl                    :
     * --threads                : number of thread shall be used for executor
     * --port
     * --timeout                : timeout in seconds for whole test
     * --scenarioTries          : number of tries of each step of scenario
     * --timeBetweenScenario    : time in milliseconds between tries of steps of scenario
     * --configurationName      : required parameter if using configuration load or configuration save
     * --configurationLoad
     * --configurationSave
     * --xmlScenarioFile        : xml scenario file
     */
    public static class Params {

        @Arg(dest = "controller-ip")
        public String controllerIP;

        @Arg(dest = "devices-count")
        public int deviceCount;

        @Arg(dest = "ssl")
        public boolean ssl;

        @Arg(dest = "threads")
        public int threads;

        @Arg(dest = "port")
        public int port;

        @Arg(dest = "timeout")
        public int timeout;

        @Arg(dest = "freeze")
        public int freeze;

        @Arg(dest = "sleep")
        public long sleep;

        @Arg(dest = "configuration-name")
        public String configurationName;

        @Arg(dest = "configuration-save")
        public boolean configurationSave;

        @Arg(dest = "configuration-load")
        public boolean configurationLoad;

        @Arg(dest = "scenario-name")
        public String scenarioName;

        @Arg(dest = "scenario-file")
        public String scenarioFile;

        static ArgumentParser getParser() {
            final ArgumentParser parser = ArgumentParsers.newArgumentParser("openflowjava test-tool");

            parser.description("Openflowjava switch-controller connector simulator");

            parser.addArgument("--device-count")
                    .type(Integer.class)
                    .setDefault(1)
                    .help("Number of simulated switches. Has to be more than 0")
                    .dest("devices-count");

            parser.addArgument("--controller-ip")
                    .type(String.class)
                    .setDefault("127.0.0.1")
                    .help("ODL controller ip address")
                    .dest("controller-ip");

            parser.addArgument("--ssl")
                    .type(Boolean.class)
                    .setDefault(false)
                    .help("Use secured connection")
                    .dest("ssl");

            parser.addArgument("--threads")
                    .type(Integer.class)
                    .setDefault(1)
                    .help("Number of threads: MAX 1024")
                    .dest("threads");

            parser.addArgument("--port")
                    .type(Integer.class)
                    .setDefault(6653)
                    .help("Connection port")
                    .dest("port");

            parser.addArgument("--timeout")
                    .type(Integer.class)
                    .setDefault(60)
                    .help("Timeout in seconds")
                    .dest("timeout");

            parser.addArgument("--scenarioTries")
                    .type(Integer.class)
                    .setDefault(3)
                    .help("Number of tries in scenario, while waiting for response")
                    .dest("freeze");

            parser.addArgument("--timeBetweenScenario")
                    .type(Long.class)
                    .setDefault(100)
                    .help("Waiting time in milliseconds between tries.")
                    .dest("sleep");

            parser.addArgument("--configurationName")
                    .type(String.class)
                    .setDefault("")
                    .help("Configuration name.")
                    .dest("configuration-name");

            parser.addArgument("--configurationLoad")
                    .type(Boolean.class)
                    .setDefault(false)
                    .help("Try to load configuration from configuration file.")
                    .dest("configuration-load");

            parser.addArgument("--configurationSave")
                    .type(Boolean.class)
                    .setDefault(false)
                    .help("Save the actual configuration into configuration file.")
                    .dest("configuration-save");

            parser.addArgument("--scenarioName")
                    .type(String.class)
                    .setDefault("")
                    .help("Scenario name it should be run")
                    .dest("scenario-name");

            parser.addArgument("--xmlScenarioFile")
                    .type(String.class)
                    .setDefault("")
                    .help("Scenario XML file")
                    .dest("scenario-file");

            return parser;
        }

        void validate() {
            checkArgument(deviceCount > 0, "Switch count has to be > 0");
            checkArgument(threads > 0 && threads < 1024, "Switch count has to be > 0 and < 1024");
            if (configurationSave) {
                checkArgument(!configurationName.isEmpty(), "Cannot save configuration without a configuration name.");
            }
            if (configurationLoad) {
                checkArgument(!configurationName.isEmpty(), "Cannot load configuration without a configuration name.");
            }
            checkArgument(!scenarioName.isEmpty(),"Scenario name cannot be empty.");
            if (!scenarioFile.isEmpty()) {
                checkArgument(Files.exists(Paths.get(scenarioFile)), "XML file does not exists");
                checkArgument(!Files.isDirectory(Paths.get(scenarioFile)), "XML file is not a file but directory");
                checkArgument(Files.isReadable(Paths.get(scenarioFile)), "XML file is not readable.");
            }
        }
    }

    public static void main(final String[] args) {

        List<Callable<Boolean>> callableList = new ArrayList<>();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        final ConnectionToolConfigurationService toolConfigurationService = new ConnectionToolConfigurationServiceImpl();

        try {
            Params params = parseArgs(args, Params.getParser());
            params.validate();

            if (params.configurationSave) {
                toolConfigurationService.marshallData(params, params.configurationName);
                LOG.info("Stop tool after configuration save.");
                System.exit(0);
            }

            if (params.configurationLoad) {
                String configurationName = params.configurationName;
                params = toolConfigurationService.unMarshallData(configurationName);
            }

            Verify.verifyNotNull(params,"Parameters are null, tool will stop.");

            for(int loop=0;loop < params.deviceCount; loop++){

                CallableClient cc = new CallableClient(
                        params.port,
                        params.ssl,
                        InetAddress.getByName(params.controllerIP),
                        "Switch no." + String.valueOf(loop),
                        new ScenarioHandler(ScenarioFactory.getScenarioFromXml(params.scenarioName, params.scenarioFile), params.freeze, params.sleep),
                        new Bootstrap(),
                        workerGroup);

                callableList.add(cc);

            }

            ExecutorService executorService = Executors.newFixedThreadPool(params.threads);
            final ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);

            final List<ListenableFuture<Boolean>> listenableFutures = new ArrayList<>();
            for (Callable<Boolean> booleanCallable : callableList) {
               listenableFutures.add(listeningExecutorService.submit(booleanCallable));
            }
            final ListenableFuture<List<Boolean>> summaryFuture = Futures.successfulAsList(listenableFutures);
            Futures.addCallback(summaryFuture, new FutureCallback<List<Boolean>>() {
                @Override
                public void onSuccess(@Nullable final List<Boolean> booleanList) {
                    if (null != booleanList) {
                        LOG.info("Tests finished");
                        workerGroup.shutdownGracefully();
                        LOG.info("Summary:");
                        int testsOK = 0;
                        int testFailure = 0;
                        for (Boolean aBoolean : booleanList) {
                            if (aBoolean) {
                                testsOK++;
                            } else {
                                testFailure++;
                            }
                        }
                        LOG.info("Tests OK: {}", testsOK);
                        LOG.info("Tests failure: {}", testFailure);
                    } else {
                        LOG.warn("Results are null, something went wrong!");
                    }
                    System.exit(0);
                }

                @Override
                public void onFailure(final Throwable throwable) {
                    LOG.warn("Tests call failure");
                    workerGroup.shutdownGracefully();
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            LOG.warn("Exception has been thrown: {}", e);
            System.exit(1);
        }
    }

    private static Params parseArgs(final String[] args, final ArgumentParser parser) throws ArgumentParserException {
        final Params opt = new Params();
        parser.parseArgs(args, opt);
        return opt;
    }


}
