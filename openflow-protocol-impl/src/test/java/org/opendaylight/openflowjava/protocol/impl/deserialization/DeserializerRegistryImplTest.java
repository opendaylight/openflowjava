package org.opendaylight.openflowjava.protocol.impl.deserialization;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.MatchDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.grouping.Match;

public class DeserializerRegistryImplTest {

    private static final short OF13 = EncodeConstants.OF13_VERSION_ID;
    private static final short OF10 = EncodeConstants.OF10_VERSION_ID;
    public static final int EMPTY_VALUE = EncodeConstants.EMPTY_VALUE;

    /**
     * Test - register deserializer without arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterDeserializerNoArgs(){
        DeserializerRegistryImpl serReg = new DeserializerRegistryImpl();
        serReg.registerDeserializer(null, null);
    }

    /**
     * Test - unregister deserializer without MessageTypeKye
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnRegisterDeserializerNoMessageTypeKey(){
        DeserializerRegistryImpl derserReg = new DeserializerRegistryImpl();
        derserReg.init();
        derserReg.registerDeserializer(new MessageCodeKey(OF13,EMPTY_VALUE, Match.class), new MatchDeserializer());
        derserReg.unregisterDeserializer(null);
    }

    /**
     * Test - unregister deserializer 
     */
    @Test
    public void testUnRegisterDeserializer(){
        DeserializerRegistryImpl derserReg = new DeserializerRegistryImpl();
        derserReg.init();
        derserReg.registerDeserializer(new MessageCodeKey(OF13,EMPTY_VALUE, Match.class), new MatchDeserializer());
        Assert.assertTrue("Wrong - unregister serializer",derserReg.unregisterDeserializer(new MessageCodeKey(OF13,EMPTY_VALUE, Match.class)));
        derserReg.registerDeserializer(new MessageCodeKey(OF13, EMPTY_VALUE, Match.class), new MatchDeserializer());
        Assert.assertFalse("Wrong - unregister serializer",derserReg.unregisterDeserializer(new MessageCodeKey(OF10,EMPTY_VALUE, Match.class)));
    }
}
