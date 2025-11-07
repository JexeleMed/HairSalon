package org.fryzjer.service;

import org.fryzjer.model.Reservation;
import org.fryzjer.model.ReservationStatus;
import org.fryzjer.repository.HairSalonRepository;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeServiceImpl implements EmployeeService {

    private final HairSalonRepository repository;

    public EmployeeServiceImpl(HairSalonRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Reservation> getMyPendingReservations(long employeeId) {
        System.out.println("Service: Employee ID=" + employeeId + " requests their pending reservations...");

        return repository.getAllReservations().stream()
                .filter(r -> r.getWorkerId() == employeeId)
                .filter(r -> r.getStatus() == ReservationStatus.PENDING)
                .collect(Collectors.toList());
    }

    @Override
    public void completeReservation(long reservationId, long employeeId) {
        System.out.println("Service: Employee ID=" + employeeId + " attempts to complete reservation ID=" + reservationId);

        var reservation = repository.findReservationById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("No reservation with ID: " + reservationId));

        if (reservation.getWorkerId() != employeeId) {
            throw new SecurityException("Employee ID=" + employeeId + " cannot modify reservation assigned to worker ID=" + reservation.getWorkerId());
        }

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot complete a reservation that is not PENDING. Current status: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
    }
}