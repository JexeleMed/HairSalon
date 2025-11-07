package org.fryzjer.model;

public class Service {

    private final long id;
    private final String serviceName;
    private int price;
    private boolean isAvailable;

    // Constructor
    public Service(long id, String serviceName, int price) {
        this.id = id;
        this.serviceName = serviceName;
        this.price = price;
        this.isAvailable = true;
    }

    // --- Getters ---
    public long getId() { return id; }
    public String getServiceName() { return serviceName; }
    public int getPrice() { return price; }
    public boolean isAvailable() { return isAvailable; }

    // --- Setters---
    public void setPrice(int newPrice) {
        this.price = newPrice;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}