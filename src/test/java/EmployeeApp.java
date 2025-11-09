import org.fryzjer.model.Person;
import org.fryzjer.model.Reservation;
import org.fryzjer.model.Role;
import org.fryzjer.repository.HairSalonRepository;
import org.fryzjer.repository.InMemoryRepository;
import org.fryzjer.service.EmployeeService;
import org.fryzjer.service.EmployeeServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;

public class EmployeeApp {

    public static void main(String[] args) {

        // --- 1. SETUP ---
        HairSalonRepository repository = new InMemoryRepository();
        EmployeeService employeeService = new EmployeeServiceImpl(repository);

        System.out.println("EmployeeApp started. Employee service initialized.");
        System.out.println("------------------------------------");


        // --- 2. DATA PREPARATION ---
        Person employee1 = repository.addPerson("Adam", "Kowalski", "111", Role.EMPLOYEE); // Gets ID=1
        Person employee2 = repository.addPerson("Ewa", "Nowak", "222", Role.EMPLOYEE); // Gets ID=2
        Person client = repository.addPerson("Test Client", "C", "333", Role.CLIENT); // Gets ID=3

        repository.addService("Haircut", 100); // ID=1
        repository.addEstablishment("Main Shop", 3, "999"); // ID=1

        // Create a reservation ASSIGNED TO EMPLOYEE 1
        Reservation res1 = repository.addReservation(
                "Haircut", 1L, LocalDate.now(), LocalTime.of(14, 0),
                employee1.getId(), // Assigned to worker 1
                client.getId()
        ); // This is Reservation ID=1, Status=PENDING

        System.out.println("Test data loaded: 2 employees, 1 client.");
        System.out.println("Reservation ID=1 (PENDING) created and assigned to Employee ID=" + employee1.getId());


        // --- 3. TEST "HAPPY PATH" (Employee 1 completes their own reservation) ---
        System.out.println("\n[TEST 1] Employee ID=" + employee1.getId() + " attempts to complete their *own* reservation (ID=1)...");
        try {
            employeeService.completeReservation(res1.getId(), employee1.getId());
            System.out.println("STATUS: SUCCESS! Reservation marked as COMPLETED.");
        } catch (Exception e) {
            System.out.println("STATUS: ERROR (UNEXPECTED): " + e.getMessage());
        }

        // --- 4. TEST "BAD PATH" (Trying to complete a non-PENDING reservation) ---
        System.out.println("\n[TEST 2] Employee ID=" + employee1.getId() + " attempts to complete reservation ID=1 *again*...");
        try {
            employeeService.completeReservation(res1.getId(), employee1.getId());
            System.out.println("STATUS: SUCCESS! (This should not happen)");
        } catch (IllegalStateException e) {
            System.out.println("STATUS: ERROR (EXPECTED): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("STATUS: ERROR (UNEXPECTED): " + e.getMessage());
        }

        // --- 5. TEST "SECURITY" (Employee 2 tries to complete Employee 1's reservation) ---
        Reservation res2 = repository.addReservation(
                "Haircut", 1L, LocalDate.now(), LocalTime.of(15, 0),
                employee1.getId(), // Assigned to worker 1
                client.getId()
        ); // This is Reservation ID=2, Status=PENDING
        System.out.println("\nTest data: Reservation ID=2 (PENDING) created for Employee ID=" + employee1.getId());

        System.out.println("\n[TEST 3] Employee ID=" + employee2.getId() + " attempts to complete reservation ID=2 (which belongs to Employee 1)...");
        try {
            employeeService.completeReservation(res2.getId(), employee2.getId());
            System.out.println("STATUS: SUCCESS! (This should not happen)");
        } catch (SecurityException e) {
            System.out.println("STATUS: SECURITY ERROR (EXPECTED): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("STATUS: ERROR (UNEXPECTED): " + e.getMessage());
        }

        System.out.println("\n------------------------------------");
        System.out.println("Simulation finished.");
    }
}