/**
* Generated file

* Generated from: yang module name: openflow-switch-connection-provider-impl  yang module local name: openflow-switch-connection-provider-impl
* Generated by: org.opendaylight.controller.config.yangjmxgenerator.plugin.JMXGenerator
* Generated at: Fri Mar 28 17:50:58 PDT 2014
*
* Do not modify this file unless it is present under src/main directory
*/
package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflow._switch.connection.provider.impl.rev140328;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.ThreadConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.TlsConfiguration;
import org.opendaylight.openflowjava.protocol.impl.connection.SwitchConnectionProviderImpl;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.IpAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.config.rev140630.KeystoreType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

/**
*
*/
public final class SwitchConnectionProviderModule extends org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflow._switch.connection.provider.impl.rev140328.AbstractSwitchConnectionProviderModule
 {
    
    private static Logger LOG = LoggerFactory
            .getLogger(SwitchConnectionProviderModule.class);

    /**
     * @param identifier
     * @param dependencyResolver
     */
    public SwitchConnectionProviderModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    /**
     * @param identifier
     * @param dependencyResolver
     * @param oldModule
     * @param oldInstance
     */
    public SwitchConnectionProviderModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver,
            SwitchConnectionProviderModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    protected void customValidation(){
        // Add custom validation for module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        LOG.info("SwitchConnectionProvider started.");
        SwitchConnectionProviderImpl switchConnectionProviderImpl = new SwitchConnectionProviderImpl();
        try {
            ConnectionConfiguration connConfiguration = createConnectionConfiguration();
            switchConnectionProviderImpl.setConfiguration(connConfiguration);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return switchConnectionProviderImpl;
    }

    /**
     * @return instance configuration object
     * @throws UnknownHostException 
     */
    private ConnectionConfiguration createConnectionConfiguration() throws UnknownHostException {
        final InetAddress address = extractIpAddressBin(getAddress());
        final Integer port = getPort();
        final long switchIdleTimeout = getSwitchIdleTimeout();
        final Tls tlsConfig = getTls();
        final Threads threads = getThreads();
        
        return new ConnectionConfiguration() {
            @Override
            public InetAddress getAddress() {
                return address;
            }
            @Override
            public int getPort() {
                return port;
            }
            @Override
            public Object getTransferProtocol() {
                // TODO Auto-generated method stub
                return null;
            }
            @Override
            public TlsConfiguration getTlsConfiguration() {
                if (tlsConfig == null) {
                    return null;
                }
                return new TlsConfiguration() {
                    @Override
                    public KeystoreType getTlsTruststoreType() {
                        return Objects.firstNonNull(tlsConfig.getTruststoreType(), null);
                    }
                    @Override
                    public String getTlsTruststore() {
                        return Objects.firstNonNull(tlsConfig.getTruststore(), null);
                    }
                    @Override
                    public KeystoreType getTlsKeystoreType() {
                        return Objects.firstNonNull(tlsConfig.getKeystoreType(), null);
                    }
                    @Override
                    public String getTlsKeystore() {
                        return Objects.firstNonNull(tlsConfig.getKeystore(), null);
                    }
                    @Override
                    public org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.config.rev140630.PathType getTlsKeystorePathType() {
                        return Objects.firstNonNull(tlsConfig.getKeystorePathType(), null);
                    }
                    @Override
                    public org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.config.rev140630.PathType getTlsTruststorePathType() {
                        return Objects.firstNonNull(tlsConfig.getTruststorePathType(), null);
                    }
                    @Override
                    public String getKeystorePassword() {
                        return Objects.firstNonNull(tlsConfig.getKeystorePassword(), null);
                    }
                    @Override
                    public String getCertificatePassword() {
                        return Objects.firstNonNull(tlsConfig.getCertificatePassword(), null);
                    }
                    @Override
                    public String getTruststorePassword() {
                        return Objects.firstNonNull(tlsConfig.getTruststorePassword(), null);
                    }
                };
            }
            @Override
            public long getSwitchIdleTimeout() {
                return switchIdleTimeout;
            }
            @Override
            public Object getSslContext() {
                // TODO Auto-generated method stub
                return null;
            }
            @Override
            public ThreadConfiguration getThreadConfiguration() {
                if (threads == null) {
                    return null;
                }
                return new ThreadConfiguration() {
                    
                    @Override
                    public int getWorkerThreadCount() {
                        return threads.getWorkerThreads();
                    }
                    
                    @Override
                    public int getBossThreadCount() {
                        return threads.getBossThreads();
                    }
                };
            }
        };
    }

    /**
     * @param address
     * @return
     * @throws UnknownHostException 
     */
    private static InetAddress extractIpAddressBin(IpAddress address) throws UnknownHostException {
        byte[] addressBin = null;
        if (address != null) {
            if (address.getIpv4Address() != null) {
                addressBin = address2bin(address.getIpv4Address().getValue());
            } else if (address.getIpv6Address() != null) {
                addressBin = address2bin(address.getIpv6Address().getValue());
            }
        }
        
        if (addressBin == null) {
            return null;
        } else {
            return InetAddress.getByAddress(addressBin);
        }
    }

    /**
     * @param value
     * @return
     */
    private static byte[] address2bin(String value) {
        //TODO: translate ipv4 or ipv6 into byte[]
        return null;
    }
}
