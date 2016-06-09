/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.clients;

import io.netty.channel.ChannelHandlerContext;

import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michal.polkorab
 *
 */
public class ScenarioHandler extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioHandler.class);
    private Deque<ClientEvent> scenario;
    private BlockingQueue<byte[]> ofMsg;
    private ChannelHandlerContext ctx;
    private int eventNumber;
    private boolean scenarioFinished = false;

    /**
     *
     * @param scenario
     */
    public ScenarioHandler(Deque<ClientEvent> scenario) {
        this.scenario = scenario;
        ofMsg = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        int freezeCounter = 0;
        while (!scenario.isEmpty()) {
            LOG.debug("Running event #{}", eventNumber);
            ClientEvent peek = scenario.peekLast();
            if (peek instanceof WaitForMessageEvent) {
                LOG.debug("WaitForMessageEvent");
                try {
                    WaitForMessageEvent event = (WaitForMessageEvent) peek;
                    event.setHeaderReceived(ofMsg.poll(2000, TimeUnit.MILLISECONDS));
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage(), e);
                    break;
                }
            } else if (peek instanceof SendEvent) {
                LOG.debug("Proceed - sendevent");
                SendEvent event = (SendEvent) peek;
                event.setCtx(ctx);
            }
            if (peek.eventExecuted()) {
                scenario.removeLast();
                eventNumber++;
                freezeCounter = 0;
            } else {
                freezeCounter++;
            }
            if (freezeCounter > 2) {
                LOG.warn("Scenario frozen: {}", freezeCounter);
                break;
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        LOG.debug("Scenario finished");
        synchronized (this) {
            scenarioFinished = true;
            this.notify();
        }
    }

    /**
     * @return true if scenario is done / empty
     */
    public boolean isEmpty() {
        return scenario.isEmpty();
    }

    /**
     * @return scenario
     */
    public Deque<ClientEvent> getScenario() {
        return scenario;
    }

    /**
     * @param scenario scenario filled with desired events
     */
    public void setScenario(Deque<ClientEvent> scenario) {
        this.scenario = scenario;
    }

    /**
     * @param ctx context which will be used for sending messages (SendEvents)
     */
    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    /**
     * @param message received message that is compared to expected message
     */
    public void addOfMsg(byte[] message) {
        ofMsg.add(message);
    }

    /**
     * @return true is scenario is finished
     */
    public boolean isScenarioFinished() {
        return scenarioFinished;
    }
}
