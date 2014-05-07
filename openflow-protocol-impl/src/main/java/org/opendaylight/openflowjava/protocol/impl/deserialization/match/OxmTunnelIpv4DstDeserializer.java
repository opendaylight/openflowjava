
package org.opendaylight.openflowjava.protocol.impl.deserialization.match;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Clazz;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TunnelIpv4Dst;

/**

 *
 */
public class OxmTunnelIpv4DstDeserializer extends AbstractOxmIpv4AddressDeserializer {

    @Override
    protected Class<? extends MatchField> getOxmField() {
        return TunnelIpv4Dst.class;
    }

    @Override
    protected Class<? extends Clazz> getOxmClass() {
        return Nxm1Class.class;
    }
}
