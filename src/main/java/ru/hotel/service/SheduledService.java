package ru.hotel.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.hotel.dataMining.DataMiningService;
import ru.hotel.dataMining.ServiceRunnable;

import java.time.LocalDate;

import static java.time.LocalDate.now;

@Service
public class SheduledService {
    private DataMiningService dataMiningService;

    public SheduledService(DataMiningService dataMiningService) {
        this.dataMiningService = dataMiningService;
    }

    @Scheduled(cron="0 0 1,13,22 * * ?")
    private void sheduledNight(){
        LocalDate ed = now();
        LocalDate bd = ed.minusDays(1);
        ServiceRunnable serviceRunnable = new ServiceRunnable(bd,ed,dataMiningService);
        Thread myThread = new Thread(serviceRunnable);
        myThread.start();
    }

    @Scheduled(cron="0 0 2 * * ?")
    private void sheduledWeek(){
        LocalDate ed = now();
        LocalDate bd = ed.minusDays(10);
        if (ed.getDayOfMonth()<11) {
            bd = ed.minusDays(ed.getDayOfMonth()-1);
        }
        ServiceRunnable serviceRunnable = new ServiceRunnable(bd, ed, dataMiningService);
        Thread myThread = new Thread(serviceRunnable);
        myThread.start();
    }

    @Scheduled(cron="0 0 4 * * ?")
    private void sheduledWeekPrevMonth(){
        LocalDate ed = now();
        if (ed.getDayOfMonth()<11) {
            ed = ed.minusDays(ed.getDayOfMonth());
            LocalDate bd = ed.minusDays(10);
            ServiceRunnable serviceRunnable = new ServiceRunnable(bd, ed, dataMiningService);
            Thread myThread = new Thread(serviceRunnable);
            myThread.start();
        }
    }

}
