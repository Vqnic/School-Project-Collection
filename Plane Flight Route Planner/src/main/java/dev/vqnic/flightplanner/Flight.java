package dev.vqnic.flightplanner;

import dev.vqnic.flightplanner.airplane.Airplane;
import dev.vqnic.flightplanner.airport.Airport;

//An individual flight which can be part of a greater trip
public class Flight {
  private Airplane airplane;
  private final Airport origin;
  private final Airport destination;

  public Flight(Airplane airplane, Airport origin, Airport destination) {
    this.airplane = airplane;
    this.origin = origin;
    this.destination = destination;
  }

  public double getHeading() { //Degrees
    double latA = origin.getLatitude();
    double latB = destination.getLatitude();
    double lonA = origin.getLongitude();
    double lonB = destination.getLongitude();
    //Only works if the real angle is less than 180.
    double heading = Math.acos((latB - latA)/Math.sqrt(Math.pow(lonB - lonA, 2) + Math.pow(latB - latA, 2)));
    //If the destination is to the left of the origin, the angle needs to be corrected as (360 - heading).
    if(lonB < lonA) heading = 360 - heading; //Correction just in case.
    return heading;
  }

  public double getTime() { //Hours
    return this.getDistance()/(airplane.getAirspeed() * 1.852 /*conversion to km*/);
  }

  public double getDistance() { //Kilometers
    return FlightPlanner.getDistanceBetweenCoordinates(origin.getLatitude(), origin.getLongitude(), destination.getLatitude(), destination.getLongitude());
  }

  @Override
  public String toString(){
    return "FLIGHT DETAILS (" + origin.getName() + " -> " + destination.getName() + ")\n" +
        "HEADING: " + Math.round(getHeading() * 100.0) / 100.0 + "\n" + //two decimal places
        "TIME: " + Math.round(getTime() * 100.0) / 100.0 + " hours\n" +
        "DISTANCE: " + Math.round(getDistance() * 100.0) / 100.0 + " km";
  }
}
