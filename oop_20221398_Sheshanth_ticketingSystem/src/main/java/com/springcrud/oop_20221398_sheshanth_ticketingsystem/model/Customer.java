package com.springcrud.oop_20221398_sheshanth_ticketingsystem.model;

public class Customer {
    private String customerId;  // Unique identifier for the customer
    private int priority;  // Priority of the customer (higher value means higher priority)
    private String eventId;  // The event associated with the customer

    // Constructor to initialize the customer with customerId, priority, and eventId
    public Customer(String customerId, int priority, String eventId) {
        this.customerId = customerId;
        this.priority = priority;
        this.eventId = eventId;
    }

    // Getter for customerId
    public String getCustomerId() {
        return customerId;
    }

    // Getter for priority
    public int getPriority() {
        return priority;
    }

    // Getter for eventId
    public String getEventId() {
        return eventId;
    }

    @Override
    public String toString() {
        // Override toString method to provide a meaningful string representation of the customer
        return "Customer{id=" + customerId + ", priority=" + priority + ", event='" + eventId + "'}";
    }
}