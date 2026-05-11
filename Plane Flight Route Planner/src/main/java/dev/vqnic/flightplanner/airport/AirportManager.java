package dev.vqnic.flightplanner.airport;

import dev.vqnic.flightplanner.FlightPlanner;

import java.io.*;
import java.util.ArrayList;

public class AirportManager {

  private ArrayList<Airport> airports;
  private int[][] adjacencyMatrix;

  @SuppressWarnings("unchecked")
  public AirportManager() {
    //load Airports from file
    try (FileInputStream fileIn = new FileInputStream("airports.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn)) {
      airports = (ArrayList<Airport>) in.readObject(); //Will ONLY ever be an arraylist of airports
      System.out.println("Loaded " + airports.size() + " airports from a previous save!");
    } catch (IOException | ClassNotFoundException e ) { //Save file doesn't exist yet, so give them some default airports.
      airports = new ArrayList<>();
      airports.add(new Airport("Columbia Metro Airport", "KCAE", 33.93, -81, new RefuelType[]{RefuelType.AVGAS, RefuelType.JA_A})); //US
      airports.add(new Airport("Augusta State Airport", "KAUG", 33.36, -81.96, new RefuelType[]{RefuelType.AVGAS, RefuelType.JA_A}));
      airports.add(new Airport("Washington Dulles International Airport", "KIAD", 38.95, -77.45, new RefuelType[]{RefuelType.AVGAS, RefuelType.JA_A}));
      airports.add(new Airport("John F. Kennedy International Airport", "KJFK", 40.64, -73.94, new RefuelType[]{RefuelType.AVGAS, RefuelType.JA_A}));
      airports.add(new Airport("Richmond Virginia International Airport", "KRIC", 37.51, -77.32, new RefuelType[]{RefuelType.AVGAS, RefuelType.JA_A}));
      airports.add(new Airport("Dallas Fort Worth International Airport", "KDFW", 32.89, -97.04, new RefuelType[]{RefuelType.AVGAS, RefuelType.JA_A}));
      airports.add(new Airport("Phoenix Sky Harbor International Airport", "KPHX", 33.43, -112.0, new RefuelType[]{RefuelType.AVGAS, RefuelType.JA_A}));  
      airports.add(new Airport("Los Angeles International Airport", "KLAX", 33.94, -118.41, new RefuelType[]{RefuelType.AVGAS, RefuelType.JA_A}));  
      airports.add(new Airport("Toronto Pearson International Airport", "CYYZ", 43.67, -79.62, new RefuelType[]{RefuelType.AVGAS, RefuelType.JA_A}));    //Canada
      airports.add(new Airport("Abbotsford International Airport", "CYYX", 49.02, -122.36, new RefuelType[]{RefuelType.AVGAS, RefuelType.JA_A}));
      airports.add(new Airport("Mexico City International Airport Benito Juárez", "MMMX", 19.436, -99.072, new RefuelType[]{RefuelType.AVGAS, RefuelType.JA_A}));

      saveToFile();
      //e.printStackTrace();
    }
    generateAdjacencyMatrix(); //Generate the adjacency matrix after we've added all the airports.
  }

  public void add(Airport airport) {
    if(getIndexOfICAO(airport.getIcaoID()) == -1){ //Make sure it doesn't already exist (it would spit out a -1 if it didn't exist)
      airports.add(airport);

      saveToFile();
      generateAdjacencyMatrix();
    }
  }

  public void delete(int airplaneIndex) {
    Airport target = airports.get(airplaneIndex);
    airports.remove(target);
    saveToFile();
  }

  public ArrayList<Airport> getAirports(){
    return this.airports;
  }

  public void saveToFile(){
    try (FileOutputStream fileOut = new FileOutputStream("airports.ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
      out.writeObject(airports);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void generateAdjacencyMatrix() { //Updated once an airport is added or deleted
    int size = airports.size();
    this.adjacencyMatrix = new int[size][size];
    for(int i = 0; i < size; i++){
      for(int j = 0; j < size; j++){
        if(i == j) adjacencyMatrix[i][j] = 0;
        else adjacencyMatrix[i][j] = (int) FlightPlanner.getDistanceBetweenCoordinates(airports.get(i).getLatitude(), airports.get(i).getLongitude(), airports.get(j).getLatitude(), airports.get(j).getLongitude());
      }
    }
  }

  public int[][] getAdjacencyMatrix() {
    return this.adjacencyMatrix;
  }

  public int getIndexOfICAO(String icaoID) { //Used to connect the adjacency matrix to the airports list
    int i = 0;
    for(Airport airport : airports){
      if(airport.getIcaoID().equals(icaoID)) { return i; }
      i++;
    }
    return -1; //Not found.
  }
}
