package com.springcrud.oop_20221398_sheshanth_ticketingsystem.service;

import org.springframework.stereotype.Service;

import java.util.Scanner;


public class ConfigService {

    private static int ticketReleaseRate = 2;  // Default ticket release rate in seconds
    private static int customerRetrievalRate = 1;  // Default customer retrieval rate in seconds
    private static int maxTicketCapacity = 100;  // Default max capacity
    private static int totalTickets = 0;



    public int getTotalTickets() {
        return totalTickets;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public static int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public static void setMaxTicketCapacity(int maxTicketCapacity) {
        ConfigService.maxTicketCapacity = maxTicketCapacity;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public static void setCustomerRetrievalRate(int rate) {
        customerRetrievalRate = rate;
    }


}
