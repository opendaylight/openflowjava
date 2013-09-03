package org.openflow.codec.protocol.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openflow.codec.io.IDataBuffer;
import org.openflow.codec.protocol.action.OFPBucket;
import org.openflow.codec.protocol.factory.OFPActionFactory;
import org.openflow.codec.protocol.factory.OFPActionFactoryAware;

/**
 * Represents an ofp_group_desc_stats structure
 *
 * @author Yugandhar Sarraju
 */
public class OFPGroupDescription implements OFPStatistics, OFPActionFactoryAware, Serializable {

    private static final int MINIMUM_LENGTH = 8;

    protected OFPActionFactory actionFactory;
    private short length = MINIMUM_LENGTH;
    private byte groupType;
    private int group_id;
    private List<OFPBucket> buckets;

    public byte getGroupType() {
        return groupType;
    }

    public OFPGroupDescription setGroupType(byte groupType) {
        this.groupType = groupType;
        return this;
    }

    public int getGroup_id() {
        return group_id;
    }

    public OFPGroupDescription setGroup_id(int group_id) {
        this.group_id = group_id;
        return this;
    }

    public List<OFPBucket> getBuckets() {
        return buckets;
    }

    @Override
    public int getLength() {
        return length;
    }

    public OFPGroupDescription setLength(int length) {
        this.length = (short) length;
        return this;
    }

    /**
     * @param buckets
     *            the buckets to set
     */
    public OFPGroupDescription setBuckets(List<OFPBucket> buckets) {
        this.buckets = buckets;
        updateLength();

        return this;
    }

    private void updateLength() {
        int totalLength = MINIMUM_LENGTH;
        if (buckets != null) {
            for (OFPBucket bucket : buckets) {
                totalLength += bucket.getLengthU();
            }
        }
        this.setLength(totalLength);
    }

    @Override
    public void readFrom(IDataBuffer data) {
        this.length = data.getShort();
        this.groupType = data.get();
        data.get();
        this.group_id = data.getInt();
        if (this.buckets == null) {
            this.buckets = new ArrayList<OFPBucket>();
        } else {
            this.buckets.clear();
        }

        OFPBucket bucket;
        while (data.remaining() > 0) {
            bucket = new OFPBucket();
            bucket.setActionFactory(actionFactory);
            bucket.readFrom(data);
            this.buckets.add(bucket);
        }

    }

    @Override
    public void writeTo(IDataBuffer data) {
        data.putShort(length);
        data.put(groupType);
        data.put((byte) 0);
        data.putInt(group_id);
        if (buckets != null) {
            for (OFPBucket bucket : buckets) {
                bucket.writeTo(data);
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 461;
        int result = 1;
        result = prime * result + length;
        result = prime * result + ((buckets == null) ? 0 : buckets.hashCode());
        result = prime * result + groupType;
        result = prime * result + group_id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OFPGroupDescription)) {
            return false;
        }
        OFPGroupDescription other = (OFPGroupDescription) obj;
        if (length != other.length) {
            return false;
        }
        if (buckets == null) {
            if (other.buckets != null) {
                return false;
            }
        } else if (!buckets.equals(other.buckets)) {
            return false;
        }
        if (groupType != other.groupType) {
            return false;
        }
        if (group_id != other.group_id) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public OFPGroupDescription clone() {
        try {
            OFPGroupDescription groupDes = (OFPGroupDescription) super.clone();
            List<OFPBucket> neoBuckets = new LinkedList<OFPBucket>();
            for (OFPBucket bucket : this.buckets)
                neoBuckets.add((OFPBucket) bucket.clone());
            groupDes.setBuckets(neoBuckets);
            return groupDes;
        } catch (CloneNotSupportedException e) {
            // Won't happen
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OFPGroupDes [ buckets=" + buckets + ", length=" + length + ", groupType=" + groupType + ", group_id="
                + group_id + "]";
    }

    @Override
    public void setActionFactory(OFPActionFactory actionFactory) {
        this.actionFactory = actionFactory;

    }

}