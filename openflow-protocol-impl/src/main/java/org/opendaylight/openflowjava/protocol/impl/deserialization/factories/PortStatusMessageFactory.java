package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessageBuilder;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class PortStatusMessageFactory implements OFDeserializer<PortStatusMessage> {

    private static PortStatusMessageFactory instance;
    private static final byte PADDING_IN_PORT_STATUS_HEADER = 7;
    private static final byte PADDING_IN_OFP_PORT_HEADER_1 = 4;
    private static final byte PADDING_IN_OFP_PORT_HEADER_2 = 2;
    private static final int macAddressLength = 6;
    
    private PortStatusMessageFactory() {
        // Singleton
    }
    
    /**
     * @return singleton factory
     */
    public static PortStatusMessageFactory getInstance(){
        if(instance == null){
            instance = new PortStatusMessageFactory();
        }
        return instance;
    }
    
    @Override
    public PortStatusMessage bufferToMessage(ByteBuf rawMessage, short version) {
        PortStatusMessageBuilder builder = new PortStatusMessageBuilder(); 
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setReason(PortReason.forValue(rawMessage.readUnsignedByte()));
        rawMessage.skipBytes(PADDING_IN_PORT_STATUS_HEADER);
        builder.setPortNo(rawMessage.readUnsignedInt());
        rawMessage.skipBytes(PADDING_IN_OFP_PORT_HEADER_1);
        StringBuffer macToString = new StringBuffer();
        for(int i=0; i<macAddressLength; i++){
            short mac = 0;
            mac = rawMessage.readUnsignedByte();
            macToString.append(String.format("%02X", mac));
        }
        builder.setHwAddr(new MacAddress(macToString.toString()));
        rawMessage.skipBytes(PADDING_IN_OFP_PORT_HEADER_2);
        builder.setConfig(createPortConfig(rawMessage.readUnsignedInt()));
        builder.setState(createPortState(rawMessage.readUnsignedInt()));
        builder.setCurrentFeatures(createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setAdvertisedFeatures(createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setSupportedFeatures(createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setPeerFeatures(createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setCurrSpeed(rawMessage.readUnsignedInt());
        builder.setMaxSpeed(rawMessage.readUnsignedInt());
        return builder.build();
    }

    private static PortFeatures createPortFeatures(long input){
        final Boolean _10mbHd = ((input) & 0x01) > 0;
        final Boolean _10mbFd = ((input) & 0x02) > 0;
        final Boolean _100mbHd = ((input) & 0x04) > 0;
        final Boolean _100mbFd = ((input) & 0x08) > 0;
        final Boolean _1gbHd = ((input) & 0x10) > 0;
        final Boolean _1gbFd = ((input) & 0x20) > 0;
        final Boolean _10gbFd = ((input) & 0x40) > 0;
        final Boolean _40gbFd = ((input) & 0x80) > 0;
        final Boolean _100gbFd = ((input) & 0x100) > 0;
        final Boolean _1tbFd = ((input) & 0x200) > 0;
        final Boolean _other = ((input) & 0x400) > 0;
        final Boolean _copper = ((input) & 0x800) > 0;
        final Boolean _fiber = ((input) & 0x1000) > 0;
        final Boolean _autoneg = ((input) & 0x2000) > 0;
        final Boolean _pause = ((input) & 0x4000) > 0;
        final Boolean _pauseAsym = ((input) & 0x8000) > 0;
        return new PortFeatures(_10mbHd, _10mbFd, _100mbHd, _100mbFd, _1gbHd, _1gbFd, _10gbFd,
                _40gbFd, _100gbFd, _1tbFd, _other, _copper, _fiber, _autoneg, _pause, _pauseAsym);
    }
    
    private static PortState createPortState(long input){
        final Boolean _linkDown = ((input) & 0x01) > 0;
        final Boolean _blocked  = ((input) & 0x02) > 0;
        final Boolean _live     = ((input) & 0x04) > 0;
        return new PortState(_linkDown, _blocked,_live);
    }
    
    private static PortConfig createPortConfig(long input){
        final Boolean _portDown   = ((input) & 0x01) > 0;
        final Boolean _noRecv    = ((input) & 0x02) > 0;
        final Boolean _noFwd       = ((input) & 0x04) > 0;
        final Boolean _noPacketIn = ((input) & 0x08) > 0;
        return new PortConfig(_portDown, _noRecv, _noFwd, _noPacketIn);
    }
    
}
