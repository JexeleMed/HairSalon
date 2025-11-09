package org.fryzjer.repository;

import org.fryzjer.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryRepository implements HairSalonRepository {
    private final List<Person> people = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private final List<Service> services = new ArrayList<>();
    private final List<Establishment> establishments = new ArrayList<>();

    private long nextEstablishmentId = 1;
    private long nextPersonId = 1;
    private long nextReservationId = 1;
    private long nextServiceId = 1;

    // ------People------

    @Override
    public Person addPerson(String firstName, String lastName, String phoneNumber, Role role) {
        long newId = nextPersonId++;
        Person newPerson = new Person(newId, firstName, lastName, phoneNumber, role);
        people.add(newPerson);
        return newPerson;
    }

    @Override
    public void deletePersonById(long id) {
        this.people.removeIf(person -> person.getId() == id);
    }

    @Override
    public List<Person> getAllPeople() {
        return this.people;
    }


    @Override
    public Optional<Person> findPersonByPhoneNumber(String phoneNumber){
        return this.people.stream()
                .filter(person -> person.getPhoneNumber().equals(phoneNumber)) // .getPhoneNumber()
                .findFirst();    }

    @Override
    public Optional<Person> findPersonById(long id) {
        return this.people.stream()
                .filter(person -> person.getId() == id) // .getId()
                .findFirst();
    }



    // ------Reservation------
    @Override
    public Reservation addReservation(String nameOfService, long establishmentId,
                                      LocalDate date, LocalTime time,
                                      long workerId, long clientId){

        long newId = nextReservationId++;
        Reservation newReservation = new Reservation(newId, nameOfService, establishmentId,
                date, time, workerId, clientId);

        reservations.add(newReservation);
        System.out.println("Repo: Added new reservation with ID=" + newId + " and Status=" + newReservation.getStatus());
        return newReservation;
    }

    @Override
    public List<Reservation> getAllReservations(){
        return this.reservations;
    }

    @Override
    public List<Reservation> findReservationByDate(LocalDate date){
        return this.reservations.stream()
                .filter(reservation -> reservation.getDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findReservationsByDateAndTime(LocalDate date, LocalTime time) {
        return this.reservations.stream()
                .filter(r -> r.getDate().equals(date) && r.getTime().equals(time))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Reservation> findReservationById(long id){
        return this.reservations.stream().filter(reservation -> reservation.getId() == id).findFirst();
    }

    @Override
    public void deleteReservationById(long id){
        this.reservations.removeIf(reservation -> reservation.getId() == id);
    }


    @Override
    public void updateReservationStatus(long reservationId, ReservationStatus newStatus) {
        findReservationById(reservationId).ifPresent(reservation -> {
            reservation.setStatus(newStatus);
        });
    }

    // ------Services------
    @Override
    public Service addService(String serviceName, int price) {
        Service newService = new Service(nextServiceId++, serviceName, price);
        services.add(newService);
        return newService;
    }

    @Override
    public Optional<Service> findServiceByName(String serviceName){
        return this.services.stream().filter(service -> service.getServiceName().equals(serviceName)).findFirst();
    }

    @Override
    public Optional<Service> findServiceById(long id){
        return this.services.stream().filter(service -> service.getId() == id).findFirst();
    }

    @Override
    public List<Service> getAllServices(){
        return this.services;
    }

    @Override
    public List<Service> getAllAvailableServices() {
        return this.services.stream()
                .filter(Service::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public void updateServicePrice(long id, int newPrice) {
        Optional<Service> serviceToUpdate = this.services.stream()
                .filter(service -> service.getId() == id)
                .findFirst();

        serviceToUpdate.ifPresent(service -> service.setPrice(newPrice));
    }

    @Override
    public void archiveService(long id) {
        Optional<Service> serviceToArchive = this.services.stream()
                .filter(service -> service.getId() == id)
                .findFirst();

        serviceToArchive.ifPresent(service -> {
            service.setAvailable(false);
        });
    }

    @Override
    public Establishment addEstablishment(String name, Integer numberOfSeats, String phoneNumber){
        Establishment establishment = new Establishment(nextEstablishmentId, name, numberOfSeats, phoneNumber);
        establishments.add(establishment);
        return establishment;
    }

    @Override
    public Optional<Establishment> findEstablishmentByPhoneNumber(String phoneNumber) {
        return this.establishments.stream()
                .filter(e -> e.getOwnerPhoneNumber().equals(phoneNumber))
                .findFirst();
    }

    @Override
    public List<Establishment> getAllEstablishments() {
        return this.establishments;
    }


    @Override
    public Optional<Establishment> findEstablishmentById(long id) {
        return this.establishments.stream()
                .filter(e -> e.getId() == id) //
                .findFirst();
    }

}
