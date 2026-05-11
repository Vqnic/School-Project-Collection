package dev.vqnic.flightplanner.airplane;

import dev.vqnic.flightplanner.airport.RefuelType;

import java.io.Serializable;

public class Airplane implements Serializable {
private String make;
  private String model;
  private AirplaneType type;
  private double fuelCapacity; //litres
  private double fuelBurnRate; //litres per hour
  private double airSpeed; //Knots


  public Airplane(String make, String model, AirplaneType type, double fuelCapacity, double fuelConsumption, double airSpeed) {
    this.make = make;
    this.model = model;
    this.type = type;
    this.fuelCapacity = fuelCapacity;
    this.fuelBurnRate = fuelConsumption;
    this.airSpeed = airSpeed;
  }

  @Override
  public String toString() {
    return "MAKE: " + make + "\n" +
        "MODEL: " + model + "\n" +
        "TYPE: " + type + "\n" +
        "FUEL CAPACITY: " + fuelCapacity + " litres\n" +
        "FUEL BURN RATE: " + fuelBurnRate + " litres/hour\n" +
        "AIR SPEED: " + airSpeed + " knots";
  }

  public String getMake() {
    return this.make;
  }

  public String getModel() {
    return this.model;
  }

  public AirplaneType getType() {
    return type;
  }

  public RefuelType getFuelType() {
    return switch (this.type) {
      case JET, TURBOPROP -> RefuelType.JA_A; //Jets and turboprops both use JA-a.
      case PROP -> RefuelType.AVGAS; //Only prop planes use AVGAS.
    };
  }

  public double getFuelCapacity() {
    return fuelCapacity;
  }

  public double getFuelConsumptionOverDistance(double distance) {
    return this.fuelBurnRate * distance;
  }

  public double getFuelBurnRate(){
    return this.fuelBurnRate;
  }

  public double getAirspeed(){ //In Knots
    return this.airSpeed;
  }

  public boolean canAcceptFuelOfferings(RefuelType[] fuelOfferings) { //Used to see if an airplane can accept what an airport offers
    for(RefuelType fuel : fuelOfferings) if(fuel == this.getFuelType()) return true;
    return false;
  }

  public double getMaximumDistance(){
    return (fuelCapacity / fuelBurnRate) * (airSpeed * 1.852);
  }
}
