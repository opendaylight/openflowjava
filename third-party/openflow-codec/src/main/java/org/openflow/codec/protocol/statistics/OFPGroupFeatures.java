package org.openflow.codec.protocol.statistics;

import java.io.Serializable;
import java.util.Arrays;

import org.openflow.codec.io.IDataBuffer;

/**
 * Represents an ofp_group_features structure
 *
 * @author Yugandhar Sarraju
 */
public class OFPGroupFeatures implements OFPStatistics, Serializable {

    private static final int MINIMUM_LENGTH = 40;

    /**
     * represents ofp_group_capabilities
     *
     */
    public enum OFGroupCapabilities {
        OFPGFC_SELECT_WEIGHT(1 << 0), /* Support weight for select groups */
        OFPGFC_SELECT_LIVENESS(1 << 1), /* Support liveness for select groups */
        OFPGFC_CHAINING(1 << 2), /* Support chaining groups */
        OFPGFC_CHAINING_CHECKS(1 << 3); /* Check chaining for loops and delete */

        protected int value;

        private OFGroupCapabilities(int value) {
            this.value = value;
        }

        /**
         * @return the value
         */
        public int getValue() {
            return value;
        }
    }

    private short length = MINIMUM_LENGTH;
    private int types;
    private int capabilities;
    private int[] max_groups;
    private int[] actions;

    public int getTypes() {
        return types;
    }

    public void setTypes(int types) {
        this.types = types;
    }

    public int getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(int capabilities) {
        this.capabilities = capabilities;
    }

    public int[] getMax_groups() {
        return max_groups;
    }

    public void setMax_groups(int[] max_groups) {
        if (max_groups.length != 4)
            throw new RuntimeException("Max Groups must have length " + 4);
        this.max_groups = max_groups;
    }

    public int[] getActions() {
        return actions;
    }

    public void setActions(int[] actions) {
        if (actions.length != 4)
            throw new RuntimeException("Actions must have length " + 4);
        this.actions = actions;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void readFrom(IDataBuffer data) {

        this.types = data.getInt();
        this.capabilities = data.getInt();
        if (this.max_groups == null)
            this.max_groups = new int[4];
        for (int i = 0; i < 4; i++) {
            this.max_groups[i] = data.getInt();
        }
        if (this.actions == null)
            this.actions = new int[4];
        for (int i = 0; i < 4; i++) {
            this.actions[i] = data.getInt();
        }
    }

    @Override
    public void writeTo(IDataBuffer data) {

        data.putInt(this.types);
        data.putInt(this.capabilities);
        for (int i = 0; i < 4; i++) {
            data.putInt(this.max_groups[i]);
        }
        for (int i = 0; i < 4; i++) {
            data.putInt(this.actions[i]);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 457;
        int result = 1;
        result = prime * result + types;
        result = prime * result + capabilities;
        result = prime * Arrays.hashCode(getMax_groups());
        result = prime * Arrays.hashCode(actions);
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
        if (!(obj instanceof OFPGroupFeatures)) {
            return false;
        }
        OFPGroupFeatures other = (OFPGroupFeatures) obj;
        if (types != other.types) {
            return false;
        }
        if (capabilities != other.capabilities) {
            return false;
        }
        if (!Arrays.equals(max_groups, other.max_groups)) {
            return false;
        }
        if (!Arrays.equals(actions, other.actions)) {
            return false;
        }
        return true;
    }

}