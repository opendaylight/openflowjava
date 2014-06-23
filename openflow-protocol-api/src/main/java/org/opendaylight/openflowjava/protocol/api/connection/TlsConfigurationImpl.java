/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.connection;

/**
 * @author michal.polkorab
 * 
 */
public class TlsConfigurationImpl implements TlsConfiguration {

	private String trustStoreType;
	private String trustStore;
	private String keyStoreType;
	private String keyStore;
	Boolean isTlsSupported;

	public TlsConfigurationImpl(String trustStoreType, String trustStore,
			Boolean isTlsSupported, String keyStoreType, String keyStore) {

		this.trustStoreType = trustStoreType;
		this.trustStore = trustStore ;
		this.isTlsSupported = isTlsSupported ;
		this.keyStoreType = keyStoreType;
		this.keyStore = keyStore ;
	}

	@Override
	public String getTlsTruststoreType() {
		return trustStoreType;
	}

	@Override
	public String getTlsTruststore() {
		return trustStore;
	}

	@Override
	public boolean isTlsSupported() {
		return isTlsSupported;
	}

	@Override
	public String getTlsKeystoreType() {
		return keyStoreType;
	}

	@Override
	public String getTlsKeystore() {
		return keyStore;
	}
}
