package org.fryzjer.app;

import org.fryzjer.model.Reservation;
import org.fryzjer.repository.HairSalonRepository;
import org.fryzjer.repository.InMemoryRepository;
import org.fryzjer.service.OwnerService;
import org.fryzjer.service.OwnerServiceImpl;
import org.fryzjer.service.PriceListService;
import org.fryzjer.service.PriceListServiceImpl;

import java.util.List;

public class OwnerApp {

    public static void main(String[] args) {

        // --- 1. SETUP (Creating and wiring layers) ---
        HairSalonRepository repository = new InMemoryRepository();

        PriceListService priceListService = new PriceListServiceImpl(repository);

        OwnerService ownerService = new OwnerServiceImpl(repository, priceListService);

        System.out.println("OwnerApp started. Owner service initialized.");
        System.out.println("------------------------------------");


        // --- 2. "HAPPY PATH" TEST (Adding a valid service) ---
        System.out.println("[TEST 1] Attempting to add a valid service ('Men's Haircut', 100)...");
        try {
            ownerService.getPriceListService().addNewService("Men's Haircut", 100);
            System.out.println("STATUS: SUCCESS! Service added.");
        } catch (IllegalArgumentException e) {
            System.out.println("STATUS: VALIDATION ERROR (UNEXPECTED): " + e.getMessage());
        }

        // --- 3. VALIDATION TEST (Adding a service with negative price) ---
        System.out.println("\n[TEST 2] Attempting to add an invalid service ('Beard Trim', -50)...");
        try {
            ownerService.getPriceListService().addNewService("Beard Trim", -50);
            System.out.println("STATUS: SUCCESS! Service added."); // This should not happen
        } catch (IllegalArgumentException e) {
            // This is what we expect!
            System.out.println("STATUS: VALIDATION ERROR (EXPECTED): " + e.getMessage());
        }

        // --- 4. VALIDATION TEST (Adding a service with blank name) ---
        System.out.println("\n[TEST 3] Attempting to add an invalid service ('', 50)...");
        try {
            ownerService.getPriceListService().addNewService("", 50);
            System.out.println("STATUS: SUCCESS! Service added."); // This should not happen
        } catch (IllegalArgumentException e) {
            System.out.println("STATUS: VALIDATION ERROR (EXPECTED): " + e.getMessage());
        }

        // --- 5. UPDATE TEST ---
        System.out.println("\n[TEST 4] Attempting to update price for service ID=1...");
        try {
            ownerService.getPriceListService().updateServicePrice(1L, 120); // 1L is the ID of "Men's Haircut"
            System.out.println("STATUS: SUCCESS! Price updated.");
        } catch (IllegalArgumentException e) {
            System.out.println("STATUS: VALIDATION ERROR (UNEXPECTED): " + e.getMessage());
        }

        // --- 6. LISTING TEST ---
        System.out.println("\n[TEST 5] Listing available services...");
        var services = ownerService.getPriceListService().getAvailablePriceList();
        services.forEach(service -> {
            System.out.println("  - " + service.getServiceName() + ", Price: " + service.getPrice());
        });
        // --- 7. RESERVATION BROWSING ---
        System.out.println("\n[TEST 6] Listing all reservations...");
        List<Reservation> reservations = ownerService.viewAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("STATUS: SUCCESS! No reservations found (as expected).");
        } else {
            System.out.println("Found reservations: " + reservations.size());
        }

        System.out.println("\n------------------------------------");
        System.out.println("Simulation finished.");
    }
}