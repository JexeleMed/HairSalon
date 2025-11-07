package org.fryzjer.service;
import org.fryzjer.model.Reservation;
import java.util.List;

public interface EmployeeService {
    List<Reservation> getMyPendingReservations(long employeeId);
    void completeReservation(long reservationId, long employeeId);
}
