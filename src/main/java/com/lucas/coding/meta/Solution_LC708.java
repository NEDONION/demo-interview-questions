package com.lucas.coding.meta;

import java.util.*;

/**
 * 给你一个「非严格递增（允许相等）」的环形单链表中的任意一个结点 head（注意：head 不一定是最小值所在节点，只是环上的某个节点），以及一个整数 insertVal。
 * 请你在这个环形链表中插入一个值为 insertVal 的新节点，使得插入后链表仍然保持非降序并且仍为环形。
 * 返回插入后链表中的任意一个结点（通常返回原来的 head 即可）。
 *
 * 特殊情况
 *
 * 如果链表为空（head == null），请创建一个只有新节点的环（newNode.next = newNode），并返回这个新节点。
 *
 * 链表可能所有节点值都相等；此时插入到任意位置都正确。
 *
 * 由于给的是环上的任意节点，遍历时可能从中间开始，遇到从「最大值到最小值」的“折返点”。
 */
public class Solution_LC708 {

	// --------- 节点定义 ---------
	static class Node {
		int val;
		Node next;
		Node() {}
		Node(int _val) { val = _val; }
		Node(int _val, Node _next) { val = _val; next = _next; }
	}

	/**
	 * 在“排序的环形链表”中插入 insertVal 并返回原 head。
	 * 规则：
	 * 1) 常规升序区间：prev.val <= insertVal <= curr.val → 插入
	 * 2) 断点处(最大->最小)：prev.val > curr.val，若 insertVal >= prev.val 或 insertVal <= curr.val → 插入
	 * 3) 绕一圈都没插：说明所有值相等等情况，任意位置插入
	 */
	public Node insert(Node head, int insertVal) {
		Node x = new Node(insertVal);
		if (head == null) { // 空表
			x.next = x;
			return x;
		}
		Node prev = head, curr = head.next;
		while (true) {
			// 情况1：常规区间
			if (prev.val <= insertVal && insertVal <= curr.val) break;
			// 情况2：断点区（最大->最小）
			if (prev.val > curr.val) {
				if (insertVal >= prev.val || insertVal <= curr.val) break;
			}
			prev = curr;
			curr = curr.next;
			// 情况3：绕一圈（例如所有值相等），随处插入
			if (prev == head) break;
		}
		prev.next = x;
		x.next = curr;
		return head;
	}

	// ========= 工具：由升序数组构造环形链表 =========
	static Node buildCircle(int[] arr) {
		if (arr == null || arr.length == 0) return null;
		Node head = new Node(arr[0]), tail = head;
		for (int i = 1; i < arr.length; i++) {
			tail.next = new Node(arr[i]);
			tail = tail.next;
		}
		tail.next = head; // 成环
		return head;
	}

	// ========= 工具：将环形链表转为列表（从 head 开始走一圈）=========
	static List<Integer> toList(Node head) {
		List<Integer> res = new ArrayList<>();
		if (head == null) return res;
		Node cur = head;
		do {
			res.add(cur.val);
			cur = cur.next;
		} while (cur != head);
		return res;
	}

	static void printCircle(String title, Node head) {
		System.out.println(title + " -> " + toList(head));
	}

	// ================== 测试入口 ==================
	public static void main(String[] args) {
		Solution_LC708 sol = new Solution_LC708();

		// 用例1：空表插入
		Node h1 = null;
		h1 = sol.insert(h1, 3);
		printCircle("Case1 空表插入 3", h1); // [3]

		// 用例2：正常中间插入
		Node h2 = buildCircle(new int[]{1, 3, 4});
		sol.insert(h2, 2);
		printCircle("Case2 [1,3,4] 插入 2", h2); // [1,2,3,4]（环形等价顺序）

		// 用例3：插入最大（落到断点前）
		Node h3 = buildCircle(new int[]{1, 3, 5});
		sol.insert(h3, 6);
		printCircle("Case3 [1,3,5] 插入 6", h3); // [1,3,5,6]

		// 用例4：插入最小（跨断点）
		Node h4 = buildCircle(new int[]{1, 3, 5});
		sol.insert(h4, 0);
		printCircle("Case4 [1,3,5] 插入 0", h4); // [0,1,3,5]

		// 用例5：所有值相等
		Node h5 = buildCircle(new int[]{3, 3, 3});
		sol.insert(h5, 2);
		printCircle("Case5 [3,3,3] 插入 2", h5); // [3,3,3,2]（环形等价顺序）

		// 用例6：单节点
		Node h6 = buildCircle(new int[]{5});
		sol.insert(h6, 4);
		printCircle("Case6 [5] 插入 4", h6); // [4,5] 或 [5,4]
	}
}
