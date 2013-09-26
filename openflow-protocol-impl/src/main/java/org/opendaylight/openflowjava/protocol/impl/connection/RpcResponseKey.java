/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.connection;


/**
 * @author mirehak
 *
 */
public class RpcResponseKey {
    
    private final long xid;
    private final String outputClazz;
    /**
     * @param xid
     * @param outputClazz
     */
    public RpcResponseKey(long xid, String outputClazz) {
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
    public String getOutputClazz() {
        return outputClazz;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((outputClazz == null) ? 0 : outputClazz.hashCode());
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
        } else if (!outputClazz.equals(other.outputClazz))
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
