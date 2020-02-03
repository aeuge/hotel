package ru.hotel.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller
public class HotelController {
    public HotelController() {
    }

    @GetMapping("/")
    public String listPayment() {
        //Locale.setDefault(new Locale("en", "EN"));
        return "index";
    }

    @GetMapping("/hotel/{id}")
    public String editHotel() {
        return "hotel";
    }

    @GetMapping("/hotels")
    public String listHotel() {
        return "hotels";
    }
}
