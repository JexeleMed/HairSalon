package org.fryzjer.app;

import org.fryzjer.exception.ReservationConflictException;
import org.fryzjer.model.Role;
import org.fryzjer.repository.HairSalonRepository;
import org.fryzjer.repository.InMemoryRepository;
import org.fryzjer.service.ClientService;
import org.fryzjer.service.ClientServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;

public class ClientApp {

    public static void main(String[] args) {

        // --- 1. SETUP ---
        HairSalonRepository repository = new InMemoryRepository();

        // Create the Service and inject the repository
        ClientService clientService = new ClientServiceImpl(repository);

        System.out.println("ClientApp started. Service layer initialized.");
        System.out.println("------------------------------------");


        // --- 2. DATA PREPARATION ---

        repository.addPerson("John", "Doe", "111222333", Role.CLIENT); // Will get ID=1
        repository.addService("Men's Haircut", 80); // Will get ID=1
        repository.addEstablishment("The Barber Shop", 1, "999888777"); // ID=1

        System.out.println("Test data loaded: 1 client, 1 service, 1 establishment (1 seat).");


        // --- 3. TEST (First successful reservation) ---
        LocalDate today = LocalDate.now();
        LocalTime time = LocalTime.of(10, 0);

        System.out.println("\n[TEST 1] Attempting reservation for " + time + "...");
        try {
            clientService.createReservation(
                    1L, // Client ID (long)
                    1L, // Service ID (long)
                    1L, // Establishment ID (long)
                    today,
                    time
            );
            System.out.println("STATUS: SUCCESS! Reservation created.");

        } catch (ReservationConflictException e) {
            System.out.println("STATUS: BUSINESS ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("STATUS: CRITICAL ERROR: " + e.getMessage());
        }


        // --- 4. BUSINESS LOGIC TEST (Attempting to book a conflicting slot) ---

        System.out.println("\n[TEST 2] Attempting reservation for the same slot (" + time + ")...");
        try {
            clientService.createReservation(
                    1L, // Same client
                    1L, // Same service
                    1L, // Same establishment
                    today,
                    time // SAME TIME
            );
            System.out.println("STATUS: SUCCESS! Reservation created.");

        } catch (ReservationConflictException e) {
            System.out.println("STATUS: BUSINESS ERROR (EXPECTED): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("STATUS: CRITICAL ERROR: " + e.getMessage());
        }

        System.out.println("\n------------------------------------");
        System.out.println("Simulation finished.");
    }
}