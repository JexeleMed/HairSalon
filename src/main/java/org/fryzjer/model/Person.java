package org.fryzjer.model;

public class Person {

    private final long id;

    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final Role role;

    public Person(long id, String firstName, String lastName, String phoneNumber, Role role) {
        this.id = id; // Ustawiamy ID
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public long getId() {
        return id;
    }

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