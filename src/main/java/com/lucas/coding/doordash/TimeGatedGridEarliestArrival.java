package com.lucas.coding.doordash;

/**
 * 题目：时间门控网格的最早到达时刻（Time-Gated Grid Earliest Arrival）
 * ---------------------------------------------------------------
 * 给定 n x n 整数矩阵 grid，grid[i][j] 表示该格子“最早可进入的时间”。
 * 从 (0,0) 出发到 (n-1,n-1)，每次可瞬时移动到上下左右相邻格子；
 * 若目标格子尚未开放（当前时间 < grid[x][y]），则需要等到它开放再进入。
 * 求：最早何时能够到达终点。
 *
 * 关键观察：
 * - 一条路径的到达时间 = 该路径上所有格子的开放时间的“最大值”；
 * - 我们要最小化这个“最大值”，可用 Dijkstra（最短路）来做：
 *   状态为格子 (i,j)，距离为到达它的最早时间 dist[i][j]。
 *   从 (r,c,t) 走到相邻 (nr,nc) 的代价是：
 *      next = max(t, grid[nr][nc])   // 移动本身不耗时，只受开放时间限制
 *
 * 复杂度：O(n^2 log n) 时间，O(n^2) 空间。
 * 备注：若题目变体规定“每移动一步耗时 1”，则把转移改为：
 *      next = max(t + 1, grid[nr][nc])。
 */
import java.util.*;

public class TimeGatedGridEarliestArrival {

	public int earliestArrival(int[][] grid) {
		int n = grid.length;
		int[][] dist = new int[n][n];
		for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);

		// 小根堆：{time, r, c}
		PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
		dist[0][0] = grid[0][0];                  // 起点也需要等到自己开放
		pq.offer(new int[]{dist[0][0], 0, 0});

		int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

		while (!pq.isEmpty()) {
			int[] cur = pq.poll();
			int t = cur[0], r = cur[1], c = cur[2];
			if (t != dist[r][c]) continue;        // 过期状态
			if (r == n - 1 && c == n - 1) return t;

			for (int[] d : DIRS) {
				int nr = r + d[0], nc = c + d[1];
				if (nr < 0 || nr >= n || nc < 0 || nc >= n) continue;
				int nt = Math.max(t, grid[nr][nc]);   // 只等开放时间，移动不计时
				if (nt < dist[nr][nc]) {
					dist[nr][nc] = nt;
					pq.offer(new int[]{nt, nr, nc});
				}
			}
		}
		return -1; // 理论上不会到这里
	}

	/* -------------------- 自带测试用例（直接运行 main） -------------------- */

	private static void check(int id, int actual, int expect) {
		if (actual != expect) {
			System.out.printf("Test %d ❌  got=%d, expect=%d%n", id, actual, expect);
		} else {
			System.out.printf("Test %d ✅  ans=%d%n", id, actual);
		}
	}

	public static void main(String[] args) {
		TimeGatedGridEarliestArrival s = new TimeGatedGridEarliestArrival();

		// Test 1：2x2 递增
		int[][] g1 = {
				{0, 2},
				{1, 3}
		};
		// 任意路径都必须进 (1,1) 且其开放时间是 3 → 答案 3
		check(1, s.earliestArrival(g1), 3);

		// Test 2：绕开高墙的有效路径（答案受终点 4 限制）
		int[][] g2 = {
				{0, 100, 100},
				{1,   2, 100},
				{100, 3,   4}
		};
		// 路径 0→1→2→3→4 的最大开放时间是 4 → 答案 4
		check(2, s.earliestArrival(g2), 4);

		// Test 3：终点较小但存在低值通路（可把最大值压到 2）
		int[][] g3 = {
				{0, 5, 5},
				{1, 3, 5},
				{2, 2, 2}
		};
		// 走到底行：0→1→2→2→2，最大开放时间为 2 → 答案 2
		check(3, s.earliestArrival(g3), 2);

		// Test 4：起点就晚开放；必须穿过 100 墙
		int[][] g4 = {
				{5,   100},
				{100,   0}
		};
		// 0,0 要等到 5；但邻居都 100，最终到达终点的最早时间 100
		check(4, s.earliestArrival(g4), 100);

		// Test 5：终点值小但被 5 围出通路 → 最小可行最大值是 5
		int[][] g5 = {
				{0, 5, 1},
				{5, 5, 1},
				{1, 1, 1}
		};
		// 任何到达 (2,2) 的路径都要穿过值为 5 的格子 → 答案 5
		check(5, s.earliestArrival(g5), 5);
	}
}
