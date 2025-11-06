package org.fryzjer.model;

// Wracamy do klasy, bo jej stan (isAvailable) będzie się zmieniał.
public class Service {

    private final long id;
    private final String serviceName;
    private int price; // Cena też może się zmieniać, więc nie 'final'
    private boolean isAvailable; // NASZA NOWA FLAGA!

    // Konstruktor
    public Service(long id, String serviceName, int price) {
        this.id = id;
        this.serviceName = serviceName;
        this.price = price;
        this.isAvailable = true; // Domyślnie każda nowa usługa jest dostępna
    }

    // --- Gettery ---
    public long getId() { return id; }
    public String getServiceName() { return serviceName; }
    public int getPrice() { return price; }
    public boolean isAvailable() { return isAvailable; }

    // --- Settery (Logika biznesowa) ---
    public void setPrice(int newPrice) {
        this.price = newPrice;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}