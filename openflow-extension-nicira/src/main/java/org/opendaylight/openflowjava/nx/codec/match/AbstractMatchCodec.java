package org.opendaylight.openflowjava.nx.codec.match;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Clazz;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm0Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

public abstract class AbstractMatchCodec implements OFSerializer<MatchEntries>, OFDeserializer<MatchEntries> {
    
    /** Backward compatibility with NXM */
    public static final int NXM_0_CODE = 0x0000;
    /** Backward compatibility with NXM */
    public static final int NXM_1_CODE = 0x0001;
    public static final Class<Nxm0Class> NXM_0_CLASS = Nxm0Class.class;
    public static final Class<Nxm1Class> NXM_1_CLASS = Nxm1Class.class;

    protected MatchEntriesBuilder deserializeHeader(ByteBuf message) {
        MatchEntriesBuilder builder = new MatchEntriesBuilder();
        builder.setOxmClass(getOxmClass());
        // skip oxm_class - provided
        message.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setOxmMatchField(getNxmField());
        boolean hasMask = (message.readUnsignedByte() & 1) != 0;
        builder.setHasMask(hasMask);
        // skip match entry length - not needed
        message.skipBytes(EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        return builder;
    }

    protected void serializeHeader(MatchEntries input, ByteBuf outBuffer) {
        outBuffer.writeInt(serializeHeaderToInt(input.isHasMask()));
    }
    
    public int serializeHeaderToInt(boolean hasMask) {
        return (getOxmClassCode() << 16) | (getNxmFieldCode() << 9) | ((hasMask ? 1 : 0) << 8) | (getValueLength());
    }
    
    /**
     * @return numeric representation of nxm_field
     */
    public abstract int getNxmFieldCode();

    /**
     * @return numeric representation of oxm_class
     */
    public abstract int getOxmClassCode();

    /**
     * @return match entry value length
     */
    public abstract int getValueLength();
    
    /**
     * @return nxm_field class
     */
    public abstract Class<? extends MatchField> getNxmField();

    /**
     * @return oxm_class class
     */
    public abstract Class<? extends Clazz> getOxmClass();

}
