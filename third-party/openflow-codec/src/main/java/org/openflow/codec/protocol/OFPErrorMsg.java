package org.openflow.codec.protocol;

import java.util.Arrays;
import java.util.List;

import org.openflow.codec.io.IDataBuffer;
import org.openflow.codec.protocol.factory.OFPMessageFactory;
import org.openflow.codec.protocol.factory.OFPMessageFactoryAware;
import org.openflow.codec.util.U16;

/**
 * Represents an ofp_error_msg and also ofp_error_experimenter_msg
 * 
 * @author David Erickson (daviderickson@cs.stanford.edu)
 * @author Rob Sherwood (rob.sherwood@stanford.edu)
 */
public class OFPErrorMsg extends OFPMessage implements OFPMessageFactoryAware
{
	private static int MINIMUM_LENGTH = 12;
	private static int EXP_MINIMUM_LENGTH = 16;

	// correspond to enum ofp_hello_failed_code
	public enum OFPHelloFailedCode
	{
		OFPHFC_INCOMPATIBLE, OFPHFC_EPERM
	}

	// correspond to enum ofp_bad_request_code
	public enum OFPBadRequestCode
	{
		OFPBRC_BAD_VERSION, OFPBRC_BAD_TYPE, OFPBRC_BAD_MULTIPART, OFPBRC_BAD_EXPERIMENTER, OFPBRC_BAD_EXP_TYPE, OFPBRC_EPERM, OFPBRC_BAD_LEN, OFPBRC_BUFFER_EMPTY, OFPBRC_BUFFER_UNKNOWN, OFPBRC_BAD_TABLE_ID, OFPBRC_IS_SLAVE, OFPBRC_BAD_PORT, OFPBRC_BAD_PACKET, OFPBRC_MULTIPART_BUFFER_OVERFLOW
	}

	// correspond to enum ofp_bad_action_code
	public enum OFPBadActionCode
	{
		OFPBAC_BAD_TYPE, OFPBAC_BAD_LEN, OFPBAC_BAD_EXPERIMENTER, OFPBAC_BAD_EXP_TYPE, OFPBAC_BAD_OUT_PORT, OFPBAC_BAD_ARGUMENT, OFPBAC_EPERM, OFPBAC_TOO_MANY, OFPBAC_BAD_QUEUE, OFPBAC_BAD_OUT_GROUP, OFPBAC_MATCH_INCONSISTENT, OFPBAC_UNSUPPORTED_ORDER, OFPBAC_BAD_TAG, OFPBAC_BAD_SET_TYPE, OFPBAC_BAD_SET_LEN, OFPBAC_BAD_SET_ARGUMENT
	}

	// correspond to enum ofp_bad_instruction_code
	public enum OFPBadInstructionCode
	{
		OFPBIC_UNKNOWN_INST, OFPBIC_UNSUP_INST, OFPBIC_BAD_TABLE_ID, OFPBIC_UNSUP_METADATA, OFPBIC_UNSUP_METADATA_MASK, OFPBIC_BAD_EXPERIMENTER, OFPBIC_BAD_EXP_TYPE, OFPBIC_BAD_LEN, OFPBIC_EPERM
	}

	// correspond to enum ofp_bad_match_code
	public enum OFPBadMatchCode
	{
		OFPBMC_BAD_TYPE, OFPBMC_BAD_LEN, OFPBMC_BAD_TAG, OFPBMC_BAD_DL_ADDR_MASK, OFPBMC_BAD_NW_ADDR_MASK, OFPBMC_BAD_WILDCARDS, OFPBMC_BAD_FIELD, OFPBMC_BAD_VALUE, OFPBMC_BAD_MASK, OFPBMC_BAD_PREREQ, OFPBMC_DUP_FIELD, OFPBMC_EPERM

	}

	// correspond to enum ofp_flow_mod_failed_code
	public enum OFPFlowModFailedCode
	{
		OFPFMFC_UNKNOWN, OFPFMFC_TABLE_FULL, OFPFMFC_BAD_TABLE_ID, OFPFMFC_OVERLAP, OFPFMFC_EPERM, OFPFMFC_BAD_TIMEOUT, OFPFMFC_BAD_COMMAND, OFPFMFC_BAD_FLAGS
	}

	// correspond to enum ofp_group_mod_failed_code
	public enum OFPGroupModFailedCode
	{
		OFPGMFC_GROUP_EXISTS, OFPGMFC_INVALID_GROUP, OFPGMFC_WEIGHT_UNSUPPORTED, OFPGMFC_OUT_OF_GROUPS, OFPGMFC_OUT_OF_BUCKETS, OFPGMFC_CHAINING_UNSUPPORTED, OFPGMFC_WATCH_UNSUPPORTED, OFPGMFC_LOOP, OFPGMFC_UNKNOWN_GROUP, OFPGMFC_CHAINED_GROUP, OFPGMFC_BAD_TYPE, OFPGMFC_BAD_COMMAND, OFPGMFC_BAD_BUCKET, OFPGMFC_BAD_WATCH, OFPGMFC_EPERM
	}

	// correspond to enum ofp_port_mod_failed_code
	public enum OFPPortModFailedCode
	{
		OFPPMFC_BAD_PORT, OFPPMFC_BAD_HW_ADDR, OFPPMFC_BAD_CONFIG, OFPPMFC_BAD_ADVERTISE, OFPPMFC_EPERM
	}

	// correspond to enum ofp_table_mod_failed_code
	public enum OFPTableModFailedCode
	{
		OFPTMFC_BAD_TABLE, OFPTMFC_BAD_CONFIG, OFPTMFC_EPERM
	}

	// correspond to enum ofp_queue_op_failed_code
	public enum OFPQueueOpFailedCode
	{
		OFPQOFC_BAD_PORT, OFPQOFC_BAD_QUEUE, OFPQOFC_EPERM
	}

	// correspond to enum ofp_switch_config_failed_code
	public enum OFPSwitchConfigFailedCode
	{
		OFPSCFC_BAD_FLAGS, OFPSCFC_BAD_LEN, OFPSCFC_EPERM
	}

	// correspond to enum ofp_role_request_failed_code
	public enum OFPRoleRequestFailedCode
	{
		OFPRRFC_STALE, OFPRRFC_UNSUP, OFPRRFC_BAD_ROLE
	}

	// correspond to enum ofp_meter_mod_failed_code
	public enum OFPMeterModFailedCode
	{
		OFPMMFC_UNKNOWN, OFPMMFC_METER_EXISTS, OFPMMFC_INVALID_METER, OFPMMFC_UNKNOWN_METER, OFPMMFC_BAD_COMMAND, OFPMMFC_BAD_FLAGS, OFPMMFC_BAD_RATE, OFPMMFC_BAD_BURST, OFPMMFC_BAD_BAND, OFPMMFC_BAD_BAND_VALUE, OFPMMFC_OUT_OF_METERS, OFPMMFC_OUT_OF_BANDS

	}

	// correspond to enum ofp_table_features_failed_code
	public enum OFPTableFeaturesFailedCode
	{
		OFPTFFC_BAD_TABLE, OFPTFFC_BAD_METADATA, OFPTFFC_BAD_TYPE, OFPTFFC_BAD_LEN, OFPTFFC_BAD_ARGUMENT, OFPTFFC_EPERM
	}

	private OFPErrorType errorType;
	private short errorCode;
	private byte[] errorData;
	private short expType;
	private int experimenter;

	// non-message field
	private OFPMessageFactory factory;
	private boolean errorIsAscii;

	public OFPErrorMsg()
	{
		super();
		this.type = OFPType.ERROR;
		this.length = U16.t(MINIMUM_LENGTH);
	}

	/**
	 * @return the errorType
	 */
	public OFPErrorType getErrorType()
	{
		return errorType;
	}

	/**
	 * @param errorType
	 *            the errorType to set
	 */
	public void setErrorType(OFPErrorType type)
	{
		this.errorType = type;
	}

	/**
	 * @return the errorCode
	 */
	public short getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(short errorCode)
	{
		this.errorCode = errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(OFPHelloFailedCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public void setErrorCode(OFPBadRequestCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public void setErrorCode(OFPBadActionCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public void setErrorCode(OFPFlowModFailedCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public void setErrorCode(OFPPortModFailedCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public void setErrorCode(OFPQueueOpFailedCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public void setErrorCode(OFPBadInstructionCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public void setErrorCode(OFPBadMatchCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public void setErrorCode(OFPGroupModFailedCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public void setErrorCode(OFPTableModFailedCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public void setErrorCode(OFPSwitchConfigFailedCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public void setErrorCode(OFPRoleRequestFailedCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public void setErrorCode(OFPMeterModFailedCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public void setErrorCode(OFPTableFeaturesFailedCode code)
	{
		this.errorCode = (short) code.ordinal();
	}

	public OFPMessage getOffendingMsg(IDataBuffer data)
	{
		// should only have one message embedded; if more than one, just
		// grab first
		if (this.errorData == null)
			return null;
		IDataBuffer errorMsg = data.wrap(this.errorData);
		if (factory == null)
			throw new RuntimeException("MessageFactory not set");
		List<OFPMessage> messages = this.factory.parseMessages(errorMsg,
				errorData.length);
		// OVS apparently sends partial messages in errors
		// need to be careful of that AND can't use data.limit() as
		// a packet boundary because there could be more data queued
		if (messages.size() > 0)
			return messages.get(0);
		else
			return null;
	}

	/**
	 * Write this offending message into the payload of the Error message
	 * 
	 * @param offendingMsg
	 */

	public void setOffendingMsg(OFPMessage offendingMsg, IDataBuffer buffer)
	{
		int minlength = MINIMUM_LENGTH;
		if (OFPErrorType.OFPET_EXPERIMENTER.getValue() == this.errorType
				.getValue())
		{
			minlength = EXP_MINIMUM_LENGTH;
		}

		if (offendingMsg == null)
		{
			super.setLengthU(minlength);
		} else
		{
			this.errorData = new byte[offendingMsg.getLengthU()];
			IDataBuffer data = buffer.wrap(errorData);
			offendingMsg.writeTo(data);
			super.setLengthU(minlength + offendingMsg.getLengthU());
		}
	}

	public OFPMessageFactory getFactory()
	{
		return factory;
	}

	@Override
	public void setMessageFactory(OFPMessageFactory factory)
	{
		this.factory = factory;
	}

	/**
	 * @return the error
	 */
	public byte[] getErrorData()
	{
		return errorData;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setErrorData(byte[] error)
	{
		this.errorData = error;
	}

	/**
	 * @return the errorIsAscii
	 */
	public boolean isErrorIsAscii()
	{
		return errorIsAscii;
	}

	/**
	 * @param errorIsAscii
	 *            the errorIsAscii to set
	 */
	public void setErrorIsAscii(boolean errorIsAscii)
	{
		this.errorIsAscii = errorIsAscii;
	}

	@Override
	public void readFrom(IDataBuffer data)
	{
		super.readFrom(data);
		this.errorType = OFPErrorType.valueOf(data.getShort());
		if (this.errorType.getValue() == OFPErrorType.OFPET_EXPERIMENTER
				.getValue())
		{
			this.expType = data.getShort();
			this.experimenter = data.getInt();
			int dataLength = this.getLengthU() - EXP_MINIMUM_LENGTH;
			if (dataLength > 0)
			{
				this.errorData = new byte[dataLength];
				data.get(this.errorData);
			}
		} else
		{

			this.errorCode = data.getShort();
			int dataLength = this.getLengthU() - MINIMUM_LENGTH;
			if (dataLength > 0)
			{
				this.errorData = new byte[dataLength];
				data.get(this.errorData);
				if (this.errorType.getValue() == OFPErrorType.OFPET_HELLO_FAILED
						.getValue())
					this.errorIsAscii = true;
			}
		}
	}

	@Override
	public void writeTo(IDataBuffer data)
	{
		if (this.errorType.getValue() == OFPErrorType.OFPET_EXPERIMENTER
				.getValue())
		{
			this.length = U16.t(EXP_MINIMUM_LENGTH);
		}
		super.writeTo(data);
		data.putShort(errorType.getValue());
		if (this.errorType.getValue() == OFPErrorType.OFPET_EXPERIMENTER
				.getValue())
		{
			data.putShort(expType);
			data.putInt(experimenter);
		} else
		{
			data.putShort(errorCode);
		}
		if (errorData != null)
			data.put(errorData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(errorData);
		result = prime * result + errorCode;
		result = prime * result + (errorIsAscii ? 1231 : 1237);
		result = prime * result + errorType.getValue();
		result = prime * result + expType;
		result = prime * result + experimenter;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		OFPErrorMsg other = (OFPErrorMsg) obj;
		if (!Arrays.equals(errorData, other.errorData))
			return false;
		if (errorCode != other.errorCode)
			return false;
		if (errorIsAscii != other.errorIsAscii)
			return false;
		if (errorType != other.errorType)
			return false;
		if (expType != other.expType)
			return false;
		if (experimenter != other.experimenter)
			return false;
		return true;
	}

	/**
	 * get experimenter type
	 * 
	 * @return
	 */
	public short getExpType()
	{
		return expType;
	}

	/**
	 * set experimenter type
	 * 
	 * @param expType
	 */
	public void setExpType(short expType)
	{
		this.expType = expType;
	}

	/**
	 * get experimenter Id
	 * 
	 * @return
	 */
	public int getExperimenter()
	{
		return experimenter;
	}

	/**
	 * set experimenter id
	 * 
	 * @param experimenter
	 */
	public void setExperimenter(int experimenter)
	{
		this.experimenter = experimenter;
	}

}
