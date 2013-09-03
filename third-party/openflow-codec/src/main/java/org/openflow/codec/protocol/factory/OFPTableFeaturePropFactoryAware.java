package org.openflow.codec.protocol.factory;

/**
 * Objects implementing this interface are expected to be instantiated with an
 * instance of an OFPTableFeaturePropFactory
 *
 * @author AnilGujele
 */
public interface OFPTableFeaturePropFactoryAware
{
	/**
	 * Sets the OFPTableFeaturePropFactory
	 *
	 * @param tableFeaturePropFactory
	 */
	public void setTableFeaturePropFactory(
			OFPTableFeaturePropFactory tableFeaturePropFactory);
}
