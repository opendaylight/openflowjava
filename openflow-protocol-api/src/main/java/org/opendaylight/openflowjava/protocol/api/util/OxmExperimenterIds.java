/*
 * Copyright (c) 2016 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.api.util;

import com.google.common.collect.ImmutableMap;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.TcpFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Anil Vishnoi (avishnoi@Brocade.com) on 7/25/16.
 */
public class OxmExperimenterIds {
    public static final Class TCP_FLAGS = TcpFlags.class;
    private static final Map<Class<? extends MatchField>, Integer> EXPERIMETER_IDS;

    static {
        ImmutableMap.Builder<Class<? extends MatchField>, Integer> builder = ImmutableMap.builder();

        //ONFOXM_ET_TCP_FLAGS Experimenter Id (0x4F4E4600)
        builder.put(TcpFlags.class, 1330529792);
        EXPERIMETER_IDS = builder.build();
    }

    public static Integer getExperimenterId(Class<? extends MatchField> expClass) {
        return EXPERIMETER_IDS.get(expClass);
    }
}
