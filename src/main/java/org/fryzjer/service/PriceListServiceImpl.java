package org.fryzjer.service;

import org.fryzjer.model.Service;
import org.fryzjer.repository.HairSalonRepository;
import java.util.List;

public class PriceListServiceImpl implements PriceListService {

    private final HairSalonRepository repository;

    public PriceListServiceImpl(HairSalonRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addNewService(String serviceName, int price) {
        validateServiceInputs(serviceName, price);
        System.out.println("Service: Adding new service...");
        repository.addService(serviceName, price);
    }

    @Override
    public void updateServicePrice(long serviceId, int newPrice) {
        // --- I TUTAJ TEŻ ---
        if (newPrice <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero. Entered price: " + newPrice);
        }

        repository.findServiceById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("No service with such ID: " + serviceId));

        System.out.println("Service: Updating price...");
        repository.updateServicePrice(serviceId, newPrice);
    }

    private void validateServiceInputs(String serviceName, int price) {
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("Name of service cannot be null or blank. Entered price: .");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero. Entered price: " + price);
        }
    }

    @Override
    public void archiveService(long serviceId) {
        repository.archiveService(serviceId);
    }

    @Override
    public List<Service> getAllServices() {
        return repository.getAllServices();
    }

    @Override
    public List<Service> getAvailablePriceList() {
        return repository.getAllAvailableServices();
    }
}