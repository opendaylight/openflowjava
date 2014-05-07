
package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.OxmMatchConstants;

/**

 *
 */
public class OxmTunnelIpv4SrcSerializer extends AbstractOxmIpv4AddressSerializer {

    @Override
    protected int getOxmClassCode() {
        return OxmMatchConstants.NXM_1_CLASS;
    }

    @Override
    protected int getOxmFieldCode() {
        return OxmMatchConstants.NXM_NX_TUN_IPV4_SRC;
    }

    @Override
    protected int getValueLength() {
        return EncodeConstants.SIZE_OF_INT_IN_BYTES;
    }

}
