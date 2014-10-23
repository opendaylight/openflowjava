/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization;

import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
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
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13CopyTtlInActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13CopyTtlOutActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13DecMplsTtlActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13DecNwTtlActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13GroupActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13OutputActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13PopMplsActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13PopPbbActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13PopVlanActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13PushMplsActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13PushPbbActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13PushVlanActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13SetFieldActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13SetMplsTtlActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13SetNwTtlActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.action.OF13SetQueueActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ActionSerializerRegistryHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.CopyTtlIn;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.CopyTtlOut;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.DecMplsTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.DecNwTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Enqueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Group;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Output;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopMpls;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopPbb;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushMpls;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushPbb;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetDlDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetDlSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetMplsTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwTos;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetTpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetTpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetVlanPcp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetVlanVid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.StripVlan;

/**
 * Initializes serializer registry with action serializers
 * @author michal.polkorab
 */
public abstract class ActionsInitializer {

    /**
     * Registers action serializers into provided registry
     * @param serializerRegistry registry to be initialized with action serializers
     */
    public static void registerActionSerializers(SerializerRegistry serializerRegistry) {
        // register OF v1.0 action serializers
        ActionSerializerRegistryHelper helper = new ActionSerializerRegistryHelper(
                EncodeConstants.OF10_VERSION_ID, serializerRegistry);
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
        // register OF v1.0 action serializers
        helper = new ActionSerializerRegistryHelper(
                EncodeConstants.OF13_VERSION_ID, serializerRegistry);
        helper.registerSerializer(Output.class, new OF13OutputActionSerializer());
        helper.registerSerializer(CopyTtlOut.class, new OF13CopyTtlOutActionSerializer());
        helper.registerSerializer(CopyTtlIn.class, new OF13CopyTtlInActionSerializer());
        helper.registerSerializer(SetMplsTtl.class, new OF13SetMplsTtlActionSerializer());
        helper.registerSerializer(DecMplsTtl.class, new OF13DecMplsTtlActionSerializer());
        helper.registerSerializer(PushVlan.class, new OF13PushVlanActionSerializer());
        helper.registerSerializer(PopVlan.class, new OF13PopVlanActionSerializer());
        helper.registerSerializer(PushMpls.class, new OF13PushMplsActionSerializer());
        helper.registerSerializer(PopMpls.class, new OF13PopMplsActionSerializer());
        helper.registerSerializer(SetQueue.class, new OF13SetQueueActionSerializer());
        helper.registerSerializer(Group.class, new OF13GroupActionSerializer());
        helper.registerSerializer(SetNwTtl.class, new OF13SetNwTtlActionSerializer());
        helper.registerSerializer(DecNwTtl.class, new OF13DecNwTtlActionSerializer());
        helper.registerSerializer(SetField.class, new OF13SetFieldActionSerializer());
        helper.registerSerializer(PushPbb.class, new OF13PushPbbActionSerializer());
        helper.registerSerializer(PopPbb.class, new OF13PopPbbActionSerializer());
    }
}