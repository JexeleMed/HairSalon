package org.fryzjer.service;

import org.fryzjer.model.Reservation;
import org.fryzjer.model.ReservationStatus;
import org.fryzjer.repository.HairSalonRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CashierServiceImpl implements CashierService {
    private final HairSalonRepository repository;

    public CashierServiceImpl(HairSalonRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Reservation> getReservationsToProcess() {
        System.out.println("Service: Cashier requests processable reservations...");
        return repository.getAllReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.COMPLETED || r.getStatus() == ReservationStatus.PAID)
                .collect(Collectors.toList());
    }

    @Override
    public void markReservationAsPaid(long reservationId) {
        System.out.println("Service: Cashier attempts to mark ID=" + reservationId + " as PAID...");

        var reservation = repository.findReservationById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("No reservation with ID: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new IllegalStateException("Cannot pay for a reservation that is not COMPLETED. Current status: " + reservation.getStatus());
        }
        repository.updateReservationStatus(reservationId, ReservationStatus.PAID);
    }
}
