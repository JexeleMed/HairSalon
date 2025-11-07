package org.fryzjer.service;

import org.fryzjer.model.Reservation;
import java.util.List;

public interface OwnerService {

    List<Reservation> viewAllReservations();
    PriceListService getPriceListService();
}