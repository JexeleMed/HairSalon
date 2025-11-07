package org.fryzjer.model;

// Zakładam, że Person to klasa, nie rekord
public class Person {

    // 1. NOWE POLE
    private final long id;

    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final Role role;

    // 2. POPRAWIONY KONSTRUKTOR
    public Person(long id, String firstName, String lastName, String phoneNumber, Role role) {
        this.id = id; // Ustawiamy ID
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // 3. NOWY GETTER
    public long getId() {
        return id;
    }

    // --- Reszta getterów (była OK) ---
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public Role getRole() {
        return role;
    }
}