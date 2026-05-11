package dev.vqnic.flightplanner.airplane;

import java.io.*;
import java.util.ArrayList;

public class AirplaneManager {

  private ArrayList<Airplane> airplanes = new ArrayList<>();

  public AirplaneManager() {
    //load Airplanes from file
    try (FileInputStream fileIn = new FileInputStream("airplanes.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn)) {
      airplanes = (ArrayList<Airplane>) in.readObject(); //Will ONLY ever be an arraylist of airplane
      System.out.println("Loaded " + airplanes.size() + " airplanes from a previous save!");
    } catch (IOException | ClassNotFoundException e ) { //Save file doesn't exist yet, so give them some default airports.
      airplanes = new ArrayList<>();
      airplanes.add(new Airplane("Boeing", "777", AirplaneType.TURBOPROP, 48000, 1980, 556));
      airplanes.add(new Airplane("Boeing", "747", AirplaneType.JET, 15698, 1795, 256));
      airplanes.add(new Airplane("Airbus", "A320", AirplaneType.JET, 24000, 3125, 485));

      saveToFile();
      //e.printStackTrace();
    }
  }

  public void add(Airplane airplane) {
    airplanes.add(airplane);
    saveToFile();
  }

  public void delete(int airplaneIndex) {
    Airplane target = airplanes.get(airplaneIndex);
    airplanes.remove(target);
    saveToFile();
  }

  public ArrayList<Airplane> getAirplanes() {
    return this.airplanes;
  }


  public void saveToFile(){
    try (FileOutputStream fileOut = new FileOutputStream("airplanes.ser");
       ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
      out.writeObject(airplanes);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
