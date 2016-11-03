package org.opendaylight.openflowjava.protocol.impl.serialization.experimenter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.BundleFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.BundlePropertyType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.bundle.properties.BundleProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.bundle.properties.BundlePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.bundle.properties.bundle.property.bundle.property.entry.BundleExperimenterPropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.bundle.properties.bundle.property.bundle.property.entry.bundle.experimenter.property.BundleExperimenterPropertyData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.experimenter.input.experimenter.data.of.choice.bundle.add.message.message.PortModCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.experimenter.input.experimenter.data.of.choice.bundle.add.message.message.PortModCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ExperimenterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yangtools.yang.binding.DataContainer;

/**
 * Created by andrej on 2.11.2016.
 */
public class AbstractBundleMessageFactoryTest {

    @Test
    public void writeBundleFlags() throws Exception {
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        AbstractBundleMessageFactory.writeBundleFlags(new BundleFlags(true, true), out);
        Assert.assertEquals("Wrong flags", 3, out.readUnsignedShort());
    }

    public static List<BundleProperty> createListWithBundleExperimenterProperty(BundleExperimenterPropertyData data) {
        BundlePropertyBuilder propertyBuilder = new BundlePropertyBuilder();
        propertyBuilder.setType(BundlePropertyType.ONFETBPTEXPERIMENTER);
        BundleExperimenterPropertyBuilder experimenterPropertyBuilder = new BundleExperimenterPropertyBuilder();
        experimenterPropertyBuilder.setExperimenter(new ExperimenterId(1L));
        experimenterPropertyBuilder.setExpType(2L);

        experimenterPropertyBuilder.setBundleExperimenterPropertyData(data);
        propertyBuilder.setBundlePropertyEntry(experimenterPropertyBuilder.build());
        return new ArrayList<>(Collections.singleton(propertyBuilder.build()));
    }

    public static BundleExperimenterPropertyData createBundleExperimenterPropertyData() {
        return new BundleExperimenterPropertyData() {
            @Override
            public Class<? extends DataContainer> getImplementedInterface() {
                return null;
            }
        };
    }

    public static PortModCase createPortModCase() {
        PortModCaseBuilder caseBuilder = new PortModCaseBuilder();
        caseBuilder.setVersion((short) EncodeConstants.OF13_VERSION_ID);
        caseBuilder.setXid(3L);
        caseBuilder.setPortNo(new PortNumber(9L));
        caseBuilder.setHwAddress(new MacAddress("08:00:27:00:B0:EB"));
        caseBuilder.setConfig(new PortConfig(true, false, true, false));
        caseBuilder.setMask(new PortConfig(false, true, false, true));
        caseBuilder.setAdvertise(new PortFeatures(true, false, false, false,
                false, false, false, true,
                false, false, false, false,
                false, false, false, false));
        return caseBuilder.build();
    }
}