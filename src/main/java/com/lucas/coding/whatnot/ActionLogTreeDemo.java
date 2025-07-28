package com.lucas.coding.whatnot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/***
 * USER  TIME   ACTION
 * 100   1000   A
 * 200   1100   A
 * 200   1200   B
 * 100   1200   B
 * 100   1300   C
 * 200   1400   A
 * 300   1500   B
 * 300   1550   B
 */
public class ActionLogTreeDemo {

	public static void main(String[] args) {
		List<LogEntry> logs = Arrays.asList(
				new LogEntry(100, 1000, "A"),
				new LogEntry(200, 1100, "A"),
				new LogEntry(200, 1200, "B"),
				new LogEntry(100, 1200, "B"),
				new LogEntry(100, 1300, "C"),
				new LogEntry(200, 1400, "A"),
				new LogEntry(300, 1500, "B"),
				new LogEntry(300, 1550, "B")
		);

		ActionLogAnalyzer analyzer = new ActionLogAnalyzer();
		analyzer.ingestLogs(logs);
		analyzer.printTree();
	}
}

// ========== Data Classes ==========

class LogEntry {
	int userId;
	int timestamp;
	String action;

	public LogEntry(int userId, int timestamp, String action) {
		this.userId = userId;
		this.timestamp = timestamp;
		this.action = action;
	}
}

class AttributionNode {
	String action;
	int count;
	Map<String, AttributionNode> children;

	public AttributionNode(String action) {
		this.action = action;
		this.count = 0;
		this.children = new HashMap<>();
	}
}

// ========== Analyzer ==========

@Getter
class ActionLogAnalyzer {
	private final AttributionNode root = new AttributionNode("root");

	/***
	 * {
	 *   100: [
	 *     LogEntry(100, 1000, "A"),
	 *     LogEntry(100, 1200, "B"),
	 *     LogEntry(100, 1300, "C")
	 *   ],
	 *   200: [
	 *     LogEntry(200, 1100, "A"),
	 *     LogEntry(200, 1200, "B"),
	 *     LogEntry(200, 1400, "A")
	 *   ],
	 *   300: [
	 *     LogEntry(300, 1500, "B"),
	 *     LogEntry(300, 1550, "B")
	 *   ]
	 * }
	 */
	public void ingestLogs(List<LogEntry> logs) {
		// 1. Group logs by userId
		Map<Integer, List<LogEntry>> logsByUserMap = new HashMap<>();
		for (LogEntry log : logs) {
			if (!logsByUserMap.containsKey(log.userId)) {
				logsByUserMap.put(log.userId, new ArrayList<>());
			}
			logsByUserMap.get(log.userId).add(log);

		}

		// 2. Process each user's logs in timestamp order
		for (List<LogEntry> userLogs : logsByUserMap.values()) {
			userLogs.sort(Comparator.comparingInt(l -> l.timestamp));
			AttributionNode current = root;
			// Step 2: 构建路径树
			for (LogEntry log : userLogs) {
				// 当前节点计数 +1
				current.count++;

				// 若子节点不存在，则新建
				if (!current.children.containsKey(log.action)) {
					current.children.put(log.action, new AttributionNode(log.action));
				}

				// 移动到子节点
				current = current.children.get(log.action);
			}

			// 最后一个 action 节点也计数
			current.count++;
		}
	}

	public void printTree() {
		for (AttributionNode child : root.children.values()) {
			printTreeRecursive(child, 0);
		}
	}

	/**
	 * A (2)
	 *   -> B (2)
	 *     -> C (1)
	 *     -> A (1)
	 * B (1)
	 *   -> B (1)
	 */
	private void printTreeRecursive(AttributionNode node, int depth) {
		System.out.println("  ".repeat(depth) + node.action + " (" + node.count + ")");
		for (AttributionNode child : node.children.values()) {
			printTreeRecursive(child, depth + 1);
		}
	}
}
