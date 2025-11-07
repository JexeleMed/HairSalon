package org.fryzjer.model;

public class Establishment {
    private final String name;
    private final Integer numberOfSeats;
    private final String ownerPhoneNumber;
    private final long id;


    public Establishment(long id, String name, Integer numberOfSeats, String ownerPhoneNumber) {
        this.id = id;
        this.name = name;
        this.numberOfSeats = numberOfSeats;
        this.ownerPhoneNumber = ownerPhoneNumber;
    }


    public String getName() {
        return name;
    }
    public String getOwnerPhoneNumber() {
        return ownerPhoneNumber;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }
    public long getId() {
        return id;
    }

}


