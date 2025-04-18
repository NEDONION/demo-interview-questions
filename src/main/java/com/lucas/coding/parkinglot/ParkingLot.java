package com.lucas.coding.parkinglot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingLot {

	private int id;
	private String name;
	private int level;
	private int totalSpots;
	private int availableSpots;

	public ParkingLot(int id, String name, int level, int totalSpots, int availableSpots) {
		this.id = id;
		this.name = name;
		this.level = level;
		this.totalSpots = totalSpots;
		this.availableSpots = availableSpots;
	}
}