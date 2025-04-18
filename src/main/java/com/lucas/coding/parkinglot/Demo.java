package com.lucas.coding.parkinglot;

public class Demo {
	public static void main(String[] args) {
		ParkingService service = new ParkingService();
		System.out.println("Available: " + service.getAvailableCount(1));

		Ticket t = service.parkVehicle("ABC123", VehicleType.CAR, 1);
		// 模拟停车3小时
		t.setEntryTime(t.getEntryTime().minusHours(3));
		service.clearSpot(t.getId());
	}
}
