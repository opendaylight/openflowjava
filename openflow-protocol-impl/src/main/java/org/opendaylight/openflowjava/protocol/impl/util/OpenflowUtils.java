/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfigV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeaturesV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortStateV10;

/**
 * Used for common structures translation / conversion
 * 
 * @author michal.polkorab
 */
public abstract class OpenflowUtils {

    /**
     * Creates PortState (OF v1.0) from input
     * @param input value read from buffer
     * @return port state
     */
    public static PortStateV10 createPortState(long input){
        final Boolean _linkDown = ((input) & (1<<0)) != 0;
        final Boolean _blocked = ((input) & (1<<1)) != 0;
        final Boolean _live = ((input) & (1<<2)) != 0;
        final Boolean _stpListen = ((input) & (1<<8)) == 0;
        final Boolean _stpLearn = ((input) & (1<<8)) != 0;
        final Boolean _stpForward = ((input) & (1<<9)) != 0; // equals 2 << 8
        final Boolean _stpBlock = (((input) & (1<<9)) != 0) && (((input) & (1<<8)) != 0); // equals 3 << 8
        final Boolean _stpMask = ((input) & (1<<10)) != 0; // equals 4 << 8
        return new PortStateV10(_blocked, _linkDown, _live, _stpBlock, _stpForward, _stpLearn, _stpListen, _stpMask);
    }

    /**
     * Creates PortConfig (OF v1.0) from input
     * @param input value read from buffer
     * @return port state
     */
    public static PortConfigV10 createPortConfig(long input){
        final Boolean _portDown = ((input) & (1<<0)) != 0;
        final Boolean _noStp = ((input) & (1<<1)) != 0;
        final Boolean _noRecv = ((input) & (1<<2)) != 0;
        final Boolean _noRecvStp = ((input) & (1<<3)) != 0;
        final Boolean _noFlood = ((input) & (1<<4)) != 0;
        final Boolean _noFwd  = ((input) & (1<<5)) != 0;
        final Boolean _noPacketIn = ((input) & (1<<6)) != 0;
        return new PortConfigV10(_noFlood, _noFwd, _noPacketIn, _noRecv, _noRecvStp, _noStp, _portDown);
    }

    /**
     * Creates PortFeatures (OF v1.0) from input
     * @param input value read from buffer
     * @return port state
     */
    public static PortFeaturesV10 createPortFeatures(long input){
        final Boolean _10mbHd = ((input) & (1<<0)) != 0;
        final Boolean _10mbFd = ((input) & (1<<1)) != 0;
        final Boolean _100mbHd = ((input) & (1<<2)) != 0;
        final Boolean _100mbFd = ((input) & (1<<3)) != 0;
        final Boolean _1gbHd = ((input) & (1<<4)) != 0;
        final Boolean _1gbFd = ((input) & (1<<5)) != 0;
        final Boolean _10gbFd = ((input) & (1<<6)) != 0;
        final Boolean _copper = ((input) & (1<<7)) != 0;
        final Boolean _fiber = ((input) & (1<<8)) != 0;
        final Boolean _autoneg = ((input) & (1<<9)) != 0;
        final Boolean _pause = ((input) & (1<<10)) != 0;
        final Boolean _pauseAsym = ((input) & (1<<11)) != 0;
        return new PortFeaturesV10(_100mbFd, _100mbHd, _10gbFd, _10mbFd, _10mbHd,
                _1gbFd, _1gbHd, _autoneg, _copper, _fiber, _pause, _pauseAsym);
    }
}