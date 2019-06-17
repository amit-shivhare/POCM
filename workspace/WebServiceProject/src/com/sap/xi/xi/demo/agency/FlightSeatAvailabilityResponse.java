
package com.sap.xi.xi.demo.agency;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Flight seat availability response (only for use in XI Demo)
 * 
 * <p>Java class for FlightSeatAvailabilityResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FlightSeatAvailabilityResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EconomyMaxSeats" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="EconomyFreeSeats" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="BusinessMaxSeats" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="BusinessFreeSeats" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FirstMaxSeats" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FirstFreeSeats" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FlightSeatAvailabilityResponse", propOrder = {
    "economyMaxSeats",
    "economyFreeSeats",
    "businessMaxSeats",
    "businessFreeSeats",
    "firstMaxSeats",
    "firstFreeSeats"
})
public class FlightSeatAvailabilityResponse {

    @XmlElement(name = "EconomyMaxSeats")
    protected int economyMaxSeats;
    @XmlElement(name = "EconomyFreeSeats")
    protected int economyFreeSeats;
    @XmlElement(name = "BusinessMaxSeats")
    protected int businessMaxSeats;
    @XmlElement(name = "BusinessFreeSeats")
    protected int businessFreeSeats;
    @XmlElement(name = "FirstMaxSeats")
    protected int firstMaxSeats;
    @XmlElement(name = "FirstFreeSeats")
    protected int firstFreeSeats;

    /**
     * Gets the value of the economyMaxSeats property.
     * 
     */
    public int getEconomyMaxSeats() {
        return economyMaxSeats;
    }

    /**
     * Sets the value of the economyMaxSeats property.
     * 
     */
    public void setEconomyMaxSeats(int value) {
        this.economyMaxSeats = value;
    }

    /**
     * Gets the value of the economyFreeSeats property.
     * 
     */
    public int getEconomyFreeSeats() {
        return economyFreeSeats;
    }

    /**
     * Sets the value of the economyFreeSeats property.
     * 
     */
    public void setEconomyFreeSeats(int value) {
        this.economyFreeSeats = value;
    }

    /**
     * Gets the value of the businessMaxSeats property.
     * 
     */
    public int getBusinessMaxSeats() {
        return businessMaxSeats;
    }

    /**
     * Sets the value of the businessMaxSeats property.
     * 
     */
    public void setBusinessMaxSeats(int value) {
        this.businessMaxSeats = value;
    }

    /**
     * Gets the value of the businessFreeSeats property.
     * 
     */
    public int getBusinessFreeSeats() {
        return businessFreeSeats;
    }

    /**
     * Sets the value of the businessFreeSeats property.
     * 
     */
    public void setBusinessFreeSeats(int value) {
        this.businessFreeSeats = value;
    }

    /**
     * Gets the value of the firstMaxSeats property.
     * 
     */
    public int getFirstMaxSeats() {
        return firstMaxSeats;
    }

    /**
     * Sets the value of the firstMaxSeats property.
     * 
     */
    public void setFirstMaxSeats(int value) {
        this.firstMaxSeats = value;
    }

    /**
     * Gets the value of the firstFreeSeats property.
     * 
     */
    public int getFirstFreeSeats() {
        return firstFreeSeats;
    }

    /**
     * Sets the value of the firstFreeSeats property.
     * 
     */
    public void setFirstFreeSeats(int value) {
        this.firstFreeSeats = value;
    }

}
