package org.opendaylight.openflowjava.protocol.impl.clients;

import com.google.common.base.Preconditions;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Jozef Bacigal
 *         Date: 9.3.2016
 */
public class ScenarioServiceImpl implements ScenarioService {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioServiceImpl.class);

    @Override
    public void marshallData(Scenarios scenarios) throws JAXBException, SAXException {
        File file = new File(XML_FILE_PATH_WITH_FILE_NAME);
        LOG.info("Marshaling scenario data to: {}", XML_FILE_PATH_WITH_FILE_NAME);

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File(XSD_SCHEMA_PATH_WITH_FILE_NAME));
        LOG.info("with schema: {}", XSD_SCHEMA_PATH_WITH_FILE_NAME);

        JAXBContext jaxbContext = JAXBContext.newInstance(Scenarios.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, SCENARIO_XSD);

        jaxbMarshaller.setSchema(schema);
        jaxbMarshaller.marshal(scenarios, file);

    }

    @Override
    public ScenarioType unMarshallData(String scenarioName) throws SAXException, JAXBException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File(XSD_SCHEMA_PATH_WITH_FILE_NAME));
        LOG.debug("Loading schema from: {}", XSD_SCHEMA_PATH_WITH_FILE_NAME);

        JAXBContext jc = JAXBContext.newInstance(Scenarios.class);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setSchema(schema);

        Scenarios scenarios = (Scenarios) unmarshaller.unmarshal(new File(XML_FILE_PATH_WITH_FILE_NAME));
        LOG.debug("Scenarios ({}) are un-marshaled from {}", scenarios.getScenario().size(), XML_FILE_PATH_WITH_FILE_NAME);

        boolean foundConfiguration = false;
        ScenarioType scenarioType = null;
        for (ScenarioType scenario : scenarios.getScenario()) {
            if (scenario.getName().equals(scenarioName)) {
                scenarioType = scenario;
                foundConfiguration = true;
            }
        }
        if (!foundConfiguration) {
            LOG.warn("Scenario {} not found.", scenarioName);
        } else {
            LOG.info("Scenario {} found with {} steps.", scenarioName, scenarioType.getStep().size());
        }
        return scenarioType;
    }

    @Override
    public SortedMap<Integer, ClientEvent> getEventsFromScenario(ScenarioType scenario) throws IOException {
        Preconditions.checkNotNull(scenario, "Scenario name not found. Check XML file, scenario name or directories.");
        SortedMap<Integer, ClientEvent> events = new TreeMap<>();
        Integer counter = 0;
        for (StepType stepType : scenario.getStep()) {
            LOG.debug("Step {}: {}, type {}, bytes {}", stepType.getOrder(), stepType.getName(), stepType.getEvent().value(), stepType.getBytes().toArray());
            switch (stepType.getEvent()) {
                case SLEEP_EVENT: events.put(counter++, new SleepEvent(1000)); break;
                case SEND_EVENT: events.put(counter++, new SendEvent(ByteBufUtils.serializableList(stepType.getBytes()))); break;
                case WAIT_FOR_MESSAGE_EVENT: events.put(counter++, new WaitForMessageEvent(ByteBufUtils.serializableList(stepType.getBytes()))); break;
            }
        }
        return events;
    }

}
