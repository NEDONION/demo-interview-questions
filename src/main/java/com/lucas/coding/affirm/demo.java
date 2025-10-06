package com.lucas.coding.affirm;

import com.lucas.coding.affirm.AffirmLoanProcessingApp.Loan;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class demo {


	static class Loan {
		String id;
		String company;
		double amount;

		Loan(String id, String company, double amount) {
			this.id = id;
			this.company = company;
			this.amount = amount;
		}

		public String toString() {
			return String.format("[%s, %s, %s]", id, company, amount);
		}
	}

	static class Transaction {
		String loanId;
		String paidCompany;
		double amount;

		Transaction(String loanId, String paidCompany, double amount) {
			this.loanId = loanId;
			this.paidCompany = paidCompany;
			this.amount = amount;
		}
		@Override
		public String toString() {
			return String.format("[%s, %s, %s]", loanId, paidCompany, amount);
		}
	}

	/**
	 * 1. findRoot method as helper -> fetch the {company: it's root}
	 * 2. getRoot helper method
	 */
	static class RootService {

		// return {c: it's root as d}
		public Map<String, String> findRoot(Map<String, List<String>> tree) {
			Map<String, String> childToParentMap = new HashMap<>(); // parent -> child
			Set<String> all = new HashSet<>();
			all.addAll(tree.keySet()); // company_a, company_d, ...

			for (var e : tree.entrySet()) {
				String key = e.getKey();
				List<String> children = e.getValue();
				for (String c : children) {
					childToParentMap.put(c, key);
					all.add(c);
				}
			}

			// 3) 求每个公司的根；findRoot 内部带路径写回
			Map<String, String> rootMap = new HashMap<>();
			for (String companyName : all) {
				// 对于每一个Key 找到root
				findRoot(companyName, childToParentMap, rootMap);
			}

			System.out.println("childToParentMap is : " + childToParentMap);
			System.out.println("rootMap is : " + rootMap);

			return rootMap;
		}

		//	usage: 找单个公司的顶层母公司（简化版实现）
		//	logic: 沿着 parent 链一路往上走直到没有父节点
		private void findRoot(String companyName, Map<String, String> childToParentMap,
				Map<String, String> rootMap) {

			if (rootMap.containsKey(companyName)) {
				rootMap.get(companyName);
				return;
			}

			String currName = companyName;
			while (childToParentMap.containsKey(currName)) {
				currName = childToParentMap.get(currName);
			}

			// finished finding the root
			rootMap.put(companyName, currName);
		}


		public Map<String, List<Loan>> getGroupLoansByRoot(Map<String, String> rootMap, List<Loan> loans) {
			Map<String, List<Loan>> loansByRoot = new HashMap<>();

			for (Loan loan : loans) {
				String companyName = loan.company;
				String root = rootMap.getOrDefault(companyName, companyName);
				if (!loansByRoot.containsKey(root)) {
					loansByRoot.put(root, new ArrayList<>());
				}
				loansByRoot.put(root, List.of(loan));

			}
			System.out.println("loansByRoot is : " + loansByRoot);
			return loansByRoot;
		}

		/* -------------------- Part 2 -------------------- */

		/**
		 * 构建 root -> (loanId -> Loan) 索引，便于 O(1) 命中
		 * {
		 *   "company_d": {
		 *       "L1" -> Loan(id=L1, company=company_c, amount=1000),
		 *       "L2" -> Loan(id=L2, company=company_b, amount=2000)
		 *   },
		 *   "company_x": {
		 *       "L3" -> Loan(id=L3, company=company_x, amount=500)
		 *   }
		 * }
		 * */

		public Map<String, Map<String, Loan>> buildNestedLoanMapByRootCompany(Map<String, String> rootMap, List<Loan> loans) {
			Map<String, Map<String, Loan>> loanMapByRootCompany = new HashMap<>();
			for (Loan loan : loans) {
				String companyName = loan.company;
				String loanId = loan.id;
				String root = rootMap.getOrDefault(companyName, companyName);
				if (!loanMapByRootCompany.containsKey(root)) {
					loanMapByRootCompany.put(root, new HashMap<>());
				}
				loanMapByRootCompany.get(root).put(loanId, loan);
			}

			System.out.println("loanMapByRootCompany is : " + loanMapByRootCompany);
			return loanMapByRootCompany;
		}

		/**
		 * 对交易进行匹配：按（root + loanId）命中
		 * - 默认只按 loanId 精确命中；如需金额校验，打开 amount 校验段
		 */
		public List<String> matchTransactions(Map<String, String> rootMap,
				Map<String, Map<String, Loan>> loanMapByRootCompany,
				List<Transaction> txs) {
			List<String> out = new ArrayList<>();
			for (Transaction tx : txs) {
				String txCompanyName = tx.paidCompany;
				String txRoot = rootMap.getOrDefault(txCompanyName, txCompanyName);
				Map<String, Loan> bucket = loanMapByRootCompany.get(txRoot);
				Loan hit = (bucket == null) ? null : bucket.get(tx.loanId);

				if (hit == null) {
					out.add("UNMATCHED: " + tx + " (root=" + txRoot + ")");
					continue;
				}

				out.add("HIT: " + tx + " -> loan " + hit + " (root=" + txRoot + ")");
			}
			return out;
		}
	}

	/**
	 *  *   "company_a": ["company_b", "company_c"],
	 *  *   "company_d": ["company_a"]
	 *
	 *  company_d
		 * 	└── company_a
		 *      ├── company_b
		 *      └── company_c
	 */

	public static void main(String[] args) {
		Map<String, List<String>> tree = new HashMap<>();
		tree.put("company_a", Arrays.asList("company_b", "company_c"));
		tree.put("company_d", List.of("company_a"));

		RootService rootService = new RootService();
		Map<String, String> rootMap = rootService.findRoot(tree);

		Loan l1 = new Loan("L1", "company_c", 1000);
		Loan l2 = new Loan("L2", "company_b", 2000);
		Loan l3 = new Loan("L3", "company_x", 500);
		List<Loan> loans = Arrays.asList(l1, l2, l3);

		// get grouped loans
		// output = {company_d=[load id:L2 company:company_b amount:2000.0]}
		Map<String, List<Loan>> loansByRoot = rootService.getGroupLoansByRoot(rootMap, loans);

		// Part2：索引 + 匹配
		Map<String, Map<String, Loan>> loanMapByRootCompany = rootService.buildNestedLoanMapByRootCompany(rootMap, loans);

		List<Transaction> txs = Arrays.asList(
				new Transaction("L1", "company_a", 1000),   // root(a)=d -> 在 d 的桶里能命中 L1
				new Transaction("L2", "company_d", 2000),   // root(d)=d -> 命中 L2
				new Transaction("L9", "company_c", 999),    // loanId不存在 -> UNMATCHED
				new Transaction("L3", "company_x", 500)     // 独立 root=x -> 命中
		);

		List<String> results = rootService.matchTransactions(rootMap, loanMapByRootCompany, txs);
		System.out.println("match results:");
		for (String s : results) System.out.println(" - " + s);
 	}
}
