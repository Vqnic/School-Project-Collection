package dev.vqnic;

import dev.vqnic.session.Session;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  public static void main(String[] args) {
    try (ServerSocket serverSocket = new ServerSocket(12345)) {
      System.out.println("Server is listening ...");
      while (true) {
        Socket clientSocket = serverSocket.accept();
        Session clientSession = new Session(clientSocket); //make a thread for this new session
        clientSession.start(); //start the thread
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}