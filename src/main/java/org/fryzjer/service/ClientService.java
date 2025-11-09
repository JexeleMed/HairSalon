package org.fryzjer.service;

import org.fryzjer.exception.ReservationConflictException;
import org.fryzjer.model.Reservation;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ClientService {
    void createReservation(long clientId, long serviceId, long establishmentId, long workerId,
                           LocalDate date, LocalTime time)
            throws ReservationConflictException;
    void cancelReservation(long reservationId, long loggedInClientId);
    List<Reservation> viewAllAnonymizedReservations();
}
