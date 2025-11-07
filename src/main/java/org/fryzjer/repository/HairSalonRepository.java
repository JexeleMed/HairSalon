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
            String establishmentNumber,
            LocalDate date,
            LocalTime time,
            String workerNumber,
            String clientNumber
    );

    List<Reservation> getAllReservations();
    List<Reservation> findReservationByDate(LocalDate date);
    Optional<Reservation> findReservationById(long id);
    void deleteReservationById(long id);

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
     * Add establishment
     */
    Establishment addEstablishment(String name, Integer numberOfSeats, String phoneNumber);
    Optional<Establishment> findEstablishmentByPhoneNumber(String phoneNumber);
    List<Establishment> getAllEstablishments();
}