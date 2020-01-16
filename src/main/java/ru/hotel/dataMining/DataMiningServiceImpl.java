package ru.hotel.dataMining;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.hotel.dataMining.DataMiningService;
import ru.hotel.domain.Hotel;
import ru.hotel.domain.PaymentType;
import ru.hotel.service.HotelService;
import ru.hotel.service.PaymentTypeService;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Service
public class DataMiningServiceImpl implements DataMiningService {
    private final HotelService service;
    private final PaymentTypeService paymentTypeService;
    private WebDriver driver = null;
    private List<PaymentType> payments;

    public DataMiningServiceImpl(HotelService service, PaymentTypeService paymentTypeService) {
        this.service = service;
        this.paymentTypeService = paymentTypeService;
    }

    private void init (){
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
        driver = new ChromeDriver();
        driver.get("https://www.travelline.ru/secure/Enter.aspx");
        WebElement element = driver.findElement(By.name("username"));
        element.sendKeys(login);
        element = driver.findElement(By.name("password"));
        element.sendKeys(password);
        element = driver.findElement(By.xpath("/html/body/div[3]/div[3]/div[1]/form/div[4]/div[2]/tl-button"));
        element.click();
    }

    private void extractExactHotel(String kod) throws InterruptedException {
        driver.get("https://www.travelline.ru/secure/SwitchUserContext.ashx?ReturnUrl=%2fsecure%2fwebpms%2fextranet%2f&App=Management&Context="+kod);
        WebElement dynamicElement = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='provider-id ng-binding']")));
        System.out.println("Номер гостинницы: "+dynamicElement.getText());

        driver.get("https://www.travelline.ru/secure/webpms/extranet/#/WebPmsReport/billing");
        Thread.sleep(3000);
        List<WebElement> listTextSpans = driver.findElements(By.xpath(".//span"));
        System.out.println(listTextSpans.size());
        WebElement itogo=dynamicElement;
        for (Integer i = 1200; i <  listTextSpans.size();i++) {
                if (!listTextSpans.get(i).getText().isEmpty()) {
                    if (listTextSpans.get(i).getText().startsWith("Всего активных")) {//находим span с результатами
                        itogo = listTextSpans.get(i);
                        break;
                    }
                }
        }
        List<WebElement> listButtons = driver.findElements(By.xpath(".//button"));
        WebElement filterButton=dynamicElement;
        for (WebElement wel:listButtons) { if (wel.getText().equalsIgnoreCase("применить фильтр")) {filterButton = wel;break;} }
        List<WebElement> listInputs = driver.findElements(By.xpath(".//input"));
        listInputs.get(0).click();//открываем календарик
        Thread.sleep(200);
        List<WebElement> listUL = driver.findElements(By.xpath(".//ul"));
//        ((ChromeDriver) driver).executeScript("i = 0; document.querySelectorAll('button').forEach(box => { box.setAttribute('id',i); this.i++;});");
//        Thread.sleep(100);
        List<WebElement> listButtonsInCalendar = listUL.get(listUL.size()-2).findElements(By.xpath(".//button"));
        listButtonsInCalendar.get(5).click();//кликаем нужную дату
        filterButton.click();
        Thread.sleep(4000);
        List<WebElement> listXButton = driver.findElements(By.xpath(".//x-button"));
        listXButton.get(1).click();//нажимаем кнопку выбора типа оплаты - если нет типа ?????
        Thread.sleep(1000);
        List<WebElement> listXButtonPaymentType = driver.findElements(By.xpath(".//x-button"));
        //payments.forEach(e->paypentsWithWebElement.add(ConverterTypeToTypeWithWebElement.toWWE(e)));
        List<PaymentTypeWithWebElement> paypentsWithWebElement= new ArrayList<>();
        WebElement otmenaButton=dynamicElement;
        for (WebElement wel:listXButtonPaymentType) {
            for (PaymentType ptwwe : payments) {
                if (wel.getText().equalsIgnoreCase(ptwwe.getType())) {
                    paypentsWithWebElement.add(new PaymentTypeWithWebElement(ptwwe, wel));
                }
                if (wel.getText().equalsIgnoreCase("Отменить все")) {
                    otmenaButton = wel;
                }
            }
        }
        for (PaymentTypeWithWebElement pwwel: paypentsWithWebElement) {
            System.out.println("Итого "+itogo.getText());
            otmenaButton.click();
            Thread.sleep(2000);
            System.out.println("Итого как убрали галку"+itogo.getText());
            pwwel.getWel().click();
            Thread.sleep(2000);
        }

    }

    public void extractData() throws InterruptedException {
        init();
        WebElement dynamicElement = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='provider-id ng-binding']")));
        System.out.println("Первоначальный номер гостинницы: "+dynamicElement.getText());
        //Mono<List<Hotel>> monol=
        List<Hotel> hotels = new ArrayList<>();
        service.getAll().map(e->hotels.add(e)).subscribe();
        payments = new ArrayList<>();
        paymentTypeService.getAll().map(e->payments.add(e)).subscribe();
        Thread.sleep(100);
        hotels.forEach(System.out::println);
        for (Hotel hotel : hotels) {
            System.out.println("Заполняем гостинницу "+hotel.getKod());
            extractExactHotel(hotel.getKod());
        }
        System.out.println(" finish");
        Thread.sleep(200000);
        driver.close();
    }
}
