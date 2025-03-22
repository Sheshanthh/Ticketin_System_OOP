package com.springcrud.oop_20221398_sheshanth_ticketingsystem.cli;

import com.springcrud.oop_20221398_sheshanth_ticketingsystem.model.Vendor;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.cli.TicketPool;

public class TicketProducer extends Thread {
    private final TicketPool ticketPool;
    private final Vendor vendor;
    private final int ticketsToAdd;
    private final String eventId;
    private final String eventName;
    private final int releaseRate;

    // Static counter to generate unique thread names
    private static int threadCounter = 1;

    // Constructor
    public TicketProducer(TicketPool ticketPool, Vendor vendor, int ticketsToAdd, String eventId, String eventName, int releaseRate) { // constructor class for the ticket producer
        this.ticketPool = ticketPool;
        this.ticketsToAdd = ticketsToAdd;
        this.eventId = eventId;
        this.eventName = eventName;
        this.releaseRate = releaseRate;
        this.vendor = vendor;

        // Generate a unique thread name using the static counter
        String threadName = "Vendor Thread " + threadCounter++;
        this.setName(threadName);  // Set the thread name
    }

    @Override
    public void run() {
        try {
            // Trying to add tickets one by one using the method from ticket pool
            for (int i = 0; i < ticketsToAdd; i++) {
                ticketPool.addTicket(1, vendor, eventId, eventName);
                System.out.println("Ticket released: " + (i + 1) + " for event '" + eventName + "' by " + getName());


                Thread.sleep(releaseRate * 1000L); // Sleep for releaseRate seconds before adding the next ticket
            }
        } catch (InterruptedException e) {
            System.err.println("Ticket producer thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Re-interrupt the thread
        } catch (Exception e) {
            System.err.println("An error occurred in the ticket producer: " + e.getMessage());
        }
    }
}
