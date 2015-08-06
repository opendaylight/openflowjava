package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortDescPropType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortStatsPropType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortDescCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortDescCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortStatsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortStatsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.desc._case.MultipartReplyPortDescBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.desc._case.multipart.reply.port.desc.Ports;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.desc._case.multipart.reply.port.desc.PortsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats._case.MultipartReplyPortStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats._case.multipart.reply.port.stats.PortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats._case.multipart.reply.port.stats.PortStatsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OF-1.4 multipart reply deserializer reusing most of OF-1.3 multiparts
 */
public class OF14MultipartReplyMessageFactory extends MultipartReplyMessageFactory {

    private static final Logger LOG = LoggerFactory.getLogger(OF14MultipartReplyMessageFactory.class);

    private static final int PADDING_IN_PORT_STATS_AFTER_LENGTH = 2;
    private static final int PADDING_IN_PORT_STATS_PROP = 4;
    private static final byte PADDING_IN_PORT_DESC_HEADER_01 = 2;
    private static final byte PADDING_IN_PORT_DESC_HEADER_02 = 2;
    private static final byte PADDING_IN_PORT_DESC_PROP = 4;

    @Override

    protected MultipartReplyPortStatsCase setPortStats(ByteBuf input) {
        MultipartReplyPortStatsCaseBuilder caseBuilder = new MultipartReplyPortStatsCaseBuilder();
        MultipartReplyPortStatsBuilder builder = new MultipartReplyPortStatsBuilder();
        List<PortStats> portStatsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            PortStatsBuilder portStatsBuilder = new PortStatsBuilder();

            int startReaderIndex = input.readerIndex();
            int length = input.readUnsignedShort();
            int endReaderIndex = startReaderIndex + length;
            // padding
            input.skipBytes(PADDING_IN_PORT_STATS_AFTER_LENGTH);

            portStatsBuilder.setPortNo(input.readUnsignedInt());
            portStatsBuilder.setDurationSec(input.readUnsignedInt());
            portStatsBuilder.setDurationNsec(input.readUnsignedInt());

            portStatsBuilder.setRxPackets(readNextUint64ToBigInteger(input));
            portStatsBuilder.setTxPackets(readNextUint64ToBigInteger(input));
            portStatsBuilder.setRxBytes(readNextUint64ToBigInteger(input));
            portStatsBuilder.setTxBytes(readNextUint64ToBigInteger(input));
            portStatsBuilder.setRxDropped(readNextUint64ToBigInteger(input));
            portStatsBuilder.setTxDropped(readNextUint64ToBigInteger(input));
            portStatsBuilder.setRxErrors(readNextUint64ToBigInteger(input));
            portStatsBuilder.setTxErrors(readNextUint64ToBigInteger(input));

            while (input.readerIndex() < endReaderIndex) {
                final int rawPropertyType = input.readUnsignedShort();
                final PortStatsPropType propType = Preconditions.checkNotNull(
                        PortStatsPropType.forValue(rawPropertyType),
                        "unsupported port-stats property type: {}", rawPropertyType);
                final int propertyLength = input.readUnsignedShort();
                switch (propType) {
                    case OFPPSPTETHERNET:
                        input.skipBytes(PADDING_IN_PORT_STATS_PROP);
                        portStatsBuilder.setRxFrameErr(readNextUint64ToBigInteger(input));
                        portStatsBuilder.setRxOverErr(readNextUint64ToBigInteger(input));
                        portStatsBuilder.setRxCrcErr(readNextUint64ToBigInteger(input));
                        portStatsBuilder.setCollisions(readNextUint64ToBigInteger(input));
                        break;
                    case OFPPSPTOPTICAL:
                        //TODO: store optical properties to model
                        input.skipBytes(PADDING_IN_PORT_STATS_PROP);
//                        uint32_t flags;        /* Features enabled by the port. */
                        input.readUnsignedInt();
//                        uint32_t tx_freq_lmda; /* Current TX Frequency/Wavelength */
                        input.readUnsignedInt();
//                        uint32_t tx_offset;    /* TX Offset */
                        input.readUnsignedInt();
//                        uint32_t tx_grid_span; /* TX Grid Spacing */
                        input.readUnsignedInt();
//                        uint32_t rx_freq_lmda; /* Current RX Frequency/Wavelength */
                        input.readUnsignedInt();
//                        uint32_t rx_offset;    /* RX Offset */
                        input.readUnsignedInt();
//                        uint32_t rx_grid_span; /* RX Grid Spacing */
                        input.readUnsignedInt();
//                        uint16_t tx_pwr;       /* Current TX power */
                        input.readUnsignedShort();
//                        uint16_t rx_pwr;       /* Current RX power */
                        input.readUnsignedShort();
//                        uint16_t bias_current; /* TX Bias Current */
                        input.readUnsignedShort();
//                        uint16_t temperature;  /* TX Laser Temperature */
                        input.readUnsignedShort();
                        break;
                    case OFPPSPTEXPERIMENTER:
                        // TODO: involve custom registered deserializer
                        input.skipBytes(propertyLength - 8);
                        break;
                    default:
                        LOG.warn("Unsupported port-stats properties type: {}", propType);
                }

            }

            portStatsList.add(portStatsBuilder.build());
        }

        builder.setPortStats(portStatsList);
        caseBuilder.setMultipartReplyPortStats(builder.build());
        return caseBuilder.build();
    }

    @Override
    protected MultipartReplyPortDescCase setPortDesc(ByteBuf input) {
        MultipartReplyPortDescCaseBuilder caseBuilder = new MultipartReplyPortDescCaseBuilder();
        MultipartReplyPortDescBuilder builder = new MultipartReplyPortDescBuilder();
        List<Ports> portsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            PortsBuilder portsBuilder = new PortsBuilder();
            final int startReaderIndex = input.readerIndex();
            portsBuilder.setPortNo(input.readUnsignedInt());
            final int descriptionLength = input.readUnsignedShort();
            final int endReaderIndex = startReaderIndex + descriptionLength;

            input.skipBytes(PADDING_IN_PORT_DESC_HEADER_01);
            byte[] hwAddress = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
            input.readBytes(hwAddress);
            portsBuilder.setHwAddr(new MacAddress(ByteBufUtils.macAddressToString(hwAddress)));
            input.skipBytes(PADDING_IN_PORT_DESC_HEADER_02);
            portsBuilder.setName(ByteBufUtils.decodeNullTerminatedString(input, EncodeConstants.MAX_PORT_NAME_LENGTH));
            portsBuilder.setConfig(createPortConfig(input.readUnsignedInt()));
            portsBuilder.setState(createPortState(input.readUnsignedInt()));

            while (input.readerIndex() < endReaderIndex) {
                final int rawPropertyType = input.readUnsignedShort();
                final PortDescPropType propType = Preconditions.checkNotNull(
                        PortDescPropType.forValue(rawPropertyType),
                        "unsupported port-stats property type: {}", rawPropertyType);
                final int propertyLength = input.readUnsignedShort();
                switch (propType) {
                    case OFPPDPTETHERNET:
                        input.skipBytes(PADDING_IN_PORT_DESC_PROP);
                        portsBuilder.setCurrentFeatures(createPortFeatures(input.readUnsignedInt()));
                        portsBuilder.setAdvertisedFeatures(createPortFeatures(input.readUnsignedInt()));
                        portsBuilder.setSupportedFeatures(createPortFeatures(input.readUnsignedInt()));
                        portsBuilder.setPeerFeatures(createPortFeatures(input.readUnsignedInt()));
                        portsBuilder.setCurrSpeed(input.readUnsignedInt());
                        portsBuilder.setMaxSpeed(input.readUnsignedInt());
                        break;
                    case OFPPDPTOPTICAL:
                        input.skipBytes(PADDING_IN_PORT_DESC_PROP);
                        //TODO: store optical properties to model
//                        uint32_t tx_min_freq_lmda;  /* Minimum TX Frequency/Wavelength */
                        input.readUnsignedInt();
//                        uint32_t tx_max_freq_lmda;  /* Maximum TX Frequency/Wavelength */
                        input.readUnsignedInt();
//                        uint32_t tx_grid_freq_lmda; /* TX Grid Spacing Frequency/Wavelength */
                        input.readUnsignedInt();
//                        uint32_t rx_min_freq_lmda;  /* Minimum RX Frequency/Wavelength */
                        input.readUnsignedInt();
//                        uint32_t rx_max_freq_lmda;  /* Maximum RX Frequency/Wavelength */
                        input.readUnsignedInt();
//                        uint32_t rx_grid_freq_lmda; /* RX Grid Spacing Frequency/Wavelength */
                        input.readUnsignedInt();
//                        uint16_t tx_pwr_min;        /* Minimum TX power */
                        input.readUnsignedShort();
//                        uint16_t tx_pwr_max;        /* Maximum TX power */
                        input.readUnsignedShort();
                        break;
                    case OFPPDPTEXPERIMENTER:
                        // TODO: involve custom registered deserializer
                        input.skipBytes(propertyLength - 8);
                        break;
                    default:
                        LOG.warn("Unsupported port-stats properties type: {}", propType);
                }
            }

            portsList.add(portsBuilder.build());
        }
        builder.setPorts(portsList);
        caseBuilder.setMultipartReplyPortDesc(builder.build());
        return caseBuilder.build();
    }

    private BigInteger readNextUint64ToBigInteger(ByteBuf input) {
        byte[] rxPackets = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        input.readBytes(rxPackets);
        return new BigInteger(1, rxPackets);
    }
}
