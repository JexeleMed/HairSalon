package org.fryzjer.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record Reservation(long id, String nameOfService, String establishmentNumber,
                          LocalDate date, LocalTime time, String workerNumber,
                          String clientNumber) {}
