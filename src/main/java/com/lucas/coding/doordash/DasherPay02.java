package com.lucas.coding.doordash;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Dasher支付模型 - Part 2（门店内等待只归属本单）
 *
 * 规则总结（片段 [prev, cur) ）：
 * - 若此片段有“门店内等待”的订单（到店已Arrive但未Pick），只按这些订单数计费： minutes * |inStore| * 0.3
 * - 否则按在送订单总数（ongoing）计费： minutes * ongoing * 0.3
 *
 * 事件类型：
 *  - ACCEPT：接单
 *  - ARRIVE：到店
 *  - PICKUP：取餐
 *  - DELIVER：送达
 *
 * 扫描线要点：
 * 1) 事件按时间升序。相同时间先处理 DELIVER、再 PICKUP、再 ARRIVE、最后 ACCEPT
 *    ——避免“瞬时并发被多算”，并使状态在下一片段开始前已稳定。
 * 2) 片段使用左闭右开 [prev, cur) 计算分钟数。
 */
public class DasherPay02 {

	private static final double BASE_PER_MIN = 0.3;
	private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

	enum Type { ACCEPT, ARRIVE, PICKUP, DELIVER }

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

	/** 计算总收入（美元，保留两位） */
	public static double calcPay(List<Event> events) {
		if (events == null || events.isEmpty()) return 0.0;

		// 事件排序：时间升序；同刻优先级 DELIVER > PICKUP > ARRIVE > ACCEPT
		events.sort(Comparator.comparing((Event a) -> a.time).thenComparingInt(a -> priority(a.type)));

		double total = 0.0;
		int ongoing = 0;                 // 正在进行中的订单数（接到但未送达）
		Set<String> inStore = new HashSet<>(); // 到店未取餐的订单集合
		LocalTime prev = events.get(0).time;

		for (Event e : events) {
			long minutes = Duration.between(prev, e.time).toMinutes();
			if (minutes > 0) {
				int multiplier = !inStore.isEmpty() ? inStore.size() : ongoing;
				if (multiplier > 0) {
					total += minutes * multiplier * BASE_PER_MIN;
				}
			}

			// 状态转移（只影响之后片段）
			switch (e.type) {
				case ACCEPT:
					ongoing++;
					break;
				case ARRIVE:
					// 到店：进入“门店内等待”
					inStore.add(e.orderId);
					break;
				case PICKUP:
					// 取餐：离开“门店内等待”
					inStore.remove(e.orderId);
					break;
				case DELIVER:
					// 送达：必须不在门店等待集合中
					inStore.remove(e.orderId);
					ongoing--;
					if (ongoing < 0) {
						throw new IllegalStateException("Invalid sequence: ongoing<0 at " + e.time);
					}
					break;
			}
			prev = e.time;
		}

		return round2(total);
	}

	private static int priority(Type t) {
		return switch (t) {
			case DELIVER -> 0;
			case PICKUP -> 1;
			case ARRIVE -> 2;
			default -> 3; // ACCEPT
		};
	}

	private static double round2(double v) {
		return Math.round(v * 100.0) / 100.0;
	}

	// ---------------- Demo（题面示例） ----------------
	public static void main(String[] args) {
		List<Event> demo = Arrays.asList(
				new Event("06:15", Type.ACCEPT, "A"),
				new Event("06:18", Type.ACCEPT, "B"),
				new Event("06:19", Type.ARRIVE, "A"),
				new Event("06:22", Type.PICKUP, "A"),
				new Event("06:30", Type.ARRIVE, "B"),
				new Event("06:33", Type.PICKUP, "B"),
				new Event("06:36", Type.DELIVER, "A"),
				new Event("06:45", Type.DELIVER, "B")
		);
		System.out.println("final pay: $" + calcPay(demo)); // 期望：12.6
	}
}
