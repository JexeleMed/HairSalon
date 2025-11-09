package org.fryzjer.repository;

import org.fryzjer.model.*;

import java.sql.*;
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

        String sql = "INSERT INTO people(first_name, last_name, phone_number, role) VALUES(?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phoneNumber);
            stmt.setString(4, role.name());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating person failed, no rows affected.");
            }

            try (var generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long newId = generatedKeys.getLong(1);
                    return new Person(newId, firstName, lastName, phoneNumber, role);
                } else {
                    throw new SQLException("Creating person failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error adding person to database: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Person> findPersonByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM people WHERE phone_number = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phoneNumber);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Person person = new Person(
                            rs.getLong("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("phone_number"),
                            Role.valueOf(rs.getString("role"))
                    );
                    return Optional.of(person);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding person by phone: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Person> findPersonById(long id) {
        String sql = "SELECT * FROM people WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Person person = new Person(
                            rs.getLong("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("phone_number"),
                            Role.valueOf(rs.getString("role"))
                    );
                    return Optional.of(person);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding person by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Person> getAllPeople() {
        String sql = "SELECT * FROM people";
        List<Person> peopleList = new java.util.ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                peopleList.add(new Person(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone_number"),
                        Role.valueOf(rs.getString("role"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all people: " + e.getMessage(), e);
        }
        return peopleList;
    }

    @Override
    public void deletePersonById(long id) {
        String sql = "DELETE FROM people WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting person: " + e.getMessage(), e);
        }
    }

    /**
     * Reservation methods (Create, Read, Delete)
     */

    @Override
    public Reservation addReservation(String nameOfService, long establishmentId, LocalDate date,
                                      LocalTime time, long workerId, long clientId) {
        String sql = """
            INSERT INTO reservations(service_name, establishment_id, date, time, worker_id, client_id, status)
            VALUES(?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nameOfService);
            stmt.setLong(2, establishmentId);
            stmt.setString(3, date.toString());
            stmt.setString(4, time.toString());
            stmt.setLong(5, workerId);
            stmt.setLong(6, clientId);
            stmt.setString(7, ReservationStatus.PENDING.name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating reservation failed.");
            }

            try (var generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long newId = generatedKeys.getLong(1);
                    return new Reservation(newId, nameOfService, establishmentId, date, time, workerId, clientId);
                } else {
                    throw new SQLException("Creating reservation failed, no ID.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding reservation: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> getAllReservations() {
        String sql = "SELECT * FROM reservations";
        List<Reservation> list = new java.util.ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getLong("id"),
                        rs.getString("service_name"),
                        rs.getLong("establishment_id"),
                        LocalDate.parse(rs.getString("date")),
                        LocalTime.parse(rs.getString("time")),
                        rs.getLong("worker_id"),
                        rs.getLong("client_id")
                );
                reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
                list.add(reservation);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all reservations: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Reservation> findReservationByDate(LocalDate date) {
        String sql = "SELECT * FROM reservations WHERE date = ?";
        List<Reservation> list = new java.util.ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, date.toString());

            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new Reservation(
                            rs.getLong("id"),
                            rs.getString("service_name"),
                            rs.getLong("establishment_id"),
                            LocalDate.parse(rs.getString("date")),
                            LocalTime.parse(rs.getString("time")),
                            rs.getLong("worker_id"),
                            rs.getLong("client_id")
                    );
                    reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
                    list.add(reservation);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservations by date: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Reservation> findReservationsByDateAndTime(LocalDate date, LocalTime time) {
        String sql = "SELECT * FROM reservations WHERE date = ? AND time = ?";
        List<Reservation> list = new java.util.ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, date.toString());
            stmt.setString(2, time.toString());

            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new Reservation(
                            rs.getLong("id"),
                            rs.getString("service_name"),
                            rs.getLong("establishment_id"),
                            LocalDate.parse(rs.getString("date")),
                            LocalTime.parse(rs.getString("time")),
                            rs.getLong("worker_id"),
                            rs.getLong("client_id")
                    );
                    reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
                    list.add(reservation);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservations by date/time: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Optional<Reservation> findReservationById(long id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = new Reservation(
                            rs.getLong("id"),
                            rs.getString("service_name"),
                            rs.getLong("establishment_id"),
                            LocalDate.parse(rs.getString("date")),
                            LocalTime.parse(rs.getString("time")),
                            rs.getLong("worker_id"),
                            rs.getLong("client_id")
                    );
                    reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
                    return Optional.of(reservation);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservation by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void deleteReservationById(long id) {
        String sql = "DELETE FROM reservations WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting reservation: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateReservationStatus(long reservationId, ReservationStatus newStatus) {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setLong(2, reservationId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating reservation status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> getAllPaidReservations() {
        String sql = "SELECT * FROM reservations WHERE status = 'PAID'";
        List<Reservation> list = new java.util.ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getLong("id"),
                        rs.getString("service_name"),
                        rs.getLong("establishment_id"),
                        LocalDate.parse(rs.getString("date")),
                        LocalTime.parse(rs.getString("time")),
                        rs.getLong("worker_id"),
                        rs.getLong("client_id")
                );
                reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
                list.add(reservation);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all paid reservations: " + e.getMessage(), e);
        }
        return list;
    }
    @Override
    public List<Reservation> findPendingReservationsBefore(LocalDate date) {
        String sql = "SELECT * FROM reservations WHERE status = 'PENDING' AND date < ?";
        List<Reservation> list = new java.util.ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, date.toString());

            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new Reservation(
                            rs.getLong("id"),
                            rs.getString("service_name"),
                            rs.getLong("establishment_id"),
                            LocalDate.parse(rs.getString("date")),
                            LocalTime.parse(rs.getString("time")),
                            rs.getLong("worker_id"),
                            rs.getLong("client_id")
                    );
                    reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
                    list.add(reservation);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding pending reservations before date: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Service methods (Create, Read, Update, Archive)
     */

    @Override
    public Service addService(String serviceName, int price) {
        String sql = "INSERT INTO services(service_name, price) VALUES(?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, serviceName);
            stmt.setInt(2, price);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating service failed.");
            }

            try (var generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long newId = generatedKeys.getLong(1);
                    return new Service(newId, serviceName, price);
                } else {
                    throw new SQLException("Creating service failed, no ID.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding service: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Service> findServiceByName(String serviceName) {
        String sql = "SELECT * FROM services WHERE service_name = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, serviceName);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Service service = new Service(
                            rs.getLong("id"),
                            rs.getString("service_name"),
                            rs.getInt("price")
                    );
                    service.setAvailable(rs.getBoolean("is_available"));
                    return Optional.of(service);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding service by name: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Service> findServiceById(long id) {
        String sql = "SELECT * FROM services WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Service service = new Service(
                            rs.getLong("id"),
                            rs.getString("service_name"),
                            rs.getInt("price")
                    );
                    service.setAvailable(rs.getBoolean("is_available"));
                    return Optional.of(service);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding service by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Service> getAllServices() {
        String sql = "SELECT * FROM services";
        List<Service> list = new java.util.ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                Service service = new Service(
                        rs.getLong("id"),
                        rs.getString("service_name"),
                        rs.getInt("price")
                );
                service.setAvailable(rs.getBoolean("is_available"));
                list.add(service);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all services: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Service> getAllAvailableServices() {
        String sql = "SELECT * FROM services WHERE is_available = TRUE";
        List<Service> list = new java.util.ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                Service service = new Service(
                        rs.getLong("id"),
                        rs.getString("service_name"),
                        rs.getInt("price")
                );
                service.setAvailable(rs.getBoolean("is_available"));
                list.add(service);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting available services: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void updateServicePrice(long id, int newPrice) {
        String sql = "UPDATE services SET price = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newPrice);
            stmt.setLong(2, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating service price: " + e.getMessage(), e);
        }
    }

    @Override
    public void archiveService(long id) {
        String sql = "UPDATE services SET is_available = FALSE WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error archiving service: " + e.getMessage(), e);
        }
    }

    /**
     * Establishment methods
     */

    @Override
    public Establishment addEstablishment(String name, Integer numberOfSeats, String phoneNumber) {
        String sql = "INSERT INTO establishments(name, number_of_seats, owner_phone_number) VALUES(?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setInt(2, numberOfSeats);
            stmt.setString(3, phoneNumber);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating establishment failed.");
            }

            try (var generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long newId = generatedKeys.getLong(1);
                    return new Establishment(newId, name, numberOfSeats, phoneNumber);
                } else {
                    throw new SQLException("Creating establishment failed, no ID.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding establishment: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Establishment> findEstablishmentByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM establishments WHERE owner_phone_number = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phoneNumber);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Establishment establishment = new Establishment(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getInt("number_of_seats"),
                            rs.getString("owner_phone_number")
                    );
                    return Optional.of(establishment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding establishment by phone: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Establishment> getAllEstablishments() {
        String sql = "SELECT * FROM establishments";
        List<Establishment> list = new java.util.ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Establishment(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getInt("number_of_seats"),
                        rs.getString("owner_phone_number")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all establishments: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Optional<Establishment> findEstablishmentById(long id) {
        String sql = "SELECT * FROM establishments WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Establishment establishment = new Establishment(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getInt("number_of_seats"),
                            rs.getString("owner_phone_number")
                    );
                    return Optional.of(establishment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding establishment by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}