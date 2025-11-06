package org.fryzjer.model;

public record Person(
        long id,
        String firstName,
        String lastName,
        String phoneNumber,
        Role role
) {
}