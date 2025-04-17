package com.lucas.coding.glider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/*
之字形遍历二叉树（Zigzag Traversal）

题目：
给定一棵二叉树，按层遍历输出节点值，但顺序如下：
- 第1层从左到右
- 第2层从右到左
- 第3层再从左到右
- 第4层再从右到左
依此类推，交替进行（之字形遍历）。

输入：
第一行：一个整数 N，表示边的数量
第二行：N组边的信息，格式为：
parent child L/R
其中 L 表示左子节点，R 表示右子节点，多个边之间用空格分隔

示例输入：
2
10 20 R 10 30 L

表示：
- 节点10的右子是20，左子是30

输出：
按之字形顺序输出节点的值，空格分隔

示例输出：
10 30 20
*/

public class NonLC_ZigzagTraversal {
	static class Node {
		int val;
		Node left, right;
		Node(int val) {
			this.val = val;
		}
	}

	public static void main(String[] args) {
		// 示例输入
		int n = 2;
		String[] edges = {"10 20 R", "10 30 L"};

		Node root = buildTree(edges);
		List<Integer> result = zigzagTraversal(root);
		result.forEach(val -> System.out.print(val + " "));
	}

	// 构建树的方法
	public static Node buildTree(String[] edges) {
		Map<Integer, Node> nodeMap = new HashMap<>();
		Node root = null;

		for (String edge : edges) {
			String[] parts = edge.split(" ");
			int parentVal = Integer.parseInt(parts[0]);
			int childVal = Integer.parseInt(parts[1]);
			String direction = parts[2];

			Node parent = nodeMap.getOrDefault(parentVal, new Node(parentVal));
			Node child = new Node(childVal);

			if (direction.equals("L")) {
				parent.left = child;
			} else {
				parent.right = child;
			}

			nodeMap.put(parentVal, parent);
			nodeMap.put(childVal, child);

			// 初始 root 就是第一个出现的 parent
			if (root == null) root = parent;
		}

		return root;
	}

	// Zigzag 遍历
	public static List<Integer> zigzagTraversal(Node root) {
		List<Integer> result = new ArrayList<>();
		if (root == null) return result;

		Queue<Node> queue = new LinkedList<>();
		boolean leftToRight = true;
		queue.offer(root);

		while (!queue.isEmpty()) {
			int size = queue.size();
			List<Integer> level = new ArrayList<>();

			for (int i = 0; i < size; i++) {
				Node curr = queue.poll();
				level.add(curr.val);

				if (curr.left != null) queue.offer(curr.left);
				if (curr.right != null) queue.offer(curr.right);
			}

			if (!leftToRight) Collections.reverse(level);
			result.addAll(level);

			leftToRight = !leftToRight; // 每层换个方向
		}

		return result;
	}
}
