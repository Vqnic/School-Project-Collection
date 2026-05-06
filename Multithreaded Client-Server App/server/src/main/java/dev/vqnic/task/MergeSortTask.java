package dev.vqnic.task;

import java.util.concurrent.RecursiveTask;

public class MergeSortTask extends RecursiveTask<Integer[]> {

  private Integer[] array;
  private int left;
  private int right;

  public MergeSortTask(Integer[] array, int left, int right){
    this.array = array;
    this.left = left;
    this.right = right;
  }

  @Override
  protected Integer[] compute() {
    if(left >= right){ //base case of an already sorted array
      return array;
    }else{ //Split the array into halves and create threads to deal with each half
      int midpoint = (left + right)/2;

      // Create tasks with the same array but different index ranges
      MergeSortTask leftTask = new MergeSortTask(array, left, midpoint);
      MergeSortTask rightTask = new MergeSortTask(array, midpoint + 1, right);

      leftTask.fork();
      rightTask.fork();

      leftTask.join();
      rightTask.join();

      //Merge the left and right subarrays back into the original array
      merge(midpoint);
      return array;
    }
  }

  private void merge(int midpoint) {
    // Creating some temporary arrays for the right and left subarrays
    int leftSize = midpoint - left + 1; //Use the midpoint and the index on the far left
    Integer[] leftSide = new Integer[leftSize]; //Create the temp array
    for (int i = 0; i < leftSize; i++) leftSide[i] = array[left + i]; //Fill up the left temp array with the left half of the array 

    int rightSize = right - midpoint; //Use the midpoint and the index on the far right
    Integer[] rightSide = new Integer[rightSize]; //Create the temp array
    for (int j = 0; j < rightSize; j++) rightSide[j] = array[midpoint + 1 + j]; //Fill up the right temp array with the right half of the array
    
    //Merge (sort) the two temp arrays we just made back into the original array
    int i = 0, j = 0, k = left;

    while (i < leftSize || j < rightSize) {
      if (i < leftSize && (j >= rightSize || leftSide[i] <= rightSide[j])) {
        array[k++] = leftSide[i++];
      } else {
        array[k++] = rightSide[j++];
      }
    }
  }
}
