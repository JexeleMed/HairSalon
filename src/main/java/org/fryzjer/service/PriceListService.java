package org.fryzjer.service;

import org.fryzjer.model.Service;
import java.util.List;

public interface PriceListService {
    void addNewService(String serviceName, int price);
    void updateServicePrice(long serviceId, int newPrice);
    void archiveService(long serviceId);
    List<Service> getAllServices();
    List<Service> getAvailablePriceList();
}