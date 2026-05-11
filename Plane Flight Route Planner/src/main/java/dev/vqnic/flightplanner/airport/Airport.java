package dev.vqnic.flightplanner.airport;

import java.io.Serializable;

public class Airport implements Serializable {
  private String name;
  private String icaoID;
  private double latitude;
  private double longitude;
  private RefuelType[] supportedFuel; //Supported planes by fuel type

  public Airport(String name, String icaoID, double latitude, double longitude, RefuelType[] fueltypes) {
    this.name = name;
    this.icaoID = icaoID;
    this.latitude = latitude;
    this.longitude = longitude;
    this.supportedFuel = fueltypes;
  }

  @Override
  public String toString() {
    String fuelTypes = "";
    for(RefuelType fuelType : supportedFuel )
      fuelTypes = fuelTypes + fuelType.toString() + " ";
    return "NAME: " + name + "\n" +
        "ICAO ID: " + icaoID + "\n" +
        "LATITUDE: " + Math.abs(latitude) + " degrees " + (latitude > 0 ? "North" : "South") + "\n" +
        "LONGITUDE: " + Math.abs(longitude) + " degrees " + (longitude > 0 ? "East" : "West") + "\n" +
        "SUPPORTED FUEL TYPES: " + fuelTypes;
  }

  public String getIcaoID(){
    return icaoID;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public RefuelType[] getSupportedFuel() { return supportedFuel; }

  public String getName() {
    return this.name;
  }
}
