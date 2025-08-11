package com.lucas.coding.doordash;

import java.util.*;
/**
 * Problem: Dashmart 最近距离 & 最忙 dashmart（BFS）
 * -----------------------------------------------
 * 给定二维网格：
 *   'D' = dashmart，'X' = 不可通行，' ' 或 '.' = 可通行。
 *
 * Part 1：对多组 location 计算到最近 dashmart 的最短步数（4 向移动）。
 *         若 location 越界/不可通行/不可达，返回 -1。
 *         解法：多源 BFS（所有 'D' 同时入队）。
 *
 * Follow-up：每个 dashmart 有 busy 值，随距离 d 衰减：score = decay(busy, d)。
 *            对每个位置，找“衰减后得分最高”的 dashmart（返回 dashmartId、距离、score）。
 *            示例中使用线性衰减：decay(base, d) = max(0, base - d)。
 *
 * 假设/细节：
 *   - 距离按最少步数（曼哈顿 4 邻接）计算。
 *   - 'D' 也视为可通行。
 *   - 复杂度：Part 1 为 O(m*n)；Follow-up（对每个 D 各跑一次 BFS）为 O(K*m*n)。
 *
 * ------------------------------------------------
 * 测试用例（对应 main 中的 3 组）：
 *
 * Test 1: 最近距离（multi-source BFS）
 *   grid1 =
 *     "  XD"
 *     "X X "
 *     "    "
 *     "DX  "
 *   locations1 = [(2,0), (0,0), (3,3), (1,3), (0,2)]
 *   期望输出（距离）= [1, 5, 3, 1, -1]
 *
 * Test 2: 没有 dashmart 可达（网格中无 'D'）
 *   grid2 =
 *     " XX"
 *     " XX"
 *     "   "
 *   locations2 = [(2,1), (0,0), (1,2)]
 *   期望输出（距离）= [-1, -1, -1]
 *
 * Test 3: Follow-up 最忙 dashmart（线性衰减）
 *   使用 grid1；dashmart 的行优先顺序：
 *     id=0 -> (0,3), id=1 -> (3,0)
 *   busyLevels = [5.0, 9.0]
 *   decay(base, d) = max(0, base - d)
 *   locations3 = [(1,1), (2,3), (3,2), (0,1)]
 *   期望输出（bestDashId, distance, score）：
 *     (1,1) -> id=1, dist=3, score=6.0
 *     (2,3) -> id=1, dist=4, score=5.0
 *     (3,2) -> id=1, dist=4, score=5.0
 *     (0,1) -> id=1, dist=4, score=5.0
 */

public class DashmartBFS {

	// 4 方向
	private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

	// ---------- Part 1: 最近 dashmart 距离（多源 BFS） ----------
	public static int[][] nearestDistance(char[][] grid) {
		int m = grid.length, n = grid[0].length;
		int[][] dist = new int[m][n];
		for (int i = 0; i < m; i++) Arrays.fill(dist[i], Integer.MAX_VALUE);

		ArrayDeque<int[]> q = new ArrayDeque<>();
		// 所有 D 作为起点
		for (int r = 0; r < m; r++) {
			for (int c = 0; c < n; c++) {
				if (grid[r][c] == 'D') {
					dist[r][c] = 0;
					q.offer(new int[]{r, c});
				}
			}
		}
		while (!q.isEmpty()) {
			int[] cur = q.poll();
			int r = cur[0], c = cur[1];
			for (int[] d : DIRS) {
				int nr = r + d[0], nc = c + d[1];
				if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;
				if (!isWalkable(grid[nr][nc])) continue;      // 不能进 X
				if (dist[nr][nc] != Integer.MAX_VALUE) continue;
				dist[nr][nc] = dist[r][c] + 1;
				q.offer(new int[]{nr, nc});
			}
		}
		return dist;
	}

	private static boolean isWalkable(char ch) {
		return ch == 'D' || ch == ' ' || ch == '.'; // D 也允许行走
	}

	public static int[] answerDistances(char[][] grid, int[][] locations) {
		int[][] dist = nearestDistance(grid);
		int m = grid.length, n = grid[0].length;
		int[] ans = new int[locations.length];
		for (int i = 0; i < locations.length; i++) {
			int r = locations[i][0], c = locations[i][1];
			if (r < 0 || r >= m || c < 0 || c >= n) { ans[i] = -1; continue; }
			if (!isWalkable(grid[r][c])) { ans[i] = -1; continue; }
			ans[i] = dist[r][c] == Integer.MAX_VALUE ? -1 : dist[r][c];
		}
		return ans;
	}

	// ---------- Follow-up: 距离衰减的“最 busy dashmart” ----------
	public static class BestCell {
		public int bestDashId;   // -1 表示不可达
		public int bestDistance; // 到所选 dashmart 的距离
		public double bestScore; // 衰减后的 busy
		BestCell(int id, int d, double s) { bestDashId = id; bestDistance = d; bestScore = s; }
	}

	public interface Decay { double apply(double baseBusy, int distance); }

	private static List<int[]> enumerateDashmarts(char[][] grid) {
		int m = grid.length, n = grid[0].length;
		List<int[]> list = new ArrayList<>();
		for (int r = 0; r < m; r++)
			for (int c = 0; c < n; c++)
				if (grid[r][c] == 'D') list.add(new int[]{r, c});
		return list;
	}

	// 对每个 dashmart 各跑一次 BFS，更新每个格子的最优 busy
	public static BestCell[][] bestBusyDashmart(char[][] grid, double[] busyLevels, Decay decay) {
		int m = grid.length, n = grid[0].length;
		BestCell[][] best = new BestCell[m][n];
		for (int r = 0; r < m; r++)
			for (int c = 0; c < n; c++)
				best[r][c] = new BestCell(-1, -1, Double.NEGATIVE_INFINITY);

		List<int[]> dashes = enumerateDashmarts(grid);
		if (busyLevels.length != dashes.size())
			throw new IllegalArgumentException("busyLevels.length 必须等于网格中的 D 数量");

		for (int id = 0; id < dashes.size(); id++) {
			int[] src = dashes.get(id);
			double base = busyLevels[id];

			int[][] dist = new int[m][n];
			for (int i = 0; i < m; i++) Arrays.fill(dist[i], Integer.MAX_VALUE);
			ArrayDeque<int[]> q = new ArrayDeque<>();
			dist[src[0]][src[1]] = 0;
			q.offer(new int[]{src[0], src[1]});

			while (!q.isEmpty()) {
				int[] cur = q.poll();
				int r = cur[0], c = cur[1];
				int d = dist[r][c];

				if (isWalkable(grid[r][c])) {
					double score = decay.apply(base, d);
					if (score > best[r][c].bestScore) {
						best[r][c] = new BestCell(id, d, score);
					}
				}
				for (int[] dir : DIRS) {
					int nr = r + dir[0], nc = c + dir[1];
					if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;
					if (!isWalkable(grid[nr][nc])) continue;
					if (dist[nr][nc] != Integer.MAX_VALUE) continue;
					dist[nr][nc] = d + 1;
					q.offer(new int[]{nr, nc});
				}
			}
		}
		return best;
	}

	// 辅助：字符串数组转网格（每行长度必须一致）
	public static char[][] toGrid(String... rows) {
		int m = rows.length, n = rows[0].length();
		char[][] g = new char[m][n];
		for (int i = 0; i < m; i++) {
			if (rows[i].length() != n) throw new IllegalArgumentException("所有行长度必须一致");
			g[i] = rows[i].toCharArray(); // 空格会被原样读入
		}
		return g;
	}

	public static void main(String[] args) {
		// === Test 1: 最近距离 ===
		System.out.println("=== Test 1: nearest distance ===");
		// 可通行用空格（或 '.'），不可通行用 'X'，dashmart 用 'D'
		char[][] grid1 = toGrid(
				"  XD",  // 两个空格, X, D
				"X X ",
				"    ",
				"DX  "
		);
		int[][] locations1 = {{2,0},{0,0},{3,3},{1,3},{0,2}}; // (0,2) 是 X → -1
		int[] ans1 = answerDistances(grid1, locations1);
		for (int i = 0; i < locations1.length; i++) {
			System.out.printf("loc (%d,%d) -> distance %d%n",
					locations1[i][0], locations1[i][1], ans1[i]);
		}

		// === Test 2: 没有 dashmart 可达 ===
		System.out.println("\n=== Test 2: no dashmart reachable ===");
		char[][] grid2 = toGrid(
				" XX",
				" XX",
				"   "
		);
		int[][] locations2 = {{2,1},{0,0},{1,2}};
		int[] ans2 = answerDistances(grid2, locations2);
		for (int i = 0; i < locations2.length; i++) {
			System.out.printf("loc (%d,%d) -> distance %d%n",
					locations2[i][0], locations2[i][1], ans2[i]);
		}

		// === Test 3: Follow-up 最 busy dashmart（线性衰减：max(0, base - dist)）===
		System.out.println("\n=== Test 3: busiest dashmart (linear decay) ===");
		// grid1 中 D 的顺序为行优先： (0,3) -> id=0, (3,0) -> id=1
		double[] busy = {5.0, 9.0};                // 每个 dashmart 的基础 busy
		Decay linear = (base, d) -> Math.max(0, base - d); // 可替换为 base/(1+d) 等其他衰减
		BestCell[][] best = bestBusyDashmart(grid1, busy, linear);
		int[][] locations3 = {{1,1},{2,3},{3,2},{0,1}};
		for (int[] loc : locations3) {
			BestCell bc = best[loc[0]][loc[1]];
			System.out.printf("loc (%d,%d) -> bestDashId=%d, distance=%d, score=%.1f%n",
					loc[0], loc[1], bc.bestDashId, bc.bestDistance, bc.bestScore);
		}
	}
}
