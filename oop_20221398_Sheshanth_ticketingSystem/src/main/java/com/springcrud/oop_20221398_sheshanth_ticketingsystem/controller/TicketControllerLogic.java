package com.springcrud.oop_20221398_sheshanth_ticketingsystem.controller;

import com.springcrud.oop_20221398_sheshanth_ticketingsystem.cli.CustomerThread;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.model.Customer;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.model.Ticket;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.model.Vendor;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.service.ConfigService;
import com.springcrud.oop_20221398_sheshanth_ticketingsystem.service.TickerServiceLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; // importing all the nessary modules

@RestController // allows to handle https request
@RequestMapping("/api/tickets") // means all methods in this class will handle request that start with api/tickets
public class TicketControllerLogic {

    @Autowired
    private TickerServiceLogic tickerServiceLogic; // class which contains the logic of tickets adding and purchasing

    private final ConcurrentHashMap<String, CustomerThread> customerthreadsmap = new ConcurrentHashMap<>();// use to store customer details
    private final ConcurrentHashMap<String, String> clientidmap = new ConcurrentHashMap<>();

    @PostMapping("/add-with-rate") // use to add  tickets in this url
    public ResponseEntity<String> addTicketsWithRate(
            @RequestParam int numoftickets, // use to pass the value into the methods
            @RequestParam String eventID,
            @RequestParam String eventname,
            @RequestParam String vendorname,
            @RequestParam int releaserate) {
        if (numoftickets <= 0) {
            return ResponseEntity.badRequest().body("Number of tickets must be greater than 0."); // sends  a error request saying should be greater than 0
        }
        if (eventID == null || eventID.isEmpty()) {
            return ResponseEntity.badRequest().body("Event ID is required.");
        }

        Vendor vendor = new Vendor(vendorname); // creating a new vendor obj
        tickerServiceLogic.addtickets(numoftickets, vendor, eventID, eventname, releaserate);
        return ResponseEntity.ok("Tickets will be released at an interval of " + releaserate + " seconds.");
    }

    @PostMapping("/register-vip")
    public ResponseEntity<String> registerVIPCustomer(@RequestParam String customerName) { // this response enetity excpets a string in return
        String customerID = tickerServiceLogic.registervip(customerName);
        return ResponseEntity.ok("VIP customer registered with ID: " + customerID);
    }

    @PostMapping("/purchase") // maps the http post request to the  method
    public ResponseEntity<String> purchaseTickets(@RequestBody Map<String, Object> requestBody) { // returns a response with status code and string message
        //ResponseEntity, Spring uses it to generate the final HTTP response.
        String eventid = (String) requestBody.get("eventId"); // convers json in java obj
        String clientid = (String) requestBody.get("clientId");
        int ticketCount = (int) requestBody.getOrDefault("ticketCount", 1); // Default to 1 ticket if not provided
        int purchaseRate = (int) requestBody.getOrDefault("purchaseRate", 1); // Default to 1 second if not provided
        boolean isVip = (boolean) requestBody.getOrDefault("isVip", false); // Default to false if not provided

        // Validate required parameters
        if (eventid == null || eventid.isEmpty()) {
            return ResponseEntity.badRequest().body("Event ID is required.");
        }
        if (clientid == null || clientid.isEmpty()) {
            return ResponseEntity.badRequest().body("Client ID is required.");
        }

        // Start ticket purchasing with the specified rate and VIP status
        tickerServiceLogic.purchaseTickets(clientid, eventid, ticketCount, purchaseRate, isVip);

        return ResponseEntity.ok("Ticket purchase initiated for " + clientid + " at a rate of " + purchaseRate + " seconds.");
    }

//    @GetMapping("/count")
//    public ResponseEntity<Integer> getTicketCount(@RequestParam String eventId) {
//        int ticketCount = tickerServiceLogic.getticketcount(eventId);
//        return ResponseEntity.ok(ticketCount);
//    }

    @PostMapping("/set-customer-retrieval-rate")
    public ResponseEntity<String> setCustomerRetrievalRate(@RequestParam int rate) {
        ConfigService.setCustomerRetrievalRate(rate);
        return ResponseEntity.ok("Customer retrieval rate set to " + rate + " seconds.");
    }

    @PostMapping("/add-priority-customer")
    public ResponseEntity<String> addPriorityCustomer(
            @RequestParam String customerId,
            @RequestParam int priority,
            @RequestParam String eventId) {
        if (!tickerServiceLogic.isvips(customerId)) {
            return ResponseEntity.badRequest().body("Customer is not a VIP.");
        }

        Customer customer = new Customer(customerId, priority, eventId);
        tickerServiceLogic.addpriorcustomer(customer);
        return ResponseEntity.ok("Priority customer " + customerId + " added to queue for event " + eventId);
    }


    @GetMapping("/events")
    public ResponseEntity<Map<String, String>> getAllEventsWithTicketCount() {
        Map<String, String> eventDetails = tickerServiceLogic.getalleventswithticketcount();
        return new ResponseEntity<>(eventDetails ,HttpStatus.OK); // used  to send both hashmap nd status code
    }



}
