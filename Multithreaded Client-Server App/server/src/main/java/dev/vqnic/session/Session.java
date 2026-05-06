package dev.vqnic.session;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Session extends Thread { //Handle multiple sessions using threads.

  Socket socket;
  Dataset dataset; //the client's dataset associated with this session.

  public Session(Socket socket){
    this.socket = socket;
    this.dataset = new Dataset(); //Create a new dataset for each session
    System.out.println("New client connected!");
  }

  @Override
  public void run() {
    try {
      OutputStream output = socket.getOutputStream();
      ObjectOutputStream objectOutput = new ObjectOutputStream(output); //what we are sending to the client (output)

      InputStream input = socket.getInputStream();
      ObjectInputStream objectInput = new ObjectInputStream(input); //what the client is sending to us (input)

      while (socket.isConnected()) { //while the socket is still connected, see what it outputs to us
        String rawCommand = objectInput.readObject().toString(); //the raw input sent by the client
        String command = rawCommand.contains(" ") ? (rawCommand+" ").split(" ")[0] : rawCommand; //The first "word" of the command
        String args; //the arguments and extras that come after the command, used in dataset and search commands
        switch(command) {
          case "dataset":
            if(command.length() + 1 < rawCommand.length()) { //Make sure we actually have arguments to prevent indexoutofbounds
              args = rawCommand.substring(command.length() + 1);
              List<Integer> numbers = new ArrayList<>(); //collecting all the numbers that follow after dataset
              String[] arguments = args.split("\\s+"); //ONLY accept numbers and filter the rest out
              for (String arg : arguments) {
                try {
                  numbers.add(Integer.parseInt(arg));
                } //Straight up don't add non-integer numbers.
                catch (NumberFormatException ignored) {
                }
              } //make sure they're all numbers
              if (!numbers.isEmpty()) { //Make sure at least one number got past filtering.
                this.dataset.setDataset(numbers.toArray(new Integer[0])); //Set the dataset to the numbers we got from the command
                objectOutput.writeObject("Dataset received.\nYour dataset is now: " + dataset.toString());
              } else {
                objectOutput.writeObject("Dataset invalid. Only integers are accepted. Please create a dataset using dataset [numbers], for example: dataset 1 2 3 4 5");
              }
            }else{
              objectOutput.writeObject("Please create a dataset using dataset [numbers], for example: dataset 1 2 3 4 5"); //If they didn't put the args they needed
            }
            break;
          case "sort":
            if(dataset.isEmpty()) objectOutput.writeObject("Please create a dataset using dataset [numbers], for example: dataset 1 2 3 4 5");
            else {
              if(!dataset.isSorted()) {
                dataset.sort(); //if not sorted, sort it!
                objectOutput.writeObject("Dataset sorted using merge sort.\nYour dataset is now: " + dataset.toString());
              }else{ //already sorted, but show the user their dataset anyways.
                objectOutput.writeObject("Dataset has already been sorted.\nYour dataset is: " + dataset.toString());
              }
            }
            break;
          case "search":
            if(dataset.isEmpty()) objectOutput.writeObject("Please create a dataset using dataset [numbers], for example: dataset 1 2 3 4 5");
            else {
              if(command.length() + 1 < rawCommand.length()) { //Make sure we actually have arguments to prevent indexoutofbounds
                args = rawCommand.substring(command.length() + 1);
                try {
                  int toFind = Integer.parseInt(args);
                  int index = dataset.search(toFind);
                  if (index == -1)
                    objectOutput.writeObject("\"" + toFind + "\" is not in your dataset and could not be found!"); //wasn't found in the dataset
                  else objectOutput.writeObject("\"" + toFind + "\" was found in your dataset at index " + index);
                } catch (NumberFormatException ignored) {
                  objectOutput.writeObject("\"" + args + "\" is not an integer. You can only search the dataset for integers.");
                }
              }else{
                objectOutput.writeObject("Specify what integer you want to search for by doing search [value]."); //If they didn't put the args they needed
              }
            }
            break;
          case "stat":
            if(dataset.isEmpty()) objectOutput.writeObject("Please create a dataset using dataset [numbers], for example: dataset 1 2 3 4 5");
            else {
              objectOutput.writeObject("Count: " + dataset.count() + "\n" +
                  "Average: " + dataset.average() + "\n" +
                  "Minimum: " + dataset.minimum() + "\n" +
                  "Maximum: " + dataset.maximum() + "\n");
            }
            break;
          case "reset":
            if(dataset.isEmpty()) objectOutput.writeObject("Your dataset is already clear.");
            else {
              this.dataset.setDataset(null); //Set the dataset to the numbers we got from the command
              objectOutput.writeObject("Dataset cleared. You may use the dataset command again to create another dataset.");
            }
            break;
        }
      }
    } catch (Exception e) {
      //e.printStackTrace();
    }
  }
}
