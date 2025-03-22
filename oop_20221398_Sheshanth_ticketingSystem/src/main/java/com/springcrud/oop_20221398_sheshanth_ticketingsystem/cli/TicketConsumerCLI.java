package com.springcrud.oop_20221398_sheshanth_ticketingsystem.cli;

import com.springcrud.oop_20221398_sheshanth_ticketingsystem.model.Vendor;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.service.ConfigService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition; // Import Condition for ReentrantLock synchronization

public class TicketConsumerCLI {
    private static final String URL = "http://localhost:8080/api/tickets"; // base URL of where the tickets will be added
    private boolean ticketHandlingActive = false;
    private boolean ticketFlag = false; // Flag to control ticket handling globally
    private final TicketPool ticketPool = new TicketPool(); // the shared pool of ticket manmagement
    private int currentTickets = 0; // current ticket count
    private int maxcapa;// ,maximum capacity


    private final Lock lock = new ReentrantLock();     // ReentrantLock and Condition for synchronization
    private final Condition ticketsAvailableCondition = lock.newCondition(); // Condition to manage thread waiting

    public TicketConsumerCLI() {}

    public static void main(String[] args) {
        TicketConsumerCLI ticketConsumerCLI = new TicketConsumerCLI();
        ticketConsumerCLI.start(); // main method to start CLI
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Ticketing System CLI");

        while (true) {
            System.out.println("\nChoose an action:");
            System.out.println("1. Start");
            System.out.println("2. Stop");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    startTicketHandling();
                    break;
                case "2":
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    public void startTicketHandling() {
        ticketHandlingActive = true;
        Scanner scanner = new Scanner(System.in);

        while (ticketHandlingActive) {
            System.out.println("\nTicket Handling Menu:");
            System.out.println("1. Vendor: Release tickets with rate");
            System.out.println("2. View all events and their ticket counts");
            System.out.println("3. Set Customer Retrieval Rate");
            System.out.println("4. Set Maximum Ticket Count");
            System.out.println("5. Stop Ticket Handling");
            System.out.println("6. Consume ticket ");

            String choice = scanner.nextLine().trim();

            switch (choice) { // asking user input
                case "1":
                    VendorOperations(scanner);
                    break;
                case "2":
                    viewAvailableEventsAndTicketCounts();
                    break;
                case "3":
                    setCustomerRetrievalRate(scanner);
                    break;
                case "4":
                    setMaxCapacity(scanner);
                    break;
                case "5":
                    ticketHandlingActive = false;
                    System.out.println("Ticket handling is stopped.");
                    break;
                case "6":
                    consumeTickets(scanner);
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void consumeTickets(Scanner scanner) {
        try {
            System.out.print("Enter event ID: ");
            String eventId = scanner.nextLine();
            if (eventId.isEmpty()) {
                throw new IllegalArgumentException("Event ID cannot be empty.");
            }

            System.out.print("Enter client ID: ");
            String clientId = scanner.nextLine();
            if (clientId.isEmpty()) {
                throw new IllegalArgumentException("Client ID cannot be empty.");
            }

            System.out.print("Enter number of tickets to consume: ");
            int ticketCount = Integer.parseInt(scanner.nextLine());
            if (ticketCount <= 0) {
                throw new IllegalArgumentException("Ticket count must be greater than zero.");
            }

            System.out.print("Enter retrieval rate (seconds between each ticket retrieval): ");
            int retrievalRate = Integer.parseInt(scanner.nextLine());
            if (retrievalRate <= 0) {
                throw new IllegalArgumentException("Retrieval rate must be greater than zero.");
            }

            System.out.print("Is this a VIP purchase? (true/false): ");
            boolean isVip = Boolean.parseBoolean(scanner.nextLine());

            // Create only one thread for the customer
            new Thread(() -> {
                for (int i = 0; i < ticketCount; i++) {
                    sendConsumeRequest(eventId, clientId, retrievalRate, isVip);
                }
            }).start();
        } catch (IllegalArgumentException e) {
            System.out.println("Input error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error occurred while consuming tickets: " + e.getMessage());
        }
    }

    private void sendConsumeRequest(String eventId, String clientId, int retrievalRate, boolean isVip) {
        try {
            Thread.sleep(retrievalRate * 1000L); // wait according to the retrieval rate delay

            String requestBody = String.format( // formatted request body
                    "{\"eventId\":\"%s\",\"clientId\":\"%s\",\"ticketCount\":1,\"purchaseRate\":%d,\"isVip\":%b}",
                    eventId, clientId, retrievalRate, isVip
            );

            HttpRequest request = HttpRequest.newBuilder() // create new request
                    .uri(URI.create(URL + "/purchase")) // URL where the request will be sent
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody)) // send the POST request in the form of string
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // send request and get response

            if (response.statusCode() == 200) {
                lock.lock(); // Lock acquired before modifying shared resource
                try {
                    currentTickets--;
                    System.out.println("Ticket consumed successfully: " + response.body());
                } finally {
                    lock.unlock(); // Ensure unlocking
                }
            } else {
                System.out.println("Failed to consume ticket. Status code: " + response.statusCode());
                System.out.println("Response body: " + response.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve the interrupt status
            System.out.println("Thread was interrupted while waiting for retrieval rate: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error while consuming ticket: " + e.getMessage());
        }
    }


    private void VendorOperations(Scanner scanner) {
        String vendorName = getValidInput(scanner, "Enter vendor name:");
        Vendor vendor = new Vendor(vendorName);
        String eventId = getValidInput(scanner, "Enter event ID:");
        String eventName = getValidInput(scanner, "Enter event name:");
        int noOfTickets = getValidIntInput(scanner, "Enter number of tickets to release:");
        int releaseRate = getValidIntInput(scanner, "Enter release rate (seconds between each ticket release):");

        while (true) {
            System.out.println("\n1. Start Ticket Handling");
            System.out.println("2. Stop Ticket Handling");
            System.out.println("3. Back to Menu");

            String option = scanner.nextLine().trim();//takes user input and removes unnecessary spaces

            switch (option) {
                case "1":
                    ticketFlag = true;
                    System.out.println("Starting ticket handling...");
                    startTicketProducer(noOfTickets, eventId, eventName, vendor, releaseRate);
                    break;
                case "2":
                    ticketFlag = false;
                    System.out.println("Ticket handling stopped.");
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void startTicketProducer(int noOfTickets, String eventId, String eventName, Vendor vendor, int releaseRate) {
        new Thread(() -> { // starts a new threaed for each an everytime for relsing tickets
            lock.lock(); // Locking before checking shared resource
            try {
                while (currentTickets + noOfTickets > maxcapa) { // checking whether the ticket release exceends the maxium capacity
                    System.out.println("Waiting as adding tickets would exceed max capacity...");
                    ticketsAvailableCondition.await(); // Waiting until there is enough space
                }
                addTickets(noOfTickets, eventId, eventName, vendor, releaseRate); // calls the funtion to realse tickets into backend
                ticketsAvailableCondition.signalAll(); // Notify waiting threads
            } catch (InterruptedException e) {
                System.out.println("Producer thread interrupted: " + e.getMessage());
            } finally {
                lock.unlock(); // Unlocking when done
            }
        }).start();
    }

    private void viewAvailableEventsAndTicketCounts() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/events"))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());// sends request and wait for the response



            if (response.statusCode() == 200) {
                System.out.println("Available events and ticket counts:");
                System.out.println(response.body());
            } else {
                System.out.println("Failed to fetch events. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Error fetching event data: " + e.getMessage());
        }
    }

    private void setCustomerRetrievalRate(Scanner scanner) { // setting customer retrival rate using user input
        int rate = getValidIntInput(scanner, "Enter new customer retrieval rate (seconds):");
        System.out.println("Customer retrieval rate set to " + rate + " seconds.");
    }

    private void setMaxCapacity(Scanner scanner) { // setting ticketpool maxcapcity using user input
        maxcapa = getValidIntInput(scanner, "Enter the maximum capacity:");
        ticketPool.updateMaxCapacityFromInput(maxcapa);
    }

    private String getValidInput(Scanner scanner, String prompt) {
        String input;
        while (true) {
            System.out.println(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    private int getValidIntInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    private void addTickets(int noOfTickets, String eventId, String eventName, Vendor vendor, int releaseRate) {
        if (!ticketFlag) { // checks the ticket flag if it is set to false
            System.out.println("Ticket handling is currently stopped.");
            return;
        }

        try {
            if (currentTickets + noOfTickets > maxcapa) { // checks whether the number of tickets exceeds the max capacity
                System.out.println("Cannot add tickets. Adding " + noOfTickets + " tickets exceeds the maximum capacity.");
                return;
            }

            String req = String.format(
                    "numoftickets=%d&eventID=%s&eventname=%s&vendorname=%s&releaserate=%d",
                    noOfTickets, eventId, eventName, vendor.getName(), releaseRate
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/add-with-rate"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(req))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                currentTickets += noOfTickets;
                System.out.println(response.body());
                System.out.println("Tickets successfully added. Current total tickets: " + currentTickets);
            } else {
                System.out.println("Failed to add tickets with rate. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Error while adding tickets: " + e.getMessage());
        }
    }
}
