package com.lucas.coding.questions;


import java.util.ArrayList;
import java.util.List;

/*
Given two integers n and k,
return all possible combinations of k numbers chosen from the range [1, n].
You may return the answer in any order.

n < 1 || k == 0 return ans;

Input: n = 4, k = 2
Output: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]
Explanation: There are 4 choose 2 = 6 total combinations.
Note that combinations are unordered,
i.e., [1,2] and [2,1] are considered to be the same combination.

Input: n = 1, k = 1
Output: [[1]]
Explanation: There is 1 choose 1 = 1 total combination.


input1:
k=2, [1, 4]

return all possible
idea1:
idea2: backtracking -> dfs method to find all the combinations meet the requirement
requirement: all possible but non-duplicate, length = k

- vars to mark which index we're at currently
- every time inside dfs
	- container/array to store the current array
	- store the current array into ans
	- continue to do loop based on the current index

void dfs(int n, int k, int index) {
	List<Intger> current = new ArrayList<>();
	// store the current array into ans


	// continue to do loop based on the current index
	for (int i = index; i <= n; i++) {
		current.add(i);
		// backtrack again
		// how to continue from the next index
		dfs(n, k, i + 1);
		// clean up operation
		current.clean();
	}
}

input: Integer
output: List<List<Integer>>

time: O(n) eventually, dfs will go through from [1,n]
space: O(n) use 1-2 container to store

 */
public class AllPossibleCombinations {

	static List<Integer> current;
	static List<List<Integer>> ans;
	public static void main(String[] args) {

		List<List<Integer>> ans = allPossibleCombinations(4, 2);
		List<List<Integer>> ans1 = allPossibleCombinations(1, 1);
		System.out.println(ans);
		System.out.println(ans1);
	}

	public static List<List<Integer>> allPossibleCombinations(int n, int k) {
		ans = new ArrayList<>();
		// bad case
		if (n < 1 || k == 0) return ans;
		current = new ArrayList<>();
		dfs(n, k, 1);
		return ans;

	}

	public static void dfs(int n, int k, int index) {
		// store the current array into ans
		// requirement: all possible but non-duplicate, length = k
		if (current.size() == k) {
			// we're adding a copy of the object
			// create a new container to add
			ans.add(new ArrayList<>(current));
			return;
		}
		// continue to do loop based on the current index
		for (int i = index; i <= n; i++) {
			current.add(i);
			// backtrack again
			// how to continue from the next index
			// n = 4, k = 2
			// dfs(4, 2, 1) -> current:null -> do for loop [1, 4] -> current[1]
			// dfs(4, 2, 2) -> current:[1] -> size=2 doesn't meet the requirment
			// -> do the for loop [2, 4] -> current[1, 2]
			// dfs(4, 2, 3) -> current:[1,2] -> meet the if statement -> ans.add([1, 2])

			dfs(n, k, i + 1);
			// clean up operation
			// remove the last element and try backtrack again
			current.remove(current.size() - 1);
		}
	}
}
