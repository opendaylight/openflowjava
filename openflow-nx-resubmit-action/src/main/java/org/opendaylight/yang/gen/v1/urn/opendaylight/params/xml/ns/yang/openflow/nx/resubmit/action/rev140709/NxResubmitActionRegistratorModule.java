package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflow.nx.resubmit.action.rev140709;

import org.opendaylight.openflowjava.protocol.nx.NxResubmitActionRegistrator;

public class NxResubmitActionRegistratorModule extends org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflow.nx.resubmit.action.rev140709.AbstractNxResubmitActionRegistratorModule {
    public NxResubmitActionRegistratorModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public NxResubmitActionRegistratorModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflow.nx.resubmit.action.rev140709.NxResubmitActionRegistratorModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        NxResubmitActionRegistrator registrator =  new NxResubmitActionRegistrator();
        registrator.registerNxResubmitSerializer(getOpenflowSwitchConnectionProviderDependency());
        registrator.registerNxResubmitDeserializer(getOpenflowSwitchConnectionProviderDependency());
        return registrator;
    }

}
