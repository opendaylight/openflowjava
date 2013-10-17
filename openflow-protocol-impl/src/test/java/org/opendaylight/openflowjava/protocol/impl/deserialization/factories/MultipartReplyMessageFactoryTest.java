/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyAggregate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroup;
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
}
