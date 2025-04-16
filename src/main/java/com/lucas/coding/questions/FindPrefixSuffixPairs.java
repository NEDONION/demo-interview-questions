package com.lucas.coding.questions;

import java.util.*;

public class FindPrefixSuffixPairs {

	public static void main(String[] args) {
		List<String> words = Arrays.asList("xxz", "xxn", "ab", "abc", "abcd", "abcxxz");
		List<List<String>> result = findPrefixSuffixPairs(words);
		System.out.println(result);
	}

	// 优化后的前缀-后缀匹配算法
	public static List<List<String>> findPrefixSuffixPairs(List<String> words) {
		// 使用 Set 存储单词以便快速查找
		Set<String> wordSet = new HashSet<>(words);

		// 将所有单词按长度分组，用于优化后续查找
		Map<Integer, List<String>> lengthMap = new HashMap<>();
		for (String word : words) {
			lengthMap.putIfAbsent(word.length(), new ArrayList<>());
			lengthMap.get(word.length()).add(word);
		}
		// 用于存储最终结果
		List<List<String>> result = new ArrayList<>();

		// 外层循环：遍历每个单词
		for (String word : words) {
			// 尝试在每个可能的分割点进行拆分
			for (int i = 1; i < word.length(); i++) {
				// 获取前缀和后缀
				String prefix = word.substring(0, i);
				String suffix = word.substring(i);

				// 使用 lengthMap 来减少无效查找，仅查找相同长度的前缀和后缀
				if (wordSet.contains(prefix) && wordSet.contains(suffix)) {
					// 如果前缀和后缀都在集合中，则将匹配项添加到结果中
					result.add(Arrays.asList(prefix, suffix, word));
				}
			}
		}
		// 返回所有匹配的结果
		return result;
	}
}
