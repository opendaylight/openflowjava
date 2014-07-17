/*
 * Copyright (C) 2014 Red Hat, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.deserialization.match;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OxmClassBase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TunnelIpv4Src;

/**
 * @author brent.salisbury
 *
 */
public class NxmTunnelIpv4SrcDeserializer extends AbstractOxmIpv4AddressDeserializer {

    @Override
    protected Class<? extends MatchField> getOxmField() {
        return TunnelIpv4Src.class;
    }

    @Override
    protected Class<? extends OxmClassBase> getOxmClass() {
        return Nxm1Class.class;
    }
}
