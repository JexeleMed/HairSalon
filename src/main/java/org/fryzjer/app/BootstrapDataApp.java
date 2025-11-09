package org.fryzjer.app;

import org.fryzjer.model.Reservation;
import org.fryzjer.model.ReservationStatus;
import org.fryzjer.model.Role;
import org.fryzjer.repository.HairSalonRepository;
import org.fryzjer.repository.SQLiteRepository;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A utility application (seeder) to clear and populate the database
 * with a set of starting data for testing.
 */
public class BootstrapDataApp {

    public static void main(String[] args) {

        System.out.println("--- DATABASE BOOTSTRAPPER STARTING ---");

        // --- STEP 1: CLEANING THE OLD DATABASE ---
        // This is our "reset button". It guarantees a clean start.
        try {
            File dbFile = new File("fryzjer.db");
            if (dbFile.exists()) {
                dbFile.delete();
                System.out.println("Old database file 'fryzjer.db' deleted.");
            }
        } catch (Exception e) {
            System.err.println("Could not delete old database: " + e.getMessage());
        }

        // --- STEP 2: INITIALIZING THE SYSTEM ---
        // This will connect to the DB (creating a new file) and initialize tables
        HairSalonRepository repository = new SQLiteRepository();
        System.out.println("New database 'fryzjer.db' initialized.");

        // --- STEP 3: SEEDING DATA ---
        try {
            System.out.println("Seeding data...");

            // 1. Add Establishment and Owner
            repository.addPerson("Adam", "Owner", "100100100", Role.OWNER); // ID=1
            repository.addEstablishment("Main Establishment", 3, "100100100"); // ID=1 (3 seats)

            // 2. Add Employees (for EmployeeApp)
            repository.addPerson("Barbara", "Employee", "200200200", Role.EMPLOYEE); // ID=2
            repository.addPerson("Cezary", "Employee", "300300300", Role.EMPLOYEE); // ID=3

            // 3. Add Clients (for ClientApp)
            repository.addPerson("Damian", "Client", "400400400", Role.CLIENT); // ID=4
            repository.addPerson("Ewelina", "Client", "500500500", Role.CLIENT); // ID=5

            // 4. Add Cashier
            repository.addPerson("Franciszek", "Cashier", "600600600", Role.CASHIER); // ID=6

            // 5. Add Services (for OwnerApp)
            repository.addService("Men's Haircut", 80); // ID=1
            repository.addService("Hair Coloring", 250); // ID=2
            repository.addService("Wash and Model", 120); // ID=3

            // 6. Add Test Reservations (THE MOST IMPORTANT PART)

            // Reservation for TOMORROW (status PENDING) - for testing EmployeeApp
            Reservation res1 = repository.addReservation(
                    "Men's Haircut", 1L, LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                    2L, // Assigned to Barbara (ID=2)
                    4L  // Client Damian (ID=4)
            );
            System.out.println("Created PENDING reservation (ID=" + res1.getId() + ") for tomorrow.");

            // Reservation for YESTERDAY (status COMPLETED) - for testing CashierApp
            Reservation res2 = repository.addReservation(
                    "Hair Coloring", 1L, LocalDate.now().minusDays(1), LocalTime.of(14, 0),
                    3L, // Assigned to Cezary (ID=3)
                    5L  // Client Ewelina (ID=5)
            );
            // We manually set its status to COMPLETED (as if the employee already did it)
            repository.updateReservationStatus(res2.getId(), ReservationStatus.COMPLETED);
            System.out.println("Created COMPLETED reservation (ID=" + res2.getId() + ") for yesterday.");

            System.out.println("\n--- BOOTSTRAP FINISHED SUCCESSFULLY ---");
            System.out.println("Database is now populated with test data.");

        } catch (Exception e) {
            System.err.println("\n--- BOOTSTRAP FAILED ---");
            e.printStackTrace();
        }
    }
}