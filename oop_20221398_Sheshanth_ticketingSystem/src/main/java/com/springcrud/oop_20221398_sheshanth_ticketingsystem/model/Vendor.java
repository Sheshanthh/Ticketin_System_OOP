package com.springcrud.oop_20221398_sheshanth_ticketingsystem.model;

public class Vendor {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Vendor{" +
                "name='" + name + '\'' +
                '}';
    }

    public Vendor(String name){
        this.name=name;
    }
}
