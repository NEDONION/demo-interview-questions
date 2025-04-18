package com.lucas.coding.parkinglot;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Ticket {
	// Getters and Setters
	private String id;
	private String vehicleId;
	private LocalDateTime entryTime;
	private LocalDateTime exitTime;
	private int level;
	private double price;
}
