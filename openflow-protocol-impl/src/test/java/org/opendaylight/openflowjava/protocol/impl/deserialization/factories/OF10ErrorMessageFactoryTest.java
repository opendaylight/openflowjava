/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;

/**
 * @author michal.polkorab
 */
public class OF10ErrorMessageFactoryTest {

	/**
     * Test of {@link OF10ErrorMessageFactory} for correct translation into POJO
     */
	@Test
	public void testWithoutData() {
		ByteBuf bb = BufferHelper.buildBuffer("00 01 00 02");
		ErrorMessage builtByFactory = BufferHelper.decodeV10(
				OF10ErrorMessageFactory.getInstance(), bb);

		BufferHelper.checkHeaderV10(builtByFactory);
		Assert.assertEquals("Wrong type", 1, builtByFactory.getType().intValue());
		Assert.assertEquals("Wrong code", 2, builtByFactory.getCode().intValue());
		Assert.assertEquals("Wrong type string", "BADREQUEST", builtByFactory.getTypeString());
		Assert.assertEquals("Wrong code string", "BADSTAT", builtByFactory.getCodeString());
		Assert.assertNull("Data is not null", builtByFactory.getData());
	}
	
	/**
     * Test of {@link OF10ErrorMessageFactory} for correct translation into POJO
     */
	@Test
	public void testWithData() {
		ByteBuf bb = BufferHelper.buildBuffer("00 00 00 01 00 01 02 03");
		ErrorMessage builtByFactory = BufferHelper.decodeV10(
				OF10ErrorMessageFactory.getInstance(), bb);

		BufferHelper.checkHeaderV10(builtByFactory);
		Assert.assertEquals("Wrong type", 0, builtByFactory.getType().intValue());
		Assert.assertEquals("Wrong code", 1, builtByFactory.getCode().intValue());
		Assert.assertEquals("Wrong type string", "HELLOFAILED", builtByFactory.getTypeString());
		Assert.assertEquals("Wrong code string", "EPERM", builtByFactory.getCodeString());
		Assert.assertArrayEquals("Wrong data", new byte[]{0x00, 0x01, 0x02, 0x03}, builtByFactory.getData());
	}
	
	/**
     * Test of {@link OF10ErrorMessageFactory} for correct translation into POJO
     */
	@Test
	public void testWithIncorrectTypeEnum() {
		ByteBuf bb = BufferHelper.buildBuffer("00 0A 00 05 00 01 02 03");
		ErrorMessage builtByFactory = BufferHelper.decodeV10(
				OF10ErrorMessageFactory.getInstance(), bb);

		BufferHelper.checkHeaderV10(builtByFactory);
		Assert.assertEquals("Wrong type", 10, builtByFactory.getType().intValue());
		Assert.assertEquals("Wrong code", 5, builtByFactory.getCode().intValue());
		Assert.assertEquals("Wrong type string", "UNKNOWN_TYPE", builtByFactory.getTypeString());
		Assert.assertEquals("Wrong code string", "UNKNOWN_CODE", builtByFactory.getCodeString());
		Assert.assertArrayEquals("Wrong data", new byte[]{0x00, 0x01, 0x02, 0x03}, builtByFactory.getData());
	}
	
	/**
     * Test of {@link OF10ErrorMessageFactory} for correct translation into POJO
     */
	@Test
	public void testWithIncorrectCodeEnum() {
		ByteBuf bb = BufferHelper.buildBuffer("00 03 00 06 00 01 02 03");
		ErrorMessage builtByFactory = BufferHelper.decodeV10(
				OF10ErrorMessageFactory.getInstance(), bb);

		BufferHelper.checkHeaderV10(builtByFactory);
		Assert.assertEquals("Wrong type", 3, builtByFactory.getType().intValue());
		Assert.assertEquals("Wrong code", 6, builtByFactory.getCode().intValue());
		Assert.assertEquals("Wrong type string", "FLOWMODFAILED", builtByFactory.getTypeString());
		Assert.assertEquals("Wrong code string", "UNKNOWN_CODE", builtByFactory.getCodeString());
		Assert.assertArrayEquals("Wrong data", new byte[]{0x00, 0x01, 0x02, 0x03}, builtByFactory.getData());
	}

}
