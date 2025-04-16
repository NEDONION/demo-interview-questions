package com.lucas.demo.coding;


/*
list of numbers
find the second largest one using stream
 */

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Coding3 {

	public static void main(String[] args) {
		List<Integer> numbers = Arrays.asList(1,4,5,2,3);

		// 1. convert in stream
		// 2. second largest
		// 3. skip(1)
		// 4. find the first one
		Optional<Integer> ans = numbers.stream()
				.sorted(
						Comparator.reverseOrder()
				)
				.skip(1)
				.findFirst();

		System.out.println(ans);
		// Optional[4]
	}
}
