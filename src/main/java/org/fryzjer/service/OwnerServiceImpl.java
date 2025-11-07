package org.fryzjer.service;

import org.fryzjer.model.Reservation;
import org.fryzjer.repository.HairSalonRepository;
import java.util.List;

public class OwnerServiceImpl implements OwnerService {

    private final HairSalonRepository repository;
    private final PriceListService priceListService;

    public OwnerServiceImpl(HairSalonRepository repository, PriceListService priceListService) {
        this.repository = repository;
        this.priceListService = priceListService;
    }

    @Override
    public List<Reservation> viewAllReservations() {
        System.out.println("Service: Owner requests all reservations...");
        return repository.getAllReservations();
    }

    @Override
    public PriceListService getPriceListService() {
        return this.priceListService;
    }
}