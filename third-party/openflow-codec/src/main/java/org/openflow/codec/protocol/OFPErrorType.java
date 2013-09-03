package org.openflow.codec.protocol;

import java.util.HashMap;
import java.util.Map;

import org.openflow.codec.util.U16;

/**
 * enum correspond to ofp_error_type
 * 
 * @author AnilGujele
 * 
 */
public enum OFPErrorType
{
	OFPET_HELLO_FAILED(0), OFPET_BAD_REQUEST(1), OFPET_BAD_ACTION(2), OFPET_BAD_INSTRUCTION(
			3), OFPET_BAD_MATCH(4), OFPET_FLOW_MOD_FAILED(5), OFPET_GROUP_MOD_FAILED(
			6), OFPET_PORT_MOD_FAILED(7), OFPET_TABLE_MOD_FAILED(8), OFPET_QUEUE_OP_FAILED(
			9), OFPET_SWITCH_CONFIG_FAILED(10), OFPET_ROLE_REQUEST_FAILED(11), OFPET_METER_MOD_FAILED(
			12), OFPET_TABLE_FEATURES_FAILED(13), OFPET_EXPERIMENTER(0xffff);

	private static Map<Integer, OFPErrorType> mapping;

	private short type;

	OFPErrorType(int type)
	{
		this.type = (short) type;
		addMapping(type, this);
	}

	/**
	 * add mapping for OFPErrorType
	 * 
	 * @param type
	 * @param errorType
	 */
	private static void addMapping(int type, OFPErrorType errorType)
	{
		if (null == mapping)
		{
			mapping = new HashMap<Integer, OFPErrorType>();
		}
		mapping.put(type, errorType);
	}

	/**
	 * get the OFPErrorType of value
	 * 
	 * @param type
	 * @return
	 */
	public static OFPErrorType valueOf(short type)
	{
		return mapping.get(U16.f(type));
	}

	/**
	 * get type value
	 * 
	 * @return
	 */
	public short getValue()
	{
		return type;
	}

}
