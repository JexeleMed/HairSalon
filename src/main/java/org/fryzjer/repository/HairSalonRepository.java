package org.fryzjer.repository;

import org.fryzjer.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface HairSalonRepository {

    /**
     * Person methods (Create, Read, Delete)
     */
    Person addPerson(String firstName, String lastName, String phoneNumber, Role role);

    Optional<Person> findPersonByPhoneNumber(String phoneNumber);
    Optional<Person> findPersonById(long id);
    List<Person> getAllPeople();

    void deletePersonById(long id);

    /**
     * Reservation methods (Create, Read, Delete)
     */
    Reservation addReservation(
            String nameOfService,
            long establishmentId,
            LocalDate date,
            LocalTime time,
            long workerId,
            long clientId
    );

    List<Reservation> getAllReservations();
    List<Reservation> findReservationByDate(LocalDate date);
    List<Reservation> findReservationsByDateAndTime(LocalDate date, LocalTime time);
    Optional<Reservation> findReservationById(long id);
    void deleteReservationById(long id);
    void updateReservationStatus(long reservationId, ReservationStatus newStatus);
    List<Reservation> getAllPaidReservations();

    /**
     * Service methods (Create, Read, Update, Archive)
     */
    Service addService(String serviceName, int price);

    Optional<Service> findServiceByName(String serviceName);
    Optional<Service> findServiceById(long id);

    List<Service> getAllServices();
    List<Service> getAllAvailableServices();

    void updateServicePrice(long id, int newPrice);
    void archiveService(long id);


    /**
     * Establishment methods
     */
    Establishment addEstablishment(String name, Integer numberOfSeats, String phoneNumber);
    Optional<Establishment> findEstablishmentByPhoneNumber(String phoneNumber);
    List<Establishment> getAllEstablishments();
    Optional<Establishment> findEstablishmentById(long id);
}