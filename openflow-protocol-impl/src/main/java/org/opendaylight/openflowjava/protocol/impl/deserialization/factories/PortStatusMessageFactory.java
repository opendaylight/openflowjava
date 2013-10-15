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
    public static synchronized PortStatusMessageFactory getInstance(){
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
        final Boolean _10mbHd = ((input) & (1<<0)) != 0;
        final Boolean _10mbFd = ((input) & (1<<1)) != 0;
        final Boolean _100mbHd = ((input) & (1<<2)) != 0;
        final Boolean _100mbFd = ((input) & (1<<3)) != 0;
        final Boolean _1gbHd = ((input) & (1<<4)) != 0;
        final Boolean _1gbFd = ((input) & (1<<5)) != 0;
        final Boolean _10gbFd = ((input) & (1<<6)) != 0;
        final Boolean _40gbFd = ((input) & (1<<7)) != 0;
        final Boolean _100gbFd = ((input) & (1<<8)) != 0;
        final Boolean _1tbFd = ((input) & (1<<9)) != 0;
        final Boolean _other = ((input) & (1<<10)) != 0;
        final Boolean _copper = ((input) & (1<<11)) != 0;
        final Boolean _fiber = ((input) & (1<<12)) != 0;
        final Boolean _autoneg = ((input) & (1<<13)) != 0;
        final Boolean _pause = ((input) & (1<<14)) != 0;
        final Boolean _pauseAsym = ((input) & (1<<15)) != 0;
        return new PortFeatures(_10mbHd, _10mbFd, _100mbHd, _100mbFd, _1gbHd, _1gbFd, _10gbFd,
                _40gbFd, _100gbFd, _1tbFd, _other, _copper, _fiber, _autoneg, _pause, _pauseAsym);
    }
    
    private static PortState createPortState(long input){
        final Boolean _linkDown = ((input) & (1<<0)) != 0;
        final Boolean _blocked  = ((input) & (1<<1)) != 0;
        final Boolean _live     = ((input) & (1<<2)) != 0;
        return new PortState(_linkDown, _blocked,_live);
    }
    
    private static PortConfig createPortConfig(long input){
        final Boolean _portDown   = ((input) & (1<<0)) != 0;
        final Boolean _noRecv    = ((input) & (1<<2)) != 0;
        final Boolean _noFwd       = ((input) & (1<<5)) != 0;
        final Boolean _noPacketIn = ((input) & (1<<6)) != 0;
        return new PortConfig(_noFwd, _noPacketIn, _noRecv, _portDown);
    }
    
}
