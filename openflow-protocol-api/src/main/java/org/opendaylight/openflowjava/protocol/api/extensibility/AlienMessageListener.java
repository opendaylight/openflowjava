package org.opendaylight.openflowjava.protocol.api.extensibility;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;

public interface AlienMessageListener {

    /**
     * Handler for alien but successfully deserialized messages for device
     * @param message alien message
     */
    void onAlienMessage(OfHeader message);
}
