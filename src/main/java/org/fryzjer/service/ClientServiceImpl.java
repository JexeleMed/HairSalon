package org.fryzjer.service;

import org.fryzjer.model.ReservationStatus;
import org.fryzjer.repository.HairSalonRepository;
import org.fryzjer.exception.ReservationConflictException;
import org.fryzjer.model.Establishment;
import org.fryzjer.model.Person;
import org.fryzjer.model.Service;
import org.fryzjer.model.Role;

import java.time.LocalDate;
import java.time.LocalTime;

public class ClientServiceImpl implements ClientService {
    private final HairSalonRepository repository;

    public ClientServiceImpl(HairSalonRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createReservation(long clientId, long serviceId, long establishmentId, long workerId,
                                  LocalDate date, LocalTime time)
            throws ReservationConflictException {


        var client = repository.findPersonById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("No such client!"));

        var service = repository.findServiceById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("No such service!"));

        if (!service.isAvailable()) {
            throw new IllegalArgumentException("This service is not available!.");
        }

        var establishment = repository.findEstablishmentById(establishmentId)
                .orElseThrow(() -> new IllegalArgumentException("No such establishment!"));

        int availableSeats = establishment.getNumberOfSeats();

        var worker = repository.findPersonById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("No such employee with ID: " + workerId));
        // I czy jest pracownikiem
        if (worker.getRole() != Role.EMPLOYEE) {
            throw new IllegalArgumentException("Person with ID: " + workerId + " is not an Employee.");
        }

        long competingReservations = repository.findReservationsByDateAndTime(date, time).stream()
                .filter(r -> r.getEstablishmentId() == establishmentId) // Porównujemy long == long
                .count();

        if (competingReservations >= availableSeats) {
            throw new ReservationConflictException("No free seat at " + time);
        }

        System.out.println("Service: Preparing reservation...");

        // --- POPRAWKA BŁĘDU NR 2 ---
        repository.addReservation(
                service.getServiceName(),
                establishmentId, // Przekazujemy czysty long
                date,
                time,
                workerId,
                clientId // Przekazujemy czysty long
        );
    }


    @Override
    public void cancelReservation(long reservationId) {
        // Ta metoda była już u Ciebie POPRAWNA. Zostaje.
        var reservation = repository.findReservationById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("No such reservation!"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        System.out.println("Service: Reservation status changed to CANCELLED for ID: " + reservationId);
    }
}