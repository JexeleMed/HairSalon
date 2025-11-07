package org.fryzjer.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {

    private final long id;
    private final String nameOfService;

    private final long establishmentId;
    private final LocalDate date;
    private final LocalTime time;
    private final long workerId;
    private final long clientId;
    // --- --- --- --- --- ---

    private ReservationStatus status;

    public Reservation(long id, String nameOfService, long establishmentId,
                       LocalDate date, LocalTime time, long workerId, long clientId) {
        this.id = id;
        this.nameOfService = nameOfService;
        this.establishmentId = establishmentId;
        this.date = date;
        this.time = time;
        this.workerId = workerId;
        this.clientId = clientId;
        this.status = ReservationStatus.PENDING;
    }

    public long getId() { return id; }
    public String getNameOfService() { return nameOfService; }
    public long getEstablishmentId() { return establishmentId; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public long getWorkerId() { return workerId; }
    public long getClientId() { return clientId; }
    public ReservationStatus getStatus() { return status; }

    public void setStatus(ReservationStatus newStatus) {
        if (this.status == ReservationStatus.PAID || this.status == ReservationStatus.CANCELLED) {
            System.out.println("STATUS CHANGE BLOCKED: Reservation is already in a final state (" + this.status + ").");
            return;
        }
        if (this.status == ReservationStatus.COMPLETED && newStatus == ReservationStatus.PENDING) {
            System.out.println("STATUS CHANGE BLOCKED: Cannot revert a COMPLETED reservation back to PENDING.");
            return;
        }
        System.out.println("STATUS CHANGE: " + this.status + " -> " + newStatus);
        this.status = newStatus;
    }
}