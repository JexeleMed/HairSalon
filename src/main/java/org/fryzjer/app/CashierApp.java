package org.fryzjer.app;

import org.fryzjer.model.Reservation;
import org.fryzjer.model.ReservationStatus;
import org.fryzjer.model.Role;
import org.fryzjer.repository.HairSalonRepository;
import org.fryzjer.repository.InMemoryRepository;
import org.fryzjer.service.CashierService;
import org.fryzjer.service.CashierServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class CashierApp {

    public static void main(String[] args) {

        // --- 1. SETUP (Creating and wiring layers) ---
        HairSalonRepository repository = new InMemoryRepository();
        CashierService cashierService = new CashierServiceImpl(repository);

        System.out.println("CashierApp started. Cashier service initialized.");
        System.out.println("------------------------------------");


        // --- 2. DATA PREPARATION (The "Cheat") ---
        System.out.println("Preparing test data...");

        // Add prerequisites
        repository.addPerson("Test Client", "Client", "111", Role.CLIENT); // ID=1
        repository.addService("Test Service", 100); // ID=1
        repository.addEstablishment("Test Shop", 2, "222"); // ID=1

        // Create the reservation (it will be 'PENDING' by default)
        Reservation res = repository.addReservation(
                "Test Service", 1L, LocalDate.now(), LocalTime.of(12, 0), 1L, 1L); // ID=1

        res.setStatus(ReservationStatus.COMPLETED);

        System.out.println("Test data loaded: 1 reservation (ID=1) created and set to COMPLETED.");


        // --- 3. TEST "HAPPY PATH" (Marking the 'COMPLETED' reservation as 'PAID') ---
        System.out.println("\n[TEST 1] Cashier attempts to mark reservation ID=1 as PAID...");
        try {
            cashierService.markReservationAsPaid(1L);
            System.out.println("STATUS: SUCCESS! Reservation marked as PAID.");
        } catch (Exception e) {
            System.out.println("STATUS: ERROR (UNEXPECTED): " + e.getMessage());
        }

        // --- 4. VERIFICATION ---
        Optional<Reservation> verifiedRes = repository.findReservationById(1L);
        if (verifiedRes.isPresent() && verifiedRes.get().getStatus() == ReservationStatus.PAID) {
            System.out.println("VERIFICATION: SUCCESS! Reservation ID=1 is now PAID in the database.");
        } else {
            System.out.println("VERIFICATION: FAILED! Reservation is not PAID.");
        }


        // --- 5. TEST "BAD PATH" (Trying to pay for it again) ---
        System.out.println("\n[TEST 2] Cashier attempts to mark reservation ID=1 as PAID *again*...");
        try {
            cashierService.markReservationAsPaid(1L);
            System.out.println("STATUS: SUCCESS! (This should not happen)");
        } catch (IllegalStateException e) {
            // This is expected! Our logic in CashierServiceImpl should throw this.
            System.out.println("STATUS: ERROR (EXPECTED): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("STATUS: ERROR (UNEXPECTED): " + e.getMessage());
        }

        System.out.println("\n------------------------------------");
        System.out.println("Simulation finished.");
    }
}