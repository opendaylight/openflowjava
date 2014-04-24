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
public class OF10SetDlDstActionSerializer extends OF10AbstractMacAddressActionSerializer {

    private static final byte SET_DL_DST_CODE = 5;
    private static final byte SET_DL_DST_LENGTH = 16;

    @Override
    protected int getType() {
        return SET_DL_DST_CODE;
    }

    @Override
    protected int getLength() {
        return SET_DL_DST_LENGTH;
    }

}
