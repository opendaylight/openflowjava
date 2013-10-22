/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandDrop;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandDscpRemark;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyAggregate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroup;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTable;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MultipartReplyMessageFactoryTest {

    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void test(){
        ByteBuf bb = BufferHelper.buildBuffer("00 07 00 01 00 00 00 00 01 02 03 04");
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        
        Assert.assertEquals("Wrong type", 0x07, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        //Assert.assertArrayEquals("Wrong body", new byte[]{0x01, 0x02, 0x03, 0x04}, builtByFactory.getBody());
    }
    
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
        MultipartReplyDesc message = (MultipartReplyDesc) builtByFactory.getMultipartReplyBody();
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
                                              "00 01 01 01 01 01 01 01 "+//cookie
                                              "00 01 01 01 01 01 01 01 "+//packetCount
                                              "00 01 01 01 01 01 01 01"//byteCount
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0x01, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyFlow message = (MultipartReplyFlow) builtByFactory.getMultipartReplyBody();
        Assert.assertEquals("Wrong tableId", 8, message.getFlowStats().get(0).getTableId().intValue());
        Assert.assertEquals("Wrong durationSec", 9, message.getFlowStats().get(0).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec", 7, message.getFlowStats().get(0).getDurationNsec().intValue());
        Assert.assertEquals("Wrong priority", 12, message.getFlowStats().get(0).getPriority().intValue());
        Assert.assertEquals("Wrong idleTimeOut", 14, message.getFlowStats().get(0).getIdleTimeout().intValue());
        Assert.assertEquals("Wrong hardTimeOut", 15, message.getFlowStats().get(0).getHardTimeout().intValue());
        Assert.assertEquals("Wrong flags", new FlowModFlags(true, false, true, false, true), 
                                           message.getFlowStats().get(0).getFlags());
        Assert.assertEquals("Wrong cookie", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getFlowStats().get(0).getCookie());
        Assert.assertEquals("Wrong packetCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getFlowStats().get(0).getPacketCount());
        Assert.assertEquals("Wrong byteCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getFlowStats().get(0).getByteCount());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyAggregateBody(){
        ByteBuf bb = BufferHelper.buildBuffer("00 02 00 01 00 00 00 00 "+
                                              "00 01 01 01 01 01 01 01 "+//packetCount
                                              "00 01 01 01 01 01 01 01 "+//byteCount
                                              "00 00 00 08 "+//flowCount
                                              "00 00 00 00"//pad
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0x02, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        MultipartReplyAggregate message = (MultipartReplyAggregate) builtByFactory.getMultipartReplyBody();
        Assert.assertEquals("Wrong packetCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getPacketCount());
        Assert.assertEquals("Wrong byteCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
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
                                              "00 01 01 01 01 01 01 01 "+//lookupCount
                                              "00 01 01 01 01 01 01 01"//matchedCount
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0x03, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        
        MultipartReplyTable message = (MultipartReplyTable) builtByFactory.getMultipartReplyBody();
        Assert.assertEquals("Wrong tableId", 8, message.getTableStats().get(0).getTableId().intValue());
        Assert.assertEquals("Wrong activeCount", 16, message.getTableStats().get(0).getActiveCount().longValue());
        Assert.assertEquals("Wrong lookupCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getTableStats().get(0).getLookupCount());
        Assert.assertEquals("Wrong matchedCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
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
                                              "00 01 01 01 01 01 01 01 "+//rxPackets
                                              "00 02 02 02 02 02 02 02 "+//txPackets
                                              "00 02 03 02 03 02 03 02 "+//rxBytes
                                              "00 02 03 02 03 02 03 02 "+//txBytes
                                              "00 02 03 02 03 02 03 02 "+//rxDropped
                                              "00 02 03 02 03 02 03 02 "+//txDropped
                                              "00 02 03 02 03 02 03 02 "+//rxErrors
                                              "00 02 03 02 03 02 03 02 "+//txErrors
                                              "00 02 03 02 03 02 03 02 "+//rxFrameErr
                                              "00 02 03 02 03 02 03 02 "+//rxOverErr
                                              "00 02 03 02 03 02 03 02 "+//rxCrcErr
                                              "00 02 03 02 03 02 03 02 "+//collisions
                                              "00 00 00 02 "+//durationSec
                                              "00 00 00 04"//durationNsec
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0x04, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        
        MultipartReplyPortStats message = (MultipartReplyPortStats) builtByFactory.getMultipartReplyBody();
        
        Assert.assertEquals("Wrong portNo", 255, message.getPortStats().get(0).getPortNo().intValue());
        Assert.assertEquals("Wrong rxPackets", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getPortStats().get(0).getRxPackets());
        Assert.assertEquals("Wrong txPackets", 
                new BigInteger(new byte[]{0x00, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getPortStats().get(0).getTxPackets());
        Assert.assertEquals("Wrong rxBytes", 
                new BigInteger(new byte[]{0x00, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getRxBytes());
        Assert.assertEquals("Wrong txBytes", 
                new BigInteger(new byte[]{0x00, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getTxBytes());
        Assert.assertEquals("Wrong rxDropped", 
                new BigInteger(new byte[]{0x00, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getRxDropped());
        Assert.assertEquals("Wrong txDropped", 
                new BigInteger(new byte[]{0x00, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getTxDropped());
        Assert.assertEquals("Wrong rxErrors", 
                new BigInteger(new byte[]{0x00, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getRxErrors());
        Assert.assertEquals("Wrong txErrors", 
                new BigInteger(new byte[]{0x00, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getTxErrors());
        Assert.assertEquals("Wrong rxFrameErr", 
                new BigInteger(new byte[]{0x00, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getRxFrameErr());
        Assert.assertEquals("Wrong rxOverErr", 
                new BigInteger(new byte[]{0x00, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getRxOverErr());
        Assert.assertEquals("Wrong rxCrcErr", 
                new BigInteger(new byte[]{0x00, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getPortStats().get(0).getRxCrcErr());
        Assert.assertEquals("Wrong collisions", 
                new BigInteger(new byte[]{0x00, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
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
                                              "00 02 03 02 03 02 03 02 "+//txBytes
                                              "00 02 02 02 02 02 02 02 "+//txPackets
                                              "00 02 03 02 03 02 03 02 "+//txErrors
                                              "00 00 00 02 "+//durationSec
                                              "00 00 00 04"//durationNsec
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0x05, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        
        MultipartReplyQueue message = (MultipartReplyQueue) builtByFactory.getMultipartReplyBody();
        
        Assert.assertEquals("Wrong portNo", 255, message.getQueueStats().get(0).getPortNo().intValue());
        Assert.assertEquals("Wrong queueId", 16, message.getQueueStats().get(0).getQueueId().intValue());
        Assert.assertEquals("Wrong txBytes", 
                new BigInteger(new byte[]{0x00, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
                message.getQueueStats().get(0).getTxBytes());
        Assert.assertEquals("Wrong txPackets", 
                new BigInteger(new byte[]{0x00, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getQueueStats().get(0).getTxPackets());
        Assert.assertEquals("Wrong txErrors", 
                new BigInteger(new byte[]{0x00, 0x02, 0x03, 0x02, 0x03, 0x02, 0x03, 0x02}), 
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
                                              "00 01 01 01 01 01 01 01 "+//packetCount
                                              "00 01 01 01 01 01 01 01 "+//byteCount
                                              "00 00 00 08 "+//durationSec
                                              "00 00 00 09 "+//durationNsec
                                              "00 01 01 01 01 01 01 01 "+//packetCountBucket
                                              "00 01 01 01 01 01 01 01 "+//byteCountBucket
                                              "00 02 02 02 02 02 02 02 "+//packetCountBucket_2
                                              "00 02 02 02 02 02 02 02 "+//byteCountBucket_2
                                              "00 48 "+//length_2
                                              "00 00 "+//pad1.2
                                              "00 00 00 10 "+//groupId_2
                                              "00 00 00 12 "+//refCount_2
                                              "00 00 00 00 "+//pad2.2
                                              "00 01 01 01 01 01 01 01 "+//packetCount_2
                                              "00 01 01 01 01 01 01 01 "+//byteCount_2
                                              "00 00 00 08 "+//durationSec_2
                                              "00 00 00 09 "+//durationNsec_2
                                              "00 01 01 01 01 01 01 01 "+//packetCountBucket_1.2
                                              "00 01 01 01 01 01 01 01 "+//byteCountBucket_1.2
                                              "00 02 02 02 02 02 02 02 "+//packetCountBucket_2.2
                                              "00 02 02 02 02 02 02 02"//byteCountBucket_2.2
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 0x06, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        
        MultipartReplyGroup message = (MultipartReplyGroup) builtByFactory.getMultipartReplyBody();
        
        Assert.assertEquals("Wrong groupId", 16, message.getGroupStats().get(0).getGroupId().intValue());
        Assert.assertEquals("Wrong refCount", 18, message.getGroupStats().get(0).getRefCount().intValue());
        Assert.assertEquals("Wrong packetCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(0).getPacketCount());
        Assert.assertEquals("Wrong byteCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(0).getByteCount());
        Assert.assertEquals("Wrong durationSec", 8, message.getGroupStats().get(0).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec", 9, message.getGroupStats().get(0).getDurationNsec().intValue());
        Assert.assertEquals("Wrong packetCountBucket", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(0).getBucketStats().get(0).getPacketCount());
        Assert.assertEquals("Wrong byteCountBucket", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(0).getBucketStats().get(0).getByteCount());
        Assert.assertEquals("Wrong packetCountBucket_2", 
                new BigInteger(new byte[]{0x00, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getGroupStats().get(0).getBucketStats().get(1).getPacketCount());
        Assert.assertEquals("Wrong byteCountBucket_2", 
                new BigInteger(new byte[]{0x00, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getGroupStats().get(0).getBucketStats().get(1).getByteCount());
        
        Assert.assertEquals("Wrong groupId_2", 16, message.getGroupStats().get(1).getGroupId().intValue());
        Assert.assertEquals("Wrong refCount_2", 18, message.getGroupStats().get(1).getRefCount().intValue());
        Assert.assertEquals("Wrong packetCount_2", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(1).getPacketCount());
        Assert.assertEquals("Wrong byteCount_2", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(1).getByteCount());
        Assert.assertEquals("Wrong durationSec_2", 8, message.getGroupStats().get(1).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec_2", 9, message.getGroupStats().get(1).getDurationNsec().intValue());
        Assert.assertEquals("Wrong packetCountBucket_1.2", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(1).getBucketStats().get(0).getPacketCount());
        Assert.assertEquals("Wrong byteCountBucket_1.2", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getGroupStats().get(1).getBucketStats().get(0).getByteCount());
        Assert.assertEquals("Wrong packetCountBucket_2.2", 
                new BigInteger(new byte[]{0x00, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getGroupStats().get(1).getBucketStats().get(1).getPacketCount());
        Assert.assertEquals("Wrong byteCountBucket_2.2", 
                new BigInteger(new byte[]{0x00, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
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
        
        MultipartReplyMeterFeatures message = (MultipartReplyMeterFeatures) builtByFactory.getMultipartReplyBody();
        
        Assert.assertEquals("Wrong maxMeter", 9, message.getMaxMeter().intValue());
        Assert.assertEquals("Wrong bandTypes", 1, message.getBandTypes().getIntValue());
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
                                              "00 01 01 01 01 01 01 01 "+//packetInCount
                                              "00 01 01 01 01 01 01 01 "+//byteInCount
                                              "00 00 00 05 "+//durationSec
                                              "00 00 00 05 "+//durationNsec
                                              "00 01 01 01 01 01 01 01 "+//packetBandCount_01
                                              "00 01 01 01 01 01 01 01 "+//byteBandCount_01
                                              "00 02 02 02 02 02 02 02 "+//packetBandCount_02
                                              "00 02 02 02 02 02 02 02 "+//byteBandCount_02
                                              "00 03 03 03 03 03 03 03 "+//packetBandCount_03
                                              "00 03 03 03 03 03 03 03"//byteBandCount_03
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 9, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        
        MultipartReplyMeter message = (MultipartReplyMeter) builtByFactory.getMultipartReplyBody();
        
        Assert.assertEquals("Wrong meterId", 9, 
                             message.getMeterStats().get(0).getMeterId().intValue());
        Assert.assertEquals("Wrong flowCount", 7, 
                            message.getMeterStats().get(0).getFlowCount().intValue());
        Assert.assertEquals("Wrong packetInCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getPacketInCount());
        Assert.assertEquals("Wrong byteInCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getByteInCount());
        Assert.assertEquals("Wrong durationSec", 5, 
                message.getMeterStats().get(0).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec", 5, 
                message.getMeterStats().get(0).getDurationNsec().intValue());
        Assert.assertEquals("Wrong packetBandCount_01", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getMeterBandStats().get(0).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_01", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getMeterBandStats().get(0).getByteBandCount());
        Assert.assertEquals("Wrong packetBandCount_02", 
                new BigInteger(new byte[]{0x00, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getMeterStats().get(0).getMeterBandStats().get(1).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_02", 
                new BigInteger(new byte[]{0x00, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getMeterStats().get(0).getMeterBandStats().get(1).getByteBandCount());
        Assert.assertEquals("Wrong packetBandCount_03", 
                new BigInteger(new byte[]{0x00, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03}), 
                message.getMeterStats().get(0).getMeterBandStats().get(2).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_03", 
                new BigInteger(new byte[]{0x00, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03}), 
                message.getMeterStats().get(0).getMeterBandStats().get(2).getByteBandCount());
    }
    
    @Test
    public void testMultipartReplyMeterBodyMulti(){
        ByteBuf bb = BufferHelper.buildBuffer("00 09 00 01 00 00 00 00 "+
                                              "00 00 00 09 "+//meterId_0
                                              "00 58 "+//len_0
                                              "00 00 00 00 00 00 "+//pad_0
                                              "00 00 00 07 "+//flowCount_0
                                              "00 01 01 01 01 01 01 01 "+//packetInCount_0
                                              "00 01 01 01 01 01 01 01 "+//byteInCount_0
                                              "00 00 00 05 "+//durationSec_0
                                              "00 00 00 05 "+//durationNsec_0
                                              "00 01 01 01 01 01 01 01 "+//packetBandCount_01
                                              "00 01 01 01 01 01 01 01 "+//byteBandCount_01
                                              "00 02 02 02 02 02 02 02 "+//packetBandCount_02
                                              "00 02 02 02 02 02 02 02 "+//byteBandCount_02
                                              "00 03 03 03 03 03 03 03 "+//packetBandCount_03
                                              "00 03 03 03 03 03 03 03 "+//byteBandCount_03
                                              "00 00 00 08 "+//meterId_1
                                              "00 58 "+//len_1
                                              "00 00 00 00 00 00 "+//pad_1
                                              "00 00 00 07 "+//flowCount_1
                                              "00 01 01 01 01 01 01 01 "+//packetInCount_1
                                              "00 01 01 01 01 01 01 01 "+//byteInCount_1
                                              "00 00 00 05 "+//durationSec_1
                                              "00 00 00 05 "+//durationNsec_1
                                              "00 01 01 01 01 01 01 01 "+//packetBandCount_11
                                              "00 01 01 01 01 01 01 01 "+//byteBandCount_11
                                              "00 02 02 02 02 02 02 02 "+//packetBandCount_12
                                              "00 02 02 02 02 02 02 02 "+//byteBandCount_12
                                              "00 03 03 03 03 03 03 03 "+//packetBandCount_13
                                              "00 03 03 03 03 03 03 03"//byteBandCount_13
                                              );
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 9, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        
        MultipartReplyMeter message = (MultipartReplyMeter) builtByFactory.getMultipartReplyBody();
        
        Assert.assertEquals("Wrong meterId", 9, 
                             message.getMeterStats().get(0).getMeterId().intValue());
        Assert.assertEquals("Wrong flowCount", 7, 
                            message.getMeterStats().get(0).getFlowCount().intValue());
        Assert.assertEquals("Wrong packetInCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getPacketInCount());
        Assert.assertEquals("Wrong byteInCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getByteInCount());
        Assert.assertEquals("Wrong durationSec", 5, 
                message.getMeterStats().get(0).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec", 5, 
                message.getMeterStats().get(0).getDurationNsec().intValue());
        Assert.assertEquals("Wrong packetBandCount_01", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getMeterBandStats().get(0).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_01", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(0).getMeterBandStats().get(0).getByteBandCount());
        Assert.assertEquals("Wrong packetBandCount_02", 
                new BigInteger(new byte[]{0x00, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getMeterStats().get(0).getMeterBandStats().get(1).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_02", 
                new BigInteger(new byte[]{0x00, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getMeterStats().get(0).getMeterBandStats().get(1).getByteBandCount());
        Assert.assertEquals("Wrong packetBandCount_03", 
                new BigInteger(new byte[]{0x00, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03}), 
                message.getMeterStats().get(0).getMeterBandStats().get(2).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_03", 
                new BigInteger(new byte[]{0x00, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03}), 
                message.getMeterStats().get(0).getMeterBandStats().get(2).getByteBandCount());
        
        Assert.assertEquals("Wrong meterId", 8, 
                message.getMeterStats().get(1).getMeterId().intValue());
        Assert.assertEquals("Wrong flowCount", 7, 
                message.getMeterStats().get(1).getFlowCount().intValue());
        Assert.assertEquals("Wrong packetInCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(1).getPacketInCount());
        Assert.assertEquals("Wrong byteInCount", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(1).getByteInCount());
        Assert.assertEquals("Wrong durationSec", 5, 
                message.getMeterStats().get(1).getDurationSec().intValue());
        Assert.assertEquals("Wrong durationNsec", 5, 
                message.getMeterStats().get(1).getDurationNsec().intValue());
        Assert.assertEquals("Wrong packetBandCount_01", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(1).getMeterBandStats().get(0).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_01", 
                new BigInteger(new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}), 
                message.getMeterStats().get(1).getMeterBandStats().get(0).getByteBandCount());
        Assert.assertEquals("Wrong packetBandCount_02", 
                new BigInteger(new byte[]{0x00, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getMeterStats().get(1).getMeterBandStats().get(1).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_02", 
                new BigInteger(new byte[]{0x00, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}), 
                message.getMeterStats().get(1).getMeterBandStats().get(1).getByteBandCount());
        Assert.assertEquals("Wrong packetBandCount_03", 
                new BigInteger(new byte[]{0x00, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03}), 
                message.getMeterStats().get(1).getMeterBandStats().get(2).getPacketBandCount());
        Assert.assertEquals("Wrong byteBandCount_03", 
                new BigInteger(new byte[]{0x00, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03}), 
                message.getMeterStats().get(1).getMeterBandStats().get(2).getByteBandCount());
    }
    
    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testMultipartReplyMeterConfigBody(){
        ByteBuf bb = BufferHelper.buildBuffer("00 0A 00 01 00 00 00 00 "+
                                              "00 38 "+//len
                                              "00 01 "+//flags
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
        
        MultipartReplyMeterConfig message = (MultipartReplyMeterConfig) builtByFactory.getMultipartReplyBody();
        
        Assert.assertEquals("Wrong flags", 1, 
                             message.getMeterConfig().get(0).getFlags().getIntValue());
        Assert.assertEquals("Wrong meterId", 9, 
                             message.getMeterConfig().get(0).getMeterId().intValue());
        
        MeterBandDrop meterBandDrop = (MeterBandDrop) message.getMeterConfig().get(0).getBands().get(0).getMeterBand(); 
        Assert.assertEquals("Wrong meterBandDrop.type", 1, meterBandDrop.getType().getIntValue()); 
        Assert.assertEquals("Wrong meterBandDrop.rate", 17, meterBandDrop.getRate().intValue());
        Assert.assertEquals("Wrong meterBandDrop.burstSize", 32, meterBandDrop.getBurstSize().intValue());
        
        MeterBandDscpRemark meterBandDscp = (MeterBandDscpRemark) message.getMeterConfig().get(0).getBands().get(1).getMeterBand(); 
        Assert.assertEquals("Wrong meterBandDscp.type", 2, meterBandDscp.getType().getIntValue()); 
        Assert.assertEquals("Wrong meterBandDscp.rate", 17, meterBandDscp.getRate().intValue());
        Assert.assertEquals("Wrong meterBandDscp.burstSize", 32, meterBandDscp.getBurstSize().intValue());
        Assert.assertEquals("Wrong meterBandDscp.precLevel", 4, meterBandDscp.getPrecLevel().intValue());
        
        MeterBandExperimenter meterBandExperimenter = (MeterBandExperimenter) message.getMeterConfig().get(0).getBands().get(2).getMeterBand(); 
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
                                              "00 01 "+//flags
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
                                              "00 00 "+//flags01
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
        
        MultipartReplyMeterConfig message = (MultipartReplyMeterConfig) builtByFactory.getMultipartReplyBody();
        
        Assert.assertEquals("Wrong flags", 1, 
                             message.getMeterConfig().get(0).getFlags().getIntValue());
        Assert.assertEquals("Wrong meterId", 9, 
                             message.getMeterConfig().get(0).getMeterId().intValue());
        
        MeterBandDrop meterBandDrop = (MeterBandDrop) message.getMeterConfig().get(0).getBands().get(0).getMeterBand(); 
        Assert.assertEquals("Wrong meterBandDrop.type", 1, meterBandDrop.getType().getIntValue()); 
        Assert.assertEquals("Wrong meterBandDrop.rate", 17, meterBandDrop.getRate().intValue());
        Assert.assertEquals("Wrong meterBandDrop.burstSize", 32, meterBandDrop.getBurstSize().intValue());
        
        MeterBandDscpRemark meterBandDscp = (MeterBandDscpRemark) message.getMeterConfig().get(0).getBands().get(1).getMeterBand(); 
        Assert.assertEquals("Wrong meterBandDscp.type", 2, meterBandDscp.getType().getIntValue()); 
        Assert.assertEquals("Wrong meterBandDscp.rate", 17, meterBandDscp.getRate().intValue());
        Assert.assertEquals("Wrong meterBandDscp.burstSize", 32, meterBandDscp.getBurstSize().intValue());
        Assert.assertEquals("Wrong meterBandDscp.precLevel", 4, meterBandDscp.getPrecLevel().intValue());
        
        MeterBandExperimenter meterBandExperimenter = (MeterBandExperimenter) message.getMeterConfig().get(0).getBands().get(2).getMeterBand(); 
        Assert.assertEquals("Wrong meterBandExperimenter.type", 0xFFFF, meterBandExperimenter.getType().getIntValue()); 
        Assert.assertEquals("Wrong meterBandExperimenter.rate", 17, meterBandExperimenter.getRate().intValue());
        Assert.assertEquals("Wrong meterBandExperimenter.burstSize", 32, meterBandExperimenter.getBurstSize().intValue());
        Assert.assertEquals("Wrong meterBandExperimenter.experimenter", 4, meterBandExperimenter.getExperimenter().intValue());
        
        Assert.assertEquals("Wrong flags01", 1, 
                             message.getMeterConfig().get(0).getFlags().getIntValue());
        Assert.assertEquals("Wrong meterId01", 7, 
                             message.getMeterConfig().get(1).getMeterId().intValue());
        
        MeterBandDscpRemark meterBandDscp01 = (MeterBandDscpRemark) message.getMeterConfig().get(1).getBands().get(0).getMeterBand(); 
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
        
        MultipartReplyExperimenter message = (MultipartReplyExperimenter) builtByFactory.getMultipartReplyBody();
        
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
                                                "00 00 00 81 " + //advertised features
                                                "00 00 00 81 " + //supported features
                                                "00 00 00 81 " + //peer features
                                                "00 00 00 81 " + //curr speed
                                                "00 00 00 80" //max speed
                                                );
        bb.writeBytes(bb2.copy(4, bb2.readableBytes()-4));//excluding version and xid
        
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 13, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        
        MultipartReplyPortDesc message = (MultipartReplyPortDesc) builtByFactory.getMultipartReplyBody();
        
        Assert.assertEquals("Wrong portNo", 66051L, message.getPorts().get(0).getPortNo().longValue());
        Assert.assertEquals("Wrong macAddress", new MacAddress("08002700B0EB"), 
                                                message.getPorts().get(0).getHwAddr());
        Assert.assertEquals("Wrong portName", "SampleText", 
                                                message.getPorts().get(0).getName());
        Assert.assertEquals("Wrong portConfig", new PortConfig(false, true, false, true), 
                                                message.getPorts().get(0).getConfig());
        Assert.assertEquals("Wrong portState", new PortState(true, false, true), 
                                               message.getPorts().get(0).getState());
        Assert.assertEquals("Wrong currentFeatures", new PortFeatures(true, false, false, false,
                                                                      false, false, false, true, 
                                                                      false, false, false, false, 
                                                                      false, false, false, false), 
                                                  message.getPorts().get(0).getCurrentFeatures());
        Assert.assertEquals("Wrong advertisedFeatures", 
                             new PortFeatures(true, false, false, false,
                                              false, false, false, true, 
                                              false, false, false, false, 
                                              false, false, false, false), 
                                              message.getPorts().get(0).getAdvertisedFeatures());
        Assert.assertEquals("Wrong supportedFeatures", 
                             new PortFeatures(true, false, false, false,
                                              false, false, false, true, 
                                              false, false, false, false, 
                                              false, false, false, false), 
                                              message.getPorts().get(0).getSupportedFeatures());
        Assert.assertEquals("Wrong peerFeatures", 
                             new PortFeatures(true, false, false, false,
                                              false, false, false, true, 
                                              false, false, false, false, 
                                              false, false, false, false), 
                                              message.getPorts().get(0).getPeerFeatures());
        Assert.assertEquals("Wrong currSpeed", 129L, message.getPorts().get(0).getCurrSpeed().longValue());
        Assert.assertEquals("Wrong maxSpeed", 128L, message.getPorts().get(0).getMaxSpeed().longValue());
    }
}
