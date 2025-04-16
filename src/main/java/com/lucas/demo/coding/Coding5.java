package com.lucas.demo.coding;

import static com.lucas.demo.coding.Coding5.ExpressionAddOperators.canGetTarget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Coding5 {

	public static void main(String[] args) {

//		List<String> ans = Solution.addOperators("123", 6);
//		System.out.println(ans);


		// Test cases
		System.out.println(canGetTarget("123", 6)); // should return true
		System.out.println(canGetTarget("232", 8)); // should return false
		System.out.println(canGetTarget("3456237490", 9191)); // should return false

	}

	public static class Solution {
		public static List<String> addOperators(String num, int target) {
			List<String> rst = new ArrayList<String>();
			if(num == null || num.length() == 0) return rst;
			helper(rst, "", num, target, 0, 0, 0);
			return rst;
		}
		public static void helper(List<String> rst, String path, String num, int target, int pos, long eval, long multed){
			if(pos == num.length()){
				if(target == eval)
					rst.add(path);
				return;
			}
			for(int i = pos; i < num.length(); i++){
				if(i != pos && num.charAt(pos) == '0') break;
				long cur = Long.parseLong(num.substring(pos, i + 1));
				if(pos == 0){
					helper(rst, path + cur, num, target, i + 1, cur, cur);
				}
				else{
					helper(rst, path + "+" + cur, num, target, i + 1, eval + cur , cur);

					helper(rst, path + "-" + cur, num, target, i + 1, eval -cur, -cur);

					helper(rst, path + "*" + cur, num, target, i + 1, eval - multed + multed * cur, multed * cur );
				}
			}
		}
	}

	public class ExpressionAddOperators {
		public static boolean canGetTarget(String num, int target) {
			if (num == null || num.length() == 0) {
				return false;
			}
			return canGetTarget(num, target, 0, 0, 0, "");
		}

		private static boolean canGetTarget(String num, int target, int pos, long eval, long multed, String expr) {
			if (pos == num.length()) {
				if (target == eval) {
					System.out.println(expr);
					return true;
				}
				return false;
			}

			for (int i = pos; i < num.length(); i++) {
				if (i != pos && num.charAt(pos) == '0') break; // Skip numbers with leading zero
				long cur = Long.parseLong(num.substring(pos, i + 1));
				if (pos == 0) {
					if (canGetTarget(num, target, i + 1, cur, cur, expr + cur)) return true;
				} else {
					if (canGetTarget(num, target, i + 1, eval + cur, cur, expr + "+" + cur))
						return true;
					if (canGetTarget(num, target, i + 1, eval - cur, -cur, expr + "-" + cur))
						return true;
				}
			}

			return false;
		}

	}


}



