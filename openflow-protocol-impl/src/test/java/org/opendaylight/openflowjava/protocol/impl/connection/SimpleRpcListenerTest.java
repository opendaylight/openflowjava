/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.connection;

import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.controller.sal.common.util.Rpcs;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.google.common.util.concurrent.SettableFuture;

/**
 * @author michal.polkorab
 *
 */
public class SimpleRpcListenerTest {

    /**
     * Test SimpleRpcListener creation
     */
    @Test
    public void test() {
        SimpleRpcListener listener = new SimpleRpcListener("MESSAGE", "Failed to send message");
        Assert.assertEquals("Wrong message", "MESSAGE", listener.takeMessage());
        Assert.assertEquals("Wrong message", listener, listener.takeListener());
    }

    /**
     * Test rpc success
     */
    @Test
    public void testSuccessfulRpc() {
        SimpleRpcListener listener = new SimpleRpcListener("MESSAGE", "Failed to send message");
        listener.operationSuccessful();
        SettableFuture<RpcResult<?>> result = SettableFuture.create();
        result.set(Rpcs.getRpcResult(true, null, Collections.<RpcError>emptyList()));
        try {
            Assert.assertEquals("Wrong result", result.get().getErrors(), listener.getResult().get().getErrors());
            Assert.assertEquals("Wrong result", result.get().getResult(), listener.getResult().get().getResult());
            Assert.assertEquals("Wrong result", result.get().isSuccessful(), listener.getResult().get().isSuccessful());
        } catch (InterruptedException | ExecutionException e) {
            fail("Problem accessing result");
        }
    }
}