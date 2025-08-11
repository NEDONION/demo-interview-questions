package com.lucas.coding.doordash;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Dasher 支付模型 - 扫描线（Sweep Line）解法
 *
 * 思路：
 * 1. 将所有事件（接单 ACCEPT / 送达 DELIVER）放在时间线上，按时间排序。
 * 2. 扫描事件，维护当前正在送的订单数 ongoing。
 * 3. 每次到一个事件时：
 *    - 计算与上一个事件之间的分钟差 minutes
 *    - 收入 += minutes × ongoing × 基础单价
 *    - 根据事件类型更新 ongoing（接单+1，送达-1）
 *
 * 注意：
 * - 同一时间点可能有多个事件，必须先处理 DELIVER 再处理 ACCEPT，
 *   否则会把瞬间接单+送达当成并行时间，导致多算。
 * - 时间用 LocalTime 方便计算分钟差；分钟数也可用 int 表示（优化性能）。
 *
 * 时间复杂度：
 * - 排序 O(n log n) + 扫描 O(n)
 * - 若输入已按时间排序，则是 O(n)。
 */
public class DasherPay01 {

	// 基础时薪（每分钟）
	private static final double BASE_PER_MIN = 0.3;
	// 时间格式：HH:mm
	private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

	/** 事件类型：接单 / 送达 */
	enum Type { ACCEPT, DELIVER }

	/** 事件结构：时间 + 类型 + 订单ID */
	static class Event {
		final LocalTime time;
		final Type type;
		final String orderId;

		Event(String hhmm, Type type, String orderId) {
			this.time = LocalTime.parse(hhmm, HH_MM);
			this.type = type;
			this.orderId = orderId;
		}
	}

	/**
	 * 核心计算方法
	 */
	public static double calcPay(List<Event> events) {
		if (events == null || events.isEmpty()) return 0.0;

		// 1. 按时间排序
		//    - 时间升序
		//    - 同一时刻：DELIVER 在前，避免瞬时并发多算
		events.sort((a, b) -> {
			int cmp = a.time.compareTo(b.time);
			if (cmp != 0) return cmp;
			if (a.type == b.type) return 0;
			return (a.type == Type.DELIVER) ? -1 : 1;
		});

		double total = 0.0;   // 总收入
		int ongoing = 0;      // 当前正在送的订单数
		LocalTime prev = events.get(0).time; // 上一个事件时间

		// 2. 扫描所有事件
		for (Event e : events) {
			// 计算 prev -> 当前事件时间的分钟差
			long minutes = Duration.between(prev, e.time).toMinutes();

			// 如果有正在送的单，则这一段产生收入
			if (minutes > 0 && ongoing > 0) {
				total += minutes * ongoing * BASE_PER_MIN;
			}

			// 更新正在送的订单数
			if (e.type == Type.ACCEPT) {
				ongoing++;
			} else { // DELIVER
				ongoing--;
				if (ongoing < 0) {
					throw new IllegalStateException("Invalid sequence: ongoing < 0");
				}
			}

			// 更新 prev
			prev = e.time;
		}

		// 题目保证当天结束所有单都送达，所以不用处理尾段
		return roundToCents(total);
	}

	/** 保留两位小数（美分） */
	private static double roundToCents(double v) {
		return Math.round(v * 100.0) / 100.0;
	}

	// ---------------------- 示例 ----------------------
	public static void main(String[] args) {
		List<Event> demo = Arrays.asList(
				new Event("06:15", Type.ACCEPT, "A"),
				new Event("06:18", Type.ACCEPT, "B"),
				new Event("06:36", Type.DELIVER, "A"),
				new Event("06:45", Type.DELIVER, "B")
		);
		double pay = calcPay(demo);
		System.out.println("final pay: $" + pay); // 期望：14.4
	}
}
