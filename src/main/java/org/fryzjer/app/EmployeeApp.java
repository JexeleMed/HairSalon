package org.fryzjer.app;

import org.fryzjer.model.Reservation;
import org.fryzjer.repository.HairSalonRepository;
import org.fryzjer.repository.SQLiteRepository;
import org.fryzjer.service.EmployeeService;
import org.fryzjer.service.EmployeeServiceImpl;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class EmployeeApp {

    private static EmployeeService employeeService;
    private static final Scanner scanner = new Scanner(System.in);
    private static long loggedInEmployeeId = -1;

    public static void main(String[] args) {
        // --- 1. SETUP ---
        HairSalonRepository repository = new SQLiteRepository();
        employeeService = new EmployeeServiceImpl(repository);

        System.out.println("--- EmployeeApp Terminal [ONLINE] ---");

        // --- 2. LOG IN ---
        try {
            System.out.print("Please enter your Employee ID to log in: ");
            loggedInEmployeeId = scanner.nextLong();
            scanner.nextLine();
            System.out.println("Logged in as Employee ID: " + loggedInEmployeeId);
        } catch (InputMismatchException e) {
            System.out.println("Invalid ID. Exiting.");
            return;
        }

        // --- 3. MAIN LOOP ---
        runEmployeeMenu();

        scanner.close();
        System.out.println("--- EmployeeApp Terminal [OFFLINE] ---");
    }

    private static void runEmployeeMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n[EMPLOYEE MENU | User: " + loggedInEmployeeId + "]");
            System.out.println("1. View my pending reservations");
            System.out.println("2. Complete a reservation");
            System.out.println("9. Logout (Exit)");
            System.out.print("Your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        handleViewMyReservations();
                        break;
                    case 2:
                        handleCompleteReservation();
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

    private static void handleViewMyReservations() {
        System.out.println("\n--- My Pending Reservations (Employee ID: " + loggedInEmployeeId + ") ---");

        List<Reservation> reservations = employeeService.getMyPendingReservations(loggedInEmployeeId);

        if (reservations.isEmpty()) {
            System.out.println("No pending reservations found for you.");
            return;
        }

        System.out.printf("%-5s | %-10s | %-12s | %-10s | %-8s%n",
                "ID", "Date", "Time", "Service", "Client ID");
        System.out.println("-------------------------------------------------------------");
        for (Reservation res : reservations) {
            System.out.printf("%-5d | %-10s | %-12s | %-10s | %-8d%n",
                    res.getId(),
                    res.getDate(),
                    res.getTime(),
                    res.getNameOfService(),
                    res.getClientId()
            );
        }
    }

    private static void handleCompleteReservation() {
        try {
            System.out.println("\n--- Complete Reservation ---");
            System.out.print("Enter Reservation ID to complete: ");
            long reservationId = scanner.nextLong();
            scanner.nextLine();

            employeeService.completeReservation(reservationId, loggedInEmployeeId);

            System.out.println("SUCCESS: Reservation ID=" + reservationId + " marked as COMPLETED.");

        } catch (SecurityException | IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("ERROR: ID must be a number.");
            scanner.nextLine();
        }
    }
}