package org.openflow.codec.protocol.factory;

import java.util.List;

import org.openflow.codec.io.IDataBuffer;
import org.openflow.codec.protocol.statistics.table.OFPTableFeaturePropHeader;
import org.openflow.codec.protocol.statistics.table.OFPTableFeaturePropType;

/**
 * The interface to factories used for retrieving OFPTableFeaturePropHeader
 * instances. All methods are expected to be thread-safe.
 *
 * @author AnilGujele
 */
public interface OFPTableFeaturePropFactory
{
	/**
	 * Retrieves an OFPTableFeaturePropHeader instance corresponding to the
	 * specified OFPTableFeaturePropType
	 *
	 * @param t
	 *            the type of the OFPTableFeaturePropHeader to be retrieved
	 * @return an OFPTableFeaturePropHeader instance
	 */
	public OFPTableFeaturePropHeader getTableFeatureProp(
			OFPTableFeaturePropType t);

	/**
	 * Attempts to parse and return all OFPTableFeaturePropHeader contained in
	 * the given DataBuffer, beginning at the DataBuffer's position, and ending
	 * at position+length.
	 *
	 * @param data
	 *            the DataBuffer to parse for OpenFlow TableFeature Property
	 * @param length
	 *            the number of Bytes to examine for OpenFlow TableFeature
	 *            Property
	 * @return a list of OFPTableFeaturePropHeader instances
	 */
	public List<OFPTableFeaturePropHeader> parseTableFeatureProp(
			IDataBuffer data, int length);

	/**
	 * Attempts to parse and return number of specified
	 * OFPTableFeaturePropHeader contained in the given DataBuffer, beginning at
	 * the DataBuffer's position, and ending at position+length.
	 *
	 * @param data
	 *            the DataBuffer to parse for OpenFlow TableFeature Property
	 * @param length
	 *            the number of Bytes to examine for OpenFlow TableFeature
	 *            Property
	 * @param limit
	 *            the maximum number of messages to return, 0 means no limit
	 * @return a list of OFPTableFeaturePropHeader instances
	 */
	public List<OFPTableFeaturePropHeader> parseTableFeatureProp(
			IDataBuffer data, int length, int limit);
}
