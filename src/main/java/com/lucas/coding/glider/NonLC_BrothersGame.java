package com.lucas.coding.glider;

import java.util.Arrays;
import java.util.List;

/*
Brothers' Game

Two brothers were playing a game. Their mother gave them an array of numbers,
where each element is either 0 or 1. She asked them to find the maximum number
of 1s they could obtain by inverting the bits (i.e., changing all the 0s to 1s
and all the 1s to 0s) for any contiguous subarray.

The younger brother tried to solve it in his head, but the elder brother
decided to write a program. Can you help him find the optimal solution?

两个兄弟在玩一个游戏。妈妈给了他们一个只包含 0 和 1 的数组。她要求他们找出：
通过反转某一段连续子数组中的所有位（即将 0 变为 1，1 变为 0）之后，整个数组中最多能有多少个 1。

弟弟试图用脑子算，但哥哥决定写程序来找出最优解。你能帮帮哥哥吗？

Input:
- The first line contains an integer N (1 <= N <= 100), the size of the array.
- The second line contains N space-separated integers (0 or 1), representing the array.

Output:
- A single integer: the maximum number of 1s obtainable by inverting exactly one contiguous subarray.

Example:
Input:
5
0 1 0 0 1

Output:
4

Explanation:
Invert subarray [0, 1, 0] to [1, 0, 1] → final array = [1, 0, 1, 0, 1], number of 1s = 4.
*/
public class NonLC_BrothersGame {

	// 直接在代码中写测试用例
	public static void main(String[] args) {
		// 示例测试用例
		List<Integer> testCase = Arrays.asList(0, 1, 0, 0, 1);

		int result = maxOnesAfterInversion(testCase);
		System.out.println("Max number of 1s after one inversion: " + result); // 期待输出：4
	}

	// 计算翻转某一段连续子数组后，最多可以得到多少个 1
	public static int maxOnesAfterInversion(List<Integer> arr) {
		// 统计原数组中 1 的个数（用于后续计算总的结果）
		int originalOnes = 0;

		// maxGain 表示翻转某段子数组后，最多能“额外增加”的 1 的数量
		// currentGain 表示当前正在尝试翻转的这段子数组的“当前增益”
		int maxGain = Integer.MIN_VALUE;
		int currentGain = 0;

		// 遍历数组中的每一个元素（0 或 1）
		for (int bit : arr) {
			// 统计原始 1 的数量
			if (bit == 1) originalOnes++;

			// 如果当前是 0，翻转后变成 1，收益 +1
			// 如果当前是 1，翻转后变成 0，损失 -1
			int gain = (bit == 0) ? 1 : -1;

			// 判断是重新开始一段翻转（gain），还是继续前一段的翻转区间（currentGain + gain）
			currentGain = Math.max(gain, currentGain + gain);

			// 更新目前为止能获得的最大翻转收益
			maxGain = Math.max(maxGain, currentGain);
		}

		// 特殊情况处理：如果数组全是 1，那么翻转任何一段都会让总数减少
		// 此时只能选择最小损失，也就是去掉一个 1
		if (maxGain <= 0) return originalOnes - 1;

		// 返回最终最多的 1 的数量 = 原本的1数量 + 翻转带来的收益
		return originalOnes + maxGain;
	}

}
