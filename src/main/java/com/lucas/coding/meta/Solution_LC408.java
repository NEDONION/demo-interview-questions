package com.lucas.coding.meta;

/**
 * LC 408. Valid Word Abbreviation
 * --------------------------------
 * Idea（核心思路）：
 * 1) 双指针：i 指向 word，j 指向 abbr；再用一个整型 number 累积 abbr 中连续数字（表示要跳过的字符数）。
 * 2) 扫描 abbr：
 *    - 若遇到“数字”，把整段数字累积到 skip（注意：数字块不能以 0 开头；"0" 或 "01" 非法）。
 *    - 若遇到“字母”，先把 i 往前挪动 skip（兑现跳过），清零 skip；随后要求 word[i] 必须与该字母一致。
 *      匹配成功后 i 前进 1。
 * 3) 扫描结束后，abbr 可能以数字结尾（还留在 skip 里），用 i + skip == word.length() 收尾。
 * 4) 任何时候只要越界/不匹配/前导 0，立即返回 false。
 *
 * 正确性要点：
 * - “数字块累积 + 兑付”保证了 abbr 中数字严格代表跳过的字符数。
 * - 以 abbr 为驱动，遇字母就对齐匹配，遇数字就累积长度。
 * - 尾部统一用 i + skip 收尾，覆盖 “abbr 以数字结尾” 的情况。
 *
 * 复杂度：
 * - 时间 O(m + n)：m=word.length(), n=abbr.length()；每个字符最多处理一次。
 * - 空间 O(1)：只用常数变量。
 *
 * 常见坑：
 * - 前导 0：比如 "a01b" 非法（abbr 中数字块不能以 0 开头）。
 * - 越界：跳过的总长度不能超过 word 长度。
 * - 最后收尾：abbr 以数字结尾必须用 i + skip == m 判断。
 */
public class Solution_LC408 {
	public boolean validWordAbbreviation(String word, String abbr) {
		int m = word.length();
		int n = abbr.length();
		int i = 0, j = 0;   // i 指向 word 的当前位置；j 指向 abbr 的当前位置
		int number = 0;       // 累积要跳过的字符数（来自 abbr 中连续数字）

		// 用 abbr 作为主循环：把字母/数字一段段消费掉
		while (j < n) {
			char c = abbr.charAt(j);
			if (Character.isDigit(c)) {
				// 数字块的第一位不能是 '0' （前导 0 非法）。例如 "a0b"、"a01b" 都不合法
				if (c == '0' && number == 0) return false;

				// 累积当前数字到 skip（多位数字）
				number = number * 10 + (c - '0');
				j++; // 消费这个数字字符

			} else {
				// 遇到字母：先“兑付”之前累积的跳过
				i += number;
				number = 0;

				// 越界或字母不匹配则失败
				if (i >= m || word.charAt(i) != c) return false;

				// 成功匹配一个字母
				i++;
				j++;
			}
		}

		// 扫描完 abbr 后，若 abbr 以数字结尾，skip 里还残留最后的跳过长度
		// 这一步把它也应用掉；只有刚好用完 word 才是有效缩写
		return i + number == m;
	}

	public static void main(String[] args) {
		Solution_LC408 sol = new Solution_LC408();

		System.out.println(sol.validWordAbbreviation("internationalization", "i12iz4n")); // true
		System.out.println(sol.validWordAbbreviation("apple", "a2e")); // false
		System.out.println(sol.validWordAbbreviation("word", "1o1d")); // true
		System.out.println(sol.validWordAbbreviation("word", "01o1d")); // false - Leading 0 is illegal
	}
}
