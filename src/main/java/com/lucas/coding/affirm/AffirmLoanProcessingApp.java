package com.lucas.coding.affirm;

import java.util.*;

/**
 * Affirm Loan Requests Processing - Part 1
 *
 * 示例输入层级：
 * {
 *   "company_a": ["company_b", "company_c"],
 *   "company_d": ["company_a"]
 * }
 *
 * 设计要点：
 * 1) 构建 child -> parent 映射（反转层级关系）；
 * 2) 对任意公司，沿着 parent 链向上追溯直到没有父亲；
 * 3) 用 Map<String, String> rootMap 保存 “公司 -> 顶层母公司”；
 * 4) 再将所有 Loan 按 root 分组：
 *      - Map<String, List<Loan>>  （root -> loans）
 *
 * 后续扩展（Part2）：
 * - 可添加 Transaction 模型与匹配逻辑（按 root + loanId 查找命中）
 * - 或增加 Map<String, Map<String, Loan>> 结构，支持 O(1) 精确匹配
 *
 * 复杂度：
 * 构建映射 O(N+M)，求根 O(N)，分组 O(L)；总复杂度 O(N+M+L)
 */
public class AffirmLoanProcessingApp {

	/** Loan 数据模型 */
	static class Loan {
		String id, company;
		double amount;

		Loan(String id, String company, double amount) {
			this.id = id;
			this.company = company;
			this.amount = amount;
		}

		public String toString() {
			return String.format("(%s, %.0f)", id, amount);
		}
	}

	/** 内部服务类，封装核心逻辑 */
	static class RootService {
		/**
		 * 计算每个公司对应的顶层母公司
		 */
		public Map<String, String> getRootMap(Map<String, List<String>> tree) {
			Map<String, String> parent = new HashMap<>(); // child -> parent
			Set<String> all = new HashSet<>();
			all.addAll(tree.keySet());

			// 2) 反转映射：parent -> [children] 变 child -> parent，并把子公司放到 all
			for (var e : tree.entrySet()) {
				String p = e.getKey();
				List<String> children = e.getValue();
				for (String c : children) {
					parent.put(c, p);
					all.add(c);
				}
			}

			// 3) 求每个公司的根；findRoot 内部带路径写回
			Map<String, String> root = new HashMap<>();
			for (String comp : all) {
				findRoot(comp, parent, root);
			}
			return root;
		}

		/**
		 * 找单个公司的顶层母公司（简化版实现）
		 * 沿着 parent 链一路往上走直到没有父节点
		 * 记忆化避免重复计算
		 */
		private String findRoot(String comp, Map<String, String> parent, Map<String, String> root) {
			if (root.containsKey(comp)) {
				return root.get(comp);
			}

			String cur = comp;
			while (parent.containsKey(cur)) {
				cur = parent.get(cur);
			}

			root.put(comp, cur);
			return cur;
		}

		/**
		 * 将贷款按顶层母公司分组
		 */
		public Map<String, List<Loan>> groupLoansByRoot(List<Loan> loans, Map<String, String> rootMap) {
			Map<String, List<Loan>> grouped = new HashMap<>();
			for (Loan loan : loans) {
				String root = rootMap.getOrDefault(loan.company, loan.company);
				grouped.computeIfAbsent(root, k -> new ArrayList<>()).add(loan);
			}
			return grouped;
		}
	}

	public static void main(String[] args) {
		RootService rootService = new RootService();

		// 示例层级数据
		Map<String, List<String>> tree = new HashMap<>();
		tree.put("company_a", Arrays.asList("company_b", "company_c"));
		tree.put("company_d", Collections.singletonList("company_a"));

		// 计算每个公司的顶层母公司 -> {company: it's root company}
		Map<String, String> rootMap = rootService.getRootMap(tree);
		System.out.println("rootMap is : " + rootMap);

		// 示例贷款数据
		List<Loan> loans = Arrays.asList(
				new Loan("L1", "company_c", 1000),
				new Loan("L2", "company_b", 2000),
				new Loan("L3", "company_x", 500)
		);

		// 按顶层母公司分组贷款
		Map<String, List<Loan>> groupedLoans = rootService.groupLoansByRoot(loans, rootMap);

		// 输出结果
		for (var entry : groupedLoans.entrySet()) {
			System.out.println("root=" + entry.getKey() + " -> " + entry.getValue());
		}

		// root=company_d -> [(L1, 1000), (L2, 2000)]
		// root=company_x -> [(L3, 500)]
	}
}

