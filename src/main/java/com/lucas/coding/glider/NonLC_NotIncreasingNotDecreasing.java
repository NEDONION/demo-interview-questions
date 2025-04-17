package com.lucas.coding.glider;

import java.util.ArrayList;
import java.util.List;

/**
 * 你有一个 由互不相同的整数构成的数组，你需要从中删除最少的元素，使得不存在任意三个连续的数字是递增或递减的。
 */
public class NonLC_NotIncreasingNotDecreasing {
	public static void main(String[] args) {
		int[] nums = {1, 2, 4, 1, 2};
		System.out.println(minRemovals(nums)); // 输出 1
	}

	public static int minRemovals(int[] nums) {
		int n = nums.length;
		int removals = 0;

		// 使用 ArrayList 来存当前有效数组
		List<Integer> list = new ArrayList<>();
		for (int num : nums) {
			list.add(num);
		}

		int i = 0;
		while (i <= list.size() - 3) {
			int a = list.get(i);
			int b = list.get(i + 1);
			int c = list.get(i + 2);

			// 检查是否是递增或递减的连续三个数
			if ((a < b && b < c) || (a > b && b > c)) {
				list.remove(i + 1); // 贪心策略：删掉中间那个数
				removals++;
				if (i > 0) i--; // 回退一步重新检查
			} else {
				i++;
			}
		}

		return removals;
	}
}
