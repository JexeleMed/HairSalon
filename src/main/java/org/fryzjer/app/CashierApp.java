package org.fryzjer.app;

import org.fryzjer.model.Reservation;
import org.fryzjer.repository.HairSalonRepository;
import org.fryzjer.repository.SQLiteRepository;
import org.fryzjer.service.CashierService;
import org.fryzjer.service.CashierServiceImpl;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CashierApp {

    private static CashierService cashierService;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // --- 1. SETUP ---
        HairSalonRepository repository = new SQLiteRepository();
        cashierService = new CashierServiceImpl(repository);

        System.out.println("--- CashierApp Terminal [ONLINE] ---");
        System.out.println("Welcome, Cashier. Please choose an option.");

        // --- 2. MAIN LOOP ---
        runCashierMenu();

        scanner.close();
        System.out.println("--- CashierApp Terminal [OFFLINE] ---");
    }

    private static void runCashierMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n[CASHIER MENU]");
            System.out.println("1. View reservations to be processed (COMPLETED)");
            System.out.println("2. Mark reservation as PAID");
            System.out.println("9. Exit application");
            System.out.print("Your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        handleViewReservationsToProcess();
                        break;
                    case 2:
                        handleMarkAsPaid();
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

    private static void handleViewReservationsToProcess() {
        System.out.println("\n--- Reservations to Process (COMPLETED or PAID) ---");

        List<Reservation> reservations = cashierService.getReservationsToProcess();

        if (reservations.isEmpty()) {
            System.out.println("No reservations found to process.");
            return;
        }

        System.out.printf("%-5s | %-10s | %-12s | %-10s | %-8s%n",
                "ID", "Date", "Time", "Status", "Client ID");
        System.out.println("-------------------------------------------------------------");
        for (Reservation res : reservations) {
            System.out.printf("%-5d | %-10s | %-12s | %-10s | %-8d%n",
                    res.getId(),
                    res.getDate(),
                    res.getTime(),
                    res.getStatus(),
                    res.getClientId()
            );
        }
    }

    private static void handleMarkAsPaid() {
        try {
            System.out.println("\n--- Mark as PAID ---");
            System.out.print("Enter Reservation ID to mark as PAID: ");
            long reservationId = scanner.nextLong();
            scanner.nextLine();

            cashierService.markReservationAsPaid(reservationId);

            System.out.println("SUCCESS: Reservation ID=" + reservationId + " marked as PAID.");

        } catch (IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("ERROR: ID must be a number.");
            scanner.nextLine();
        }
    }
}