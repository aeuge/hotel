package ru.hotel.dataMining;

import lombok.Data;
import org.openqa.selenium.WebElement;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.hotel.domain.PaymentType;

@Data
public class PaymentTypeWithWebElement {
    private String id;
    private Integer kod;
    private String type;
    private WebElement wel;

    public PaymentTypeWithWebElement() {}

    public PaymentTypeWithWebElement(String type) {
        this.type = type;
    }

    public PaymentTypeWithWebElement(Integer kod, String type) {
        this.kod = kod;
        this.type = type;
    }

    public PaymentTypeWithWebElement(String id, Integer kod, String type) {
        this.id = id;
        this.kod = kod;
        this.type = type;
    }

    public PaymentTypeWithWebElement(PaymentType paymentType) {
        this.id = paymentType.getId();
        this.kod = paymentType.getKod();
        this.type = paymentType.getType();
    }

    public PaymentTypeWithWebElement(PaymentType paymentType, WebElement wel) {
        this.wel = wel;
        this.id = paymentType.getId();
        this.kod = paymentType.getKod();
        this.type = paymentType.getType();
    }
}
