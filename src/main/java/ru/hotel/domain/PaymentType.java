package ru.hotel.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("types")
public class PaymentType {
    @Id
    private String id;
    private Integer kod;
    private String type;

    public PaymentType() {}

    public PaymentType(String type) {
        this.type = type;
    }

    public PaymentType(Integer kod, String type) {
        this.kod = kod;
        this.type = type;
    }
}
