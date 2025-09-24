package com.lucas.coding.meta;

import java.util.*;

/**
 * LC 489. Robot Room Cleaner
 *
 * IDEA（思路）：
 * - 把起点看作 (0,0)，用 DFS + 回溯遍历未知房间。
 * - 每到新格子：clean() + 标记 visited。
 * - 按当前朝向依次尝试 4 个方向：
 *   * 若前方 move() 成功 -> 根据朝向得到 (nx,ny) 递归 -> 递归返回后 goBack() 回到原位与朝向。
 *   * 每轮末尾 turnRight()，使物理朝向与 (dir+i+1)%4 对齐。
 * - 关键：回溯必须把“位置+朝向”都恢复。
 *
 * time: O(n*m) n, m is the length of grid
 *  space: O(n*m) n, m is the length of grid
 */
public class Solution_LC489 {

	/** 题目约定的 Robot 接口 */
	interface Robot {
		boolean move();
		void turnLeft();
		void turnRight();
		void clean();
	}

	/** 解法：DFS + 回溯（标准转向模板） */
	static class Cleaner {
		// 0上,1右,2下,3左
		private final int[][] dirs = {{-1,0},{0,1},{1,0},{0,-1}};
		private final Set<String> visited = new HashSet<>();

		public void cleanRoom(Robot robot) {
			dfs(robot, 0, 0, 0);
		}

		private void dfs(Robot robot, int x, int y, int dir) {
			String key = x + "#" + y;
			if (visited.contains(key)) return;
			visited.add(key);
			robot.clean();

			for (int i = 0; i < 4; i++) {
				int ndir = (dir + i) % 4;
				int nx = x + dirs[ndir][0];
				int ny = y + dirs[ndir][1];

				// 此时物理朝向 == (dir + i)，可以直接尝试 move()
				if (!visited.contains(nx + "#" + ny) && robot.move()) {
					dfs(robot, nx, ny, ndir);
					goBack(robot); // 回到 (x,y) 并恢复物理朝向为 ndir
				}
				robot.turnRight(); // 右转到下一方向 (dir+i+1)%4
			}
		}

		// 经典回退模板：RR -> move -> RR
		private void goBack(Robot robot) {
			robot.turnRight();
			robot.turnRight();
			robot.move();
			robot.turnRight();
			robot.turnRight();
		}
	}

	/** ======== 本地仿真器与测试 ======== */

	static class RobotSim implements Robot {
		private final int[][] grid;
		private final boolean[][] cleaned;
		private final int n, m;

		// 当前位置与方向
		private int r, c, dir;
		// 保存原始起点用于统计 BFS
		private final int startR, startC;

		public RobotSim(int[][] grid, int startR, int startC, int startDir) {
			if (grid == null || grid.length == 0 || grid[0].length == 0)
				throw new IllegalArgumentException("grid 不能为空");
			this.n = grid.length;
			this.m = grid[0].length;
			this.grid = copy(grid);
			if (!inBounds(startR, startC) || grid[startR][startC] == 1)
				throw new IllegalArgumentException("起点非法或在障碍上: (" + startR + "," + startC + ")");
			this.cleaned = new boolean[n][m];
			this.r = this.startR = startR;
			this.c = this.startC = startC;
			this.dir = (startDir % 4 + 4) % 4;
		}

		@Override public boolean move() {
			int nr = r + dRow(dir), nc = c + dCol(dir);
			if (!inBounds(nr, nc) || grid[nr][nc] == 1) return false;
			r = nr; c = nc;
			return true;
		}
		@Override public void turnLeft()  { dir = (dir + 3) % 4; }
		@Override public void turnRight() { dir = (dir + 1) % 4; }
		@Override public void clean()     { cleaned[r][c] = true; }

		public int cleanedCount() {
			int cnt = 0;
			for (int i = 0; i < n; i++)
				for (int j = 0; j < m; j++)
					if (cleaned[i][j]) cnt++;
			return cnt;
		}

		/** 用原始起点 BFS 统计可达格子数 */
		public int reachableCountFromStart() {
			boolean[][] vis = new boolean[n][m];
			Deque<int[]> q = new ArrayDeque<>();
			q.offer(new int[]{startR, startC});
			vis[startR][startC] = true;
			int count = 0;
			while (!q.isEmpty()) {
				int[] cur = q.poll();
				count++;
				for (int k = 0; k < 4; k++) {
					int nr = cur[0] + dRow(k), nc = cur[1] + dCol(k);
					if (inBounds(nr, nc) && grid[nr][nc] == 0 && !vis[nr][nc]) {
						vis[nr][nc] = true; q.offer(new int[]{nr, nc});
					}
				}
			}
			return count;
		}

		public void printCleanedMap() {
			System.out.println("清洁结果（C=清洁, #=障碍, .=可走未清洁）：");
			for (int i = 0; i < n; i++) {
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < m; j++) {
					if (grid[i][j] == 1) sb.append('#');
					else sb.append(cleaned[i][j] ? 'C' : '.');
				}
				System.out.println(sb);
			}
		}

		private boolean inBounds(int x, int y) { return x >= 0 && x < n && y >= 0 && y < m; }
		private static int dRow(int d) { return new int[]{-1,0,1,0}[d]; }
		private static int dCol(int d) { return new int[]{0,1,0,-1}[d]; }
		private static int[][] copy(int[][] a) {
			int[][] b = new int[a.length][];
			for (int i = 0; i < a.length; i++) b[i] = Arrays.copyOf(a[i], a[i].length);
			return b;
		}
	}

	static void runCase(String name, int[][] grid, int sr, int sc, int startDir) {
		System.out.println("==== 测试用例: " + name + " ====");
		RobotSim sim = new RobotSim(grid, sr, sc, startDir);
		Cleaner solver = new Cleaner();
		solver.cleanRoom(sim);

		int cleaned = sim.cleanedCount();
		int reachable = sim.reachableCountFromStart();
		sim.printCleanedMap();
		System.out.println("起点可达格子数: " + reachable);
		System.out.println("实际清洁格子数: " + cleaned);
		System.out.println(cleaned == reachable ? "✅ 通过（全部可达格子均已清洁）" : "❌ 未清洁完全");
		System.out.println();
	}

	public static void main(String[] args) {
		int[][] grid1 = {
				{0,0,0,0},
				{0,1,1,0},
				{0,0,0,0},
				{0,1,0,0}
		};
		runCase("Case-1 基本障碍图", grid1, 1, 0, 0);

		int[][] grid2 = {
				{0,1,0,0,0},
				{0,1,0,1,0},
				{0,0,0,1,0},
				{1,1,0,0,0}
		};
		runCase("Case-2 狭窄走廊", grid2, 2, 0, 1);

		int[][] grid3 = {
				{1,1,1,1,1,1},
				{1,0,0,0,1,1},
				{1,0,1,0,0,1},
				{1,0,0,0,1,1},
				{1,1,1,1,1,1}
		};
		runCase("Case-3 封闭空间", grid3, 2, 1, 0);

		int[][] grid4 = new int[5][6]; // 全 0
		runCase("Case-4 无障碍大房间", grid4, 0, 0, 0);
	}
}
