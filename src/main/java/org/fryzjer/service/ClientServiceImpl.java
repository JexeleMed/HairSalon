package org.fryzjer.service;

import org.fryzjer.repository.HairSalonRepository;
import org.fryzjer.exception.ReservationConflictException;
import org.fryzjer.model.Establishment;
import java.time.LocalDate;
import java.time.LocalTime;

public class ClientServiceImpl implements ClientService {
    private final HairSalonRepository repository;

    public ClientServiceImpl(HairSalonRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createReservation(long clientId, long serviceId, long establishmentId,
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
        long competingReservations = repository.findReservationsByDateAndTime(date, time).stream()
                .filter(r -> r.establishmentNumber().equals(String.valueOf(establishmentId))) // To jest słabe (string), ale trzymam się Twojego modelu
                .count();

        if (competingReservations >= availableSeats) {
            throw new ReservationConflictException("No free seat at " + time);
        }

        System.out.println("Service: Preparing reservation...");
        repository.addReservation(
                service.getServiceName(),
                String.valueOf(establishmentId),
                date,
                time,
                "Pracownik nr 1", // TODO
                String.valueOf(clientId)
        );
    }


    @Override
    public void cancelReservation(long reservationId) {
        repository.findReservationById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("No such reservation!"));

        // 2. Jeśli tak -> ZAWOŁAJ REPOZYTORIUM
        System.out.println("Service: Canceling reservation...");
        repository.deleteReservationById(reservationId);
    }
}
