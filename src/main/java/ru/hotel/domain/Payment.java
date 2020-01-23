package ru.hotel.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document("payments")
public class Payment {
    @Id
    private String id;
    private Integer kodHotel;
    private LocalDate data;
    private Integer paymentTotal = 0;
    private Integer payment1 = 0;
    private Integer payment2 = 0;
    private Integer payment3 = 0;
    private Integer payment4 = 0;

    public Payment() {}

    public Payment(Integer kodHotel, LocalDate data) {
        this.kodHotel = kodHotel;
        this.data = data;
    }

    public boolean checkEquals() {
        return (paymentTotal==payment1+payment2+payment3+payment4)?true:false;
    }

    public void setAll(Payment payment) {
        this.kodHotel = payment.getKodHotel();
        this.data = payment.getData();
        this.paymentTotal = payment.getPaymentTotal();
        this.payment1 = payment.getPayment1();
        this.payment2 = payment.getPayment2();
        this.payment3 = payment.getPayment3();
        this.payment4 = payment.getPayment4();
    }
}
