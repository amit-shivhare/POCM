
package com.sap.xi.xi.demo.agency;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Flight seat availability query (only for use in XI Demo)
 * 
 * <p>Java class for FlightSeatAvailabilityQuery complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FlightSeatAvailabilityQuery">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FlightID" type="{http://sap.com/xi/XI/Demo/Agency}FlightID"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FlightSeatAvailabilityQuery", propOrder = {
    "flightID"
})
public class FlightSeatAvailabilityQuery {

    @XmlElement(name = "FlightID", required = true)
    protected FlightID flightID;

    /**
     * Gets the value of the flightID property.
     * 
     * @return
     *     possible object is
     *     {@link FlightID }
     *     
     */
    public FlightID getFlightID() {
        return flightID;
    }

    /**
     * Sets the value of the flightID property.
     * 
     * @param value
     *     allowed object is
     *     {@link FlightID }
     *     
     */
    public void setFlightID(FlightID value) {
        this.flightID = value;
    }

}
