package com.lucas.coding.meta;

/**
 * Input: word = "internationalization", abbr = "i12iz4n"
 * Output: true
 *
 * 解释：
 * "internationalization" 可以缩写为：
 * i + 12 (中间12个字符) + iz + 4 (又4个字符) + n
 *
 * Input: word = "apple", abbr = "a2e"
 * Output: false
 *
 * 解释：
 * 缩写中 e 和原单词中的字符不匹配
 */
public class ValidWordAbbreviation_408 {
	public boolean validWordAbbreviation(String word, String abbr) {
		int wordIndex = 0;
		int abbrIndex = 0;

		while (wordIndex < word.length() && abbrIndex < abbr.length()) {
			char current = abbr.charAt(abbrIndex);

			if (Character.isDigit(current)) {
				// 前导 0 是非法的
				if (current == '0') return false;
				int start = abbrIndex;
				// 解析数字部分
				Node numberAndNextIndex = parseNumber(abbr, abbrIndex);
				// 更新 abbrIndex 到数字结束位置
				abbrIndex = numberAndNextIndex.abbrIndex;
				wordIndex += numberAndNextIndex.number;
			} else {
				// 字符必须逐个匹配
				if (word.charAt(wordIndex) != current) {
					return false;
				}
				wordIndex++;
				abbrIndex++;
			}
		}

		return wordIndex == word.length() && abbrIndex == abbr.length();
	}

	/**
	 * 从 abbr[startIndex] 开始解析连续数字并返回其值。
	 * for example: parseNumber("w12rd", 1) = Node(12, 3)
	 */
	private Node parseNumber(String abbr, int startIndex) {
		int num = 0;
		while (startIndex < abbr.length() && Character.isDigit(abbr.charAt(startIndex))) {
			num = num * 10 + (abbr.charAt(startIndex) - '0');
			startIndex++;
		}
		return new Node(num, startIndex);
	}

	static class Node {
		int number; // 解析出来的数字
		int abbrIndex; // 数字后一个字符的索引位置
		Node(int number, int abbrIndex) {
			this.number = number;
			this.abbrIndex = abbrIndex;
		}
	}

	public static void main(String[] args) {
		ValidWordAbbreviation_408 sol = new ValidWordAbbreviation_408();

		System.out.println(sol.validWordAbbreviation("internationalization", "i12iz4n")); // true
		System.out.println(sol.validWordAbbreviation("apple", "a2e")); // false
		System.out.println(sol.validWordAbbreviation("word", "1o1d")); // true
		System.out.println(sol.validWordAbbreviation("word", "01o1d")); // false - Leading 0 is illegal
	}
}
