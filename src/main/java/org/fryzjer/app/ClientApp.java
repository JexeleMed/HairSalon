package org.fryzjer.app;

import org.fryzjer.exception.ReservationConflictException;
import org.fryzjer.model.Establishment;
import org.fryzjer.model.Person;
import org.fryzjer.model.Role;
import org.fryzjer.model.Service;
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
        ClientService clientService = new ClientServiceImpl(repository);
        System.out.println("ClientApp started. Service layer initialized.");
        System.out.println("------------------------------------");


        // --- 2. Bootstrap ---
        Person client = repository.addPerson("Jan", "Client", "111222333", Role.CLIENT); // ID=1
        Service service = repository.addService("Men's Haircut", 80); // ID=1
        Establishment shop = repository.addEstablishment("Cut 'U Janka'", 1, "999888777"); // ID=1

        Person employee = repository.addPerson("Adam", "Worker", "111", Role.EMPLOYEE); // ID=2

        System.out.println("Test data loaded: 1 client, 1 service, 1 establishment, 1 employee (ID=2).");


        // --- 3. TEST "HAPPY PATH"  ---
        LocalDate today = LocalDate.now();
        LocalTime time = LocalTime.of(10, 0);

        System.out.println("\n[TEST 1] Attempting reservation for " + time + "...");
        try {
            clientService.createReservation(
                    client.getId(),
                    service.getId(),
                    shop.getId(),
                    employee.getId(),
                    today,
                    time
            );
            System.out.println("STATUS: SUCCESS! Reservation created.");

        } catch (ReservationConflictException e) {
            System.out.println("STATUS: BUSINESS ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("STATUS: CRITICAL ERROR: " + e.getMessage());
        }


        // --- 4. Business logic test
        System.out.println("\n[TEST 2] Attempting reservation for the same slot (" + time + ")...");
        try {
            clientService.createReservation(
                    client.getId(),
                    service.getId(),
                    shop.getId(),
                    employee.getId(),
                    today,
                    time
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