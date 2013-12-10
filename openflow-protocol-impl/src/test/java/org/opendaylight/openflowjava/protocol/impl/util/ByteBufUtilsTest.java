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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author michal.polkorab
 *
 */
public class ByteBufUtilsTest {

    private byte[] expected = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, (byte) 0xff};
    
    /**
     * Test of {@link ByteBufUtils#hexStringToBytes(String)}
     */
    @Test
    public void testHexStringToBytes() {
        byte[] data = ByteBufUtils.hexStringToBytes("01 02 03 04 05 ff");

        Assert.assertArrayEquals(expected, data);
    }
    
    /**
     * Test of {@link ByteBufUtils#hexStringToBytes(String, boolean)}
     */
    @Test
    public void testHexStringToBytes2() {
        byte[] data = ByteBufUtils.hexStringToBytes("0102030405ff", false);

        Assert.assertArrayEquals(expected, data);
    }
    
    /**
     * Test of {@link ByteBufUtils#hexStringToByteBuf(String)}
     */
    @Test
    public void testHexStringToByteBuf() {
        ByteBuf bb = ByteBufUtils.hexStringToByteBuf("01 02 03 04 05 ff");
        
        Assert.assertArrayEquals(expected, byteBufToByteArray(bb));
    }
    
    /**
     * Test of {@link ByteBufUtils#hexStringToByteBuf(String, ByteBuf)}
     */
    @Test
    public void testHexStringToGivenByteBuf() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        ByteBufUtils.hexStringToByteBuf("01 02 03 04 05 ff", buffer);

        Assert.assertArrayEquals(expected, byteBufToByteArray(buffer));
    }
    
    private static byte[] byteBufToByteArray(ByteBuf bb) {
        byte[] result = new byte[bb.readableBytes()];
        bb.readBytes(result);
        return result;
    }
    
    /**
     * Test of {@link ByteBufUtils#fillBitMaskFromMap(java.util.Map)}
     */
    @Test
    public void testFillBitmaskByEmptyMap() {
        Map<Integer, Boolean> emptyMap = new HashMap<>();
        String expectedBinaryString = "00000000000000000000000000000000";
        String bitmaskInBinaryString = toBinaryString(emptyMap, 32);
        
        Assert.assertEquals("Not null string", expectedBinaryString, bitmaskInBinaryString);
    }

    private static String toBinaryString(Map<Integer, Boolean> emptyMap, int length) {
        String binaryString = Integer.toBinaryString(ByteBufUtils.fillBitMaskFromMap(emptyMap)); 
        return String.format("%"+length+"s", binaryString).replaceAll(" ", "0");
    }
    
    /**
     * Test of {@link ByteBufUtils#fillBitMaskFromMap(java.util.Map)}
     */
    @Test
    public void testFillBitmaskByFullMap() {
        Map<Integer, Boolean> fullMap = new HashMap<>();
        String expectedBinaryString = "11111111111111111111111111111111";
        String bitmaskValueInBinarySytring;
        for(Integer i=0;i<=31;i++) {
            fullMap.put(i, true);
        }
        bitmaskValueInBinarySytring = toBinaryString(fullMap, 32);
        Assert.assertEquals("Strings does not match", expectedBinaryString, bitmaskValueInBinarySytring);
    }
    
    /**
     * Test of {@link ByteBufUtils#fillBitMaskFromMap(java.util.Map)}
     */
    @Test
    public void testFillBitmaskByZeroMap() {
        Map<Integer, Boolean> zeroMap = new HashMap<>();
        String expectedBinaryString = "00000000000000000000000000000000";
        String bitmaskValueInBinarySytring;
        for(Integer i=0;i<=31;i++) {
            zeroMap.put(i, false);
        }
        bitmaskValueInBinarySytring = toBinaryString(zeroMap, 32);
        Assert.assertEquals("Strings does not match", expectedBinaryString, bitmaskValueInBinarySytring);
    }
    
    /**
     * Test of {@link ByteBufUtils#fillBitMaskFromMap(java.util.Map)}
     */
    @Test
    public void testFillBitmaskByRandomSet() {
        Map<Integer, Boolean> randomMap = new HashMap<>();
        String expectedBinaryString = "00000000000000000111100000000000";
        String bitmaskValueInBinarySytring;
        Boolean mapValue;
        for(Integer i=0;i<=31;i++) {
            mapValue = false;
            if(i>=11 && i<=14) {
                mapValue = true;
            }
            randomMap.put(i, mapValue);
        }
        bitmaskValueInBinarySytring = toBinaryString(randomMap, 32);
        Assert.assertEquals("Strings does not match", expectedBinaryString, bitmaskValueInBinarySytring);
    }
    
    /**
     * Test of {@link ByteBufUtils#fillBitMaskFromList(java.util.Map)}
     */
    @Test
    public void testFillBitmaskByEmptyList() {
        List<Boolean> emptyList = new ArrayList<>();
        emptyList.add(null);
        String expectedBinaryString = "00000000000000000000000000000000";
        String bitmaskInBinaryString = listToBinaryString(emptyList, 32);
        
        Assert.assertEquals("Not null string", expectedBinaryString, bitmaskInBinaryString);
    }

    private static String listToBinaryString(List<Boolean> emptyList, int length) {
        int[] bitMaskArray;
        bitMaskArray = ByteBufUtils.fillBitMaskFromList(emptyList);
        String binaryString = Integer.toBinaryString(bitMaskArray[0]); 
        return String.format("%"+length+"s", binaryString).replaceAll(" ", "0");
    }
    
    /**
     * Test of {@link ByteBufUtils#fillBitMaskFromList(java.util.Map)}
     */
    @Test
    public void testFillBitmaskByFullList() {
        List<Boolean> fullList = new ArrayList<>();
        String expectedBinaryString = "11111111111111111111111111111111";
        String bitmaskValueInBinarySytring;
        for(Integer i=0;i<=31;i++) {
            fullList.add(true);
        }
        bitmaskValueInBinarySytring = listToBinaryString(fullList, 32);
        Assert.assertEquals("Strings does not match", expectedBinaryString, bitmaskValueInBinarySytring);
    }
    
    /**
     * Test of {@link ByteBufUtils#fillBitMaskFromList(java.util.Map)}
     */
    @Test
    public void testFillBitmaskByZeroList() {
        List<Boolean> zeroList = new ArrayList<>();
        String expectedBinaryString = "00000000000000000000000000000000";
        String bitmaskValueInBinarySytring;
        for(Integer i=0;i<=31;i++) {
            zeroList.add(false);
        }
        bitmaskValueInBinarySytring = listToBinaryString(zeroList, 32);
        Assert.assertEquals("Strings does not match", expectedBinaryString, bitmaskValueInBinarySytring);
    }
    
    /**
     * Test of {@link ByteBufUtils#fillBitMaskFromList(java.util.Map)}
     */
    @Test
    public void testFillBitmaskFromRandomList() {
        List<Boolean> randomList = new ArrayList<>();
        String expectedBinaryString = "00000000000000000111100000000000";
        String bitmaskValueInBinarySytring;
        Boolean listValue;
        for(Integer i=0;i<=31;i++) {
            listValue = false;
            if(i>=11 && i<=14) {
                listValue = true;
            }
            randomList.add(listValue);
        }
        bitmaskValueInBinarySytring = listToBinaryString(randomList, 32);
        Assert.assertEquals("Strings does not match", expectedBinaryString, bitmaskValueInBinarySytring);
    }

}
