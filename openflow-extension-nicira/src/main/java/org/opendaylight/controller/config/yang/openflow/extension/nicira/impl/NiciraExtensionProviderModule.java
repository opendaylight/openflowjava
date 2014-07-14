package org.opendaylight.controller.config.yang.openflow.extension.nicira.impl;

import org.opendaylight.openflowjava.nx.NiciraExtensionsRegistrator;

public class NiciraExtensionProviderModule extends org.opendaylight.controller.config.yang.openflow.extension.nicira.impl.AbstractNiciraExtensionProviderModule {
    public NiciraExtensionProviderModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public NiciraExtensionProviderModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.controller.config.yang.openflow.extension.nicira.impl.NiciraExtensionProviderModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        NiciraExtensionsRegistrator niciraExtensionsRegistrator = new NiciraExtensionsRegistrator(getOpenflowSwitchConnectionProviderDependency());
        niciraExtensionsRegistrator.registerNiciraExtensions();
        return niciraExtensionsRegistrator;
    }

}
