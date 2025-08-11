package com.lucas.coding.doordash;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DasherPay03 {

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

	public static double calcPay(List<Event> events, List<String[]> rushWindows) {
		if (events == null || events.isEmpty()) return 0.0;

		events.sort(Comparator
				.comparing((Event a) -> a.time)
				.thenComparingInt(a -> priority(a.type)));

		double total = 0.0;
		int ongoing = 0;
		Set<String> inStore = new HashSet<>();
		LocalTime prev = events.get(0).time;

		for (Event e : events) {
			long minutes = Duration.between(prev, e.time).toMinutes();
			if (minutes > 0) {
				int mul = !inStore.isEmpty() ? inStore.size() : ongoing;
				if (mul > 0) {
					int a = toMinute(prev);
					int b = toMinute(e.time);
					// 直接判断每个窗口
					int rushMinutes = 0;
					for (String[] w : rushWindows) {
						int ws = toMinute(w[0]);
						int we = toMinute(w[1]);
						if (we <= a || ws >= b) continue;
						int s = Math.max(a, ws);
						int end = Math.min(b, we);
						rushMinutes += (end - s);
					}
					long nonRush = minutes - rushMinutes;

					total += nonRush * mul * BASE_PER_MIN;
					total += rushMinutes * mul * BASE_PER_MIN * 2;
				}
			}

			switch (e.type) {
				case ACCEPT -> ongoing++;
				case ARRIVE -> inStore.add(e.orderId);
				case PICKUP -> inStore.remove(e.orderId);
				case DELIVER -> {
					inStore.remove(e.orderId);
					ongoing--;
				}
			}
			prev = e.time;
		}

		return Math.round(total * 100.0) / 100.0;
	}

	private static int priority(Type t) {
		return switch (t) {
			case DELIVER -> 0;
			case PICKUP -> 1;
			case ARRIVE -> 2;
			default -> 3;
		};
	}

	private static int toMinute(LocalTime t) { return t.getHour() * 60 + t.getMinute(); }
	private static int toMinute(String hhmm) {
		String[] p = hhmm.split(":");
		return Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1]);
	}

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
		List<String[]> rush = Arrays.asList(
				new String[]{"06:20", "06:30"},
				new String[]{"07:00", "07:15"}
		);
		System.out.println("final pay: $" + calcPay(demo, rush)); // 18.0
	}
}
