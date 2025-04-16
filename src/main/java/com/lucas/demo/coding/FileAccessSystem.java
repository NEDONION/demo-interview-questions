package com.lucas.demo.coding;

/*

设计一个文件系统+权限系统，要求满足以下的 APIs
文件系统要用 Trie 实现
权限系统可以包括 (User, Role, Permission)

- 当用户允许访问 父文件夹时，用户也可以访问其中的所有子文件夹
-

API:
- boolean is_entity_allowed(String filepath, User user)
- add_allowedList(String filepath, List<User> users)

 */

import java.util.*;

class FileAccessSystem {

	private TrieNode root;

	public FileAccessSystem() {
		root = new TrieNode();
	}

	public boolean is_entity_allowed(String filepath, User user) {
		TrieNode node = getNode(filepath);
		// 向上遍历，检查是否有对任何父文件夹的访问权限
		while (node != null) {
			if (node.allowedUsers.contains(user)) {
				return true;
			}
			node = node.parent; // 向上移动到父节点
		}
		return false;
	}

	/**
	 * 将一组用户添加到给定文件路径的允许列表中
	 */
	public void add_allowedList(String filepath, List<User> users) {
		TrieNode node = getNode(filepath);
		if (node != null) {
			node.allowedUsers.addAll(users);
		}
	}

	/**
	 * 遍历Trie并获取给定文件路径的节点
	 * 可选择在不存在时创建新节点（由 add_allowedList 使用）
	 */
	private TrieNode getNode(String filepath) {
		TrieNode node = root;
		String[] parts = filepath.split("/");
		for (String part : parts) {
			if (part.isEmpty()) {
				continue; // 跳过空字符串（这通常发生在路径开始的 "/"）
			}
			TrieNode next = node.children.get(part);
			if (next == null) {
				next = new TrieNode();
				next.parent = node; // 设置父节点
				node.children.put(part, next);
			}
			node = node.children.get(part);
		}
		return node;
	}

	// Trie Node class
	private static class TrieNode {
		Map<String, TrieNode> children;
		List<User> allowedUsers;
		TrieNode parent; // 父节点引用

		public TrieNode() {
			children = new HashMap<>();
			allowedUsers = new ArrayList<>();
			parent = null;
		}
	}

	// User class
	public static class User {
		String name;

		public User(String name) {
			this.name = name;
		}
	}

	// 示例用法
	public static void main(String[] args) {
		FileAccessSystem fileAccessSystem = new FileAccessSystem();
		FileAccessSystem.User user1 = new FileAccessSystem.User("Alice");
		FileAccessSystem.User user2 = new FileAccessSystem.User("Bob");
		FileAccessSystem.User user3 = new FileAccessSystem.User("Ned");

		System.out.println(fileAccessSystem.is_entity_allowed("/folder/subfolder/file.txt", user1)); // false
		fileAccessSystem.add_allowedList("/folder/subfolder", Arrays.asList(user1, user2));
		System.out.println(fileAccessSystem.is_entity_allowed("/folder/subfolder/file.txt", user1)); // true


		// 测试不存在的文件路径
		System.out.println("测试不存在的文件路径: " + fileAccessSystem.is_entity_allowed("/nonexistent/path", user1)); // 应该是 false

		// 测试用户对没有直接权限的子目录的访问
		fileAccessSystem.add_allowedList("/folder", Arrays.asList(user1));
		System.out.println("测试子目录访问: " + fileAccessSystem.is_entity_allowed("/folder/subfolder", user1)); // 应该是 true

		// 测试用户对同一路径多次添加权限
		fileAccessSystem.add_allowedList("/folder/subfolder", Arrays.asList(user2));
		fileAccessSystem.add_allowedList("/folder/subfolder", Arrays.asList(user2, user3));
		System.out.println("测试多次添加权限: " + fileAccessSystem.is_entity_allowed("/folder/subfolder", user2)); // 应该是 true
		System.out.println("测试多次添加权限: " + fileAccessSystem.is_entity_allowed("/folder/subfolder", user3)); // 应该是 true

		// 测试没有用户权限的文件路径的访问
		System.out.println("测试没有权限的路径: " + fileAccessSystem.is_entity_allowed("/folder2", user1)); // 应该是 false

		// 测试路径的根目录访问
		fileAccessSystem.add_allowedList("/", Arrays.asList(user1));
		System.out.println("测试根目录访问: " + fileAccessSystem.is_entity_allowed("/any/other/path", user1)); // 应该是 true

	}
}
