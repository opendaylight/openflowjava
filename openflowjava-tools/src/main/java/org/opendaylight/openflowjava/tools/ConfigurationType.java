
package org.opendaylight.openflowjava.tools;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for configurationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="controller-ip" type="{}ipAddressType"/>
 *         &lt;element name="devices-count" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="ssl" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="threads" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="timeout" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="freeze" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="sleep" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configurationType", propOrder = {
    "controllerIp",
    "devicesCount",
    "ssl",
    "threads",
    "port",
    "timeout",
    "freeze",
    "sleep"
})
public class ConfigurationType {

    @XmlElement(name = "controller-ip", required = true, defaultValue = "127.0.0.1")
    protected String controllerIp;
    @XmlElement(name = "devices-count", required = true, defaultValue = "1")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger devicesCount;
    @XmlElement(defaultValue = "false")
    protected Boolean ssl;
    @XmlElement(defaultValue = "1")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger threads;
    @XmlElement(required = true, defaultValue = "6653")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger port;
    @XmlElement(defaultValue = "1000")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger timeout;
    @XmlElement(defaultValue = "3")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger freeze;
    @XmlElement(defaultValue = "100")
    protected Long sleep;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the controllerIp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getControllerIp() {
        return controllerIp;
    }

    /**
     * Sets the value of the controllerIp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setControllerIp(String value) {
        this.controllerIp = value;
    }

    /**
     * Gets the value of the devicesCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDevicesCount() {
        return devicesCount;
    }

    /**
     * Sets the value of the devicesCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDevicesCount(BigInteger value) {
        this.devicesCount = value;
    }

    /**
     * Gets the value of the ssl property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSsl() {
        return ssl;
    }

    /**
     * Sets the value of the ssl property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSsl(Boolean value) {
        this.ssl = value;
    }

    /**
     * Gets the value of the threads property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getThreads() {
        return threads;
    }

    /**
     * Sets the value of the threads property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setThreads(BigInteger value) {
        this.threads = value;
    }

    /**
     * Gets the value of the port property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPort() {
        return port;
    }

    /**
     * Sets the value of the port property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPort(BigInteger value) {
        this.port = value;
    }

    /**
     * Gets the value of the timeout property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTimeout() {
        return timeout;
    }

    /**
     * Sets the value of the timeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTimeout(BigInteger value) {
        this.timeout = value;
    }

    /**
     * Gets the value of the freeze property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFreeze() {
        return freeze;
    }

    /**
     * Sets the value of the freeze property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFreeze(BigInteger value) {
        this.freeze = value;
    }

    /**
     * Gets the value of the sleep property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSleep() {
        return sleep;
    }

    /**
     * Sets the value of the sleep property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSleep(Long value) {
        this.sleep = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
