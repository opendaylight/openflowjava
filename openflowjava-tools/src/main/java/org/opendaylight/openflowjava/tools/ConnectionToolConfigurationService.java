package org.opendaylight.openflowjava.tools;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;

/**
 * @author Jozef Bacigal
 * Date: 8.3.2016
 */
public interface ConnectionToolConfigurationService {


    String xmlFilePath = "openflowjava-tools/src/main/resources/";
    String xsdSchemaPath = "openflowjava-tools/src/main/resources/";
    String xsdFileName = "configuration.xsd";
    String xmlFileName = "configuration.xml";
    String xmlFilePathWithFileName = xmlFilePath + xmlFileName;
    String xsdSchemaPathWithFileName = xsdSchemaPath + xsdFileName;

    /**
     * Method to save configuration into XML configuration file
     * @param params {@link ConnectionTestTool.Params}
     * @param configurationName {@link String}
     * @throws JAXBException
     * @throws SAXException
     */
    void marshallData(ConnectionTestTool.Params params, String configurationName) throws JAXBException, SAXException;

    /**
     * Method to load data from XML configuration file. Each configuration has a name.
     * @param configurationName {@link String}
     * @return parameters {@link org.opendaylight.openflowjava.tools.ConnectionTestTool.Params}
     * @throws SAXException
     * @throws JAXBException
     */
    ConnectionTestTool.Params unMarshallData(String configurationName) throws SAXException, JAXBException;
}
