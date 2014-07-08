package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflow.exemplary.rev140509;

import org.opendaylight.openflowjava.protocol.ext.ExemplaryRegistrator;

public class ExemplaryRegistratorModule extends org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflow.exemplary.rev140509.AbstractExemplaryRegistratorModule {
    public ExemplaryRegistratorModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public ExemplaryRegistratorModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflow.exemplary.rev140509.ExemplaryRegistratorModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        ExemplaryRegistrator registrator =  new ExemplaryRegistrator();
        registrator.registerDefaultExperimenterSerializers(getOpenflowSwitchConnectionProviderDependency());
        registrator.registerDefaultExperimenterDeserializers(getOpenflowSwitchConnectionProviderDependency());
        return registrator;
    }

}
