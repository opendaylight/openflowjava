/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import org.openflow.clients.SimpleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michal.polkorab
 */
public class TCPHandlerTest {

    private int port;
    private String address;
    private static final Logger LOGGER = LoggerFactory.getLogger(TCPHandler.class);

    /**
     * Test of run method, of class TCPHandler.
     */
    @org.junit.Test
    public void testRun() {
        System.out.println("# processors: " + Runtime.getRuntime().availableProcessors());
        getFreePortAndAddress();
        TCPHandler tcphandler = new TCPHandler(port);
        tcphandler.start();
        ArrayList<SimpleClient> simpleClients = new ArrayList<>();
        SimpleClient sc;
        for (int i = 0; i < 100; i++) {
            sc = new SimpleClient(address, port, "C:/Users/michal.polkorab/Desktop/oftest.txt");
            simpleClients.add(sc);
            sc.start();
        }
        
        System.out.println("po sc");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        int pocet = tcphandler.getNumberOfConnections();
        LOGGER.debug("pocet: " + pocet);
        //sc.disconnect();
    }

    public void getFreePortAndAddress() {
        try {
            ServerSocket ss = new ServerSocket(0);
            port = ss.getLocalPort();
            InetAddress ia = InetAddress.getLocalHost();
            InetAddress[] all = InetAddress.getAllByName(ia.getHostName());
            address = all[0].getHostAddress();
            ss.close();
        } catch (IOException ex) {
            LOGGER.error(ex.toString());
        }
    }
}
