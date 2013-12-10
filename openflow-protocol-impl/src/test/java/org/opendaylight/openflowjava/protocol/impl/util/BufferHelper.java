/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 * 
 */
public abstract class BufferHelper {

    /**
     * 
     */
    public static final Long DEFAULT_XID = 0x01020304L;
    private static final byte[] XID = new byte[] { 0x01, 0x02, 0x03, 0x04 };

    /**
     * @param payload
     * @return ByteBuf filled with OpenFlow protocol message without first 4
     *         bytes
     */
    public static ByteBuf buildBuffer(byte[] payload) {
        ByteBuf bb = UnpooledByteBufAllocator.DEFAULT.buffer();
        bb.writeBytes(XID);
        bb.writeBytes(payload);
        return bb;
    }
    
    /**
     * @param payload String in hex format
     * @return ByteBuf filled with OpenFlow protocol message without first 4
     *         bytes
     */
    public static ByteBuf buildBuffer(String payload) {
        return buildBuffer(ByteBufUtils.hexStringToBytes(payload));
    }
    
    /**
     * @return ByteBuf filled with OpenFlow protocol header message without first 4
     *         bytes
     */
    public static ByteBuf buildBuffer() {
        ByteBuf bb = UnpooledByteBufAllocator.DEFAULT.buffer();
        bb.writeBytes(XID);
        bb.writeBytes(new byte[0]);
        return bb;
    }

    /**
     * Use version 1.3 for encoded message
     * @param input ByteBuf to be checked for correct OpenFlow Protocol header
     * @param msgType type of received message
     * @param length expected length of message in header
     */
    public static void checkHeaderV13(ByteBuf input, byte msgType, int length) {
        checkHeader(input, msgType, length, (short) EncodeConstants.OF13_VERSION_ID);
    }
    
    /**
     * Use version 1.0 for encoded message
     * @param input ByteBuf to be checked for correct OpenFlow Protocol header
     * @param msgType type of received message
     * @param length expected length of message in header
     */
    public static void checkHeaderV10(ByteBuf input, byte msgType, int length) {
        checkHeader(input, msgType, length, (short) EncodeConstants.OF10_VERSION_ID);
    }
    
    private static void checkHeader(ByteBuf input, byte msgType, int length, Short version) {
        Assert.assertEquals("Wrong version", version, Short.valueOf(input.readByte()));
        Assert.assertEquals("Wrong type", msgType, input.readByte());
        Assert.assertEquals("Wrong length", length, input.readUnsignedShort());
        Assert.assertEquals("Wrong Xid", DEFAULT_XID, Long.valueOf(input.readUnsignedInt()));
    }
    

    /**
     * @param ofHeader OpenFlow protocol header
     */
    public static void checkHeaderV13(OfHeader ofHeader) {
        checkHeader(ofHeader, (short) EncodeConstants.OF13_VERSION_ID);
    }
    
    /**
     * @param ofHeader OpenFlow protocol header
     */
    public static void checkHeaderV10(OfHeader ofHeader) {
        checkHeader(ofHeader, (short) EncodeConstants.OF10_VERSION_ID);
    }
    
    private static void checkHeader(OfHeader ofHeader, Short version) {
        Assert.assertEquals("Wrong version", version, ofHeader.getVersion());
        Assert.assertEquals("Wrong Xid", DEFAULT_XID, ofHeader.getXid());
    }
    
    /**
     * @param builder
     * @param version wire protocol number used
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static void setupHeader(Object builder, int version) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method m = builder.getClass().getMethod("setVersion", Short.class);
        m.invoke(builder, (short) version);
        Method m2 = builder.getClass().getMethod("setXid", Long.class);
        m2.invoke(builder, BufferHelper.DEFAULT_XID);
    }
    
    /**
     * Use version 1.3 for decoding message
     * @param decoder decoder instance
     * @param bb data input buffer
     * @return message decoded pojo
     */
    public static <E extends DataObject> E decodeV13(OFDeserializer<E> decoder, ByteBuf bb) {
        return bufferToMessage(decoder, EncodeConstants.OF13_VERSION_ID, bb);
    }
    
    /**
     * Use version 1.0 for decoding message
     * @param decoder decoder instance
     * @param bb data input buffer
     * @return message decoded pojo
     */
    public static <E extends DataObject> E decodeV10(OFDeserializer<E> decoder, ByteBuf bb) {
        return bufferToMessage(decoder, EncodeConstants.OF10_VERSION_ID, bb);
    }
    
    private static <E extends DataObject> E bufferToMessage(OFDeserializer<E> decoder, short version, ByteBuf bb) {
        return decoder.bufferToMessage(bb, version);
    }
    
    /**
     * Use OF-protocol version 1.3
     * @param encoder serialize factory
     * @param out buffer the result will be written into
     * @param pojo input message
     */
    public static <E extends DataObject> void encodeV13(OFSerializer<E> encoder, ByteBuf out, E pojo) {
        messageToBuffer(encoder, out, pojo, HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
    }
    
    private static <E extends DataObject> void messageToBuffer(
            OFSerializer<E> encoder, ByteBuf out, E pojo, Short version) {
        encoder.messageToBuffer(version, out, pojo);
    }
}
