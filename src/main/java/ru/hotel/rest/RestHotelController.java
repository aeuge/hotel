package ru.hotel.rest;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.Hotel;
import ru.hotel.dataMining.DataMiningService;
import ru.hotel.service.HotelService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
public class RestHotelController {

    private final HotelService service;
    private final DataMiningService dataMiningService;

    @Autowired
    public RestHotelController(HotelService service, DataMiningService dataMiningService) {
        this.service = service;
        this.dataMiningService = dataMiningService;
    }

    @GetMapping("/api/allhotels")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'book', 'read')")
    @HystrixCommand(fallbackMethod = "getDefaultHotels", groupKey = "HotelService", commandKey = "findAll", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")})
    public Flux<HotelDto> getAllBooks(@AuthenticationPrincipal(expression = "principal") Principal principal) throws InterruptedException {
        System.out.println("Privileges: " + ((Authentication) principal).getAuthorities());
        //Thread.sleep(10000);
        return service.getAll().map(ConverterHotelToDto::toDto);
    }

    @GetMapping("/api/hotel/{id}")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'book', 'read')")
    @HystrixCommand(groupKey = "HotelService", commandKey = "findHotel")
    public Mono<HotelDto> getBook(@PathVariable String id, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        System.out.println("запрос обработан "+id);
        return service.getById(id).map(ConverterHotelToDto::toDto);
    }

    @DeleteMapping("/hotel/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @HystrixCommand(groupKey = "HotelService", commandKey = "deleteBook")
    public Mono<Void> deleteBook(@PathVariable String id, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        return service.deleteHotel(id);
    }

    @PostMapping("/api/hotel/{id}")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'book', 'write')")
    @HystrixCommand(groupKey = "HotelService", commandKey = "saveHotel")
    public Mono<Hotel> saveBook(@RequestBody HotelDto hotelDto, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        return service.getById(hotelDto.getId()).switchIfEmpty(Mono.just(new Hotel()))
                .map(v -> {
                    v.setName(hotelDto.getName());
                    return v;
                }).flatMap(service::saveHotel);
    }

    public Flux<HotelDto> getDefaultHotels(@AuthenticationPrincipal(expression = "principal") Principal principal) {
        return Flux.just(new HotelDto("ПУстотень"));
    }

    @GetMapping("/parse")
    public Integer parse(@RequestParam String beginDate, @RequestParam String endDate, @AuthenticationPrincipal(expression = "principal") Principal principal)  {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate bd = LocalDate.parse(beginDate, dateTimeFormatter);
        LocalDate ed = LocalDate.parse(endDate, dateTimeFormatter);
        try {
            return dataMiningService.extractData(bd, ed);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return 500;
        }
    }
}