package dev.vqnic.flightplanner;

import dev.vqnic.flightplanner.airplane.Airplane;
import dev.vqnic.flightplanner.airplane.AirplaneManager;
import dev.vqnic.flightplanner.airplane.AirplaneType;
import dev.vqnic.flightplanner.airport.Airport;
import dev.vqnic.flightplanner.airport.AirportManager;
import dev.vqnic.flightplanner.airport.RefuelType;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
  public static AirplaneManager airplaneManager; //All airplanes in file.
  public static AirportManager airportManager; //Manages all airport save files.

  public static FlightPlanner flightPlanner;

  public static void main(String[] args) {
    airportManager = new AirportManager();
    airplaneManager = new AirplaneManager();
    handleUserInput();
  }

  public static void handleUserInput(){
    Scanner scanner = new Scanner(System.in);
    String commandInput = "";


    System.out.println("THIS SOFTWARE IS NOT TO BE USED FOR FLIGHT PLANNING OR NAVIGATIONAL PURPOSE");
    String helpMessage = "\nLIST OF COMMANDS:\n" +
      "planflight - Plan a flight\n" +
      "manageairplanes - Edit the Airplanes Database\n" +
      "manageairports - Edit the Airports Database\n";
    System.out.println(helpMessage);

    ArrayList<Airplane> airplanes = airplaneManager.getAirplanes();
    ArrayList<Airport> airports = airportManager.getAirports();

    while(true){
      commandInput = scanner.nextLine();
      switch(commandInput.toLowerCase()){
        case "planflight": //Plan a flight
          flightPlanner = new FlightPlanner();
          if(airplanes.isEmpty()) {
            System.out.println("There aren't any airplanes in the database to delete!");
            break;
          }
          if(airports.size() < 2) {
            System.out.println("There must be at least two airports in the database to plan a flight!");
            break;
          }
          System.out.println("Let's start planning your flight. Type 'exit' to exit to the main menu.");
          boolean confirmed = false;
          while(!commandInput.toLowerCase().equals("exit")) {
            //SELECT THE AIRPLANE
            while (flightPlanner.getAirplane() == null || !confirmed) {
              if (flightPlanner.getAirplane() == null) {
                System.out.println("Please input the ID of the airplane you want to use. \n" +
                    "AVAILABLE OPTIONS ARE:");
                //get the list of airplanes
                airplanes = airplaneManager.getAirplanes();
                listAirplanes(airplanes);
                try {
                  Integer airplaneIndex = Integer.parseInt(scanner.nextLine());
                  if (airplaneIndex >= 1 && airplaneIndex <= airplanes.size()) {
                    Airplane airplane = airplanes.get(airplaneIndex - 1);
                    flightPlanner.setAirplane(airplane);
                    System.out.println("SELECTED AIRPLANE: " + airplane.getMake() + " " + airplane.getModel());
                  } else {
                    System.out.println("INVALID INPUT. Please input a number corresponding to the ID of the airplane you want to use.\n");
                  }
                } catch (NumberFormatException e) {
                  System.out.println("INVALID INPUT. Please input a number corresponding to the ID of the airplane you want to use.\n");
                }
              }
              System.out.print("Is this airplane correct? (Y/N)\n");
              String confirmation = scanner.nextLine().toString();
              if (confirmation.equalsIgnoreCase("Y"))
                confirmed = true;
              else if (confirmation.equalsIgnoreCase("N")) {
                flightPlanner.setAirplane(null);
                System.out.println("Let's select the airplane again.");
              } else
                System.out.println("INVALID INPUT. Please input Y for yes or N for no.\n");
            }


            //SELECT THE ORIGIN AIRPORT
            confirmed = false; //reset the value
            while (flightPlanner.getOrigin() == null || !confirmed) {
              if (flightPlanner.getOrigin() == null)
                selectAirport(scanner, true);
              if (flightPlanner.getOrigin() != null) { //only confirm if they successfully picked one above
                System.out.print("Is this airport correct? (Y/N)\n");
                String confirmation = scanner.nextLine().toString();
                if (confirmation.equalsIgnoreCase("Y"))
                  confirmed = true;
                else if (confirmation.equalsIgnoreCase("N")) {
                  flightPlanner.setOrigin(null);
                  System.out.println("Let's select the origin again.");
                } else
                  System.out.println("INVALID INPUT. Please input Y for yes or N for no.\n");
              }
            }

            //SELECT THE DESTINATION AIRPORT
            confirmed = false; //reset the value
            while (flightPlanner.getDestination() == null || !confirmed) {
              if (flightPlanner.getDestination() == null)
                selectAirport(scanner, false);
              if (flightPlanner.getDestination() != null) { //only confirm if they successfully picked one above
                System.out.print("Is this airport correct? (Y/N)");
                String confirmation = scanner.nextLine().toString();
                if (confirmation.equalsIgnoreCase("Y"))
                  confirmed = true;
                else if (confirmation.equalsIgnoreCase("N")) {
                  flightPlanner.setDestination(null);
                  System.out.println("Let's select the destination again.");
                } else
                  System.out.println("Invalid input. Please input Y for yes or N for no.\n");
              }
            }
            confirmed = false;
            System.out.println("\n" +
                "SELECTED AIRPLANE:\n" + flightPlanner.getAirplane().toString() + "\n" +
                "\nSELECTED ORIGIN:\n" + flightPlanner.getOrigin().toString() + "\n" +
                "\nSELECTED DESTINATION:\n" + flightPlanner.getDestination().toString() + "\n");
            if(flightPlanner.generateBestRoute()){ //returns true if possible, false if not
              System.out.println("GENERATING ROUTE WITH " + (flightPlanner.getFlights().size() + 1) + " STOPS...");
              System.out.println("TOTAL TIME (ALL FLIGHTS) " + (Math.round(flightPlanner.getTotalTime() * 100.0) / 100.0) + " hours");
              System.out.println("TOTAL DISTANCE (ALL FLIGHTS) " + (Math.round(flightPlanner.getTotalDistance() * 100.0) / 100.0) + " km");
              for(Flight flight : flightPlanner.getFlights()){
                System.out.println("\n" + flight.toString());
              }
            } else System.out.println("The flight provided is impossible with your current airplane and airports due to lack of refueling stops.\n");
            flightPlanner = null; //reset the planner
            break;
          }
          System.out.println(helpMessage);
          break;



        case "manageairplanes": //Edit the Airplanes Database
          String airplanesHelpMessage = "LIST OF COMMANDS:\n" +
              "list - List all airplanes\n" +
              "add - Add an airplane\n" +
              "delete - Delete an airplane entry";
          airplanesMenu:
          while(!commandInput.toLowerCase().equals("exit")) {
            System.out.println("Type 'exit' to exit to the main menu.\n" + airplanesHelpMessage);
            commandInput = scanner.nextLine();
            switch(commandInput.toLowerCase()){
              case "list":
                airplanes = airplaneManager.getAirplanes();
                listAirplanes(airplanes);
                break;
              case "add":
                System.out.println("You are now entering an airplane. Follow the instructions provided or type 'exit' to exit to the main menu.");
                confirmed = false;
                String make = null; //airplane traits we need before we add it
                String model = null;
                AirplaneType type = null;
                double fuelCapacity = 0;
                double fuelConsumption = 0;
                double airSpeed = 0;
                while(!confirmed) {
                  while (make == null) {
                    System.out.println("Please input the make of the airplane you want to add.\n");
                    commandInput = scanner.nextLine();
                    if (commandInput.toLowerCase().equals("exit")) break airplanesMenu;
                    if (commandInput.length() >= 3) make = commandInput;
                    else System.out.println("INVALID INPUT. Please input a make that is at least 3 characters long.\n");
                  }
                  while (model == null) {
                    System.out.println("Please input the model of the airplane you want to add.\n");
                    commandInput = scanner.nextLine();
                    if (commandInput.toLowerCase().equals("exit")) break airplanesMenu;
                    if (commandInput.length() >= 3) model = commandInput;
                    else
                      System.out.println("INVALID INPUT. Please input a model that is at least 3 characters long.\n");
                  }
                  while (type == null) {
                    System.out.println("Please input the ID of the type of the airplane you want to add.\n" +
                        "[ID: 1] JET\n" +
                        "[ID: 2] PROP\n" +
                        "[ID: 3] TURBOPROP\n");
                    commandInput = scanner.nextLine();
                    if (commandInput.toLowerCase().equals("exit")) break airplanesMenu;
                    if (commandInput.equals("1")) type = AirplaneType.JET;
                    else if (commandInput.equals("2")) type = AirplaneType.PROP;
                    else if (commandInput.equals("3")) type = AirplaneType.TURBOPROP;
                    else
                      System.out.println("INVALID INPUT. input the ID of the type of the airplane you want to add.\n");
                  }
                  while (fuelCapacity == 0) {
                    System.out.println("Please input the fuel capacity (in litres) of the airplane you want to add.\n");
                    commandInput = scanner.nextLine();
                    if (commandInput.toLowerCase().equals("exit")) break airplanesMenu;
                    try {
                      if (Double.parseDouble(commandInput) > 0) fuelCapacity = Double.parseDouble(commandInput);
                      else System.out.println("INVALID INPUT. Please input a positive fuel capacity.\n");
                    } catch (NumberFormatException e) {
                      System.out.println("INVALID INPUT. Please input a positive number for the fuel capacity.\n");
                    }
                  }
                  while (fuelConsumption == 0) {
                    System.out.println("Please input the fuel consumption (in litres) of the airplane you want to add.\n");
                    commandInput = scanner.nextLine();
                    if (commandInput.toLowerCase().equals("exit")) break airplanesMenu;
                    try {
                      if (Double.parseDouble(commandInput) > 0) fuelConsumption = Double.parseDouble(commandInput);
                      else System.out.println("INVALID INPUT. Please input a positive fuel consumption.\n");
                    } catch (NumberFormatException e) {
                      System.out.println("INVALID INPUT. Please input a positive number for fuel consumption.\n");
                    }
                  }
                  while (airSpeed == 0) {
                    System.out.println("Please input the air speed (in knots) of the airplane you want to add.\n");
                    commandInput = scanner.nextLine();
                    if (commandInput.toLowerCase().equals("exit")) break airplanesMenu;
                    try {
                      if (Double.parseDouble(commandInput) > 0) airSpeed = Double.parseDouble(commandInput);
                      else System.out.println("INVALID INPUT. Please input a positive number for airspeed.\n");
                    } catch (NumberFormatException e) {
                      System.out.println("INVALID INPUT. Please input a positive number for airspeed.\n");
                    }
                  }
                  Airplane airplane = new Airplane(make, model, type, fuelCapacity, fuelConsumption, airSpeed);
                  System.out.println("PROVIDED AIRPLANE: " + airplane + "\n");
                  System.out.print("Is this airplane correct? (Y/N)\n");
                  String confirmation = scanner.nextLine().toString();
                  if (confirmation.equalsIgnoreCase("Y")) {
                    System.out.println(airplane.getMake() + " " + airplane.getModel() + " added!\n");
                    airplaneManager.add(airplane);
                    confirmed = true;
                  }else if (confirmation.equalsIgnoreCase("N")) {
                    System.out.println("Let's try adding an airplane again.");
                  } else
                    System.out.println("INVALID INPUT. Please input Y for yes or N for no.\n");
                }
                break;

              case "delete":
                confirmed = false;
                if(airplanes.isEmpty()) {
                  System.out.println("There aren't any airplanes in the database to delete!");
                  break airplanesMenu;
                }
                while(!confirmed) {
                  System.out.println("Follow the instructions provided or type 'exit' to exit to the main menu.");
                  System.out.println("Please input the ID of the airplane you want to delete:\nAVAILABLE OPTIONS ARE:");
                  airplanes = airplaneManager.getAirplanes();
                  listAirplanes(airplanes);

                  Airplane airplane = null;
                  Integer airplaneIndex = 0;
                  while(airplane == null) {
                    if (commandInput.toLowerCase().equals("exit")) break airplanesMenu;
                    try {
                      airplaneIndex = Integer.parseInt(scanner.nextLine());
                      if (airplaneIndex >= 1 && airplaneIndex <= airplanes.size()) {
                        airplane = airplanes.get(airplaneIndex - 1);
                        System.out.println("SELECTED AIRPLANE: " + airplane.getMake() + " " + airplane.getModel());
                      } else {
                        System.out.println("INVALID INPUT. Please input a number corresponding to the ID of the airplane you want to delete.\n");
                      }
                    } catch (NumberFormatException e) {
                      System.out.println("INVALID INPUT. Please input a number corresponding to the ID of the airplane you want to delete.\n");
                    }
                  }
                  System.out.print("Is this airplane correct? (Y/N)\n");
                  String confirmation = scanner.nextLine().toString();
                  if (confirmation.equalsIgnoreCase("Y")) {
                    confirmed = true;
                    airplaneManager.delete(airplaneIndex - 1);
                  }else if (confirmation.equalsIgnoreCase("N")) {
                    airplane = null;
                    System.out.println("Let's select the airplane again.");
                  } else
                    System.out.println("INVALID INPUT. Please input Y for yes or N for no.\n");
                }
                System.out.println(airplanesHelpMessage);
                break;
            }
          }
          System.out.println(helpMessage);
          break;


        case "manageairports": //Edit the Airports Database
          String airportsHelpMessage = "LIST OF COMMANDS:\n" +
              "list - List all airports\n" +
              "add - Add an airport\n" +
              "delete - Delete an airport entry";
          airportsMenu:
          while(!commandInput.toLowerCase().equals("exit")) {
            System.out.println("Type 'exit' to exit to the main menu.\n" + airportsHelpMessage);
            commandInput = scanner.nextLine();
            switch(commandInput.toLowerCase()){
              case "list":
                airports = airportManager.getAirports();
                listAirports(airports);
                break;

              case "add":
                System.out.println("You are now entering an airport. Follow the instructions provided or type 'exit' to exit to the main menu.");
                confirmed = false;
                String name = null; //airport traits we need before we add it
                String icaoID = null;
                double latitude = 0;
                double longitude = 0;
                RefuelType[] fueltypes = null;
                while(!confirmed) {
                  while (name == null) {
                    System.out.println("Please input the name of the airport you want to add.\n");
                    commandInput = scanner.nextLine();
                    if (commandInput.toLowerCase().equals("exit")) break airportsMenu;
                    if (commandInput.length() >= 3) name = commandInput;
                    else System.out.println("INVALID INPUT. Please input a name that is at least 3 characters long.\n");
                  }
                  while (icaoID == null) {
                    System.out.println("Please input the ICAO of the airport you want to add.\n");
                    commandInput = scanner.nextLine();
                    if (commandInput.toLowerCase().equals("exit")) break airportsMenu;
                    if (commandInput.length() >= 3) icaoID = commandInput;
                    else
                      System.out.println("INVALID INPUT. Please input a model that is at least 3 characters long.\n");
                  }
                  while (latitude == 0) {
                    System.out.println("Please input the latitude (in decimal degrees) of the airport you want to add.\n");
                    commandInput = scanner.nextLine();
                    if (commandInput.toLowerCase().equals("exit")) break airportsMenu;
                    try {
                      if (Double.parseDouble(commandInput) <= 90 && Double.parseDouble(commandInput) >= -90) latitude = Double.parseDouble(commandInput); //can be between 90 and -90
                      else System.out.println("INVALID INPUT. Please make sure you enter a value between -90 and 90 for latitude.\n");
                    } catch (NumberFormatException e) {
                      System.out.println("INVALID INPUT. Please make sure you enter a value between -90 and 90 for latitude.\n");
                    }
                  }
                  while (longitude == 0) {
                    System.out.println("Please input the longitude (in decimal degrees) of the airport you want to add.\n");
                    commandInput = scanner.nextLine();
                    if (commandInput.toLowerCase().equals("exit")) break airportsMenu;
                    try {
                      if (Double.parseDouble(commandInput) <= 180 && Double.parseDouble(commandInput) >= -180) longitude = Double.parseDouble(commandInput); //can be between 90 and -90
                      else System.out.println("INVALID INPUT. Please make sure you enter a value between -180 and 180 for longitude.\n");
                    } catch (NumberFormatException e) {
                      System.out.println("INVALID INPUT. Please make sure you enter a value between -180 and 180 for longitude.\n");
                    }
                  }


                  while (fueltypes == null) {
                    System.out.println("Please input the ID of the type of the airplane you want to add.\n" +
                        "[ID: 1] AVGAS\n" +
                        "[ID: 2] JA-A\n" +
                        "[ID: 3] BOTH\n");
                    commandInput = scanner.nextLine();
                    if (commandInput.toLowerCase().equals("exit")) break airportsMenu;
                    if (commandInput.equals("1")) fueltypes = new RefuelType[]{RefuelType.AVGAS};
                    else if (commandInput.equals("2")) fueltypes = new RefuelType[]{RefuelType.JA_A};
                    else if (commandInput.equals("3")) fueltypes = new RefuelType[]{RefuelType.AVGAS, RefuelType.JA_A};
                    else
                      System.out.println("INVALID INPUT. Please enter a provided ID.\n");
                  }

                  Airport airport = new Airport(name, icaoID, latitude, longitude, fueltypes);
                  System.out.println("PROVIDED AIRPORT: " + airport + "\n");
                  System.out.print("Is this airport correct? (Y/N)\n");
                  String confirmation = scanner.nextLine().toString();
                  if (confirmation.equalsIgnoreCase("Y")) {
                    System.out.println(airport.getName() + " (" + airport.getIcaoID() + ") added!\n");
                    airportManager.add(airport);
                    confirmed = true;
                  }else if (confirmation.equalsIgnoreCase("N")) {
                    System.out.println("Let's try adding an airport again.");
                  } else
                    System.out.println("INVALID INPUT. Please input Y for yes or N for no.\n");
                }
                break;

              case "delete":
                confirmed = false;
                if(airports.isEmpty()) {
                  System.out.println("There aren't any airplanes in the database to delete!");
                  break airportsMenu;
                }
                while(!confirmed) {
                  System.out.println("Follow the instructions provided or type 'exit' to exit to the main menu.");
                  System.out.println("Please input the ID of the airport you want to delete:\nAVAILABLE OPTIONS ARE:");
                  airports = airportManager.getAirports();
                  listAirports(airports);

                  Airport airport = null;
                  Integer airportIndex = 0;
                  while(airport == null) {
                    if (commandInput.toLowerCase().equals("exit")) break airportsMenu;
                    try {
                      airportIndex = Integer.parseInt(scanner.nextLine());
                      if (airportIndex >= 1 && airportIndex <= airplanes.size()) {
                        airport = airports.get(airportIndex - 1);
                        System.out.println("SELECTED AIRPLANE: " + airport.getName());
                      } else {
                        System.out.println("INVALID INPUT. Please input a number corresponding to the ID of the airport you want to delete.\n");
                      }
                    } catch (NumberFormatException e) {
                      System.out.println("INVALID INPUT. Please input a number corresponding to the ID of the airport you want to delete.\n");
                    }
                  }
                  System.out.print("Is this airport correct? (Y/N)\n");
                  String confirmation = scanner.nextLine().toString();
                  if (confirmation.equalsIgnoreCase("Y")) {
                    confirmed = true;
                    airportManager.delete(airportIndex - 1);
                  }else if (confirmation.equalsIgnoreCase("N")) {
                    airport = null;
                    System.out.println("Let's select the airport again.");
                  } else
                    System.out.println("INVALID INPUT. Please input Y for yes or N for no.\n");
                }
                break;
            }
          }
          System.out.println(helpMessage);
          break;

        default:
          System.out.println("UNKNOWN COMMAND.\n" + helpMessage);
      }
    }
  }

  public static void selectAirport(Scanner scanner, boolean isOrigin){
    System.out.println("Please input the ID of the " + (isOrigin ? "origin" : "destination") + " you want to use. \n" + "AVAILABLE OPTIONS ARE:");
    // get the list of airports
    ArrayList<Airport> airports = airportManager.getAirports();
    listAirports(airports);
    try {
      Integer airportIndex = Integer.parseInt(scanner.nextLine());
      if (airportIndex >= 1 && airportIndex <= airports.size()) {
        Airport airport = airports.get(airportIndex - 1);
        if (isOrigin)
          flightPlanner.setOrigin(airport);
        else
          flightPlanner.setDestination(airport);
        if (flightPlanner.getOrigin().equals(flightPlanner.getDestination())) {
          if (isOrigin)
            flightPlanner.setOrigin(null);
          else
            flightPlanner.setDestination(null);
          System.out.println("INVALID INPUT. Origin and destination cannot be the same. Please select a different " + (isOrigin ? "origin" : "destination") + ".");
        } else {
          if (isOrigin)
            flightPlanner.setOrigin(airport);
          else
            flightPlanner.setDestination(airport);
          System.out.println("SELECTED " + (isOrigin ? "ORIGIN" : "DESTINATION") + "AIRPORT: " + airport.getName());
        }
      } else {
        System.out.println("INVALID INPUT. Please input a number corresponding to the ID of the airport you want to use.");
      }
    } catch (NumberFormatException e) {
      System.out.println("INVALID INPUT. Please input a number corresponding to the ID of the airport you want to use.");
    }
  }

  public static void listAirplanes(ArrayList<Airplane> airplanes){
    int i = 1; //Start it at 1 instead of 0
    for (Airplane airplane : airplanes) {
      System.out.println("[ID: " + i + "] " + airplane.getMake() + " " + airplane.getModel());
      i++;
    }
  }

  public static void listAirports(ArrayList<Airport> airports){
    int i = 1; //Start it at 1 instead of 0
    for (Airport airport : airports) {
      System.out.println("[ID: " + i + "] " + airport.getName());
      i++;
    }
  }
}
