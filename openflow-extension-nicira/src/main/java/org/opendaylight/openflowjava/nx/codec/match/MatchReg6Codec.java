package org.opendaylight.openflowjava.nx.codec.match;

import org.opendaylight.openflowjava.protocol.api.keys.MatchEntryDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntrySerializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.api.util.OxmMatchConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.match.rev140421.OfjNxMatchReg6;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;

public class MatchReg6Codec extends AbstractMatchRegCodec {

    private static final int NXM_FIELD_CODE = 6;
    public static final MatchEntrySerializerKey<Nxm1Class, OfjNxMatchReg6> SERIALIZER_KEY = new MatchEntrySerializerKey<>(
            EncodeConstants.OF13_VERSION_ID, Nxm1Class.class, OfjNxMatchReg6.class);
    public static final MatchEntryDeserializerKey DESERIALIZER_KEY = new MatchEntryDeserializerKey(
            EncodeConstants.OF13_VERSION_ID, OxmMatchConstants.NXM_1_CLASS, NXM_FIELD_CODE);

    @Override
    public int getNxmFieldCode() {
        return NXM_FIELD_CODE;
    }

    @Override
    public Class<? extends MatchField> getNxmField() {
        return OfjNxMatchReg6.class;
    }

}
