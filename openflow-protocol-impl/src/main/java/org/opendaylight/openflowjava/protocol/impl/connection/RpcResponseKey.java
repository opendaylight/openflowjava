/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.connection;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;


/**
 * @author mirehak
 *
 */
public class RpcResponseKey {
    
    private final long xid;
    private final Class<? extends OfHeader> outputClazz;
    /**
     * @param xid
     * @param outputClazz
     */
    public RpcResponseKey(long xid, Class<? extends OfHeader> outputClazz) {
        super();
        this.xid = xid;
        this.outputClazz = outputClazz;
    }
    
    /**
     * @return the xid
     */
    public long getXid() {
        return xid;
    }

    /**
     * @return the outputClazz
     */
    public Class<? extends OfHeader> getOutputClazz() {
        return outputClazz;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (xid ^ (xid >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RpcResponseKey other = (RpcResponseKey) obj;
        if (outputClazz == null) {
            if (other.outputClazz != null)
                return false;
            } else if (!other.outputClazz.isAssignableFrom(outputClazz))
                return false;
        if (xid != other.xid)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RpcResultKey [xid=" + xid + ", outputClazz=" + outputClazz
                + "]";
    }

}
