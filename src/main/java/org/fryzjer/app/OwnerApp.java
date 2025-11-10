package org.fryzjer.app;

import org.fryzjer.model.Reservation;
import org.fryzjer.repository.HairSalonRepository;
import org.fryzjer.repository.SQLiteRepository;
import org.fryzjer.service.OwnerService;
import org.fryzjer.service.OwnerServiceImpl;
import org.fryzjer.service.PriceListService;
import org.fryzjer.service.PriceListServiceImpl;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class OwnerApp {

    private static OwnerService ownerService;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // --- 1. SETUP ---
        HairSalonRepository repository = new SQLiteRepository();

        PriceListService priceListService = new PriceListServiceImpl(repository);
        ownerService = new OwnerServiceImpl(repository, priceListService);

        System.out.println("--- OwnerApp Terminal [ONLINE] ---");
        System.out.println("Welcome, Owner. Please choose an option.");

        // --- 2. MAIN LOOP ---
        runOwnerMenu();

        scanner.close();
        System.out.println("--- OwnerApp Terminal [OFFLINE] ---");
    }

    private static void runOwnerMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n[OWNER MENU]");
            System.out.println("1. Add new service to price list");
            System.out.println("2. Update service price");
            System.out.println("3. Archive a service");
            System.out.println("4. View all reservations");
            System.out.println("5. View total revenue");
            System.out.println("6. Cleanup old/missed reservations");
            System.out.println("7. [DEBUG] Advance time (e.g., 30 days)");
            System.out.println("9. Exit application");
            System.out.print("Your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        handleAddService();
                        break;
                    case 2:
                        handleUpdatePrice();
                        break;
                    case 3:
                        handleArchiveService();
                        break;
                    case 4:
                        handleViewAllReservations();
                        break;
                    case 5:
                        handleViewTotalRevenue();
                        break;
                    case 6:
                        handleCleanupOldReservations();
                        break;
                    case 7:
                        handleAdvanceTime();
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

    // --- MENU METHODS ---

    private static void handleAddService() {
        try {
            System.out.println("\n--- Add New Service ---");
            System.out.print("Enter service name: ");
            String name = scanner.nextLine();

            System.out.print("Enter price: ");
            int price = scanner.nextInt();
            scanner.nextLine();

            ownerService.getPriceListService().addNewService(name, price);
            System.out.println("SUCCESS: Service '" + name + "' added.");

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR (Validation): " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("ERROR (Input): Price must be a number.");
            scanner.nextLine();
        }
    }

    private static void handleUpdatePrice() {
        try {
            System.out.println("\n--- Update Service Price ---");
            System.out.print("Enter Service ID to update: ");
            long id = scanner.nextLong();

            System.out.print("Enter NEW price: ");
            int newPrice = scanner.nextInt();
            scanner.nextLine();

            ownerService.getPriceListService().updateServicePrice(id, newPrice);
            System.out.println("SUCCESS: Price updated for service ID=" + id);

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR (Validation): " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("ERROR (Input): ID and price must be numbers.");
            scanner.nextLine();
        }
    }

    private static void handleArchiveService() {
        try {
            System.out.println("\n--- Archive Service ---");
            System.out.print("Enter Service ID to archive: ");
            long id = scanner.nextLong();
            scanner.nextLine();

            ownerService.getPriceListService().archiveService(id);
            System.out.println("SUCCESS: Service ID=" + id + " archived.");

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("ERROR: ID must be a number.");
            scanner.nextLine();
        }
    }

    private static void handleViewAllReservations() {
        System.out.println("\n--- All Reservations (All Time) ---");
        List<Reservation> reservations = ownerService.viewAllReservations();

        if (reservations.isEmpty()) {
            System.out.println("No reservations found in the system.");
            return;
        }

        System.out.printf("%-5s | %-10s | %-12s | %-10s | %-8s | %-8s%n",
                "ID", "Date", "Time", "Status", "Client", "Worker");
        System.out.println("------------------------------------------------------------------");
        for (Reservation res : reservations) {
            System.out.printf("%-5d | %-10s | %-12s | %-10s | %-8d | %-8d%n",
                    res.getId(),
                    res.getDate(),
                    res.getTime(),
                    res.getStatus(),
                    res.getClientId(),
                    res.getWorkerId()
            );
        }
    }

    private static void handleViewTotalRevenue() {
        System.out.println("\n--- Total Revenue ---");
        double revenue = ownerService.calculateTotalRevenue();
        System.out.printf("Total revenue from all PAID reservations: %.2f PLN%n", revenue);
    }
    private static void handleCleanupOldReservations() {
        System.out.println("\n--- Cleaning up Missed Reservations ---");
        System.out.println("Checking for all PENDING reservations before today (" + LocalDate.now() + ")...");

        int cleanedCount = ownerService.cleanupMissedReservations();

        if (cleanedCount > 0) {
            System.out.println("SUCCESS: Found and marked " + cleanedCount + " reservations as MISSED.");
        } else {
            System.out.println("SUCCESS: No missed reservations found to clean up.");
        }
    }
    private static void handleAdvanceTime() {
        try {
            System.out.println("\n--- [DEBUG] Advance Time ---");
            System.out.print("Enter number of days to advance time by (e.g., 30): ");
            int days = scanner.nextInt();
            scanner.nextLine();

            if (days <= 0) {
                System.out.println("Days must be a positive number.");
                return;
            }

            int updatedCount = ownerService.debug_AdvanceTimeByDays(days);

            System.out.println("SUCCESS: Advanced time for " + updatedCount + " reservations by " + days + " days.");
            System.out.println("You can now run 'Cleanup old/missed reservations' (option 6) to see the effect.");

        } catch (InputMismatchException e) {
            System.out.println("ERROR: Please enter a valid number.");
            scanner.nextLine();
        }
    }
}