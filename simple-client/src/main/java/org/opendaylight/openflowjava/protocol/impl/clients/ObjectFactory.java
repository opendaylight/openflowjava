
package org.opendaylight.openflowjava.protocol.impl.clients;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.opendaylight.openflowjava.protocol.impl.clients package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.opendaylight.openflowjava.protocol.impl.clients
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Scenarios }
     * 
     */
    public Scenarios createScenarios() {
        return new Scenarios();
    }

    /**
     * Create an instance of {@link ScenarioType }
     * 
     */
    public ScenarioType createScenarioType() {
        return new ScenarioType();
    }

    /**
     * Create an instance of {@link StepType }
     * 
     */
    public StepType createStepType() {
        return new StepType();
    }

}
