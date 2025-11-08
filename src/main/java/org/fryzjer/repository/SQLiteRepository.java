package org.fryzjer.repository;

import org.fryzjer.model.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class SQLiteRepository implements HairSalonRepository {
    private final String dbUrl = "jdbc:sqlite:fryzjer.db";


    public SQLiteRepository(){
        initializeDatabase();
    }
    /**
     * Connect with a database
    **/
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    /**
    * Create scheme for a database
     **/

    private void initializeDatabase() {

        String sqlPerson = """
            CREATE TABLE IF NOT EXISTS people (
                id INTEGER PRIMARY KEY,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                phone_number TEXT UNIQUE,
                role TEXT NOT NULL
            );""";

        String sqlEstablishment = """
            CREATE TABLE IF NOT EXISTS establishments (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                number_of_seats INTEGER,
                owner_phone_number TEXT
            );""";

        String sqlService = """
            CREATE TABLE IF NOT EXISTS services (
                id INTEGER PRIMARY KEY,
                service_name TEXT NOTNOWA NULL UNIQUE,
                price INTEGER NOT NULL,
                is_available BOOLEAN NOT NULL DEFAULT TRUE
            );""";

        String sqlReservation = """
            CREATE TABLE IF NOT EXISTS reservations (
                id INTEGER PRIMARY KEY,
                service_name TEXT NOT NULL,
                establishment_id INTEGER NOT NULL,
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                worker_id INTEGER NOT NULL,
                client_id INTEGER NOT NULL,
                status TEXT NOT NULL
            );""";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlPerson);
            stmt.execute(sqlEstablishment);
            stmt.execute(sqlService);
            stmt.execute(sqlReservation);

            System.out.println("Database tables initialized successfully.");

        } catch (SQLException e) {
            // Throw exception if database doesn't work
            throw new RuntimeException("Error initializing database: " + e.getMessage(), e);
        }
    }

    /**
     * Person methods (Create, Read, Delete)
     */

    @Override
    public Person addPerson(String firstName, String lastName, String phoneNumber, Role role) {
        System.out.println("TODO: Implement addPerson SQL");
        // TODO: SQL code
        return null; // For now
    }

    @Override
    public Optional<Person> findPersonByPhoneNumber(String phoneNumber) {
        System.out.println("TODO: Implement findPersonByPhoneNumber SQL");
        // TODO: SQL code
        return Optional.empty(); // For now
    }

    @Override
    public Optional<Person> findPersonById(long id){
        System.out.println("TODO: Implement findPersonById SQL");
        // TODO: SQL code
        return Optional.empty();
    }

    @Override
    public List<Person> getAllPeople() {
        System.out.println("TODO: Implement getAllPeople SQL");
        // TODO: SQL code
        return null;
    }

    @Override
    public void deletePersonById(long id){
        System.out.println("TODO: Implement deletePersonById SQL");
        // TODO: SQL code
    }

    /**
     * Reservation methods (Create, Read, Delete)
     */

    @Override
    public Reservation addReservation(
            String nameOfService,
            long establishmentId,
            LocalDate date,
            LocalTime time,
            long workerId,
            long clientId
    ) {
        System.out.println("TODO: Implement addReservation SQL");
        // TODO: SQL code
        return null;
    }

    @Override
    public List<Reservation> getAllReservations(){
        System.out.println("TODO: Implement getAllReservations SQL");
        // TODO: SQL code
        return null;
    }

    @Override
    public List<Reservation> findReservationByDate(LocalDate date){
        System.out.println("TODO: Implement findReservationByDate SQL");
        // TODO: SQL code
        return null;
    }

    @Override
    public List<Reservation> findReservationsByDateAndTime(LocalDate date, LocalTime time){
        System.out.println("TODO: Implement findReservationsByDateAndTime SQL");
        // TODO: SQL code
        return null;
    }

    @Override
    public Optional<Reservation> findReservationById(long id){
        System.out.println("TODO: Implement findReservationById SQL");
        // TODO: SQL code
        return Optional.empty();
    }

    @Override
    public void deleteReservationById(long id){
        System.out.println("TODO: Implement deleteReservationById SQL");
        // TODO: SQL code

    }

    /**
     * Service methods (Create, Read, Update, Archive)
     */

    public Service addService(String serviceName, int price){
        System.out.println("TODO: Implement addService SQL");
        // TODO: SQL code
        return null;
    }

    @Override
    public Optional<Service> findServiceByName(String serviceName){
        System.out.println("TODO: Implement findServiceByName SQL");
        // TODO: SQL code
        return Optional.empty();
    }

    public Optional<Service> findServiceById(long id){
        System.out.println("TODO: Implement findServiceById SQL");
        // TODO: SQL code
        return Optional.empty();
    }

    public List<Service> getAllServices(){
        System.out.println("TODO: Implement getAllServices SQL");
        // TODO: SQL code
        return null;
    }

    @Override
    public List<Service> getAllAvailableServices(){
        System.out.println("TODO: Implement getAllAvailableServices SQL");
        // TODO: SQL code
        return null;
    }

    @Override
    public void updateServicePrice(long id, int newPrice){
        System.out.println("TODO: Implement updateServicePrice SQL");
        // TODO: SQL code
    }

    @Override
    public void archiveService(long id){
        System.out.println("TODO: Implement archiveService SQL");
        // TODO: SQL code
    }

    /**
     * Establishment methods
     */

    @Override
    public Establishment addEstablishment(String name, Integer numberOfSeats, String phoneNumber){
        System.out.println("TODO: Implement addEstablishment SQL");
        // TODO: SQL code
        return null;
    }

    @Override
    public Optional<Establishment> findEstablishmentByPhoneNumber(String phoneNumber){
        System.out.println("TODO: Implement findEstablishmentByPhoneNumber SQL");
        // TODO: SQL code
        return Optional.empty();
    }

    @Override
    public List<Establishment> getAllEstablishments(){
        System.out.println("TODO: Implement getAllEstablishments SQL");
        // TODO: SQL code
        return null;
    }

    @Override
    public Optional<Establishment> findEstablishmentById(long id){
        System.out.println("TODO: Implement findEstablishmentById SQL");
        return Optional.empty();
    }
}