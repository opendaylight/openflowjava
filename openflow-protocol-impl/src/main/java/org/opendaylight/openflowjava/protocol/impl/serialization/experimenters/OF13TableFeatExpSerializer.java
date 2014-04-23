package org.opendaylight.openflowjava.protocol.impl.serialization.experimenters;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableFeaturesPropType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeatureProperties;

public class OF13TableFeatExpSerializer implements OFSerializer<TableFeatureProperties> {

    private static final int EXPERIMENTER_CODE = 65534; // 0xFFFE
    private static final int EXPERIMENTER_MISS_CODE = 65535; // 0xFFFF
    
	@Override
	public void serialize(TableFeatureProperties property, ByteBuf outBuffer) {
		int startIndex = outBuffer.writerIndex();
		if (property.getType().equals(TableFeaturesPropType.OFPTFPTEXPERIMENTERMISS)) {
			outBuffer.writeShort(EXPERIMENTER_CODE);
		} else {
			outBuffer.writeShort(EXPERIMENTER_MISS_CODE);
		}
		int lengthIndex = outBuffer.writerIndex();
		outBuffer.writeShort(EncodeConstants.EMPTY_LENGTH);
        ExperimenterRelatedTableFeatureProperty exp = property.
                getAugmentation(ExperimenterRelatedTableFeatureProperty.class);
        outBuffer.writeInt(exp.getExperimenter().intValue());
        outBuffer.writeInt(exp.getExpType().intValue());
        byte[] data = exp.getData();
        if (data != null) {
            outBuffer.writeBytes(data);
        }
        int paddingRemainder = (outBuffer.writerIndex() - startIndex) % EncodeConstants.PADDING;
        if (paddingRemainder != 0) {
        	int padding = EncodeConstants.PADDING - paddingRemainder;
            ByteBufUtils.padBuffer(padding, outBuffer);
        }
        outBuffer.setShort(lengthIndex, outBuffer.writerIndex() - startIndex);
	}

}
