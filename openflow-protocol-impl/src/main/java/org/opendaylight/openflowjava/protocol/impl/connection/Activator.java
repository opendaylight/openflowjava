/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.connection;

import java.util.Hashtable;

import org.opendaylight.openflowjava.protocol.spi.connection.SwitchConnectionProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Activate library bundle, expose provided implementations:
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
        Hashtable<String, String> props = new Hashtable<String, String>();
        props.put("type", "default");
        context.registerService(
                SwitchConnectionProvider.class.getName(), 
                new SwitchConnectionProviderImpl(), props);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        LOG.debug("stopping OF Library");
        //TODO:: add teardown activities (check, if servers are running, stop if necessary..)
    }
}
