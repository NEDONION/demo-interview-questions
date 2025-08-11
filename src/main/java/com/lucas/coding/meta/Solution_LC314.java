package com.lucas.coding.meta;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

public class Solution_LC314 {
	public List<List<Integer>> verticalOrder(TreeNode root) {
		// 1. map to store {col: node.val[]}
		List<List<Integer>> ans = new ArrayList<>();
		if (root == null) return ans;

		// TreeMap 保证列号从小到大排序
		Map<Integer, List<Integer>> map = new TreeMap<>();

		// 2. init queue and start bfs
		Deque<Pair> queue = new ArrayDeque<>();
		queue.offer(new Pair(root, 0));
		while (!queue.isEmpty()) {
			Pair p = queue.poll();
			TreeNode node = p.node;
			int col = p.col;

			if (!map.containsKey(col)) {
				map.put(col, new ArrayList<>());
			}
			map.get(col).add(node.val);

			if (node.left != null) {
				queue.offer(new Pair(node.left, p.col - 1));
			}
			if (node.right != null) {
				queue.offer(new Pair(node.right, p.col + 1));
			}
		}
		return new ArrayList<>(map.values());
	}


	static class Pair {
		TreeNode node;
		int col;

		Pair(TreeNode node, int col) {
			this.node = node;
			this.col = col;
		}
	}


	// TreeNode 定义
	static class TreeNode {
		int val;
		TreeNode left, right;
		TreeNode(int val) {
			this.val = val;
		}
	}

	// 构造二叉树的辅助方法
	static TreeNode buildTree(Integer[] arr) {
		if (arr.length == 0 || arr[0] == null) return null;
		TreeNode root = new TreeNode(arr[0]);
		Queue<TreeNode> queue = new LinkedList<>();
		queue.offer(root);
		int i = 1;
		while (!queue.isEmpty() && i < arr.length) {
			TreeNode curr = queue.poll();
			if (i < arr.length && arr[i] != null) {
				curr.left = new TreeNode(arr[i]);
				queue.offer(curr.left);
			}
			i++;
			if (i < arr.length && arr[i] != null) {
				curr.right = new TreeNode(arr[i]);
				queue.offer(curr.right);
			}
			i++;
		}
		return root;
	}

	// 测试入口
	public static void main(String[] args) {
		Solution_LC314 sol = new Solution_LC314();

		// 测试用例 1：LeetCode 示例
		TreeNode root1 = buildTree(new Integer[]{3, 9, 20, null, null, 15, 7});
		System.out.println(sol.verticalOrder(root1)); // [[9], [3, 15], [20], [7]]

		// 测试用例 2：完全二叉树
		TreeNode root2 = buildTree(new Integer[]{1, 2, 3, 4, 5, 6, 7});
		System.out.println(sol.verticalOrder(root2)); // [[4], [2], [1, 5, 6], [3], [7]]

		// 测试用例 3：只有左子树
		TreeNode root3 = buildTree(new Integer[]{1, 2, null, 3, null, 4});
		System.out.println(sol.verticalOrder(root3)); // [[4], [3], [2], [1]]

		// 测试用例 4：只有右子树
		TreeNode root4 = buildTree(new Integer[]{1, null, 2, null, 3});
		System.out.println(sol.verticalOrder(root4)); // [[1], [2], [3]]

		// 测试用例 5：空树
		TreeNode root5 = buildTree(new Integer[]{});
		System.out.println(sol.verticalOrder(root5)); // []
	}

}
