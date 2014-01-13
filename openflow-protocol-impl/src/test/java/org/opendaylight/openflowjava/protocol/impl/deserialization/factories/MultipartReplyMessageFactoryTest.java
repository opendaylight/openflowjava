/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthertypeAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.GroupIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MplsTtlAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTtlAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmFieldsAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortNumberMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.CopyTtlIn;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.CopyTtlOut;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.DecMplsTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.DecNwTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Group;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Output;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopMpls;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopPbb;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushMpls;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushPbb;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetMplsTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterBandTypeBitmap;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandDropCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandDscpRemarkCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.meter.band.drop._case.MeterBandDrop;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.meter.band.dscp.remark._case.MeterBandDscpRemark;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.meter.band.experimenter._case.MeterBandExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyAggregateCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyDescCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyFlowCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroupCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroupDescCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterConfigCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterFeaturesCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortDescCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortStatsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyQueueCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTableCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.aggregate._case.MultipartReplyAggregate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.desc._case.MultipartReplyDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.experimenter._case.MultipartReplyExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.flow._case.MultipartReplyFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group._case.MultipartReplyGroup;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.desc._case.MultipartReplyGroupDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter._case.MultipartReplyMeter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.config._case.MultipartReplyMeterConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.features._case.MultipartReplyMeterFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.desc._case.MultipartReplyPortDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats._case.MultipartReplyPortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.queue._case.MultipartReplyQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table._case.MultipartReplyTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MultipartReplyMessageFactoryTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MultipartReplyMessageFactoryTest.class);
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyDescBody(){
        final int DESC_STR_LEN = 256;
        final int SERIAL_NUM_LEN = 32;
        ByteBuf bb = BufferHelper.buildBuffer("00 00 00 01 00 00 00 00");
        
        String mfrDesc = "Manufacturer description";
        byte[] mfrDescBytes = new byte[256];
        mfrDescBytes = mfrDesc.getBytes();
        bb.writeBytes(mfrDescBytes);
        ByteBufUtils.padBuffer((DESC_STR_LEN - mfrDescBytes.length), bb);
        
        String hwDesc = "Hardware description";
        byte[] hwDescBytes = new byte[256];
        hwDescBytes = hwDesc.getBytes();
        bb.writeBytes(hwDescBytes);
        ByteBufUtils.padBuffer((DESC_STR_LEN - hwDescBytes.length), bb);
        
        String swDesc = "Software description";
        byte[] swDescBytes = new byte[256];
        swDescBytes = swDesc.getBytes();
        bb.writeBytes(swDescBytes);
        ByteBufUtils.padBuffer((DESC_STR_LEN - swDescBytes.length), bb);
        
        String serialNum = "SN0123456789";
        byte[] serialNumBytes = new byte[32];
        serialNumBytes = serialNum.getBytes();
        bb.writeBytes(serialNumBytes);
        ByteBufUtils.padBuffer((SERIAL_NUM_LEN - serialNumBytes.length), bb);
        
        String dpDesc = "switch3 in room 3120";
        byte[] dpDescBytes = new byte[256];
        dpDescBytes = dpDesc.getBytes();
        bb.writeBytes(dpDescBytes);
        ByteBufUtils.padBuffer((DESC_STR_LEN - dpDescBytes.length), bb);
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0x00, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyDescCase messageCase = (MultipartReplyDescCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyDesc message = messageCase.getMultipartReplyDesc();
        Assert.assertEquals("Wrong mfrDesc", "Manufacturer description", message.getMfrDesc());
        Assert.assertEquals("Wrong hwDesc", "Hardware description", message.getHwDesc());
        Assert.assertEquals("Wrong swDesc", "Software description", message.getSwDesc());
        Assert.assertEquals("Wrong serialNum", "SN0123456789", message.getSerialNum());
        Assert.assertEquals("Wrong dpDesc", "switch3 in room 3120", message.getDpDesc());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyFlowBody(){
        ByteBuf bb = BufferHelper.buildBuffer("00 01 00 01 00 00 00 00 "+
                                              "00 0C "+//length
                                              "08 "+//tableId
                                              "00 "+//pad_01
                                              "00 00 00 09 "+//durationSec
                                              "00 00 00 07 "+//durationNsec
                                              "00 0C "+//priority
                                              "00 0E "+//idleTimeout
                                              "00 0F "+//hardTimeout
                                              "00 0B "+//flags
                                              "00 00 00 00 "+//pad_02
                                              "FF 01 01 01 01 01 01 01 "+//cookie
                                              "EF 01 01 01 01 01 01 01 "+//packetCount
                                              "7F 01 01 01 01 01 01 01"//byteCount
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0x01, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyFlowCase messageCase = (MultipartReplyFlowCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyFlow message = messageCase.getMultipartReplyFlow();
        Assert.assertEquals("Wrong tableId", 8, message.getFlowStats().get(0).getTableId().intValue());
        Assert.assertEquals("Wrong durationSec", 9, message.getFlowStats().get(0).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec", 7, message.getFlowStats().get(0).getDurationNsec().intValue());
        Assert.assertEquals("Wrong priority", 12, message.getFlowStats().get(0).getPriority().intValue());
        Assert.assertEquals("Wrong idleTimeOut", 14, message.getFlowStats().get(0).getIdleTimeout().intValue());
        Assert.assertEquals("Wrong hardTimeOut", 15, message.getFlowStats().get(0).getHardTimeout().intValue());
        Assert.assertEquals("Wrong flags", new FlowModFlags(true, false, true, false, true), 
                                           message.getFlowStats().get(0).getFlags());
        Assert.assertEquals("Wrong cookie", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getFlowStats().get(0).getCookie());
        Assert.assertEquals("Wrong packetCount", 
                new BigInteger(1, new byte[]{(byte) 0xEF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getFlowStats().get(0).getPacketCount());
        Assert.assertEquals("Wrong byteCount", 
                new BigInteger(1, new byte[]{(byte) 0x7F, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getFlowStats().get(0).getByteCount());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyAggregateBody(){
        ByteBuf bb = BufferHelper.buildBuffer("00 02 00 01 00 00 00 00 "+
                                              "FF 01 01 01 01 01 01 01 "+//packetCount
                                              "0F 01 01 01 01 01 01 01 "+//byteCount
                                              "00 00 00 08 "+//flowCount
                                              "00 00 00 00"//pad
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0x02, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyAggregateCase messageCase = (MultipartReplyAggregateCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyAggregate message = messageCase.getMultipartReplyAggregate();
        Assert.assertEquals("Wrong packetCount", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getPacketCount());
        Assert.assertEquals("Wrong byteCount", 
                new BigInteger(1, new byte[]{(byte) 0x0F, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getByteCount());
        Assert.assertEquals("Wrong flowCount", 
                8, 
                message.getFlowCount().intValue());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyTableBody(){
        ByteBuf bb = BufferHelper.buildBuffer("00 03 00 01 00 00 00 00 "+
                                              "08 "+//tableId
                                              "00 00 00 "+//pad
                                              "00 00 00 10 "+//activeCount
                                              "FF 01 01 01 01 01 01 01 "+//lookupCount
                                              "AF 01 01 01 01 01 01 01"//matchedCount
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0x03, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        
        MultipartReplyTableCase messageCase = (MultipartReplyTableCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyTable message = messageCase.getMultipartReplyTable();
        Assert.assertEquals("Wrong tableId", 8, message.getTableStats().get(0).getTableId().intValue());
        Assert.assertEquals("Wrong activeCount", 16, message.getTableStats().get(0).getActiveCount().longValue());
        Assert.assertEquals("Wrong lookupCount", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getTableStats().get(0).getLookupCount());
        Assert.assertEquals("Wrong matchedCount", 
                new BigInteger(1, new byte[]{(byte) 0xAF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getTableStats().get(0).getMatchedCount());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyPortStatsBody(){
        ByteBuf bb = BufferHelper.buildBuffer("00 04 00 01 00 00 00 00 "+
                                              "00 00 00 FF "+//portNo
                                              "00 00 00 00 "+//pad
                                              "FF 01 01 01 01 01 01 01 "+//rxPackets
                                              "FF 02 02 02 02 02 02 02 "+//txPackets
                                              "FF 02 03 02 03 02 03 02 "+//rxBytes
                                              "FF 02 03 02 03 02 03 02 "+//txBytes
                                              "FF 02 03 02 03 02 03 02 "+//rxDropped
                                              "FF 02 03 02 03 02 03 02 "+//txDropped
                                              "FF 02 03 02 03 02 03 02 "+//rxErrors
                                              "FF 02 03 02 03 02 03 02 "+//txErrors
                                              "FF 02 03 02 03 02 03 02 "+//rxFrameErr
                                              "FF 02 03 02 03 02 03 02 "+//rxOverErr
                                              "FF 02 03 02 03 02 03 02 "+//rxCrcErr
                                              "FF 02 03 02 03 02 03 02 "+//collisions
                                              "00 00 00 02 "+//durationSec
                                              "00 00 00 04"//durationNsec
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0x04, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyPortStatsCase messageCase = (MultipartReplyPortStatsCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyPortStats message = messageCase.getMultipartReplyPortStats();
        Assert.assertEquals("Wrong portNo", 255, message.getPortStats().get(0).getPortNo().intValue());
        Assert.assertEquals("Wrong rxPackets", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getPortStats().get(0).getRxPackets());
        Assert.assertEquals("Wrong txPackets", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getPortStats().get(0).getTxPackets());
        Assert.assertEquals("Wrong rxBytes", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getRxBytes());
        Assert.assertEquals("Wrong txBytes", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getTxBytes());
        Assert.assertEquals("Wrong rxDropped", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getRxDropped());
        Assert.assertEquals("Wrong txDropped", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getTxDropped());
        Assert.assertEquals("Wrong rxErrors", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getRxErrors());
        Assert.assertEquals("Wrong txErrors", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getTxErrors());
        Assert.assertEquals("Wrong rxFrameErr", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getRxFrameErr());
        Assert.assertEquals("Wrong rxOverErr", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getRxOverErr());
        Assert.assertEquals("Wrong rxCrcErr", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getRxCrcErr());
        Assert.assertEquals("Wrong collisions", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getCollisions());
        Assert.assertEquals("Wrong durationSec", 2, message.getPortStats().get(0).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec", 4, message.getPortStats().get(0).getDurationNsec().intValue());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyQueueBody(){
        ByteBuf bb = BufferHelper.buildBuffer("00 05 00 01 00 00 00 00 "+
                                              "00 00 00 FF "+//portNo
                                              "00 00 00 10 "+//queueId
                                              "FF 02 03 02 03 02 03 02 "+//txBytes
                                              "FF 02 02 02 02 02 02 02 "+//txPackets
                                              "FF 02 03 02 03 02 03 02 "+//txErrors
                                              "00 00 00 02 "+//durationSec
                                              "00 00 00 04"//durationNsec
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0x05, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyQueueCase messageCase = (MultipartReplyQueueCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyQueue message = messageCase.getMultipartReplyQueue();
        Assert.assertEquals("Wrong portNo", 255, message.getQueueStats().get(0).getPortNo().intValue());
        Assert.assertEquals("Wrong queueId", 16, message.getQueueStats().get(0).getQueueId().intValue());
        Assert.assertEquals("Wrong txBytes", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getQueueStats().get(0).getTxBytes());
        Assert.assertEquals("Wrong txPackets", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getQueueStats().get(0).getTxPackets());
        Assert.assertEquals("Wrong txErrors", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getQueueStats().get(0).getTxErrors());
        Assert.assertEquals("Wrong durationSec", 2, message.getQueueStats().get(0).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec", 4, message.getQueueStats().get(0).getDurationNsec().intValue());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyGroupBody(){
        ByteBuf bb = BufferHelper.buildBuffer("00 06 00 01 00 00 00 00 "+
                                              "00 48 "+//length
                                              "00 00 "+//pad1
                                              "00 00 00 10 "+//groupId
                                              "00 00 00 12 "+//refCount
                                              "00 00 00 00 "+//pad2
                                              "FF 01 01 01 01 01 01 01 "+//packetCount
                                              "FF 01 01 01 01 01 01 01 "+//byteCount
                                              "00 00 00 08 "+//durationSec
                                              "00 00 00 09 "+//durationNsec
                                              "FF 01 01 01 01 01 01 01 "+//packetCountBucket
                                              "FF 01 01 01 01 01 01 01 "+//byteCountBucket
                                              "FF 02 02 02 02 02 02 02 "+//packetCountBucket_2
                                              "FF 02 02 02 02 02 02 02 "+//byteCountBucket_2
                                              "00 48 "+//length_2
                                              "00 00 "+//pad1.2
                                              "00 00 00 10 "+//groupId_2
                                              "00 00 00 12 "+//refCount_2
                                              "00 00 00 00 "+//pad2.2
                                              "FF 01 01 01 01 01 01 01 "+//packetCount_2
                                              "FF 01 01 01 01 01 01 01 "+//byteCount_2
                                              "00 00 00 08 "+//durationSec_2
                                              "00 00 00 09 "+//durationNsec_2
                                              "FF 01 01 01 01 01 01 01 "+//packetCountBucket_1.2
                                              "FF 01 01 01 01 01 01 01 "+//byteCountBucket_1.2
                                              "FF 02 02 02 02 02 02 02 "+//packetCountBucket_2.2
                                              "FF 02 02 02 02 02 02 02"//byteCountBucket_2.2
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0x06, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyGroupCase messageCase = (MultipartReplyGroupCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyGroup message = messageCase.getMultipartReplyGroup();
        Assert.assertEquals("Wrong groupId", 16, message.getGroupStats().get(0).getGroupId().getValue().intValue());
        Assert.assertEquals("Wrong refCount", 18, message.getGroupStats().get(0).getRefCount().intValue());
        Assert.assertEquals("Wrong packetCount", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(0).getPacketCount());
        Assert.assertEquals("Wrong byteCount", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(0).getByteCount());
        Assert.assertEquals("Wrong durationSec", 8, message.getGroupStats().get(0).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec", 9, message.getGroupStats().get(0).getDurationNsec().intValue());
        Assert.assertEquals("Wrong packetCountBucket", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(0).getBucketStats().get(0).getPacketCount());
        Assert.assertEquals("Wrong byteCountBucket", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(0).getBucketStats().get(0).getByteCount());
        Assert.assertEquals("Wrong packetCountBucket_2", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getGroupStats().get(0).getBucketStats().get(1).getPacketCount());
        Assert.assertEquals("Wrong byteCountBucket_2", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getGroupStats().get(0).getBucketStats().get(1).getByteCount());
        
        Assert.assertEquals("Wrong groupId_2", 16, message.getGroupStats().get(1).getGroupId().getValue().intValue());
        Assert.assertEquals("Wrong refCount_2", 18, message.getGroupStats().get(1).getRefCount().intValue());
        Assert.assertEquals("Wrong packetCount_2", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(1).getPacketCount());
        Assert.assertEquals("Wrong byteCount_2", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(1).getByteCount());
        Assert.assertEquals("Wrong durationSec_2", 8, message.getGroupStats().get(1).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec_2", 9, message.getGroupStats().get(1).getDurationNsec().intValue());
        Assert.assertEquals("Wrong packetCountBucket_1.2", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(1).getBucketStats().get(0).getPacketCount());
        Assert.assertEquals("Wrong byteCountBucket_1.2", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(1).getBucketStats().get(0).getByteCount());
        Assert.assertEquals("Wrong packetCountBucket_2.2", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getGroupStats().get(1).getBucketStats().get(1).getPacketCount());
        Assert.assertEquals("Wrong byteCountBucket_2.2", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getGroupStats().get(1).getBucketStats().get(1).getByteCount());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyMeterFeaturesBody(){
        ByteBuf bb = BufferHelper.buildBuffer("00 0B 00 01 00 00 00 00 "+
                                              "00 00 00 09 "+//maxMeter
                                              "00 00 00 01 "+//bandTypes
                                              "00 00 00 03 "+//capabilities
                                              "03 "+//maxBands
                                              "04 "+//maxColor
                                              "00 00"//pad
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 11, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyMeterFeaturesCase messageCase = (MultipartReplyMeterFeaturesCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyMeterFeatures message = messageCase.getMultipartReplyMeterFeatures();        
        Assert.assertEquals("Wrong maxMeter", 9, message.getMaxMeter().intValue());
        Assert.assertEquals("Wrong bandTypes", new MeterBandTypeBitmap(true, false), message.getBandTypes());
        Assert.assertEquals("Wrong capabilities", new MeterFlags(false, true, true, false), 
                                                      message.getCapabilities());
        Assert.assertEquals("Wrong maxBands", 3, message.getMaxBands().intValue());
        Assert.assertEquals("Wrong maxColor", 4, message.getMaxColor().intValue());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyMeterBody(){
        ByteBuf bb = BufferHelper.buildBuffer("00 09 00 01 00 00 00 00 "+
                                              "00 00 00 09 "+//meterId
                                              "00 58 "+//len
                                              "00 00 00 00 00 00 "+//pad
                                              "00 00 00 07 "+//flowCount
                                              "FF 01 01 01 01 01 01 01 "+//packetInCount
                                              "FF 01 01 01 01 01 01 01 "+//byteInCount
                                              "00 00 00 05 "+//durationSec
                                              "00 00 00 05 "+//durationNsec
                                              "FF 01 01 01 01 01 01 01 "+//packetBandCount_01
                                              "FF 01 01 01 01 01 01 01 "+//byteBandCount_01
                                              "FF 02 02 02 02 02 02 02 "+//packetBandCount_02
                                              "FF 02 02 02 02 02 02 02 "+//byteBandCount_02
                                              "FF 03 03 03 03 03 03 03 "+//packetBandCount_03
                                              "FF 03 03 03 03 03 03 03"//byteBandCount_03
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 9, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyMeterCase messageCase = (MultipartReplyMeterCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyMeter message = messageCase.getMultipartReplyMeter();
        Assert.assertEquals("Wrong meterId", 9, 
                             message.getMeterStats().get(0).getMeterId().getValue().intValue());
        Assert.assertEquals("Wrong flowCount", 7, 
                            message.getMeterStats().get(0).getFlowCount().intValue());
        Assert.assertEquals("Wrong packetInCount", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getPacketInCount());
        Assert.assertEquals("Wrong byteInCount", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getByteInCount());
        Assert.assertEquals("Wrong durationSec", 5, 
                message.getMeterStats().get(0).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec", 5, 
                message.getMeterStats().get(0).getDurationNsec().intValue());
        Assert.assertEquals("Wrong packetBandCount_01", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getMeterBandStats().get(0).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_01", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getMeterBandStats().get(0).getByteBandCount());
        Assert.assertEquals("Wrong packetBandCount_02", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getMeterStats().get(0).getMeterBandStats().get(1).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_02", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getMeterStats().get(0).getMeterBandStats().get(1).getByteBandCount());
        Assert.assertEquals("Wrong packetBandCount_03", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03}), 
                message.getMeterStats().get(0).getMeterBandStats().get(2).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_03", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03}), 
                message.getMeterStats().get(0).getMeterBandStats().get(2).getByteBandCount());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyMeterBodyMulti(){
        ByteBuf bb = BufferHelper.buildBuffer("00 09 00 01 00 00 00 00 "+
                                              "00 00 00 09 "+//meterId_0
                                              "00 58 "+//len_0
                                              "00 00 00 00 00 00 "+//pad_0
                                              "00 00 00 07 "+//flowCount_0
                                              "FF 01 01 01 01 01 01 01 "+//packetInCount_0
                                              "FF 01 01 01 01 01 01 01 "+//byteInCount_0
                                              "00 00 00 05 "+//durationSec_0
                                              "00 00 00 05 "+//durationNsec_0
                                              "FF 01 01 01 01 01 01 01 "+//packetBandCount_01
                                              "FF 01 01 01 01 01 01 01 "+//byteBandCount_01
                                              "FF 02 02 02 02 02 02 02 "+//packetBandCount_02
                                              "FF 02 02 02 02 02 02 02 "+//byteBandCount_02
                                              "FF 03 03 03 03 03 03 03 "+//packetBandCount_03
                                              "FF 03 03 03 03 03 03 03 "+//byteBandCount_03
                                              "00 00 00 08 "+//meterId_1
                                              "00 58 "+//len_1
                                              "00 00 00 00 00 00 "+//pad_1
                                              "00 00 00 07 "+//flowCount_1
                                              "FF 01 01 01 01 01 01 01 "+//packetInCount_1
                                              "FF 01 01 01 01 01 01 01 "+//byteInCount_1
                                              "00 00 00 05 "+//durationSec_1
                                              "00 00 00 05 "+//durationNsec_1
                                              "FF 01 01 01 01 01 01 01 "+//packetBandCount_11
                                              "FF 01 01 01 01 01 01 01 "+//byteBandCount_11
                                              "FF 02 02 02 02 02 02 02 "+//packetBandCount_12
                                              "FF 02 02 02 02 02 02 02 "+//byteBandCount_12
                                              "FF 03 03 03 03 03 03 03 "+//packetBandCount_13
                                              "FF 03 03 03 03 03 03 03"//byteBandCount_13
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 9, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyMeterCase messageCase = (MultipartReplyMeterCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyMeter message = messageCase.getMultipartReplyMeter();        
        Assert.assertEquals("Wrong meterId", 9, 
                             message.getMeterStats().get(0).getMeterId().getValue().intValue());
        Assert.assertEquals("Wrong flowCount", 7, 
                            message.getMeterStats().get(0).getFlowCount().intValue());
        Assert.assertEquals("Wrong packetInCount", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getPacketInCount());
        Assert.assertEquals("Wrong byteInCount", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getByteInCount());
        Assert.assertEquals("Wrong durationSec", 5, 
                message.getMeterStats().get(0).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec", 5, 
                message.getMeterStats().get(0).getDurationNsec().intValue());
        Assert.assertEquals("Wrong packetBandCount_01", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getMeterBandStats().get(0).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_01", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getMeterBandStats().get(0).getByteBandCount());
        Assert.assertEquals("Wrong packetBandCount_02", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getMeterStats().get(0).getMeterBandStats().get(1).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_02", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getMeterStats().get(0).getMeterBandStats().get(1).getByteBandCount());
        Assert.assertEquals("Wrong packetBandCount_03", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03}), 
                message.getMeterStats().get(0).getMeterBandStats().get(2).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_03", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03}), 
                message.getMeterStats().get(0).getMeterBandStats().get(2).getByteBandCount());
        
        Assert.assertEquals("Wrong meterId", 8, 
                message.getMeterStats().get(1).getMeterId().getValue().intValue());
        Assert.assertEquals("Wrong flowCount", 7, 
                message.getMeterStats().get(1).getFlowCount().intValue());
        Assert.assertEquals("Wrong packetInCount", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(1).getPacketInCount());
        Assert.assertEquals("Wrong byteInCount", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(1).getByteInCount());
        Assert.assertEquals("Wrong durationSec", 5, 
                message.getMeterStats().get(1).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec", 5, 
                message.getMeterStats().get(1).getDurationNsec().intValue());
        Assert.assertEquals("Wrong packetBandCount_01", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(1).getMeterBandStats().get(0).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_01", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(1).getMeterBandStats().get(0).getByteBandCount());
        Assert.assertEquals("Wrong packetBandCount_02", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getMeterStats().get(1).getMeterBandStats().get(1).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_02", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getMeterStats().get(1).getMeterBandStats().get(1).getByteBandCount());
        Assert.assertEquals("Wrong packetBandCount_03", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03}), 
                message.getMeterStats().get(1).getMeterBandStats().get(2).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_03", 
                new BigInteger(1, new byte[]{(byte) 0xFF, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03}), 
                message.getMeterStats().get(1).getMeterBandStats().get(2).getByteBandCount());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyMeterConfigBody(){
        ByteBuf bb = BufferHelper.buildBuffer("00 0A 00 01 00 00 00 00 "+
                                              "00 38 "+//len
                                              "00 0A "+//flags
                                              "00 00 00 09 "+//meterId
                                              "00 01 "+//meterBandDrop.type
                                              "00 10 "+//meterBandDrop.len
                                              "00 00 00 11 "+//meterBandDrop.rate
                                              "00 00 00 20 "+//meterBandDrop.burstSize
                                              "00 00 00 00 "+//meterBandDrop.pad
                                              "00 02 "+//meterBandDscp.type
                                              "00 10 "+//meterBandDscp.len
                                              "00 00 00 11 "+//meterBandDscp.rate
                                              "00 00 00 20 "+//meterBandDscp.burstSize
                                              "04 "+//meterBandDscp.precLevel
                                              "00 00 00 "+//meterBandDscp.pad
                                              "FF FF "+//meterBandExperimenter.type
                                              "00 10 "+//meterBandExperimenter.len
                                              "00 00 00 11 "+//meterBandExperimenter.rate
                                              "00 00 00 20 "+//meterBandExperimenter.burstSize
                                              "00 00 00 04"//meterBandExperimenter.experimenter
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 10, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyMeterConfigCase messageCase = (MultipartReplyMeterConfigCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyMeterConfig message = messageCase.getMultipartReplyMeterConfig();        
        Assert.assertEquals("Wrong flags", new MeterFlags(false, false, true, true),
                             message.getMeterConfig().get(0).getFlags());
        Assert.assertEquals("Wrong meterId", 9, 
                             message.getMeterConfig().get(0).getMeterId().getValue().intValue());
        
        MeterBandDropCase dropCase = (MeterBandDropCase) message.getMeterConfig().get(0).getBands().get(0).getMeterBand(); 
        MeterBandDrop meterBandDrop = dropCase.getMeterBandDrop();
        Assert.assertEquals("Wrong meterBandDrop.type", 1, meterBandDrop.getType().getIntValue()); 
        Assert.assertEquals("Wrong meterBandDrop.rate", 17, meterBandDrop.getRate().intValue());
        Assert.assertEquals("Wrong meterBandDrop.burstSize", 32, meterBandDrop.getBurstSize().intValue());
        
        MeterBandDscpRemarkCase dscpCase = (MeterBandDscpRemarkCase) message.getMeterConfig().get(0).getBands().get(1).getMeterBand(); 
        MeterBandDscpRemark meterBandDscp = dscpCase.getMeterBandDscpRemark();
        Assert.assertEquals("Wrong meterBandDscp.type", 2, meterBandDscp.getType().getIntValue()); 
        Assert.assertEquals("Wrong meterBandDscp.rate", 17, meterBandDscp.getRate().intValue());
        Assert.assertEquals("Wrong meterBandDscp.burstSize", 32, meterBandDscp.getBurstSize().intValue());
        Assert.assertEquals("Wrong meterBandDscp.precLevel", 4, meterBandDscp.getPrecLevel().intValue());
        
        MeterBandExperimenterCase experimenterCase = (MeterBandExperimenterCase) message.getMeterConfig().get(0).getBands().get(2).getMeterBand(); 
        MeterBandExperimenter meterBandExperimenter = experimenterCase.getMeterBandExperimenter();
        Assert.assertEquals("Wrong meterBandExperimenter.type", 0xFFFF, meterBandExperimenter.getType().getIntValue()); 
        Assert.assertEquals("Wrong meterBandExperimenter.rate", 17, meterBandExperimenter.getRate().intValue());
        Assert.assertEquals("Wrong meterBandExperimenter.burstSize", 32, meterBandExperimenter.getBurstSize().intValue());
        Assert.assertEquals("Wrong meterBandExperimenter.experimenter", 4, meterBandExperimenter.getExperimenter().intValue());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyMeterConfigBodyMulti(){
        ByteBuf bb = BufferHelper.buildBuffer("00 0A 00 01 00 00 00 00 "+
                                              "00 38 "+//len
                                              "00 06 "+//flags
                                              "00 00 00 09 "+//meterId
                                              "00 01 "+//meterBandDrop.type
                                              "00 10 "+//meterBandDrop.len
                                              "00 00 00 11 "+//meterBandDrop.rate
                                              "00 00 00 20 "+//meterBandDrop.burstSize
                                              "00 00 00 00 "+//meterBandDrop.pad
                                              "00 02 "+//meterBandDscp.type
                                              "00 10 "+//meterBandDscp.len
                                              "00 00 00 11 "+//meterBandDscp.rate
                                              "00 00 00 20 "+//meterBandDscp.burstSize
                                              "04 "+//meterBandDscp.precLevel
                                              "00 00 00 "+//meterBandDscp.pad
                                              "FF FF "+//meterBandExperimenter.type
                                              "00 10 "+//meterBandExperimenter.len
                                              "00 00 00 11 "+//meterBandExperimenter.rate
                                              "00 00 00 20 "+//meterBandExperimenter.burstSize
                                              "00 00 00 04 "+//meterBandExperimenter.experimenter
                                              
                                              "00 18 "+//len01
                                              "00 03 "+//flags01
                                              "00 00 00 07 "+//meterId01
                                              "00 02 "+//meterBandDscp01.type
                                              "00 10 "+//meterBandDscp01.len
                                              "00 00 00 11 "+//meterBandDscp01.rate
                                              "00 00 00 20 "+//meterBandDscp01.burstSize
                                              "04 "+//meterBandDscp01.precLevel
                                              "00 00 00"//meterBandDscp01.pad
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 10, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyMeterConfigCase messageCase = (MultipartReplyMeterConfigCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyMeterConfig message = messageCase.getMultipartReplyMeterConfig();        
        Assert.assertEquals("Wrong flags", new MeterFlags(true, false, true, false), 
                             message.getMeterConfig().get(0).getFlags());
        Assert.assertEquals("Wrong meterId", 9, 
                             message.getMeterConfig().get(0).getMeterId().getValue().intValue());
        
        MeterBandDropCase dropCase = (MeterBandDropCase) message.getMeterConfig().get(0).getBands().get(0).getMeterBand(); 
        MeterBandDrop meterBandDrop = dropCase.getMeterBandDrop(); 
        Assert.assertEquals("Wrong meterBandDrop.type", 1, meterBandDrop.getType().getIntValue()); 
        Assert.assertEquals("Wrong meterBandDrop.rate", 17, meterBandDrop.getRate().intValue());
        Assert.assertEquals("Wrong meterBandDrop.burstSize", 32, meterBandDrop.getBurstSize().intValue());
        
        MeterBandDscpRemarkCase dscpCase = (MeterBandDscpRemarkCase) message.getMeterConfig().get(0).getBands().get(1).getMeterBand(); 
        MeterBandDscpRemark meterBandDscp = dscpCase.getMeterBandDscpRemark();
        Assert.assertEquals("Wrong meterBandDscp.type", 2, meterBandDscp.getType().getIntValue()); 
        Assert.assertEquals("Wrong meterBandDscp.rate", 17, meterBandDscp.getRate().intValue());
        Assert.assertEquals("Wrong meterBandDscp.burstSize", 32, meterBandDscp.getBurstSize().intValue());
        Assert.assertEquals("Wrong meterBandDscp.precLevel", 4, meterBandDscp.getPrecLevel().intValue());
        
        MeterBandExperimenterCase experimenterCase = (MeterBandExperimenterCase) message.getMeterConfig().get(0).getBands().get(2).getMeterBand(); 
        MeterBandExperimenter meterBandExperimenter = experimenterCase.getMeterBandExperimenter();
        Assert.assertEquals("Wrong meterBandExperimenter.type", 0xFFFF, meterBandExperimenter.getType().getIntValue()); 
        Assert.assertEquals("Wrong meterBandExperimenter.rate", 17, meterBandExperimenter.getRate().intValue());
        Assert.assertEquals("Wrong meterBandExperimenter.burstSize", 32, meterBandExperimenter.getBurstSize().intValue());
        Assert.assertEquals("Wrong meterBandExperimenter.experimenter", 4, meterBandExperimenter.getExperimenter().intValue());
        
        LOGGER.info(message.getMeterConfig().get(0).getFlags().toString());
        Assert.assertEquals("Wrong flags01", new MeterFlags(false, true, true, false),
                             message.getMeterConfig().get(1).getFlags());
        Assert.assertEquals("Wrong meterId01", 7, 
                             message.getMeterConfig().get(1).getMeterId().getValue().intValue());
        
        MeterBandDscpRemarkCase dscpCase01 = (MeterBandDscpRemarkCase) message.getMeterConfig().get(1).getBands().get(0).getMeterBand(); 
        MeterBandDscpRemark meterBandDscp01 = dscpCase01.getMeterBandDscpRemark();
        Assert.assertEquals("Wrong meterBandDscp01.type", 2, meterBandDscp01.getType().getIntValue()); 
        Assert.assertEquals("Wrong meterBandDscp01.rate", 17, meterBandDscp01.getRate().intValue());
        Assert.assertEquals("Wrong meterBandDscp01.burstSize", 32, meterBandDscp01.getBurstSize().intValue());
        Assert.assertEquals("Wrong meterBandDscp01.precLevel", 4, meterBandDscp01.getPrecLevel().intValue());
        
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyExperimenterBody(){
        ByteBuf bb = BufferHelper.buildBuffer("FF FF 00 01 00 00 00 00 "+
                                              "00 00 00 0F "+//experimenterId
                                              "00 00 00 FF "+//expType
                                              "00 00 01 01 00 00 01 01"
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0xFFFF, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyExperimenterCase messageCase = (MultipartReplyExperimenterCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyExperimenter message = messageCase.getMultipartReplyExperimenter();        
        Assert.assertEquals("Wrong experimenterId", 15, message.getExperimenter().intValue());
        Assert.assertEquals("Wrong expType", 255, message.getExpType().intValue());
        Assert.assertArrayEquals("Wrong data", new byte[]{0x00, 0x00, 0x01, 0x01, 0x00, 0x00, 0x01, 0x01}, 
                             message.getData());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyPortDescBody(){
        final byte MAX_PORT_NAME_LEN = 16;
        ByteBuf bb = BufferHelper.buildBuffer("00 0D 00 01 00 00 00 00 "+
                                              "00 01 02 03 "+ //portNo
                                              "00 00 00 00 "+ //padding01
                                              "08 00 27 00 B0 EB " + //mac address
                                              "00 00 "); //padding02
        //port name
        String portName = "SampleText";
        byte[] portNameBytes = new byte[MAX_PORT_NAME_LEN];
        portNameBytes = portName.getBytes();
        bb.writeBytes(portNameBytes);
        ByteBufUtils.padBuffer((MAX_PORT_NAME_LEN - portNameBytes.length), bb);
        
        ByteBuf bb2 =  BufferHelper.buildBuffer("00 00 00 41 " + //port config
                                                "00 00 00 05 " + //port state
                                                "00 00 00 81 " + //current features
                                                "00 00 81 81 " + //advertised features
                                                "00 00 C1 89 " + //supported features
                                                "00 00 C5 8D " + //peer features
                                                "00 00 00 81 " + //curr speed
                                                "00 00 00 80" //max speed
                                                );
        bb.writeBytes(bb2.copy(4, bb2.readableBytes()-4));//excluding version and xid
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 13, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyPortDescCase messageCase = (MultipartReplyPortDescCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyPortDesc message = messageCase.getMultipartReplyPortDesc();
        Assert.assertEquals("Wrong portNo", 66051L, message.getPorts().get(0).getPortNo().longValue());
        Assert.assertEquals("Wrong macAddress", new MacAddress("08:00:27:00:B0:EB"), 
                                                message.getPorts().get(0).getHwAddr());
        Assert.assertEquals("Wrong portName", "SampleText", 
                                                message.getPorts().get(0).getName());
        Assert.assertEquals("Wrong portConfig", new PortConfig(false, true, false, true), 
                message.getPorts().get(0).getConfig());
        Assert.assertEquals("Wrong portState", new PortState(false, true, true),
                                               message.getPorts().get(0).getState());
        Assert.assertEquals("Wrong currentFeatures", new PortFeatures(false, false, false, false,
                                                                      false, true, false, false, 
                                                                      false, true, false, false, 
                                                                      false, false, false, false), 
                                                  message.getPorts().get(0).getCurrentFeatures());
        Assert.assertEquals("Wrong advertisedFeatures", 
                                            new PortFeatures(true, false, false, false,
                                                            false, true, false, false, 
                                                            false, true, false, false, 
                                                            false, false, false, true), 
                                              message.getPorts().get(0).getAdvertisedFeatures());
        Assert.assertEquals("Wrong supportedFeatures", 
                                            new PortFeatures(true, true, false, false,
                                                            false, true, false, false, 
                                                            false, true, false, false, 
                                                            false, false, true, true), 
                                              message.getPorts().get(0).getSupportedFeatures());
        Assert.assertEquals("Wrong peerFeatures", 
                                            new PortFeatures(true, true, true, false,
                                                    false, true, false, false, 
                                                    false, true, false, false, 
                                                    false, true, true, true), 
                                              message.getPorts().get(0).getPeerFeatures());
        Assert.assertEquals("Wrong currSpeed", 129L, message.getPorts().get(0).getCurrSpeed().longValue());
        Assert.assertEquals("Wrong maxSpeed", 128L, message.getPorts().get(0).getMaxSpeed().longValue());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     * Test covers bodies of actions Output, Copy TTL Out, Copy TTL In
     */
    @Test
    public void testMultipartReplyGroupDescBody01(){
        ByteBuf bb = BufferHelper.buildBuffer("00 07 00 01 00 00 00 00 "+
                                              "00 38 "+//len
                                              "01 "+//type
                                              "00 "+//pad
                                              "00 00 00 08 "+//groupId
                                              "00 30 "+//bucketLen
                                              "00 06 "+//bucketWeight
                                              "00 00 00 05 "+//bucketWatchPort
                                              "00 00 00 04 "+//bucketWatchGroup
                                              "00 00 00 00 "+//bucketPad
                                              "00 00 "+//outputType
                                              "00 10 "+//outputLen
                                              "00 00 10 FF "+//outputPort
                                              "FF FF "+//outputMaxLen
                                              "00 00 00 00 00 00 "+//outputPad
                                              "00 0B "+//copyTTLOutType
                                              "00 08 "+//copyTTLOutLen
                                              "00 00 00 00 "+//copyTTLOutPad
                                              "00 0C "+//copyTTLIntType
                                              "00 08 "+//copyTTLIntLen
                                              "00 00 00 00"//copyTTLInPad
                                              );
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 7, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyGroupDescCase messageCase = (MultipartReplyGroupDescCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyGroupDesc message = messageCase.getMultipartReplyGroupDesc();
        Assert.assertEquals("Wrong type", 1, 
                             message.getGroupDesc().get(0).getType().getIntValue());
        Assert.assertEquals("Wrong groupId", 8, 
                             message.getGroupDesc().get(0).getGroupId().getValue().intValue());
        Assert.assertEquals("Wrong bucketWeight", 6, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getWeight().intValue());
        Assert.assertEquals("Wrong bucketWatchPort", 5, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getWatchPort().
                                                                        getValue().intValue());
        Assert.assertEquals("Wrong bucketWatchGroup", 4, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getWatchGroup().intValue());
        
        Assert.assertEquals("Wrong outputType", Output.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(0).
                getAction().getType());
        
        Assert.assertEquals("Wrong outputPort", 4351, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(0).
                getAction().getAugmentation(PortAction.class).
                getPort().getValue().intValue());
        
        Assert.assertEquals("Wrong outputMaxLen", 65535, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(0).
                getAction().getAugmentation(MaxLengthAction.class).
                getMaxLength().intValue());
        
        Assert.assertEquals("Wrong copyTtlOutType", CopyTtlOut.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(1).
                getAction().getType());
        
        Assert.assertEquals("Wrong copyTtlInType", CopyTtlIn.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(2).
                getAction().getType());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     * Test covers bodies of actions Set MPLS TTL , Dec MPLS TTL, Push VLAN. Push MPLS, Push PBB
     */
    @Test
    public void testMultipartReplyGroupDescBody02(){
        ByteBuf bb = BufferHelper.buildBuffer("00 07 00 01 00 00 00 00 "+
                                              "00 40 "+//len
                                              "01 "+//type
                                              "00 "+//pad
                                              "00 00 00 08 "+//groupId
                                              "00 38 "+//bucketLen
                                              "00 06 "+//bucketWeight
                                              "00 00 00 05 "+//bucketWatchPort
                                              "00 00 00 04 "+//bucketWatchGroup
                                              "00 00 00 00 "+//bucketPad
                                              "00 0F "+//setMplsTtlType
                                              "00 08 "+//setMplsTtlLen
                                              "09 "+//setMplsTtlMPLS_TTL
                                              "00 00 00 "+//setMplsTtlPad
                                              "00 10 "+//decMplsTtlType
                                              "00 08 "+//decMplsTtlLen
                                              "00 00 00 00 "+//decMplsTtlPad
                                              "00 11 "+//pushVlanType
                                              "00 08 "+//pushVlanLen
                                              "00 20 "+//pushVlanEthertype
                                              "00 00 "+//pushVlanPad
                                              "00 13 "+//pushMplsType
                                              "00 08 "+//pushMplsLen
                                              "00 FF "+//pushMplsEthertype
                                              "00 00 "+//pushMplsPad
                                              "00 1A "+//pushPbbType
                                              "00 08 "+//pushPbbLen
                                              "0F FF "+//pushPbbEthertype
                                              "00 00"//pushPbbPad
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 7, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyGroupDescCase messageCase = (MultipartReplyGroupDescCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyGroupDesc message = messageCase.getMultipartReplyGroupDesc();        
        Assert.assertEquals("Wrong type", 1, 
                             message.getGroupDesc().get(0).getType().getIntValue());
        Assert.assertEquals("Wrong groupId", 8, 
                             message.getGroupDesc().get(0).getGroupId().getValue().intValue());
        Assert.assertEquals("Wrong bucketWeight", 6, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getWeight().intValue());
        Assert.assertEquals("Wrong bucketWatchPort", 5, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getWatchPort().getValue().intValue());
        Assert.assertEquals("Wrong bucketWatchGroup", 4, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getWatchGroup().intValue());
        Assert.assertEquals("Wrong setMplsTtlType", SetMplsTtl.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(0).
                getAction().getType());
        Assert.assertEquals("Wrong setMplsTtlMPLS_TTL", 9, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(0).
                getAction().getAugmentation(MplsTtlAction.class).
                getMplsTtl().intValue());
        Assert.assertEquals("Wrong decMplsTtlType", DecMplsTtl.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(1).
                getAction().getType());
        Assert.assertEquals("Wrong pushVlanType", PushVlan.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(2).
                getAction().getType());
        Assert.assertEquals("Wrong pushVlanEthertype", 32, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(2).
                getAction().getAugmentation(EthertypeAction.class).
                getEthertype().getValue().intValue());
        Assert.assertEquals("Wrong pushMplsType", PushMpls.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(3).
                getAction().getType());
        Assert.assertEquals("Wrong pushMplsEthertype", 255, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(3).
                getAction().getAugmentation(EthertypeAction.class).
                getEthertype().getValue().intValue());
        Assert.assertEquals("Wrong pushPbbType", PushPbb.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(4).
                getAction().getType());
        Assert.assertEquals("Wrong pushPbbEthertype", 4095, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(4).
                getAction().getAugmentation(EthertypeAction.class).
                getEthertype().getValue().intValue());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     * Test covers bodies of actions Pop VLAN, Pop PBB, Pop MPLS, Group, Dec NW TTL
     */
    @Test
    public void testMultipartReplyGroupDescBody03(){
        ByteBuf bb = BufferHelper.buildBuffer("00 07 00 01 00 00 00 00 "+
                                              "00 48 "+//len
                                              "01 "+//type
                                              "00 "+//pad
                                              "00 00 00 08 "+//groupId
                                              "00 40 "+//bucketLen
                                              "00 06 "+//bucketWeight
                                              "00 00 00 05 "+//bucketWatchPort
                                              "00 00 00 04 "+//bucketWatchGroup
                                              "00 00 00 00 "+//bucketPad
                                              "00 12 "+//popVlanType
                                              "00 08 "+//popVlanLen
                                              "00 00 00 00 "+//popVlanPad
                                              "00 1B "+//popPbbType
                                              "00 08 "+//popPbbLen
                                              "00 00 00 00 "+//popPbbPad
                                              "00 14 "+//popMplsType
                                              "00 08 "+//popMplsLen
                                              "00 CF "+//popMplsEthertype
                                              "00 00 "+//popMplsPad
                                              "00 15 "+//setQueueType
                                              "00 08 "+//setQueueLen
                                              "00 CF 00 00 "+//setQueueQueueId
                                              "00 16 "+//groupType
                                              "00 08 "+//groupLen
                                              "00 CF 00 00 "+//groupGroupId
                                              "00 18 "+//decNwTtlType
                                              "00 08 "+//decNwTtlLen
                                              "00 00 00 00"//decNwTtlPad
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 7, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyGroupDescCase messageCase = (MultipartReplyGroupDescCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyGroupDesc message = messageCase.getMultipartReplyGroupDesc();        
        Assert.assertEquals("Wrong type", 1, message.getGroupDesc().get(0).getType().getIntValue());
        Assert.assertEquals("Wrong groupId", 8, message.getGroupDesc().get(0).getGroupId().getValue().intValue());
        Assert.assertEquals("Wrong bucketWeight", 6, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getWeight().intValue());
        Assert.assertEquals("Wrong bucketWatchPort", 5, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getWatchPort().getValue().intValue());
        Assert.assertEquals("Wrong bucketWatchGroup", 4, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getWatchGroup().intValue());
        Assert.assertEquals("Wrong popVlanType", PopVlan.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(0).
                getAction().getType());
        Assert.assertEquals("Wrong popPbbType", PopPbb.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(1).
                getAction().getType());
        Assert.assertEquals("Wrong popMplsType", PopMpls.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(2).
                getAction().getType());
        Assert.assertEquals("Wrong popMplsEthertype", 207, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(2).
                getAction().getAugmentation(EthertypeAction.class).
                getEthertype().getValue().intValue());
        Assert.assertEquals("Wrong setQueueType", SetQueue.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(3).
                getAction().getType());
        Assert.assertEquals("Wrong setQueueQueueId", 13565952, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(3).
                getAction().getAugmentation(QueueIdAction.class).
                getQueueId().intValue());
        Assert.assertEquals("Wrong groupType", Group.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(4).
                getAction().getType());
        Assert.assertEquals("Wrong groupGroupId", 13565952, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(4).
                getAction().getAugmentation(GroupIdAction.class).
                getGroupId().intValue());
        Assert.assertEquals("Wrong decNwTtlType", DecNwTtl.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(5).
                getAction().getType());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     * Test covers bodies of actions NW TTL, Experimenter
     */
    @Test
    public void testMultipartReplyGroupDescBody04(){
        ByteBuf bb = BufferHelper.buildBuffer("00 07 00 01 00 00 00 00 "+
                                              "00 30 "+//len
                                              "01 "+//type
                                              "00 "+//pad
                                              "00 00 00 08 "+//groupId
                                              "00 28 "+//bucketLen
                                              "00 06 "+//bucketWeight
                                              "00 00 00 05 "+//bucketWatchPort
                                              "00 00 00 04 "+//bucketWatchGroup
                                              "00 00 00 00 "+//bucketPad
                                              "00 17 "+//nwTTlType
                                              "00 08 "+//nwTTlLen
                                              "0E "+//nwTTlnwTTL
                                              "00 00 00 "+//nwTTlPad
                                              "00 19 "+//setFieldType
                                              "00 10 "+//setFieldLen
                                              "80 00 "+//setFieldOXMClass
                                              "00 "+//setFieldOXMField
                                              "04 "+//setFieldOXMLength
                                              "00 00 00 FF "+ //setFieldPort
                                              "00 00 00 00"
                );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 7, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyGroupDescCase messageCase = (MultipartReplyGroupDescCase) builtByFactory.getMultipartReplyBody();
        MultipartReplyGroupDesc message = messageCase.getMultipartReplyGroupDesc();
        Assert.assertEquals("Wrong type", 1, 
                             message.getGroupDesc().get(0).getType().getIntValue());
        Assert.assertEquals("Wrong groupId", 8, 
                             message.getGroupDesc().get(0).getGroupId().getValue().intValue());
        Assert.assertEquals("Wrong bucketWeight", 6, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getWeight().intValue());
        Assert.assertEquals("Wrong bucketWatchPort", 5, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getWatchPort().
                                                                        getValue().intValue());
        Assert.assertEquals("Wrong bucketWatchGroup", 4, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getWatchGroup().intValue());
        
        Assert.assertEquals("Wrong nwTTlType", SetNwTtl.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(0).
                getAction().getType());
        
        Assert.assertEquals("Wrong nwTTlnwTTL", 14, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(0).
                getAction().getAugmentation(NwTtlAction.class).getNwTtl().intValue());
        
        Assert.assertEquals("Wrong setFieldType", SetField.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(1).
                getAction().getType());
        
        Assert.assertEquals("Wrong setFieldOXMClass", OpenflowBasicClass.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(1).
                getAction().getAugmentation(OxmFieldsAction.class).getMatchEntries().get(0).getOxmClass());
        
        Assert.assertEquals("Wrong setFieldOXMField", InPort.class, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(1).
                getAction().getAugmentation(OxmFieldsAction.class).getMatchEntries().get(0).getOxmMatchField());
        
        Assert.assertEquals("Wrong setFieldOXMValue", 255, 
                message.getGroupDesc().get(0).getBucketsList().get(0).getActionsList().get(1).
                getAction().getAugmentation(OxmFieldsAction.class).getMatchEntries().get(0).
                getAugmentation(PortNumberMatchEntry.class).getPortNumber().getValue().intValue());
    }
}
