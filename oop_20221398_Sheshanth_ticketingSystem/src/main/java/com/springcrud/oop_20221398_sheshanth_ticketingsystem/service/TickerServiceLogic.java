package com.springcrud.oop_20221398_sheshanth_ticketingsystem.service;

import com.springcrud.oop_20221398_sheshanth_ticketingsystem.cli.CustomerThread;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.cli.TicketPool;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.cli.TicketProducer;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.model.Customer;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.model.Ticket;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.model.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class TickerServiceLogic {

    private final TicketPool ticketPool;
    private final Map<String, Long> lastpurchasetimemap = new ConcurrentHashMap<>();
    private final Map<String, Boolean> purchaseInProgressMap = new ConcurrentHashMap<>();
    private final Map<String, ConcurrentLinkedDeque<Customer>> eventPriorityQueue = new ConcurrentHashMap<>();
    private final Map<String, Customer> vipCustomer = new ConcurrentHashMap<>();

    @Autowired // automaticaly creates new obj
    public TickerServiceLogic(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    // Getter for eventPriorityQueue
    public ConcurrentLinkedDeque<Customer> getEventPriorityQueue(String eventID) {
        return eventPriorityQueue.getOrDefault(eventID, new ConcurrentLinkedDeque<>());
    }

    // Getter for ticketPool
    public TicketPool getTicketPool() {
        return ticketPool;
    }

    // Getter for lastpurchasetimemap
    public Map<String, Long> getLastPurchaseTimeMap() {
        return lastpurchasetimemap;
    }

    // Log a transaction
    public void logTransaction(String message) {
        System.out.println("[LOG] " + message);
    }

    // Add tickets to the pool (new tickets for an event)
    public void addtickets(int numoftickets, Vendor vendor, String eventid, String eventname, int releaseRate) {
        if (ticketPool == null || vendor == null || eventid == null || eventname == null) {
            throw new IllegalArgumentException("Invalid arguments provided to addtickets method");
        }

        TicketProducer producer = new TicketProducer(ticketPool, vendor, numoftickets, eventid, eventname, releaseRate);
        producer.start(); // calling the run method internaly
        logTransaction("Started releasing " + numoftickets + " tickets for event " + eventname +
                " by vendor " + vendor.getName() + " with release rate of " + releaseRate + " seconds");
    }

    // Get all events with their ticket count
    public Map<String, String> getalleventswithticketcount() {
        Map<String, String> eventdetails = ticketPool.getAllEVENTWITHTICKETCOUNT();
        logTransaction("Fetched all events with their ticket counts");
        return eventdetails;
    }

    // Register a VIP customer
    public String registervip(String customername) {
        String customerID = UUID.randomUUID().toString();
        Customer vipCustomer = new Customer(customerID, 10, "VIP");
        this.vipCustomer.put(customerID, vipCustomer);
        return customerID;
    }

    // Check if the customer is VIP
    public boolean isvips(String customerid) {
        return vipCustomer.containsKey(customerid);
    }

    // Purchase tickets for a customer
    public void purchaseTickets(String clientID, String eventID, int numoftickets, int purchaseRate, boolean isVip) {
        if (ticketPool == null || clientID == null || eventID == null) {
            throw new IllegalArgumentException("Invalid arguments provided to purchaseTickets method");
        }

        synchronized (this) {
            if (purchaseInProgressMap.getOrDefault(eventID, false)) {
                System.out.println("Purchase is already in progress for event " + eventID);
                return;
            }
            purchaseInProgressMap.put(eventID, true);
        }

        try {
            CustomerThread customerThread = new CustomerThread(clientID, this, numoftickets, eventID, purchaseRate, isVip);
            customerThread.start();
            logTransaction("Started purchasing " + numoftickets + " tickets for event " + eventID +
                    " by client " + clientID + " with a purchase rate of " + purchaseRate + " seconds.");
        } finally {
            purchaseInProgressMap.put(eventID, false);
        }
    }

    // Add a priority customer to the queue for an event
    public void addpriorcustomer(Customer customer) {
        String eventid = customer.getEventId();
        eventPriorityQueue.putIfAbsent(eventid, new ConcurrentLinkedDeque<>());
        eventPriorityQueue.get(eventid).offer(customer);
        logTransaction("Added priority customer " + customer.getCustomerId() + " to queue for event " + eventid);
    }

    // Get the last purchase time for an event
    public long getLASTpurchase(String eventid) {
        return lastpurchasetimemap.getOrDefault(eventid, 0L);
    }
}
