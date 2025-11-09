package org.fryzjer.app;

import org.fryzjer.exception.ReservationConflictException;
import org.fryzjer.repository.HairSalonRepository;
import org.fryzjer.repository.SQLiteRepository;
import org.fryzjer.service.ClientService;
import org.fryzjer.service.ClientServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientApp {

    private static ClientService clientService;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // --- 1. SETUP ---
        HairSalonRepository repository = new SQLiteRepository();
        clientService = new ClientServiceImpl(repository);

        System.out.println("--- ClientApp Terminal [ONLINE] ---");
        System.out.println("Welcome, Client. Please choose an option.");

        // --- 2. MAIN LOOP ---
        runClientMenu();

        scanner.close();
        System.out.println("--- ClientApp Terminal [OFFLINE] ---");
    }

    private static void runClientMenu() {
        boolean running = true;
        while (running) {
            // --- 3. PRINT MENU ---
            System.out.println("\n[CLIENT MENU]");
            System.out.println("1. Create a new reservation");
            System.out.println("2. Cancel an existing reservation");
            System.out.println("9. Exit application");
            System.out.print("Your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                // --- 4. CHOICE HANDLING ---
                switch (choice) {
                    case 1:
                        handleCreateReservation();
                        break;
                    case 2:
                        handleCancelReservation();
                        break;
                    case 9:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private static void handleCreateReservation() {
        try {
            // --- 5. DATA COLLECTION ---
            System.out.println("\n--- Creating New Reservation ---");
            System.out.print("Enter your Client ID: ");
            long clientId = scanner.nextLong();

            System.out.print("Enter desired Service ID: ");
            long serviceId = scanner.nextLong();

            System.out.print("Enter Establishment ID: ");
            long establishmentId = scanner.nextLong();

            System.out.print("Enter Employee ID: ");
            long workerId = scanner.nextLong();

            scanner.nextLine();

            System.out.print("Enter date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());

            System.out.print("Enter time (HH:MM): ");
            LocalTime time = LocalTime.parse(scanner.nextLine());

            // --- 6. SERVICE CALL ---
            clientService.createReservation(clientId, serviceId, establishmentId, workerId, date, time);

            System.out.println("SUCCESS: Reservation created successfully!");

        } catch (ReservationConflictException e) {
            // --- 7. BUSINESS ERRORS HANDLING ---
            System.out.println("ERROR (Business): " + e.getMessage());
        } catch (IllegalArgumentException | DateTimeParseException | InputMismatchException e) {
            // --- 8. ILLEGAL ARGUMENTS CATCHING ---
            System.out.println("ERROR (Input): " + e.getMessage());
            System.out.println("Please check your data and try again. IDs must be numbers, dates YYYY-MM-DD.");
            scanner.nextLine();
        }
    }

    private static void handleCancelReservation() {
        try {
            System.out.println("\n--- Cancelling Reservation ---");
            System.out.print("Enter Reservation ID to cancel: ");
            long reservationId = scanner.nextLong();
            scanner.nextLine();

            clientService.cancelReservation(reservationId);

            System.out.println("SUCCESS: Reservation ID=" + reservationId + " has been cancelled.");

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("ERROR: Invalid input. Please enter a number.");
            scanner.nextLine();
        }
    }
}