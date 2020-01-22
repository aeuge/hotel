package ru.hotel.dataMining;

import java.time.LocalDate;

public interface DataMiningService {
    Integer extractData(LocalDate beginDate, LocalDate endDate) throws InterruptedException;
}
