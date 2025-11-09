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
        if (worker.getRole() != Role.EMPLOYEE) {
            throw new IllegalArgumentException("Person with ID: " + workerId + " is not an Employee.");
        }

        long competingReservations = repository.findReservationsByDateAndTime(date, time).stream()
                .filter(r -> r.getEstablishmentId() == establishmentId)
                .count();

        if (competingReservations >= availableSeats) {
            throw new ReservationConflictException("No free seat at " + time);
        }

        System.out.println("Service: Preparing reservation...");

        repository.addReservation(
                service.getServiceName(),
                establishmentId,
                date,
                time,
                workerId,
                clientId
        );
    }


    @Override
    public void cancelReservation(long reservationId) {
        var reservation = repository.findReservationById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("No such reservation!"));

        repository.updateReservationStatus(reservationId, ReservationStatus.CANCELLED);
        System.out.println("Service: Reservation status changed to CANCELLED for ID: " + reservationId);
    }
}