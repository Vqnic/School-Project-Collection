package dev.vqnic;

import java.io.*;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

  public static void main(String[] args) {
    try (Socket socket = new Socket("localhost", 12345)) {
      OutputStream output = socket.getOutputStream();
      ObjectOutputStream toSend = new ObjectOutputStream(output); //what we are sending to the server (output)
      InputStream input = socket.getInputStream();
      ObjectInputStream result = new ObjectInputStream(input); //what the server is sending to us (input)


      Scanner scanner = new Scanner(System.in);
      String commandInput = "";

      String helpMessage = "LIST OF ALL COMMANDS\n" +
          "dataset - Sends a dataset of integers to the server\n" +
          "sort - Sorts the dataset\n" +
          "search - Search for a specific value in the dataset\n" +
          "stat - Computes count, minimum, maximum and average\n" +
          "reset - Clears the current dataset\n" +
          "exit - Disconnect from the server and close the program\n" +
          "help - See a list of all available commands\n";

      System.out.println(helpMessage);
      boolean connected = true; //used for letting the user run commands as long as they are connected
      while (connected){
        commandInput = scanner.nextLine();
        String command =  commandInput.contains(" ") ? (commandInput+" ").split(" ")[0] : commandInput; //Get the first word from the command regardless as to if it has arguments
        switch(command.toLowerCase()) {
          case "dataset": //sends a dataset of integers to the server
          case "sort": //sorts the dataset
          case "search": //search for a specific value in the dataset
          case "stat": //computes count, minimum, maximum and average
          case "reset": //clears the current dataset
            toSend.writeObject(commandInput); //include the args
            System.out.println(result.readObject());
            break;
          case "exit":
            output.close();
            input.close();
            socket.close();
            connected = false;
            break;
          case "help": //send a list of all commands
            System.out.println(helpMessage);
            break;
          default:
            System.out.println("Invalid command! Run help for a list of commands.");
            break;
        }
      }
      scanner.close();
    } catch (Exception e) {
      System.out.println("Issue communicating with server!");
    }
  }
}