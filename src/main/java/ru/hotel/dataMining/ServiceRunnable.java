package ru.hotel.dataMining;

import java.time.LocalDate;

public class ServiceRunnable implements Runnable {
    private LocalDate beginDate;
    private LocalDate endDate;
    private Integer result = 200;
    private DataMiningService dataMiningService;
    private Object holder;

    public ServiceRunnable(Object holder, LocalDate beginDate, LocalDate endDate, DataMiningService dataMiningService) {
        this.holder = holder;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.dataMiningService = dataMiningService;
    }

    @Override
    public void run() {
        try {
            /*synchronized (holder) {
                holder.notifyAll();
            }*/
            result = dataMiningService.extractData(beginDate, endDate);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = 500;
        }
    }

    public Integer getResult() {
        return result;
    }
}
