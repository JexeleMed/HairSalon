package org.fryzjer.app;

import org.fryzjer.model.Establishment;
import org.fryzjer.model.Person;
import org.fryzjer.model.Reservation;
import org.fryzjer.model.Role;
import org.fryzjer.model.Service;
import org.fryzjer.repository.HairSalonRepository;
import org.fryzjer.repository.InMemoryRepository;
import org.fryzjer.service.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class FullSystemSimulationApp {

    public static void main(String[] args) {

        System.out.println("--- FULL SYSTEM SIMULATION START ---");
        System.out.println("Timestamp: " + LocalDate.now() + " " + LocalTime.now());

        // --- 1. SETUP ---
        HairSalonRepository repository = new InMemoryRepository();

        PriceListService priceListService = new PriceListServiceImpl(repository);
        OwnerService ownerService = new OwnerServiceImpl(repository, priceListService);
        ClientService clientService = new ClientServiceImpl(repository);
        EmployeeService employeeService = new EmployeeServiceImpl(repository);
        CashierService cashierService = new CashierServiceImpl(repository);

        System.out.println("System online. All services initialized with shared repository.");
        System.out.println("------------------------------------");

        // --- 2. OWNER ---
        System.out.println("[Phase 1: OWNER]");

        Person owner = repository.addPerson("Owner", "Boss", "123", Role.OWNER); // ID=1
        Establishment shop = repository.addEstablishment("Main Establishment", 2, owner.getPhoneNumber()); // ID=1

        Person employee = repository.addPerson("Adam", "Worker", "777", Role.EMPLOYEE); // ID=2
        Service service = repository.addService("Men's haircut", 100); // ID=1
        Person client = repository.addPerson("Jan", "Client", "456", Role.CLIENT); // ID=3

        System.out.println("Owner added Employee ID=" + employee.getId() + " and Service ID=" + service.getId());
        System.out.println("------------------------------------");

        // --- 3. CLIENT ---
        System.out.println("[Phase 2: CLIENT]");
        long reservationId = -1;
        try {
            LocalDate reservationDate = LocalDate.now().plusDays(1);
            LocalTime reservationTime = LocalTime.of(12, 0);

            clientService.createReservation(
                    client.getId(),
                    service.getId(),
                    shop.getId(),
                    employee.getId(),
                    reservationDate,
                    reservationTime
            );

            Reservation res = repository.findReservationByDate(reservationDate).get(0);
            reservationId = res.getId();
            System.out.println("Client created Reservation ID=" + reservationId + ". Status: " + res.getStatus());

        } catch (Exception e) {
            System.out.println("Client action FAILED: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        System.out.println("------------------------------------");

        // --- 4. EMPLOYEE ---
        System.out.println("[Phase 3: EMPLOYEE]");
        try {
            employeeService.completeReservation(reservationId, employee.getId());
            Reservation res = repository.findReservationById(reservationId).get();
            System.out.println("Employee ID=" + employee.getId() + " completed Reservation ID=" + reservationId + ". New Status: " + res.getStatus());
        } catch (Exception e) {
            System.out.println("Employee action FAILED: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        System.out.println("------------------------------------");

        // --- 5. CASHIER ---
        System.out.println("[Phase 4: CASHIER]");
        try {
            cashierService.markReservationAsPaid(reservationId);
            Reservation res = repository.findReservationById(reservationId).get();
            System.out.println("Cashier marked Reservation ID=" + reservationId + " as PAID. Final Status: " + res.getStatus());
        } catch (Exception e) {
            System.out.println("Cashier action FAILED: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        System.out.println("------------------------------------");
        System.out.println("--- SIMULATION FINISHED SUCCESSFULLY ---");
    }
}