package dev.vqnic.flightplanner;

import dev.vqnic.flightplanner.airplane.Airplane;
import dev.vqnic.flightplanner.airport.Airport;
import dev.vqnic.flightplanner.airport.RefuelType;

import java.util.ArrayList;
import java.util.Arrays;

public class FlightPlanner {
  private Airport initialOrigin; //The airport the user arrives at to start their trip
  private Airport finalDestination; //The end airport the user wants to finish their trip at.
  private ArrayList<Flight> flights; //All the individual flights in the trip that the user needs to do to get from their origin to their destination.
  private Airplane airplane; //The airplane the user selected to do this trip with.

  public void setOrigin(Airport origin) {
    this.initialOrigin = origin;
  }

  public void setDestination(Airport destination) {
    this.finalDestination = destination;
  }

  public void setAirplane(Airplane airplane) {
    this.airplane = airplane;
  }

  public Airplane getAirplane() {
    return this.airplane;
  }

  public Airport getOrigin() {
    return this.initialOrigin;
  }

  public Airport getDestination() {
    return this.finalDestination;
  }

  public ArrayList<Flight> getFlights() {
    return this.flights;
  }

  public boolean generateBestRoute() { //Returns false if the flight is impossible and sets the flights to null. It sets it to true if it is possible. Also updates the flights variable to be the best route.
    ArrayList<Airport> airports = Main.airportManager.getAirports();
    int[][] adjacencyMatrix = Main.airportManager.getAdjacencyMatrix();
    int originIndex = Main.airportManager.getIndexOfICAO(initialOrigin.getIcaoID());
    int destinationIndex = Main.airportManager.getIndexOfICAO(finalDestination.getIcaoID());

    // Dijkstra's algirithm, my beloved <3
    int airportsAmount = airports.size();
    final double INF = Double.POSITIVE_INFINITY;
    double[] distances = new double[airportsAmount];
    int[] previous = new int[airportsAmount];
    boolean[] visited = new boolean[airportsAmount];
    Arrays.fill(distances, INF);
    Arrays.fill(previous, -1);
    distances[originIndex] = 0;

    for (int i = 0; i < airportsAmount; i++) { //Iterate though all airports
      int currentAirport = -1;
      double best = INF; //Set it to an outrageously high number so any actual distance would be better.
      for (int j = 0; j < airportsAmount; j++) { //Iterate though all airports
        if (!visited[j] && distances[j] < best) { //If the airport hasn't been visited and is closer
          best = distances[j]; //Set it to our best option so far
          currentAirport = j;
        }
      }
      if (currentAirport == -1) break; // There aren't anymore reachable airports
      if (currentAirport == destinationIndex) break; //Already there!
      visited[currentAirport] = true; // Mark off we visited it to not try checking it again

      // Go through all the neighboring airports to see if we can get to them from our current airport based on fuel constraints
      for (int airport = 0; airport < airportsAmount; airport++) { 
        if (currentAirport == airport || visited[airport]) continue;

        int edgeLengthKm = adjacencyMatrix[currentAirport][airport]; //see how far the flight would be from the current airport to the vertex we're checking
        if (edgeLengthKm <= 0) continue; //ignore itself
        if (edgeLengthKm > airplane.getMaximumDistance()) continue; // ignore ones where it'd run out of fuel

        if (airport != destinationIndex) { // If we don't get there in one trip
          RefuelType[] offerings = airports.get(airport).getSupportedFuel(); 
          if (!this.airplane.canAcceptFuelOfferings(offerings)) continue;
        }

        double altDistance = distances[currentAirport] + edgeLengthKm; // Helps with finding the shortest path
        if (altDistance < distances[airport]) {
          distances[airport] = altDistance;
          previous[airport] = currentAirport;
        }
      }
    }

    if (Double.isInfinite(distances[destinationIndex])) { //If the destination was never reached (would still be positive infinity from above)
      this.flights = null;
      return false;  //Impossible trip, return false.
    }

    // Take the best path we just found using djasktra's and make it into a list of flights
    ArrayList<Integer> reversePath = new ArrayList<>();
    for (int i = destinationIndex; i != -1; i = previous[i]) { 
      reversePath.add(i);
      if (i == originIndex) break; // Made it back!
    }

    ArrayList<Flight> allFlights = new ArrayList<>();
    for (int i = reversePath.size() - 1; i > 0; i--) {
      Airport from = airports.get(reversePath.get(i));
      Airport to = airports.get(reversePath.get(i - 1));
      allFlights.add(new Flight(this.airplane, from, to));
    }
    this.flights = allFlights;
    return true;
  }

  public double getTotalDistance(){ //Combine the distance length of all flights
    double totalDistance = 0;
    for (Flight flight : this.flights) {
      totalDistance += flight.getDistance();
    }
    return totalDistance;
  }

  public double getTotalTime(){ //Combine the time of all flights
    double totalTime = 0;
    for (Flight flight : this.flights) {
      totalTime += flight.getTime();
    }
    return totalTime;
  }

  public static double getDistanceBetweenCoordinates(double latA, double lonA, double latB, double lonB) { //Haversine formula :)
    /*
    Haversine formula
    a = sin^2(change in latitude/2) + sin^2((change in longitude/2) * cos(latA) * cos(latB)
    c = 2 * arctan(sqrt(a), sqrt(1-a))
    Distance = radius of earth * c
     */
    double earthsRadius = 6371; //in kilos as the project wanted
    double latDifference = Math.toRadians(latB - latA); //working in radians
    double lonDifference = Math.toRadians(lonB - lonA);
    latA = Math.toRadians(latA);
    latB = Math.toRadians(latB);

    double a = Math.pow(Math.sin(latDifference / 2), 2) + Math.pow(Math.sin(lonDifference / 2), 2) * Math.cos(latA) * Math.cos(latB);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return earthsRadius * c;
  }
}
