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
import ru.hotel.dataMining.ServiceRunnable;
import ru.hotel.domain.Hotel;
import ru.hotel.dataMining.DataMiningService;
import ru.hotel.service.HotelService;
import ru.hotel.service.PaymentService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RestHotelController {

    private final HotelService service;
    private final PaymentService paymentService;
    private final DataMiningService dataMiningService;

    @Autowired
    public RestHotelController(HotelService service, DataMiningService dataMiningService, PaymentService paymentService) {
        this.service = service;
        this.dataMiningService = dataMiningService;
        this.paymentService = paymentService;
    }

    @GetMapping("/api/allhotels")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'hotel', 'read')")
    @HystrixCommand(fallbackMethod = "getDefaultHotels", groupKey = "HotelService", commandKey = "findAll", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")})
    public Flux<HotelDto> getAllHotels(@AuthenticationPrincipal(expression = "principal") Principal principal) throws InterruptedException {
        System.out.println("Privileges: " + ((Authentication) principal).getAuthorities());
        return service.getAll().map(ConverterHotelToDto::toDto);
    }

    @GetMapping("/api/allpayments")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'hotel', 'read')")
    @HystrixCommand(fallbackMethod = "getDefaultPayments", groupKey = "PaymentService", commandKey = "findAll", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")})
    public Flux<PaymentDto> getAllPayments(@RequestParam String beginDate, @RequestParam String endDate, @AuthenticationPrincipal(expression = "principal") Principal principal) throws InterruptedException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate bd = LocalDate.parse(beginDate, dateTimeFormatter);
        LocalDate ed = LocalDate.parse(endDate, dateTimeFormatter);
        List<Hotel> hotels = new ArrayList<>();
        service.getAll().map(e->hotels.add(e)).subscribe();
        Flux<PaymentDto> fp = paymentService.agreg(bd, ed).map(e->{hotels.forEach(r->{
            if (r.getKod().equals(e.getKodHotel())) {
                e.setName(r.getName());
            }});
            return e;
        });
        fp.subscribe(System.out::println);
        return fp;
    }

    @GetMapping("/api/hotel/{id}")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'hotel', 'read')")
    @HystrixCommand(groupKey = "HotelService", commandKey = "findHotel")
    public Mono<HotelDto> getBook(@PathVariable String id, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        System.out.println("запрос обработан "+id);
        return service.getById(id).map(ConverterHotelToDto::toDto);
    }

    @DeleteMapping("/hotel/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_ADMIN2')")
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

    public Flux<PaymentDto> getDefaultPayments(@RequestParam String beginDate, @RequestParam String endDate, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        return Flux.just(new PaymentDto("Ошибка"));
    }

    @GetMapping("/parse")
    public Integer parse(@RequestParam String beginDate, @RequestParam String endDate, @AuthenticationPrincipal(expression = "principal") Principal principal) throws InterruptedException {
        Object holder = new Object();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate bd = LocalDate.parse(beginDate, dateTimeFormatter);
        LocalDate ed = LocalDate.parse(endDate, dateTimeFormatter);
        ServiceRunnable serviceRunnable = new ServiceRunnable(holder,bd,ed,dataMiningService);
        Thread myThread = new Thread(serviceRunnable);
        myThread.start();
        /*synchronized (holder) {
            holder.wait();
        }*/
        return serviceRunnable.getResult();
    }
}