package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.TcpFlagsContainer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.TcpFlagsContainerBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.oxm.container.match.entry.value.experimenter.id._case.TcpFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.oxm.container.match.entry.value.experimenter.id._case.TcpFlagsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev150225.oxm.container.match.entry.value.ExperimenterIdCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev150225.oxm.container.match.entry.value.ExperimenterIdCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev150225.oxm.container.match.entry.value.experimenter.id._case.ExperimenterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ExperimenterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.ExperimenterClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entries.grouping.MatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entries.grouping.MatchEntryBuilder;

/**
 * Created by mpolkora on 2016-08-01.
 */
public class Example {

    public static void main(String[] args) {
        // deserialization
        MatchEntryBuilder matchEntryBuilder = new MatchEntryBuilder();
        matchEntryBuilder.setOxmClass(ExperimenterClass.class);
        matchEntryBuilder.setOxmMatchField(org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.TcpFlags.class);
        matchEntryBuilder.setHasMask(false);

        // create body for match entry value
        ExperimenterIdCaseBuilder expCaseBuilder = new ExperimenterIdCaseBuilder();

        // add experimenter value
        ExperimenterBuilder expBuilder = new ExperimenterBuilder();
        expBuilder.setExperimenter(new ExperimenterId(123l));
        expCaseBuilder.setExperimenter(expBuilder.build());

        // add tcp flags information
        TcpFlagsContainerBuilder flagsContainerBuilder = new TcpFlagsContainerBuilder();
        TcpFlagsBuilder flagsBuilder = new TcpFlagsBuilder();
        flagsBuilder.setFlags(345);
        flagsBuilder.setMask(new byte[]{1,2});
        flagsContainerBuilder.setTcpFlags(flagsBuilder.build());

        // add tcp flag information into match entry value
        expCaseBuilder.addAugmentation(TcpFlagsContainer.class, flagsContainerBuilder.build());

        // add match entry value
        matchEntryBuilder.setMatchEntryValue(expCaseBuilder.build());
        MatchEntry matchEntry = matchEntryBuilder.build();


        // serialization
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
//        serializeHeader(); // inherited from e.g. AbstractOxmMatchEntrySerializer or
        ExperimenterIdCase expCase = (ExperimenterIdCase) matchEntry.getMatchEntryValue();
        long experimenterId = expCase.getExperimenter().getExperimenter().getValue();
        TcpFlagsContainer augmentation = expCase.getAugmentation(TcpFlagsContainer.class);
        TcpFlags tcpFlags = augmentation.getTcpFlags();


    }
}
