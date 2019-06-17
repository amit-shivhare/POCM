package com.sap.xi.xi.demo.agency;

/**
 * Exception class for service fault.
 */
@javax.xml.ws.WebFault(name = "FlightNotFound", targetNamespace = "http://sap.com/xi/XI/Demo/Airline", faultBean = "com.sap.xi.xi.demo.airline.FlightNotFound")
public class FlightNotFound extends java.lang.Exception {

  private com.sap.xi.xi.demo.airline.FlightNotFound _FlightNotFound;

  public FlightNotFound(String message, com.sap.xi.xi.demo.airline.FlightNotFound faultInfo){
    super(message);
    this._FlightNotFound = faultInfo;
  }

  public FlightNotFound(String message, com.sap.xi.xi.demo.airline.FlightNotFound faultInfo, Throwable cause){
    super(message, cause);
    this._FlightNotFound = faultInfo;
  }

  public com.sap.xi.xi.demo.airline.FlightNotFound getFaultInfo(){
    return this._FlightNotFound;
  }

}
