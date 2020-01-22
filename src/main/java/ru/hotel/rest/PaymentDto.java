package ru.hotel.rest;

import lombok.Data;

@Data
public class PaymentDto {
    private Integer kodHotel = 0;
    private String name = "";
    private Integer paymentTotal = 0;
    private Integer payment1 = 0;
    private Integer payment2 = 0;
    private Integer payment3 = 0;

    public PaymentDto() {}

    public PaymentDto(String name) {
        this.name = name;
    }

    public boolean checkEquals() {
        return (paymentTotal==payment1+payment2+payment3)?true:false;
    }

    public void setAll(PaymentDto payment) {
        this.kodHotel = payment.getKodHotel();
        this.paymentTotal = payment.getPaymentTotal();
        this.payment1 = payment.getPayment1();
        this.payment2 = payment.getPayment2();
        this.payment3 = payment.getPayment3();
    }
}
