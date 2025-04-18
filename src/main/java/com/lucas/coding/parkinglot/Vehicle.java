package com.lucas.coding.parkinglot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vehicle {
	private String plateNumber;
	private VehicleType type;

	public Vehicle(String plateNumber, VehicleType type) {
		this.plateNumber = plateNumber;
		this.type = type;
	}
}
