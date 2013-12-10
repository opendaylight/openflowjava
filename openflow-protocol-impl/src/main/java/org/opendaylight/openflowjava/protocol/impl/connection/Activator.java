/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.connection;

import java.util.Hashtable;

import org.opendaylight.openflowjava.protocol.spi.connection.SwitchConnectionProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Activates library bundle, exposes provided implementations:
 * <ul>
 *  <li>{@link SwitchConnectionProviderImpl}</li>
 * </ul>
 * 
 * @author mirehak
 */
public class Activator implements BundleActivator {
    
    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext context) throws Exception {
        LOG.debug("starting OF Library");
        Hashtable<String, String> props = new Hashtable<>();
        props.put("type", "default");
        context.registerService(
                SwitchConnectionProvider.class.getName(), 
                new SwitchConnectionProviderImpl(), props);
        LOG.debug("started OF Library");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        LOG.debug("stopping OF Library");
        //TODO:: add teardown activities (check, if servers are running, stop if necessary..)
    }
}
