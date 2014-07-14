package org.opendaylight.openflowjava.nx.codec.match;

import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.of.extension.nicira.match.rev140421.NxMatchReg1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

public class MatchReg1Codec extends AbstractMatchRegCodec {

    private static final int NXM_FIELD_CODE = 1;
    private static final Class<NxMatchReg1> NXM_FIELD_CLASS = NxMatchReg1.class;
    public static final EnhancedMessageTypeKey<Nxm1Class, NxMatchReg1> MESSAGE_TYPE_KEY = new EnhancedMessageTypeKey<>(
            EncodeConstants.OF13_VERSION_ID, AbstractMatchCodec.NXM_1_CLASS, NXM_FIELD_CLASS);
    public static final EnhancedMessageCodeKey MESSAGE_CODE_KEY = new EnhancedMessageCodeKey(
            EncodeConstants.OF13_VERSION_ID, AbstractMatchCodec.NXM_1_CODE, NXM_FIELD_CODE, MatchEntries.class);

    @Override
    public int getNxmFieldCode() {
        return NXM_FIELD_CODE;
    }

    @Override
    public Class<? extends MatchField> getNxmField() {
        return NXM_FIELD_CLASS;
    }

}
