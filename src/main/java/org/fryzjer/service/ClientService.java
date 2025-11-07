package org.fryzjer.service;

import org.fryzjer.exception.ReservationConflictException;


import java.time.LocalDate;
import java.time.LocalTime;

public interface ClientService {
    void createReservation(long clientId, long serviceId, long establishmentId, long workerId,
                           LocalDate date, LocalTime time)
            throws ReservationConflictException;
    void cancelReservation(long reservationId);
}
