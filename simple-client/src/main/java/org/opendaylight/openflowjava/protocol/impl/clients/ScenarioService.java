package org.opendaylight.openflowjava.protocol.impl.clients;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.SortedMap;

/**
 *
 * @author Jozef Bacigal
 * Date: 8.3.2016
 */
interface ScenarioService {

    String SIMPLE_CLIENT_SRC_MAIN_RESOURCES = "simple-client/src/main/resources/";
    String SIMPLE_CLIENT_SRC_MAIN_RESOURCES1 = "simple-client/src/main/resources/";
    String SCENARIO_XSD = "scenario.xsd";
    String SCENARIO_XML = "scenario.xml";
    String XML_FILE_PATH_WITH_FILE_NAME = SIMPLE_CLIENT_SRC_MAIN_RESOURCES + SCENARIO_XML;
    String XSD_SCHEMA_PATH_WITH_FILE_NAME = SIMPLE_CLIENT_SRC_MAIN_RESOURCES1 + SCENARIO_XSD;

    /**
     * Method to save configuration into XML configuration file
     * @param scenarios {@link Scenarios}
     * @throws JAXBException
     * @throws SAXException
     */
    void marshallData(Scenarios scenarios) throws JAXBException, SAXException;

    /**
     * Method to load data from XML configuration file. Each configuration has a name.
     * @param scenarioName {@link String}
     * @return scenarios
     * @throws SAXException
     * @throws JAXBException
     */
    ScenarioType unMarshallData(String scenarioName) throws SAXException, JAXBException;

    SortedMap<Integer, ClientEvent> getEventsFromScenario(ScenarioType scenario) throws IOException;

}
