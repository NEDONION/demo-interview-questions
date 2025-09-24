package com.lucas.coding.intuit;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service scheduling
 *
 * Design and implement a service scheduler for an in-person customer service center
 * (Very similar to genius bar at the Apple centers or Xfinity store service).
 * Customer walks into the store and checks in.
 * They are given a ticket with a sequential service number.
 * The service number is called by the staff in the order determined by the scheduler.
 * There are 2 different tiers of customers:
 * - Regular customers: serviced in the order they arrive.
 * - VIP customers: given higher priorities compared to Regular customers.
 *
 * ticket -> id -> uuid for pk
 * sequential number: int -> auto-increased
 * meta data..
 * customerId,
 * ...

 * type/class -> Enum CustomerTier (Regular, VIP,...) -> String
 *
 *Do the following,
 * Design class for customer and ServiceScheduler with required high-level characteristics.
 * Implement the ServiceScheduler to serve ALL VIP customers before serving regular customers.
 * Implement two methods checkIn(Customer) getNextCustomer()
 * Implement the scheduler to make sure 2:1 VIP vs. Regular customer processing rate.
 * Modify getNextCustomer() method to implement the customer processing rate.
 *
 *  customer processing rate. -> rate limiter
 *
 */


public class ServiceScheduler {

	public static void main(String[] args) {
		ServiceScheduler scheduler = new ServiceScheduler();

		Customer customer = getCustomer("VIP-customer-id1", "VIP");

		Customer c2 = getCustomer("Regular-customer-id1", "REGULAR");
		Customer c3 = getCustomer("Regular-customer-id2", "REGULAR");

		Customer c4 = getCustomer("VIP-customer-id2", "VIP");
		Customer c5 = getCustomer("VIP-customer-id3", "VIP");
		Customer c6 = getCustomer("VIP-customer-id4", "VIP");

		scheduler.checkIn(c2); // REGULAR
		scheduler.checkIn(customer); // VIP
		scheduler.checkIn(c4); // VIP
		scheduler.checkIn(c5); // VIP
		scheduler.checkIn(c6); // VIP
		scheduler.checkIn(c3); // REGULAR

		String nextID = scheduler.getNextCustomer();
		System.out.println("next Id is: " + nextID);

		String n1 = scheduler.getNextCustomer();
		System.out.println("next Id is: " + n1);
		scheduler.getNextCustomer();
		scheduler.getNextCustomer();
		scheduler.getNextCustomer();
		scheduler.getNextCustomer();
		scheduler.getNextCustomer();
		scheduler.getNextCustomer();
	}

	private static Customer getCustomer(String customerId, String REGULAR) {
		Customer c2 = new Customer();
		c2.customerId = customerId;
		c2.customerName = UUID.randomUUID().toString();
		c2.customerTier = REGULAR;
		return c2;
	}

	// Queue -> Vip, regular
	Queue<Ticket> vipQueue; // ConcurrentLinkedQueue
	Queue<Ticket> regularQueue;
	int ticketNo = 0;

	int processedVIPCustomerNumber = 0;
	int processedRegularCustomerNumber = 0;

	public ServiceScheduler() {
		vipQueue = new ArrayDeque<>();
		regularQueue = new ArrayDeque<>();
	}

	/**
	 * 1. get the customer info
	 * 2. check his/her customerTier ->
	 * 2.1 vip -> vipQueue
	 * 2.2 else -> regularQueue
	 * 3. generate a ticket
	 */
	public void checkIn(Customer customer) {
		// valid checks
		if (customer == null) {
			System.out.println("Customer is null");
			return;
		}
		if (customer.customerTier == null) {
			System.out.println("Customer tier is null");
			return;
		}
		if (customer.customerId == null) {
			System.out.println("Customer id is null");
			return;
		}

		// going to business logic
		String customerTier = customer.customerTier;
		if (customerTier.equals("VIP")) {
			Ticket ticket = createTicket(customer);
			vipQueue.offer(ticket);
		} else if (customerTier.equals("REGULAR")) {
			Ticket ticket = createTicket(customer);
			regularQueue.offer(ticket);
		}
		System.out.println("Ticket no: is checked in to system" + ticketNo);
	}

	public String getNextCustomer() {

		// 1. if we have customer in vip queue -> process first
		// 1. vipQueue is not empty
		// 2. if we finish the vip -> regular

		// 1. if we have customer in vip queue -> process first
		if (!vipQueue.isEmpty()) {
			processedVIPCustomerNumber++; // counter as marker -> use as metrics in grafana

			// use for future traffics strategy
			Ticket ticket = vipQueue.poll();
			String customerId = ticket.customerId;
			System.out.println("getting next VIP Customer id: " + customerId);
			return customerId;
		}

		// 2. if we have customer in regular queue -> process second
		if (!regularQueue.isEmpty()) {
			processedRegularCustomerNumber++;
			Ticket ticket = regularQueue.poll();
			String customerId = ticket.customerId;
			// call a service to get Customer based on customer_id
			System.out.println("getting next Regular Customer id: " + customerId);
			return customerId;
		} else {
			// 3. finished all -> no customer
			System.out.println("No VIP and Regular Customer found");
		}
		return "Not_Customer_Found";
	}

	private Ticket createTicket(Customer customer) {

		Ticket ticket = new Ticket();
		// get the lock
		Lock lock = new ReentrantLock();
		lock.lock();

		try {
			ticket = new Ticket();
			ticket.id = UUID.randomUUID().toString();

			// simply -> global var ticketNo here
			// make sure safer -> 1. atomicInt -> has no lock and thread-safe
			// 2. thread-safe data type
			ticket.serviceSequentialNumber = ticketNo++;
			ticket.serviceState = "check-in";
			ticket.customerId = customer.customerId;
			ticket.createdAt = new Date(); // no time-zone
		} catch (Exception e) {
			System.out.println("Error creating ticket");
		} finally {
			lock.unlock();
		}

		return ticket;
	}

}

class Ticket {
	String id;
	Integer serviceSequentialNumber;
	String serviceState;
	String customerId;
	Date createdAt;
}

class Customer {
	String customerId;
	String customerName;
	// (Regular, VIP,...)
	String customerTier;
	Date createdAt;
}