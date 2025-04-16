package com.lucas.coding.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Coding1 {
	/*
	a array of Integers {1,2,3,4,5}

	stream
	return the subarray which has even number

	output:{2,4}
	 */

	public static void main(String[] args) {
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);


		List<Integer> evens = numbers.stream()
				.filter(
						n -> n % 2 == 0
				)
				.collect(Collectors.toList());

		System.out.println(evens);


		// 1 2 3 2 1 ->
		// {1:2, 2:2, 3:1}
		// map<key=, value=>
		List<Integer> nums = Arrays.asList(1, 2, 3, 2, 1);

		Map<Integer, Long> result = nums.stream()
				.collect(Collectors.groupingBy(
						Integer::intValue,  // 分组键
						Collectors.counting()  // 计数操作
				));

		System.out.println(result);

	}


}
