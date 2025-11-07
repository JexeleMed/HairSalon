package org.fryzjer.service;
import org.fryzjer.model.Reservation;
import java.util.List;

public interface CashierService {
    List<Reservation> getReservationsToProcess();

    void markReservationAsPaid(long reservationId);
}
