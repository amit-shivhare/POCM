
package com.sap.xi.xi.demo.airline;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import com.sap.xi.xi.demo.agency.FlightSeatAvailabilityQuery;
import com.sap.xi.xi.demo.agency.FlightSeatAvailabilityResponse;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.sap.xi.xi.demo.airline package. 
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

    private final static QName _FlightSeatAvailabilityResponse_QNAME = new QName("http://sap.com/xi/XI/Demo/Airline", "FlightSeatAvailabilityResponse");
    private final static QName _FlightSeatAvailabilityQuery_QNAME = new QName("http://sap.com/xi/XI/Demo/Airline", "FlightSeatAvailabilityQuery");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.sap.xi.xi.demo.airline
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FlightNotFound }
     * 
     */
    public FlightNotFound createFlightNotFound() {
        return new FlightNotFound();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FlightSeatAvailabilityResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sap.com/xi/XI/Demo/Airline", name = "FlightSeatAvailabilityResponse")
    public JAXBElement<FlightSeatAvailabilityResponse> createFlightSeatAvailabilityResponse(FlightSeatAvailabilityResponse value) {
        return new JAXBElement<FlightSeatAvailabilityResponse>(_FlightSeatAvailabilityResponse_QNAME, FlightSeatAvailabilityResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FlightSeatAvailabilityQuery }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sap.com/xi/XI/Demo/Airline", name = "FlightSeatAvailabilityQuery")
    public JAXBElement<FlightSeatAvailabilityQuery> createFlightSeatAvailabilityQuery(FlightSeatAvailabilityQuery value) {
        return new JAXBElement<FlightSeatAvailabilityQuery>(_FlightSeatAvailabilityQuery_QNAME, FlightSeatAvailabilityQuery.class, null, value);
    }

}
