/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization;

import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF10EnqueueActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF10OutputActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF10SetDlDstActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF10SetDlSrcActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF10SetNwDstActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF10SetNwSrcActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF10SetNwTosActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF10SetTpDstActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF10SetTpSrcActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF10SetVlanPcpActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF10SetVlanVidActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF10StripVlanActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.experimenters.OF10VendorActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.EnhancedKeyRegistryHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Enqueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Output;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetDlDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetDlSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwTos;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetTpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetTpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetVlanPcp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetVlanVid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.StripVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;

/**
 * Initializes serializer registry with action serializers
 * @author michal.polkorab
 */
public class ActionsInitializer {

    /**
     * Registers action serializers into provided registry
     * @param serializerRegistry registry to be initialized with action serializers
     */
    public static void registerActionSerializers(SerializerRegistry serializerRegistry) {
        // register OF v1.3 OpenflowBasicClass action serializers
        Class<Action> actionClass = Action.class;
        EnhancedKeyRegistryHelper<Action> helper =
                new EnhancedKeyRegistryHelper<>(EncodeConstants.OF10_VERSION_ID, actionClass, serializerRegistry);
        helper.registerSerializer(Output.class, new OF10OutputActionSerializer());
        helper.registerSerializer(SetVlanVid.class, new OF10SetVlanVidActionSerializer());
        helper.registerSerializer(SetVlanPcp.class, new OF10SetVlanPcpActionSerializer());
        helper.registerSerializer(StripVlan.class, new OF10StripVlanActionSerializer());
        helper.registerSerializer(SetDlSrc.class, new OF10SetDlSrcActionSerializer());
        helper.registerSerializer(SetDlDst.class, new OF10SetDlDstActionSerializer());
        helper.registerSerializer(SetNwSrc.class, new OF10SetNwSrcActionSerializer());
        helper.registerSerializer(SetNwDst.class, new OF10SetNwDstActionSerializer());
        helper.registerSerializer(SetNwTos.class, new OF10SetNwTosActionSerializer());
        helper.registerSerializer(SetTpSrc.class, new OF10SetTpSrcActionSerializer());
        helper.registerSerializer(SetTpDst.class, new OF10SetTpDstActionSerializer());
        helper.registerSerializer(Enqueue.class, new OF10EnqueueActionSerializer());
        helper.registerSerializer(Experimenter.class, new OF10VendorActionSerializer());
    }
}
