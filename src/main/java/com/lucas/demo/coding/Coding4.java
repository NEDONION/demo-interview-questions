package com.lucas.demo.coding;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
public class Coding4 {

	public static void main(String[] args) {
		String[] strs = {"eat", "tea", "tan", "ate", "nat", "bat"};
		System.out.println(groupAnagrams(strs));
	}

	public static List<List<String>> groupAnagrams(String[] strs) {
		// bad case
		if (strs.length == 0) return new ArrayList<>();

		Map<String, List<String>> ans = new HashMap<>();

		for (String s : strs) {
			char[] curr = s.toCharArray();
			Arrays.sort(curr);
			String key = String.valueOf(curr);
			if (!ans.containsKey(key))
				ans.put(key, new ArrayList<>());
			ans.get(key).add(s);
		}
		return new ArrayList<>(ans.values());
	}

}
