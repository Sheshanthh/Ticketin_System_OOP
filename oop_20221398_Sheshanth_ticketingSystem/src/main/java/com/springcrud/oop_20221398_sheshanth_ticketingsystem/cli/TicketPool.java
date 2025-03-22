package com.springcrud.oop_20221398_sheshanth_ticketingsystem.cli;

import com.springcrud.oop_20221398_sheshanth_ticketingsystem.model.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;// importing all nessary classes

@Component // Spring-managed bean
public class TicketPool {

    // Instance-level variable for max capacity
    private int maxCapacity = 1000000000; // Default initial value for maxCapacity

    private final Map<String, Queue<Ticket>> eventTickets = new ConcurrentHashMap<>(); // Thread-safe map for ticket queues
    private final Map<String, Integer> eventTicketCount = new ConcurrentHashMap<>(); // Thread-safe map for ticket counts
    private final Map<String, String> eventNames = new ConcurrentHashMap<>(); // Thread-safe map for event names

    private final Lock lock = new ReentrantLock(); // ReentrantLock lock can be used to control access to shared resource
    private final Condition notFull = lock.newCondition(); // Condition for ticket pool being not full This condition allows threads to wait until there is space available
    private final Condition notEmpty = lock.newCondition(); // Condition for ticket pool being not empty waits unitil the tickets are added

    // Method to update max capacity based on user input
    public int updateMaxCapacityFromInput(int maximum) { // update max capacity from user input cli
        try {
            int newMaxCapacity = maximum;
            if (newMaxCapacity > 0) {
                this.maxCapacity = newMaxCapacity; // Update the instance variable
                System.out.println("Max capacity updated to " + newMaxCapacity);
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
        return this.maxCapacity; // Return the updated max capacity (instance variable)
    }



    // Add tickets to the pool in a thread-safe way
    public void addTicket(int count, Vendor vendor, String eventId, String eventName) {
        lock.lock(); // make sure only 1 thread can run at a time
        try {
            eventTickets.putIfAbsent(eventId, new ConcurrentLinkedDeque<>()); // if there is no event this creates a new concurrent linked list
            eventTicketCount.putIfAbsent(eventId, 0);
            eventNames.putIfAbsent(eventId, eventName);

            while (eventTicketCount.get(eventId) + count >maxCapacity ) {
                System.out.println("Waiting to add tickets for event " + eventName + " as the pool is full...");
                notFull.await(); // this condition wait till the space become available
            }

            for (int i = 0; i < count; i++) {
                Ticket ticket = new Ticket(eventTicketCount.get(eventId) + 1, eventName, vendor.getName());
                eventTickets.get(eventId).offer(ticket);// adding the ticket to the queue assocaited with event id

                synchronized (eventTicketCount) {// ensure only 1 thread can acces and modify the block of code
                    eventTicketCount.put(eventId, eventTicketCount.get(eventId) + 1);
                }
            }

            //logTransaction(count + " tickets added for event " + eventName + " by vendor " + vendor.getName());
            notEmpty.signalAll(); // Signal other parts of prohram that may be waiting for tickets

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted while waiting to add tickets.");
        } finally {
            lock.unlock();
        }
    }

    // Get details of all events with their ticket counts
    public Map<String, String> getAllEVENTWITHTICKETCOUNT() {
        lock.lock();
        try {
            Map<String, String> eventDetails = new HashMap<>();
            for (String eventId : eventNames.keySet()) { // goes through each event key in eventName map
                String eventName = eventNames.get(eventId);// gets the assciated value with that key
                int ticketCount = eventTicketCount.getOrDefault(eventId, 0); // gets the ticket count or set as 0
                eventDetails.put(eventName, "Tickets Available: " + ticketCount);
            }
            return eventDetails;
        } finally {
            lock.unlock();
        }
    }

    // Buy a ticket for a specific event
    public Ticket buytickets(String eventId, boolean isVIP) {
        lock.lock();
        try {
            if (!eventTickets.containsKey(eventId)) { // if  hashmap doesent contain the requested event id
                System.out.println("Event ID not found.");
                return null;
            }

            Queue<Ticket> tickets = eventTickets.get(eventId); // retrieves the queue of tickets available for the specified eventId
            while (tickets.isEmpty()) {
                System.out.println("No tickets available for event " + eventId + ". Waiting...");
                notEmpty.await(); // Wait until tickets are available
            }

            Ticket ticket = tickets.poll(); // removes and retrives the ticket from the front
            synchronized (eventTicketCount) {
                eventTicketCount.put(eventId, eventTicketCount.get(eventId) - 1);
            }

            //logTransaction((isVIP ? "VIP " : "Regular ") + "ticket purchased for event " + eventId + ": " + ticket);
            notFull.signalAll(); // Signal waiting producers
            return ticket;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted while waiting for tickets.");
            return null;
        } finally {
            lock.unlock();
        }
    }

}
