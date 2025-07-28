package com.lucas.coding.meta;

import java.util.*;

class SparseVector_1570 {
	// 用 HashMap 存储非零元素的索引和值，key 是索引，value 是值
	private final Map<Integer, Integer> indexToValue;

	// 构造函数，初始化稀疏向量，只记录非零元素
	// 比如 {1, 0, 0, 2, 3} = {0: 1, 3: 2, 4: 3}
	public SparseVector_1570(int[] nums) {
		indexToValue = new HashMap<>();
		for (int i = 0; i < nums.length; i++) {
			if (nums[i] != 0) {
				indexToValue.put(i, nums[i]);
			}
		}
	}

	// 计算当前向量和另一个稀疏向量 vec 的点积
	public int dotProduct(SparseVector_1570 vec) {
		int ans = 0;

		// 遍历当前向量中的非零元素
		for (var entry : indexToValue.entrySet()) {
			int index = entry.getKey();     // 当前索引
			int value = entry.getValue();   // 当前值

			// 如果另一个向量在该索引上也有非零值，则计算乘积并累加
			if (vec.indexToValue.containsKey(index)) {
				ans += value * vec.indexToValue.get(index);
			}
		}

		return ans;
	}

	public static void main(String[] args) {
		SparseVector_1570 v1 = new SparseVector_1570(new int[]{1, 0, 0, 2, 3});
		SparseVector_1570 v2 = new SparseVector_1570(new int[]{0, 3, 0, 4, 0});
		int result = v1.dotProduct(v2);
		System.out.println(result); // 输出 8
	}
}
