package com.lucas.coding.meta;

import java.util.HashSet;
import java.util.Set;

public class lowestCommonAncestor_1650 {

	// 节点定义，包含 parent 指针
	static class Node {
		int val;
		Node left;
		Node right;
		Node parent;

		Node(int val) {
			this.val = val;
		}
	}

	// 求最近公共祖先的方法
	public static Node lowestCommonAncestor(Node p, Node q) {
		Set<Node> seen = new HashSet<>(); // 创建一个集合，用来存放从 p 向上遍历经过的所有祖先节点

		// 把 p 节点一路向上的所有祖先节点都加入集合
		while (p != null) {
			seen.add(p);      // 记录当前节点
			p = p.parent;     // 向上走到父节点
		}

		// 从 q 开始向上找，遇到的第一个在 seen 中的节点就是最近公共祖先
		while (q != null) {
			if (seen.contains(q)) {
				return q;     // 找到第一个共同祖先，直接返回
			}
			q = q.parent;     // 向上继续走
		}

		return null;
	}


	// main 方法：构造测试树结构并调用 LCA 方法
	public static void main(String[] args) {
		/*
		        3
			   / \
			  5   1
			 / \ / \
			6  2 0  8
			  / \
			 7   4
		 */
		// 创建所有节点
		Node n3 = new Node(3);
		Node n5 = new Node(5);
		Node n1 = new Node(1);
		Node n6 = new Node(6);
		Node n2 = new Node(2);
		Node n0 = new Node(0);
		Node n8 = new Node(8);
		Node n7 = new Node(7);
		Node n4 = new Node(4);

		// 建立连接关系 + 设置 parent 指针
		n3.left = n5;    n5.parent = n3;
		n3.right = n1;   n1.parent = n3;

		n5.left = n6;    n6.parent = n5;
		n5.right = n2;   n2.parent = n5;

		n1.left = n0;    n0.parent = n1;
		n1.right = n8;   n8.parent = n1;

		n2.left = n7;    n7.parent = n2;
		n2.right = n4;   n4.parent = n2;

		// 测试：最近公共祖先 of 5 和 1 是 3
		Node lca = lowestCommonAncestor(n5, n1);
		System.out.println("LCA of 5 and 1 is: " + (lca != null ? lca.val : "null"));
	}
}
