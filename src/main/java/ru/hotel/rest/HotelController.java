package ru.hotel.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HotelController {
    public HotelController() {
    }

    @GetMapping("/")
    public String listHotel() {
        return "index";
    }

    @GetMapping("/book/{id}")
    public String editHotel() {
        return "hotel";
    }
}
