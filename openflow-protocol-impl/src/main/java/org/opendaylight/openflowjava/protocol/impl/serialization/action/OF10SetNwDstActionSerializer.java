/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.action;

/**
 * @author michal.polkorab
 *
 */
public class OF10SetNwDstActionSerializer extends OF10AbstractIpAddressActionSerializer {

    private static final byte SET_NW_DST_CODE = 7;
    private static final byte SET_NW_DST_LENGTH = 8;

    @Override
    protected int getType() {
        return SET_NW_DST_CODE;
    }

    @Override
    protected int getLength() {
        return SET_NW_DST_LENGTH;
    }

}
