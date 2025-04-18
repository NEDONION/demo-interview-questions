package com.lucas.coding.questions;

public class BinarySearchTree {

	/**
	 *       5
	 *      / \
	 *     3   7
	 *    /
	 *   1
	 */
	public static void main(String[] args) {
		BinarySearchTree bst = new BinarySearchTree();
		bst.insert(5);
		bst.insert(3);
		bst.insert(7);
		bst.insert(1);

		bst.inorder(bst.root); // 输出：1 3 5 7
	}

	TreeNode root;

	// 插入
	public void insert(int val) {
		root = insertNode(root, val);
	}

	// 向二叉搜索树中插入一个值 val，并返回插入后的树的根节点
	private TreeNode insertNode(TreeNode node, int val) {
		// 如果当前节点为空，创建新节点返回
		if (node == null) return new TreeNode(val);

		// 如果 val 小于当前节点值，递归插入到左子树
		if (val < node.val) {
			node.left = insertNode(node.left, val);
		}
		// 如果 val 大于当前节点值，递归插入到右子树
		else if (val > node.val) {
			node.right = insertNode(node.right, val);
		}

		// 返回当前节点（保持树结构）
		return node;
	}


	// 中序遍历（递增顺序）
	public void inorder(TreeNode node) {
		if (node == null) return;
		inorder(node.left);
		System.out.print(node.val + " ");
		inorder(node.right);
	}

	static class TreeNode {
		int val;
		TreeNode left, right;

		TreeNode(int val) {
			this.val = val;
		}
	}
}

