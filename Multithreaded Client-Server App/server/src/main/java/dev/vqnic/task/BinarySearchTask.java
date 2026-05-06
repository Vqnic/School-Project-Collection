package dev.vqnic.task;

import java.util.concurrent.RecursiveTask;

public class BinarySearchTask extends RecursiveTask<Integer> {

  //Important to understand that we only use after an array has been sorted. (ascending order, to be specific)

  private int target; //The target value it's going to search for
  private Integer[] array;
  private int left; //lowest value
  private int right; //largest value

  public BinarySearchTask(int target, Integer[] array, int left, int right){
    this.target = target;
    this.array = array;
    this.left = left;
    this.right = right;
  }

  @Override
  protected Integer compute() {
    // TODO Auto-generated method stub
    Integer midpoint = array[(left+right)/2]; //Get the value in the middle of the array
    if(left > right) return -1; //Base case if the target isn't in the array
    if(midpoint == target){
      return midpoint;
    }else if (midpoint > target) { //If the midpoint is larger than the target, go to the left side of the array and look for smaller values
      BinarySearchTask leftTask = new BinarySearchTask(target, array, left, (left+right)/2 - 1);
      leftTask.fork();
      return leftTask.join();
    }else{ //If the midpoint is bigger than the target, go to the right side of the array and look for bigger values
      BinarySearchTask rightTask = new BinarySearchTask(target, array, (left+right)/2 + 1, right);
      rightTask.fork();
      return rightTask.join();
    }
  }
}
