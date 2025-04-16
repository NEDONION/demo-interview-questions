package com.lucas.coding.questions;

/*
array[]={1,3,-5,7,8,20,-40,6};
Print a pair where sum is closest to zero : -5 and 6

input: int[]
output: int[]


idea1:
loop -> while loop
int currentSum to store the current sum
minSum = 0
two pointer to store the minLeftIndex and minRightIndex

1,3,-5,7,8,20,-40,6
^                 ^
l                 r

currentSum=7 > minSum -> continue moving
currentSum=-5+6=1 < minSum -> note the minLeftIndex and minRightIndex

return new int[]{minLeftIndex, minRightIndex}

time: O(n), left -> middle, right -> middle, 1 time
space: O(1)

 */


import java.util.Arrays;

public class GetPairClosestZero {

	public static void main(String[] args) {
		int[] array = {1,3,-5,7,8,20,-40,6};
		int[] ans = getPairClosestZero(array);
		System.out.println(Arrays.toString(ans));
	}

	public static int[] getPairClosestZero(int[] array) {

		int[] ans = new int[2];
		// bad case
		if (array == null || array.length < 0) {
			return ans;
		}

		Arrays.sort(array);
		int minLeftIndex = 0;
		int minRightIndex = 0;
		int minSum = Integer.MAX_VALUE;
		int left = 0;
		int right = array.length - 1;

		while (left < right) {
			int currentSum = array[left] + array[right];
			if (Math.abs(currentSum) < Math.abs(minSum)) {
				minSum = currentSum;
				minLeftIndex = left;
				minRightIndex = right;
			}

			// logic to move the pointer
			if (currentSum < 0) {
				left++;
			} else {
				right--;
			}

		}
		ans[0] = array[minLeftIndex];
		ans[1] = array[minRightIndex];
		return ans;
	}

}
