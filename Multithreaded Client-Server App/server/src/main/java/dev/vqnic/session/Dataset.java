package dev.vqnic.session;

import dev.vqnic.task.BinarySearchTask;
import dev.vqnic.task.MergeSortTask;

import java.util.concurrent.ForkJoinPool;

public class Dataset { //The user's session

  private Integer[] dataset = null; //The user's dataset or null if not set
  private boolean sorted = false; //If the dataset is sorted yet
  ForkJoinPool commonPool = ForkJoinPool.commonPool(); //Common pool for tasks

  public void setDataset(Integer[] dataset){ //Setting the dataset
    this.dataset = dataset;
    this.sorted = false; //Make sure to set it back to false so they sort it again
  }

  public boolean isEmpty(){ //Check if the dataset has any values in it at all
    return dataset == null || count() <= 0;
  }

  public int search(int target){
    if(dataset == null){ //Make sure the dataset is set
      return -1; //Returns -1 if it isn't in the dataset.
    }
    if(isSorted()) //Do a binary search if it's already sorted
      return new BinarySearchTask(target, dataset, 0, count()-1).invoke();
    else{ //do a linear search if it's not sorted
      for(int i = 0; i < count(); i++){
        if(dataset[i] == target){
          return i;
        }
      }
      return -1; //Returns -1 if it isn't in the dataset.
    }
  }

  public void sort(){
    dataset = new MergeSortTask(dataset, 0, count() -1).invoke(); //Give it the array, the leftmost index and the rightmost index.
    sorted = true; //Make sure to mark the dataset as already sorted
  }

  public boolean isSorted(){ //self-explanatory
    return this.sorted;
  }

  public String toString(){ // Getting the dataset as a string for when we display it to the client.
    String string = "";
    for(Integer number : dataset){
      string += " " + number;
    }
    return string;
  }

  //Used for the stat command
  public int count(){
    return dataset.length;
  }

  public int average() { //get the average value
    int average = 0;
    for(Integer number : dataset){ //add all the values up
      average += number;
    }
    average = average/count(); //divide them by the amount of values
    return average;
  }

  public int minimum() { //get the smallest value in the dataset
    int smallest = Integer.MAX_VALUE; //guarantees that something in the array will be smaller than this and immediately gets replaced
    for(Integer number : dataset){
      if (number < smallest) smallest = number; //replace it with the smaller number
    }
    return smallest;
  }

  public int maximum() { //get the largest value in the dataset
    int largest = Integer.MIN_VALUE; //guarantees that something in the array will be larger than this and immediately gets replaced
    for(Integer number : dataset){
      if (number > largest) largest = number; //replace it with the larger number
    }
    return largest;
  }
}
