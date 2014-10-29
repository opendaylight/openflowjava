package org.opendaylight.openflowjava.protocol.api.keys;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.keys.ActionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.ActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.InstructionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.InstructionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntryDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntrySerializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.api.util.OxmMatchConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.ExperimenterActionSubType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;

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

        MatchEntryDeserializerKey matchKey = new MatchEntryDeserializerKey(EncodeConstants.OF13_VERSION_ID,
                OxmMatchConstants.OPENFLOW_BASIC_CLASS, OxmMatchConstants.ARP_OP);
        MatchEntryDeserializerKey matchKey2 = new MatchEntryDeserializerKey(EncodeConstants.OF13_VERSION_ID,
                OxmMatchConstants.OPENFLOW_BASIC_CLASS, OxmMatchConstants.ARP_OP);
        Assert.assertEquals(matchKey, matchKey2);
        Assert.assertEquals(matchKey.hashCode(), matchKey2.hashCode());
    }

    /**
     * Testing equals() and hashcode() methods of extension serializer's keys
     */
    @Test
    public void testEqualsAndHashcodeOfActionDeserializerKeys() {
        ActionSerializerKey<Experimenter> actionSerializerKey = new ActionSerializerKey<>(
                EncodeConstants.OF13_VERSION_ID, Experimenter.class, 1L);
        ExperimenterActionSerializerKey experimenterActionSerializerKey = new ExperimenterActionSerializerKey(
                EncodeConstants.OF13_VERSION_ID, 1L, ExpSubType.class);
        Assert.assertFalse(actionSerializerKey.equals(experimenterActionSerializerKey));
        Assert.assertFalse(experimenterActionSerializerKey.equals(actionSerializerKey));

        InstructionSerializerKey<org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Experimenter> instructionSerializerKey = new InstructionSerializerKey<>(
                EncodeConstants.OF13_VERSION_ID,
                org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Experimenter.class,
                1L);
        ExperimenterInstructionSerializerKey experimenterInstructionSerializerKey = new ExperimenterInstructionSerializerKey(EncodeConstants.OF13_VERSION_ID, 1L);
        Assert.assertEquals(instructionSerializerKey, experimenterInstructionSerializerKey);
        Assert.assertEquals(instructionSerializerKey.hashCode(), experimenterInstructionSerializerKey.hashCode());

        MatchEntrySerializerKey<OpenflowBasicClass, InPort> matchKey = new MatchEntrySerializerKey<>(
                EncodeConstants.OF10_VERSION_ID, OpenflowBasicClass.class, InPort.class);
        MatchEntrySerializerKey<OpenflowBasicClass, InPort> matchKey2 = new MatchEntrySerializerKey<>(
                EncodeConstants.OF10_VERSION_ID, OpenflowBasicClass.class, InPort.class);
        Assert.assertEquals(matchKey, matchKey2);
        Assert.assertEquals(matchKey.hashCode(), matchKey2.hashCode());
    }

    private static class ExpSubType extends ExperimenterActionSubType {
        // empty class - only used in test for comparation
    }

}
