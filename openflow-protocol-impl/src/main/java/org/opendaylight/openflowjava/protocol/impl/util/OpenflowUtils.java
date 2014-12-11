/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortConfigV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortConfigV13;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortFeaturesV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortFeaturesV13;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortStateV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortStateV13;

/**
 * Used for common structures translation / conversion
 *
 * @author michal.polkorab
 */
public abstract class OpenflowUtils {

    private OpenflowUtils() {
        //not called
    }
    
    /**
     * Creates PortState from input
     * @param input value read from buffer
     * @param version openflow version
     * @return port state
     */
    public static PortState createPortState(long input, byte version){
        final Boolean psLinkDown = ((input) & (1<<0)) != 0;
        final Boolean psBlocked = ((input) & (1<<1)) != 0;
        final Boolean psLive = ((input) & (1<<2)) != 0;
        if (version == EncodeConstants.OF10_VERSION_ID) {
            final Boolean psStpListen = ((input) & (1<<8)) == 0;
            final Boolean psStpLearn = ((input) & (1<<8)) != 0;
            final Boolean psStpForward = ((input) & (1<<9)) != 0; // equals 2 << 8
            final Boolean psStpBlock = (((input) & (1<<9)) != 0) && (((input) & (1<<8)) != 0); // equals 3 << 8
            final Boolean psStpMask = ((input) & (1<<10)) != 0; // equals 4 << 8
            return new PortState(new PortStateV10(psBlocked, psLinkDown, psLive, psStpBlock,
                    psStpForward, psStpLearn, psStpListen, psStpMask));
        }
        return new PortState(new PortStateV13(psBlocked, psLinkDown, psLive));
    }

    /**
     * Creates PortConfig from input
     * @param input value read from buffer
     * @param version openflow version
     * @return port state
     */
    public static PortConfig createPortConfig(long input, byte version){
        final Boolean pcPortDown = ((input) & (1<<0)) != 0;
        final Boolean pcNoRecv = ((input) & (1<<2)) != 0;
        final Boolean pcNoFwd  = ((input) & (1<<5)) != 0;
        final Boolean pcNoPacketIn = ((input) & (1<<6)) != 0;
        if (version == EncodeConstants.OF10_VERSION_ID) {
            final Boolean pcNoStp = ((input) & (1<<1)) != 0;
            final Boolean pcNoRecvStp = ((input) & (1<<3)) != 0;
            final Boolean pcNoFlood = ((input) & (1<<4)) != 0;
            return new PortConfig(new PortConfigV10(pcNoFlood, pcNoFwd, pcNoPacketIn, pcNoRecv,
                    pcNoRecvStp, pcNoStp, pcPortDown));
        }
        return new PortConfig(new PortConfigV13(pcNoFwd, pcNoPacketIn, pcNoRecv, pcPortDown));
    }

    /**
     * Creates PortFeatures from input
     * @param input value read from buffer
     * @param version openflow version
     * @return port state
     */
    public static PortFeatures createPortFeatures(long input, byte version){
        final Boolean pf10mbHd = ((input) & (1<<0)) != 0;
        final Boolean pf10mbFd = ((input) & (1<<1)) != 0;
        final Boolean pf100mbHd = ((input) & (1<<2)) != 0;
        final Boolean pf100mbFd = ((input) & (1<<3)) != 0;
        final Boolean pf1gbHd = ((input) & (1<<4)) != 0;
        final Boolean pf1gbFd = ((input) & (1<<5)) != 0;
        final Boolean pf10gbFd = ((input) & (1<<6)) != 0;
        Boolean pfCopper;
        Boolean pfFiber;
        Boolean pfAutoneg;
        Boolean pfPause;
        Boolean pfPauseAsym;
        if (version == EncodeConstants.OF10_VERSION_ID) {
            pfCopper = ((input) & (1<<7)) != 0;
            pfFiber = ((input) & (1<<8)) != 0;
            pfAutoneg = ((input) & (1<<9)) != 0;
            pfPause = ((input) & (1<<10)) != 0;
            pfPauseAsym = ((input) & (1<<11)) != 0;
            return new PortFeatures(new PortFeaturesV10(pf100mbFd, pf100mbHd, pf10gbFd, pf10mbFd, pf10mbHd,
                    pf1gbFd, pf1gbHd, pfAutoneg, pfCopper, pfFiber, pfPause, pfPauseAsym));
        }
        final Boolean pf40gbFd = ((input) & (1<<7)) != 0;
        final Boolean pf100gbFd = ((input) & (1<<8)) != 0;
        final Boolean pf1tbFd = ((input) & (1<<9)) != 0;
        final Boolean pfOther = ((input) & (1<<10)) != 0;
        pfCopper = ((input) & (1<<11)) != 0;
        pfFiber = ((input) & (1<<12)) != 0;
        pfAutoneg = ((input) & (1<<13)) != 0;
        pfPause = ((input) & (1<<14)) != 0;
        pfPauseAsym = ((input) & (1<<15)) != 0;
        return new PortFeatures(new PortFeaturesV13(pf100gbFd, pf100mbFd, pf100mbHd, pf10gbFd, pf10mbFd, pf10mbHd,
                pf1gbFd, pf1gbHd, pf1tbFd, pf40gbFd, pfAutoneg, pfCopper, pfFiber, pfOther, pfPause, pfPauseAsym));
    }
}