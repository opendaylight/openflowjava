/*
 * Copyright Ericsson AB 2016 and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

/**
 * Class to get controller service status.
 *
 * @author ravikumar.chiguruvada
 */
public class SnDHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnDHandler.class);
    private static final int STATUS_CHECK_DELAY = Integer.getInteger("snd.health.check.time.delay");
    public final String ENABLE_RECON_VIA_RESYNC = "enable-recon-via-resync";
    private java.util.HashMap statusMap = new java.util.HashMap();

    private void getControllerStatus() {
        String JMX_OBJECT_NAME = "com.ericsson.sdncp.services.status:type=SvcStatus";
        try {
            invokeLocalJMXCommand(JMX_OBJECT_NAME, "acquireServiceStatusMAP");
        } catch (Exception e) {
            LOGGER.info("ERROR::" + e);
        }
    }

    private void invokeLocalJMXCommand(String objectName, String operationToExec) {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        boolean check = true;
        while (check) {
            LOGGER.error(".");
            if (mbs != null) {
                try {
                    statusMap = (java.util.HashMap) mbs.invoke(new ObjectName(objectName), operationToExec, null, null);
                    check = false;
                } catch (MalformedObjectNameException monEx) {
                    LOGGER.error("CRITICAL EXCEPTION : Malformed Object Name Exception");
                    check = false;
                } catch (MBeanException mbEx) {
                    LOGGER.error("CRITICAL EXCEPTION : MBean Exception");
                    check = false;
                } catch (InstanceNotFoundException infEx) {
                    LOGGER.error("CRITICAL EXCEPTION : Instance Not Found Exception");
                } catch (ReflectionException rEx) {
                    LOGGER.error("CRITICAL EXCEPTION : Reflection Exception");
                    check = false;
                } catch (Exception exp) {
                    LOGGER.error("CRITICAL EXCEPTION : Exception");
                    check = false;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (Exception eee) {
                LOGGER.error("CRITICAL EXCEPTION : Exception in sleep...");
            }
        }
    }

    public boolean healthCheck() {
        boolean status = false;
        try {
            /**
             * TODO: Any other depedent services to be added later, we can add here
             */
            
            while (true) {
                /**
                 * 
                 * Checking for SCF & Datastore if resync flag is enabled other wise check only for Datastore availability
                 * 
                 * 
                 */
                getControllerStatus();
                LOGGER.info("Resync flag::::"+System.getProperty(ENABLE_RECON_VIA_RESYNC));
                if (Boolean.getBoolean(ENABLE_RECON_VIA_RESYNC)) {
                    LOGGER.info("Resync flag is true");
                    if (statusMap.get("SCF_SERVICE").toString().equals("OPERATIONAL")
                            && statusMap.get("DATASTORE_SERVICE").toString().equals("OPERATIONAL")) {
                        LOGGER.info("SCF & DATASTORE services status found as ----OPERATIONAL----Enabling plugin-----");
                        status = true;
                        break;
                    } else {
                        TimeUnit.SECONDS.sleep(STATUS_CHECK_DELAY);
                        LOGGER.info(
                                "SCF or DATASTORE services status found as ----ERROR----plugin will check health after 10 seconds-----");
                        LOGGER.info(statusMap.get("SCF_SERVICE").toString()+"----"+statusMap.get("DATASTORE_SERVICE").toString());
                        continue;
                    }
                } else {
                    LOGGER.info("Resync flag disabled!!!! SnD checking only DATASTORE readyness");
                    if (statusMap.get("DATASTORE_SERVICE").toString().equals("OPERATIONAL")) {
                        LOGGER.info("DATASTORE_SERVICE found as ----OPERATIONAL----Enabling plugin-----");
                        status = true;
                        break;
                    } else {
                        TimeUnit.SECONDS.sleep(STATUS_CHECK_DELAY);
                        LOGGER.info(
                                "DATASTORE_SERVICE status found as ----ERROR----plugin will check health after "+STATUS_CHECK_DELAY+" seconds-----");
                        continue;
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("Exception while doing health check!!"+e);
        }
        return status;
    }

}
