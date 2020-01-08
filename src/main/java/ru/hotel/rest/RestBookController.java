package ru.hotel.rest;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.Book;
import ru.hotel.service.BookService;

import java.io.File;
import java.io.FileInputStream;
import java.security.Principal;
import java.util.Properties;

@RestController
public class RestBookController {

    private final BookService service;

    @Autowired
    public RestBookController(BookService service) {
        this.service = service;
    }

    @GetMapping("/api/allbooks")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'book', 'read')")
    @HystrixCommand(fallbackMethod = "getDefaultBooks", groupKey = "BookService", commandKey = "findAll", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")})
    public Flux<BookDto> getAllBooks(@AuthenticationPrincipal(expression = "principal") Principal principal) throws InterruptedException {
        System.out.println("Privileges: " + ((Authentication) principal).getAuthorities());
        //Thread.sleep(10000);
        return service.getAll().map(ConverterBookToDto::toDto);
    }

    @GetMapping("/api/book/{id}")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'book', 'write')")
    @HystrixCommand(groupKey = "BookService", commandKey = "findBook")
    public Mono<BookDto> getBook(@PathVariable String id, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        return service.getById(id).map(ConverterBookToDto::toDto);
    }

    @DeleteMapping("/book/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @HystrixCommand(groupKey = "BookService", commandKey = "deleteBook")
    public Mono<Void> deleteBook(@PathVariable String id, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        return service.deleteBook(id);
    }

    @PostMapping("/api/book/{id}")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'book', 'write')")
    @HystrixCommand(groupKey = "BookService", commandKey = "saveBook")
    public Mono<Book> saveBook(@RequestBody BookDto bookDto, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        return service.getById(bookDto.getId()).switchIfEmpty(Mono.just(new Book()))
                .map(v -> {
                    v.setTitle(bookDto.getTitle());
                    v.setAuthor(bookDto.getAuthor());
                    v.setGenre(bookDto.getGenre());
                    v.setComment(bookDto.getComment());
                    return v;
                }).flatMap(service::saveBook);
    }

    public Flux<BookDto> getDefaultBooks(@AuthenticationPrincipal(expression = "principal") Principal principal) {
        return Flux.just(new BookDto("Конституция"));
    }

    @GetMapping("/parse")
    public void parse(@AuthenticationPrincipal(expression = "principal") Principal principal) throws InterruptedException {
        Properties props = new Properties();
        String login = new String();
        String password = new String();
        try {
            props.load(new FileInputStream(new File("config/example.ini")));
            login = props.getProperty("LOGIN");
            password = props.getProperty("PASSWORD");
        }
        catch (Exception e) {
        }
        WebDriverManager.firefoxdriver().setup();
        WebDriver driver = new FirefoxDriver();
        driver.get("https://www.travelline.ru/secure/Enter.aspx");
        WebElement element = driver.findElement(By.name("username"));
        element.sendKeys(login);
        element = driver.findElement(By.name("password"));
        element.sendKeys(password);
        element = driver.findElement(By.xpath("/html/body/div[3]/div[3]/div[1]/form/div[4]/div[2]/tl-button"));
        element.click();
        WebElement dynamicElement = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='provider-id ng-binding']")));
        System.out.println("Номер гостинницы: "+dynamicElement.getText());
        /*driver.get("https://www.travelline.ru/secure/SwitchUserContext.ashx?ReturnUrl=%2fsecure%2fwebpms%2fextranet%2f&App=Management&Context=7730");
        dynamicElement = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='provider-id ng-binding']")));
        System.out.println("Номер гостинницы: "+dynamicElement.getText());*/
        element = driver.findElement(By.xpath("/html/body/div/div[2]/ul/li[1]/a"));
        System.out.println("имя гостинницы: "+element.getText());

        System.out.println("mother fucker "+((FirefoxDriver) driver).getSessionStorage().getItem("_ym_d"));
        driver.get("https://www.travelline.ru/secure/webpms/extranet/#/WebPmsReport/billing");
        //driver.get("https://www.travelline.ru/secure/webpms/ExtranetWebApi/service/reports/billing-report?end=20200107235959&reportKind=0&start=20200102000000");
        ///html/body/div[3]/tl-extranet-host/div/div[3]/div/div[2]/div/div/div/div/div[3]/tl-control-billing-report/div/div[4]/div/tl-control-date[1]/div/input
        dynamicElement = (new WebDriverWait(driver, 20))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@class='tl-control form-control datepicker-input ng-valid ng-isolate-scope datepicker-input-not-readonly ng-valid-date ng-touched ng-dirty']")));
        ///html/body/div[3]/tl-extranet-host/div/div[3]/div/div[2]/div/div/div/div/div[3]/tl-control-billing-report/div/div[4]/div/tl-control-date[1]/div/input
        System.out.println("Номер гостинницы: "+dynamicElement.getText());
        System.out.println(((FirefoxDriver) driver).getSessionStorage().getItem("_ym_d"));

        //WebElement dynamicElement2 = (new WebDriverWait(driver, 10))
        //        .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='b-menu-list__submenu_link']")));
        //System.out.println("Гостиница: "+dynamicElement2.getText());
        //dynamicElement2.click();
        //element = driver.findElement(By.partialLinkText("Nord Point"));
        //element.click();
        Thread.sleep(200000);
        driver.close();
    }
}