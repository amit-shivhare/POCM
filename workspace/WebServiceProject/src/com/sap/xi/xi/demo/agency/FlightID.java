
package com.sap.xi.xi.demo.agency;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Flight ID (only for use in XI Demo)
 * 
 * <p>Java class for FlightID complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FlightID">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AirlineID">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ConnectionID">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="4"/>
 *               &lt;pattern value="\d+"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FlightDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FlightID", propOrder = {
    "airlineID",
    "connectionID",
    "flightDate"
})
public class FlightID {

    @XmlElement(name = "AirlineID", required = true)
    protected String airlineID;
    @XmlElement(name = "ConnectionID", required = true)
    protected String connectionID;
    @XmlElement(name = "FlightDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar flightDate;

    /**
     * Gets the value of the airlineID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAirlineID() {
        return airlineID;
    }

    /**
     * Sets the value of the airlineID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAirlineID(String value) {
        this.airlineID = value;
    }

    /**
     * Gets the value of the connectionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnectionID() {
        return connectionID;
    }

    /**
     * Sets the value of the connectionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnectionID(String value) {
        this.connectionID = value;
    }

    /**
     * Gets the value of the flightDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFlightDate() {
        return flightDate;
    }

    /**
     * Sets the value of the flightDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFlightDate(XMLGregorianCalendar value) {
        this.flightDate = value;
    }

}
