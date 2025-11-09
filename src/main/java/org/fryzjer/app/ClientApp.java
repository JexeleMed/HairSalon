package org.fryzjer.app;

import org.fryzjer.exception.ReservationConflictException;
import org.fryzjer.model.Reservation;
import org.fryzjer.repository.HairSalonRepository;
import org.fryzjer.repository.SQLiteRepository;
import org.fryzjer.service.ClientService;
import org.fryzjer.service.ClientServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ClientApp {

    private static ClientService clientService;
    private static final Scanner scanner = new Scanner(System.in);
    private static long loggedInClientId = -1;

    public static void main(String[] args) {
        // --- 1. SETUP ---
        HairSalonRepository repository = new SQLiteRepository();
        clientService = new ClientServiceImpl(repository);

        System.out.println("--- ClientApp Terminal [ONLINE] ---");

        // --- 2. LOG IN ---
        try {
            System.out.print("Please enter your Client ID to log in: ");
            loggedInClientId = scanner.nextLong();
            scanner.nextLine();
            System.out.println("Logged in as Client ID: " + loggedInClientId);
        } catch (InputMismatchException e) {
            System.out.println("Invalid ID. Exiting.");
            return;
        }

        // --- 3. MAIN LOOP ---
        runClientMenu();

        scanner.close();
        System.out.println("--- ClientApp Terminal [OFFLINE] ---");
    }

    private static void runClientMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n[CLIENT MENU | User: " + loggedInClientId + "]");
            System.out.println("1. Create a new reservation");
            System.out.println("2. Cancel an existing reservation");
            System.out.println("3. View all (anonymized) reservations");
            System.out.println("9. Exit application");
            System.out.print("Your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        handleCreateReservation();
                        break;
                    case 2:
                        handleCancelReservation();
                        break;
                    case 3:
                        handleViewAnonymizedReservations();
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
            System.out.println("\n--- Creating New Reservation ---");

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

            clientService.createReservation(loggedInClientId, serviceId, establishmentId, workerId, date, time);

            System.out.println("SUCCESS: Reservation created successfully!");

        } catch (ReservationConflictException e) {
            System.out.println("ERROR (Business): " + e.getMessage());
        } catch (IllegalArgumentException | DateTimeParseException | InputMismatchException e) {
            System.out.println("ERROR (Input): " + e.getMessage());
            System.out.println("Please check your data and try again.");
            if (e instanceof InputMismatchException) scanner.nextLine();
        }
    }

    private static void handleCancelReservation() {
        try {
            System.out.println("\n--- Cancelling Reservation ---");
            System.out.print("Enter Reservation ID to cancel: ");
            long reservationId = scanner.nextLong();
            scanner.nextLine();

            clientService.cancelReservation(reservationId, loggedInClientId);

            System.out.println("SUCCESS: Reservation ID=" + reservationId + " has been cancelled.");

        } catch (SecurityException e) {
            System.out.println("ERROR (Security): " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR (Validation): " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("ERROR (Input): Invalid input. Please enter a number.");
            scanner.nextLine();
        }
    }

    private static void handleViewAnonymizedReservations() {
        System.out.println("\n--- Anonymized Reservation List ---");
        List<Reservation> reservations = clientService.viewAllAnonymizedReservations();

        if (reservations.isEmpty()) {
            System.out.println("No reservations found in the system.");
            return;
        }

        System.out.printf("%-5s | %-10s | %-12s | %-10s | %-8s%n",
                "ID", "Date", "Time", "Status", "Worker");
        System.out.println("----------------------------------------------------------");
        for (Reservation res : reservations) {
            System.out.printf("%-5d | %-10s | %-12s | %-10s | %-8d%n",
                    res.getId(),
                    res.getDate(),
                    res.getTime(),
                    res.getStatus(),
                    res.getWorkerId()
            );
        }
    }
}