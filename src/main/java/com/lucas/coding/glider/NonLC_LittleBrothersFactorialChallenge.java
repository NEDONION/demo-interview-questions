package com.lucas.coding.glider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Your 10-year-old brother is a curious boy who has just learned factorials.
 * He has made a list of factorials of numbers from m to n. Now your brother wants to challenge you.
 *
 * You must write a program to find all the numbers (if any) whose factorials start with an even number.
 * Can you write a program to do this?
 *
 * Input:
 * - The first line of input contains an integer m.
 * - The second line of input contains an integer n.
 *
 * Output:
 * - The output is a single line of integers. The first integer represents x,
 * the number of factorial numbers that start with an even number,
 * followed by x space-separated integers representing the factorials in increasing order.
 * - A space should separate all integers in the output.
 *
 * 即 给你两个数字 m 和 n，比如 m = 1, n = 10，意思是让你看从 1 到 10 这些数的阶乘，
 * 然后从中找出哪些数，它们的阶乘结果是以偶数开头的。
 *
 * Constraints:
 * - 1 ≤ m ≤ 100
 * - 1 ≤ n ≤ 100
 *
 * Example:
 * Input:
 * 1
 * 10
 *
 * Output:
 * 2 3 4 8
 *
 * Explanation:
 * Among the numbers from 1 to 10, the following factorials start with an even number:
 * 2! = 2 → starts with 2
 * 3! = 6 → starts with 6
 * 4! = 24 → starts with 2
 * 8! = 40320 → starts with 4
 */

public class NonLC_LittleBrothersFactorialChallenge {
	public static void main(String[] args) {
		int m = 1;
		int n = 10;

		// 获取阶乘以偶数开头的数字列表
		List<Integer> result = getNumbersWithEvenStartingFactorial(m, n);

		// 输出结果格式：先输出个数，然后输出这些数字
		System.out.print(result.size());
		for (int num : result) {
			System.out.print(" " + num);
		}
	}

	// 返回在 [m, n] 区间内，阶乘以偶数开头的数字
	public static List<Integer> getNumbersWithEvenStartingFactorial(int m, int n) {
		List<Integer> res = new ArrayList<>();

		for (int i = m; i <= n; i++) {
			BigInteger fact = factorial(i);

			// 获取阶乘的首位数字
			String firstDigit = fact.toString().substring(0, 1);
			int first = Integer.parseInt(firstDigit);

			// 判断是否为偶数
			if (first % 2 == 0) {
				res.add(i);
			}
		}

		return res;
	}

	// 计算阶乘（使用 BigInteger 以支持大数）
	// 20! will be bigger than long range: 2^63-1
	public static BigInteger factorial(int num) {
		BigInteger result = BigInteger.ONE;
		for (int i = 2; i <= num; i++) {
			result = result.multiply(BigInteger.valueOf(i));
		}
		return result;
	}
}
