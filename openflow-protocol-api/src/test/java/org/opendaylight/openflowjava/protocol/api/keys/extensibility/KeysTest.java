package org.opendaylight.openflowjava.protocol.api.keys.extensibility;

import junit.framework.Assert;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.keys.ActionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.ActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.InstructionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.InstructionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterInstructionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterInstructionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;

/**
 * @author michal.polkorab
 */
public class KeysTest {

    /**
     * Testing equals() and hashcode() methods of extension deserializer's keys
     */
    @Test
    public void testEqualsAndHashcodeOfDeserializerKeys() {
        ActionDeserializerKey actionDeserializerKey = new ActionDeserializerKey(EncodeConstants.OF13_VERSION_ID,
                EncodeConstants.EXPERIMENTER_VALUE, 1L);
        ExperimenterActionDeserializerKey experimenterActionDeserializerKey = new ExperimenterActionDeserializerKey(
                EncodeConstants.OF13_VERSION_ID, 1L);
        Assert.assertEquals(actionDeserializerKey, experimenterActionDeserializerKey);
        Assert.assertEquals(actionDeserializerKey.hashCode(), experimenterActionDeserializerKey.hashCode());

        InstructionDeserializerKey instructionDeserializerKey = new InstructionDeserializerKey(
                EncodeConstants.OF13_VERSION_ID, EncodeConstants.EXPERIMENTER_VALUE, 1L);
        ExperimenterInstructionDeserializerKey experimenterInstructionDeserializerKey = new ExperimenterInstructionDeserializerKey(
                EncodeConstants.OF13_VERSION_ID, 1L);
        Assert.assertEquals(instructionDeserializerKey, experimenterInstructionDeserializerKey);
        Assert.assertEquals(instructionDeserializerKey.hashCode(), experimenterInstructionDeserializerKey.hashCode());
        
        
    }

    /**
     * Testing equals() and hashcode() methods of extension serializer's keys
     */
    @Test
    public void testEqualsAndHashcodeOfActionDeserializerKeys() {
        ActionSerializerKey<Experimenter> actionSerializerKey = new ActionSerializerKey<>(
                EncodeConstants.OF13_VERSION_ID, Experimenter.class, 1L);
        ExperimenterActionSerializerKey experimenterActionSerializerKey = new ExperimenterActionSerializerKey(
                EncodeConstants.OF13_VERSION_ID, 1L);
        Assert.assertEquals(actionSerializerKey, experimenterActionSerializerKey);
        Assert.assertEquals(actionSerializerKey.hashCode(), experimenterActionSerializerKey.hashCode());

        InstructionSerializerKey<org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Experimenter> instructionSerializerKey = new InstructionSerializerKey<>(
                EncodeConstants.OF13_VERSION_ID,
                org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Experimenter.class,
                1L);
        ExperimenterInstructionSerializerKey experimenterInstructionSerializerKey = new ExperimenterInstructionSerializerKey(EncodeConstants.OF13_VERSION_ID, 1L);
        Assert.assertEquals(instructionSerializerKey, experimenterInstructionSerializerKey);
        Assert.assertEquals(instructionSerializerKey.hashCode(), experimenterInstructionSerializerKey.hashCode());
    }

}
