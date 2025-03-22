package com.springcrud.oop_20221398_sheshanth_ticketingsystem.cli;

import com.springcrud.oop_20221398_sheshanth_ticketingsystem.model.Customer;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.model.Ticket;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.service.TickerServiceLogic;

import java.util.concurrent.ConcurrentLinkedDeque;

public class CustomerThread extends Thread {
    private final String customerId;
    private final TickerServiceLogic tickerServiceLogic;
    private final int numoftickets;
    private final String eventID;
    private final int purchaseRate;
    private final boolean isVip;

    public CustomerThread(String customerId, TickerServiceLogic serviceLogic, int numoftickets, String eventID, int purchaseRate, boolean isVip) { // constructor for customer thread class
        this.customerId = customerId;
        this.tickerServiceLogic = serviceLogic;
        this.numoftickets = numoftickets;
        this.eventID = eventID;
        this.purchaseRate = purchaseRate;
        this.isVip = isVip;
    }

    @Override // overriding the run method in Thread class
    public void run() {
        try {
            for (int i = 0; i < numoftickets; i++) {
                synchronized (tickerServiceLogic) { // only 1 thread can acces the obj at a time
                    ConcurrentLinkedDeque<Customer> queue = tickerServiceLogic.getEventPriorityQueue(eventID);
                    Customer priorityCustomer = queue.poll(); // retrives and removes the first customer Fifo
                    Ticket ticket; // declares a ticket obj

                    if (priorityCustomer != null && isVip) {
                        ticket = tickerServiceLogic.getTicketPool().buytickets(eventID, true);
                        if (ticket != null) { // if ticket obj is not null
                            tickerServiceLogic.logTransaction("VIP customer " + customerId + " purchased a ticket for event " + eventID);
                        }
                    } else {
                        ticket = tickerServiceLogic.getTicketPool().buytickets(eventID, false); // buys a normal ticket
                        if (ticket != null) {
                            tickerServiceLogic.logTransaction("Regular customer " + customerId + " purchased a ticket for event " + eventID);
                        }
                    }

                    if (ticket != null) {
                        System.out.println("Ticket #" + (i + 1) + " purchased by " + customerId + " for event " + ticket.getEventName());
                    } else {
                        System.out.println("Ticket #" + (i + 1) + " purchase failed for " + customerId);
                    }

                    tickerServiceLogic.getLastPurchaseTimeMap().put(eventID, System.currentTimeMillis());// updating the latest purchase time
                }

                Thread.sleep(purchaseRate * 1000L); // implementing purchasing simulation
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Customer thread interrupted for customer " + customerId);
        }
    }

    public String getCustomerId() {
        return customerId;
    }
}
