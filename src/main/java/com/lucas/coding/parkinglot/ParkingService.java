package com.lucas.coding.parkinglot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ParkingService {

	private static final double HOURLY_RATE_CAR = 2.0;
	private static final double HOURLY_RATE_VAN = 3.0;
	private static final double HOURLY_RATE_TRUCK = 5.0;

	// 假数据库
	private final Map<String, Vehicle> vehicleTable = new HashMap<>();
	private final Map<Integer, ParkingLot> parkingLotTable = new HashMap<>();
	private final Map<String, Ticket> ticketTable = new HashMap<>();

	public ParkingService() {
		// 模拟初始化一个停车场楼层
		parkingLotTable.put(1, new ParkingLot(1, "Garage A", 1, 50, 50));
	}

	// 查询楼层可用车位
	public int getAvailableCount(int level) {
		ParkingLot lot = parkingLotTable.get(level);
		return lot != null ? lot.getAvailableSpots() : 0;
	}

	// 停车操作
	public Ticket parkVehicle(String plateNumber, VehicleType type, int level) {
		ParkingLot lot = parkingLotTable.get(level);
		if (lot == null || lot.getAvailableSpots() <= 0) {
			System.out.println("No available spots on level " + level);
			return null;
		}

		// 注册车辆（如果不存在）
		vehicleTable.putIfAbsent(plateNumber, new Vehicle(plateNumber, type));

		// 创建 ticket
		Ticket ticket = new Ticket();
		ticket.setId(UUID.randomUUID().toString());
		ticket.setVehicleId(plateNumber);
		ticket.setEntryTime(LocalDateTime.now());
		ticket.setLevel(level);

		// 更新表
		ticketTable.put(ticket.getId(), ticket);
		lot.setAvailableSpots(lot.getAvailableSpots() - 1);

		System.out.println("Parked: " + plateNumber + " | Ticket ID: " + ticket.getId());
		return ticket;
	}

	// 出场并计费
	public Ticket clearSpot(String ticketId) {
		Ticket ticket = ticketTable.get(ticketId);
		if (ticket == null) {
			System.out.println("Ticket not found.");
			return null;
		}

		ticket.setExitTime(LocalDateTime.now());

		Vehicle vehicle = vehicleTable.get(ticket.getVehicleId());
		double rate = getRate(vehicle.getType());
		long minutes = Duration.between(ticket.getEntryTime(), ticket.getExitTime()).toMinutes();
		long hours = (minutes + 59) / 60; // 向上取整
		ticket.setPrice(hours * rate);

		// 更新可用车位
		ParkingLot lot = parkingLotTable.get(ticket.getLevel());
		if (lot != null) {
			lot.setAvailableSpots(lot.getAvailableSpots() + 1);
		}

		System.out.println("Vehicle exited. Price: $" + ticket.getPrice());
		return ticket;
	}

	private double getRate(VehicleType type) {
		return switch (type) {
			case CAR -> HOURLY_RATE_CAR;
			case VAN -> HOURLY_RATE_VAN;
			case TRUCK -> HOURLY_RATE_TRUCK;
		};
	}
}

