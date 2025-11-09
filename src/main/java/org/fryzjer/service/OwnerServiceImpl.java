package org.fryzjer.service;

import org.fryzjer.model.Reservation;
import org.fryzjer.model.ReservationStatus;
import org.fryzjer.model.Service;
import org.fryzjer.repository.HairSalonRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @Override
    public double calculateTotalRevenue() {
        System.out.println("Service: Owner requests total revenue...");

        List<Reservation> paidReservations = repository.getAllPaidReservations();

        double totalRevenue = 0.0;

        for (Reservation res : paidReservations) {
            Optional<Service> serviceOpt = repository.findServiceByName(res.getNameOfService());

            if (serviceOpt.isPresent()) {
                totalRevenue += serviceOpt.get().getPrice();
            } else {
                System.err.println("Warning: Cannot calculate revenue for reservation ID="
                        + res.getId() + ". Service '" + res.getNameOfService() + "' no longer exists.");
            }
        }

        return totalRevenue;
    }
    @Override
    public int cleanupMissedReservations() {
        System.out.println("Service: Owner requests cleanup of old (missed) reservations...");

        List<Reservation> missedReservations = repository.findPendingReservationsBefore(LocalDate.now());

        if (missedReservations.isEmpty()) {
            System.out.println("Service: No missed reservations found.");
            return 0;
        }

        for (Reservation res : missedReservations) {
            repository.updateReservationStatus(res.getId(), ReservationStatus.MISSED);
        }

        System.out.println("Service: Cleaned up " + missedReservations.size() + " reservations.");
        return missedReservations.size();
    }
}