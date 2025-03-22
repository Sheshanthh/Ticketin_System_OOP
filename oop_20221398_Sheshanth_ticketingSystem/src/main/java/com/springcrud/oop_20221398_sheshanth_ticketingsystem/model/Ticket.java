package com.springcrud.oop_20221398_sheshanth_ticketingsystem.model;

public class Ticket {
    private int id;
    private String eventName;
    private String vendorName;

    public Ticket(int id, String eventName, String vendorName) {
        this.id = id;
        this.eventName = eventName;
        this.vendorName = vendorName;
    }

    public int getId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }

    public String getVendorName() {
        return vendorName;
    }

    @Override
    public String toString() {
        return "Ticket{id=" + id + ", event='" + eventName + "', vendor='" + vendorName + "'}";
    }
}