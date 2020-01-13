package ru.hotel.rest;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
import ru.hotel.domain.Hotel;
import ru.hotel.service.HotelService;

import java.io.FileInputStream;
import java.security.Principal;
import java.util.List;
import java.util.Properties;

@RestController
public class RestBookController {

    private final HotelService service;

    @Autowired
    public RestBookController(HotelService service) {
        this.service = service;
    }

    @GetMapping("/api/allbooks")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'book', 'read')")
    @HystrixCommand(fallbackMethod = "getDefaultBooks", groupKey = "HotelService", commandKey = "findAll", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")})
    public Flux<BookDto> getAllBooks(@AuthenticationPrincipal(expression = "principal") Principal principal) throws InterruptedException {
        System.out.println("Privileges: " + ((Authentication) principal).getAuthorities());
        //Thread.sleep(10000);
        return service.getAll().map(ConverterBookToDto::toDto);
    }

    @GetMapping("/api/book/{id}")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'book', 'write')")
    @HystrixCommand(groupKey = "HotelService", commandKey = "findBook")
    public Mono<BookDto> getBook(@PathVariable String id, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        return service.getById(id).map(ConverterBookToDto::toDto);
    }

    @DeleteMapping("/book/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @HystrixCommand(groupKey = "HotelService", commandKey = "deleteBook")
    public Mono<Void> deleteBook(@PathVariable String id, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        return service.deleteBook(id);
    }

    @PostMapping("/api/book/{id}")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'book', 'write')")
    @HystrixCommand(groupKey = "HotelService", commandKey = "saveBook")
    public Mono<Hotel> saveBook(@RequestBody BookDto bookDto, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        return service.getById(bookDto.getId()).switchIfEmpty(Mono.just(new Hotel()))
                .map(v -> {
                    v.setName(bookDto.getTitle());
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
            ClassLoader classLoader = getClass().getClassLoader();
            props.load(new FileInputStream(classLoader.getResource("config.ini").getFile()));
            login = props.getProperty("LOGIN");
            password = props.getProperty("PASSWORD");
        }
        catch (Exception e) {
        }
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
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
        driver.get("https://www.travelline.ru/secure/SwitchUserContext.ashx?ReturnUrl=%2fsecure%2fwebpms%2fextranet%2f&App=Management&Context=7730");
        dynamicElement = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='provider-id ng-binding']")));
        System.out.println("Номер гостинницы: "+dynamicElement.getText());

        driver.get("https://www.travelline.ru/secure/webpms/extranet/#/WebPmsReport/billing");
        Thread.sleep(3000);
        System.out.println("hi");
        List<WebElement> els = driver.findElements(By.xpath(".//span"));
        System.out.println(els.size());
        WebElement itogo=element;
        Integer iii = 0;
        for (WebElement wel:els) {
            if (iii++>1200) {
                //System.out.println(iii);
                if (!wel.getText().isEmpty())
                    if (wel.getText().startsWith("Всего активных")) {
                        itogo = wel;
                        break;
                    }
            }
        }
        System.out.println("_-----buttons-----_-");
        List<WebElement> els2 = driver.findElements(By.xpath(".//button"));

        WebElement filter=element;
        for (WebElement wel:els2) {
            if (wel.getText().equalsIgnoreCase("применить фильтр")) {filter = wel;break;}
        }
        System.out.println("_-----inputs-----_-");
        List<WebElement> els3 = driver.findElements(By.xpath(".//input"));
        //els3.forEach((k) -> System.out.println(k.getText() + " -- "));
        System.out.println("_-----uls-----_-");
        els3.get(0).click();
        Thread.sleep(200);
        List<WebElement> els6 = driver.findElements(By.xpath(".//ul"));
        //els6.forEach((k) -> System.out.println(k.getText() + " -- ul"));
        //WebElement dynamicElement2 = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(els7.get(0)));
        ((ChromeDriver) driver).executeScript("i = 0; document.querySelectorAll('button').forEach(box => { box.setAttribute('id',i); this.i++;});");
        Thread.sleep(100);
        List<WebElement> els7 = els6.get(els6.size()-2).findElements(By.xpath(".//button"));
        System.out.println(els7.get(0).getAttribute("id").toString()+" uti puti");
        els7.get(5).click();
        filter.click();
        Thread.sleep(4000);
        System.out.println("x-button");
        List<WebElement> els11 = driver.findElements(By.xpath(".//x-button"));
        //els11.forEach((k) -> System.out.println(k.getText() + " -- x-buton"+k.getAttribute("id").toString()));
        //((ChromeDriver) driver).executeScript("i = 0; document.querySelectorAll('x-control').forEach(box => { box.setAttribute('id',i); this.i++;});");
        els11.get(1).click();
        Thread.sleep(1000);
        List<WebElement> els12 = driver.findElements(By.xpath(".//x-button"));
        els12.forEach((k) -> System.out.println(k.getText() + " -- x-button after"+k.getAttribute("id").toString()));
        WebElement card=element;
        for (WebElement wel:els12) {
            if (wel.getText().equalsIgnoreCase("картой через терминал")) {card = wel;break;}
        }
        System.out.println("Итого "+itogo.getText());
        card.click();
        Thread.sleep(1000);
        card.click();
        Thread.sleep(1000);
        card.click();
        Thread.sleep(1000);
        System.out.println("Итого как убрали галку"+itogo.getText());

        System.out.println(" finish");
        Thread.sleep(10000);


        Thread.sleep(200000);
        driver.close();
    }
}