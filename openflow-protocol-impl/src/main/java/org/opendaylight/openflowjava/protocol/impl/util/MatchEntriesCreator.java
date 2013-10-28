/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.PortNumber;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MatchEntries11;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MatchEntries11Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MatchEntries17Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MatchEntries24Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.EthDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ExperimenterClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPhyPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Metadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm0Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.MatchEntriesBuilder;

/**
 * To create matches
 * @author timotej.kubas
 * @author michal.polkorab
 */
public abstract class MatchEntriesCreator {
    private static List<MatchEntries> matchEntriesList = new ArrayList<>();
    private static MatchEntriesBuilder matchEntriesBuilder = new MatchEntriesBuilder(); 
    
    /**
     * @param in input ByteBuf
     * @param matchArrayLength to infer size of array
     * @return MatchEntriesList
     */
    public static List<MatchEntries> createMatchEntry(ByteBuf in, int matchArrayLength) {
        int currMatchLength = 0;
        final byte MATCH_LENGTH = 1;
        
        while(currMatchLength < matchArrayLength) {
            
            switch (in.readUnsignedShort()) { 
            case 0x0000:
                        matchEntriesBuilder.setOxmClass(Nxm0Class.class);
                        break;
            case 0x0001:
                        matchEntriesBuilder.setOxmClass(Nxm1Class.class);
                        break;
            case 0x8000:
                        matchEntriesBuilder.setOxmClass(OpenflowBasicClass.class);
                        break;
            case 0xFFFF:
                        matchEntriesBuilder.setOxmClass(ExperimenterClass.class);
                        break;
            default:
                        break;
            }
            currMatchLength = currMatchLength +2;
            
            int matchField = in.readUnsignedByte() >>> 1;
            in.skipBytes(MATCH_LENGTH);
            
            currMatchLength = currMatchLength + 2;
            
            switch(matchField) {
            case 0: 
                matchEntriesBuilder.setOxmMatchField(InPort.class);
                MatchEntries11Builder port = new MatchEntries11Builder();
                port.setPort(new PortNumber(in.readInt())); 
                matchEntriesBuilder.addAugmentation(MatchEntries11.class, port.build());
                currMatchLength = currMatchLength + 4;
                break;
            case 1:
                matchEntriesBuilder.setOxmMatchField(InPhyPort.class);
                MatchEntries11Builder phyPort = new MatchEntries11Builder();
                phyPort.setPort(new PortNumber(in.readInt())); 
                matchEntriesBuilder.addAugmentation(MatchEntries11.class, phyPort.build());
                currMatchLength = currMatchLength + 4;
                break;
            case 2:
                matchEntriesBuilder.setOxmMatchField(Metadata.class);
                MatchEntries24Builder metadata = new MatchEntries24Builder();
                byte[] metadataBytes = new byte[Long.SIZE/Byte.SIZE];
                in.readBytes(metadataBytes);
                metadata.setMetadata(metadataBytes); 
                matchEntriesBuilder.addAugmentation(MatchEntries11.class, metadata.build());
                currMatchLength = currMatchLength + 8;
                break;
            case 3:
                matchEntriesBuilder.setOxmMatchField(EthDst.class);
                MatchEntries17Builder macAddress = new MatchEntries17Builder();
                StringBuffer macToString = new StringBuffer();
                final int macAddressLength = 6;
                for(int i=0; i<macAddressLength ; i++){
                    short mac = 0;
                    mac = in.readUnsignedByte();
                    macToString.append(String.format("%02X", mac));
                }
                macAddress.setMacAddress(new MacAddress(macToString.toString())); 
                matchEntriesBuilder.addAugmentation(MatchEntries11.class, macAddress.build());
                currMatchLength = currMatchLength + 8;
                break;
            default: 
                break;
            }
          matchEntriesList.add(matchEntriesBuilder.build());
        }
        
        return matchEntriesList;
    }
}
