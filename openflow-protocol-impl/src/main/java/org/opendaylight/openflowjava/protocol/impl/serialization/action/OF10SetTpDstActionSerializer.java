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
public class OF10SetTpDstActionSerializer extends OF10AbstractPortActionSerializer {

    private static final byte SET_TP_DST_CODE = 10;
    private static final byte SET_TP_DST_LENGTH = 8;

    @Override
    protected int getType() {
        return SET_TP_DST_CODE;
    }

    @Override
    protected int getLength() {
        return SET_TP_DST_LENGTH;
    }

}
