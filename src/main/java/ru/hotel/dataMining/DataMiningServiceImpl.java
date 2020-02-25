package ru.hotel.dataMining;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.hotel.Main;
import ru.hotel.domain.Hotel;
import ru.hotel.domain.Payment;
import ru.hotel.domain.PaymentType;
import ru.hotel.service.HotelService;
import ru.hotel.service.PaymentService;
import ru.hotel.service.PaymentTypeService;

import java.io.FileInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.LocalDate.now;

@Service
public class DataMiningServiceImpl implements DataMiningService {
    private final HotelService service;
    private final PaymentTypeService paymentTypeService;
    private final PaymentService paymentService;
    private WebDriver driver = null;
    private List<PaymentType> payments;
    private WebElement itogo = null;
    private WebElement filterButton = null;
    private Integer kodHotel = 0;
    private LocalDate beginDate = null;
    private LocalDate endDate = null;
    private Integer exitStatus = 500;
    private Logger logger = null;

    public DataMiningServiceImpl(HotelService service, PaymentTypeService paymentTypeService, PaymentService paymentService) {
        this.service = service;
        this.paymentTypeService = paymentTypeService;
        this.paymentService = paymentService;
    }

    private void init () throws InterruptedException {
        Properties props = new Properties();
        String login = new String();
        String password = new String();
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            props.load(classLoader.getResourceAsStream("config.ini"));
            login = props.getProperty("LOGIN");
            password = props.getProperty("PASSWORD");
        }
        catch (Exception e) {
            logger.error("failed to find config.ini");
        }
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");
        options.addArguments("window-size=1800x900");
        driver = new ChromeDriver(options);
        driver.get("https://www.travelline.ru/secure/Enter.aspx?lng=ru");
        Thread.sleep(3000);
        logger.info("login-"+login);
        WebElement element = driver.findElement(By.name("username"));
        element.sendKeys(login);
        element = driver.findElement(By.name("password"));
        element.sendKeys(password);
        element = driver.findElement(By.xpath("/html/body/div[3]/div[3]/div[1]/form/div[4]/div[2]/tl-button"));
        element.click();
    }

    private Integer extractPaymentFromString(String str) {
        String strnum = str.substring(str.indexOf("на сумму")+9,str.indexOf("RUB")-1).replace(" ", "");
        if (strnum.contains(",")==true) strnum = strnum.substring(0,strnum.indexOf(","));
        Integer num = Integer.parseInt(strnum);
        return num;
    }

    private void findItogo() throws InterruptedException {
        driver.get("https://www.travelline.ru/secure/webpms/extranet/#/WebPmsReport/billing");
        Thread.sleep(3000);
        List<WebElement> listTextSpans = driver.findElements(By.xpath(".//span"));
        //System.out.println("list spans "+listTextSpans.size());
        for (Integer i = listTextSpans.size()-200; i <  listTextSpans.size();i++) {
            if (!listTextSpans.get(i).getText().isEmpty()) {
                if (listTextSpans.get(i).getText().startsWith("Всего активных")) {//находим span с результатами
                    itogo = listTextSpans.get(i);
                    break;
                }
            }
        }
    }

    private void findFilterButton() {
        List<WebElement> listButtons = driver.findElements(By.xpath(".//button"));
        for (WebElement wel:listButtons) {
            if (wel.getText().equalsIgnoreCase("применить фильтр")) {
                filterButton = wel;
                break;
            }
        }
        //((ChromeDriver) driver).executeScript("i = 0; document.querySelectorAll('button').forEach(box => { box.setAttribute('id',i); this.i++;});");
    }

    private void fillPayment(Payment payment) throws InterruptedException {
        filterButton.click();
        Thread.sleep(4000);
        //если сумма оплаты 0, то и кнопок не будет
        payment.setPaymentTotal(extractPaymentFromString(itogo.getText()));
        //System.out.println("Итого " + payment.getPaymentTotal());
        if (extractPaymentFromString(itogo.getText())>0) {
            List<WebElement> listXButton = driver.findElements(By.xpath(".//x-button"));
            listXButton.get(1).click();//нажимаем кнопку выбора типа оплаты
            Thread.sleep(1000);
            List<WebElement> listXButtonPaymentType = driver.findElements(By.xpath(".//x-button"));
            List<PaymentTypeWithWebElement> paypentsWithWebElement = new ArrayList<>();
            WebElement otmenaButton = null;
            for (WebElement wel : listXButtonPaymentType) {
                for (PaymentType ptwwe : payments) {
                    if (wel.getText().startsWith(ptwwe.getType())) {
                        paypentsWithWebElement.add(new PaymentTypeWithWebElement(ptwwe, wel));
                    }
                    if (wel.getText().equalsIgnoreCase("Отменить все")) {
                        otmenaButton = wel;
                    }
                }
            }
            if (otmenaButton==null) {
                for (PaymentTypeWithWebElement pwwel : paypentsWithWebElement) {
                    pwwel.getWel().click();
                    Thread.sleep(1000);
                }
            } else otmenaButton.click();
            for (PaymentTypeWithWebElement pwwel : paypentsWithWebElement) {
                pwwel.getWel().click();
                Thread.sleep(2000);
                Integer paymentAmount = extractPaymentFromString(itogo.getText());
                //System.out.println(pwwel.getType() + " " + paymentAmount);
                switch (pwwel.getKod()) {
                    case (1) : {payment.setPayment1(paymentAmount);break;}
                    case (2) : {payment.setPayment2(paymentAmount);break;}
                    case (3) : {payment.setPayment3(paymentAmount);break;}
                    case (4) : {payment.setPayment4(paymentAmount);break;}
                }
                pwwel.getWel().click();
                Thread.sleep(1000);
            }
            for (PaymentTypeWithWebElement pwwel : paypentsWithWebElement) {
                pwwel.getWel().click();
                Thread.sleep(1000);
            }
            if (!payment.checkEquals()) {
                System.out.println("!!! суммы не совпадают "+payment);
                Thread.sleep(1000);
                Integer paymentAmount = extractPaymentFromString(itogo.getText());
                payment.setPaymentTotal(paymentAmount);
            }
        }
        paymentService.savePayment(payment).subscribe();
    }

    private void chooseDateAndFill(Integer month, Integer day, Integer firstChangeDate, Payment payment) throws InterruptedException {
        //System.out.println("ВЫзвали изменить календарь на даты "+month+" "+day);
        List<WebElement> listInputs = driver.findElements(By.xpath(".//input"));
        List<WebElement> listButtonsInCalendarDate = null;
        if (firstChangeDate==0) {
            listInputs.get(0).click();//открываем календарик
            Thread.sleep(200);
            List<WebElement> listUL = driver.findElements(By.xpath(".//ul"));
            listButtonsInCalendarDate = listUL.get(listUL.size() - 2).findElements(By.xpath(".//button"));
        } else {
            listInputs.get(2).click();//открываем календарик
            Thread.sleep(200);
            List<WebElement> listUL2 = driver.findElements(By.xpath(".//ul"));
            listButtonsInCalendarDate = listUL2.get(listUL2.size() - 1).findElements(By.xpath(".//button"));
            for (int i = 0; i < month; i++) {
                listButtonsInCalendarDate.get(0).click();
                Thread.sleep(100);
                listButtonsInCalendarDate = listUL2.get(listUL2.size() - 1).findElements(By.xpath(".//button"));
                Thread.sleep(100);
            }//выбираем нужный месяц
        }
        Integer dayButton = -1;
        for (WebElement wel : listButtonsInCalendarDate) {
            dayButton++;
            if (wel.getText().equalsIgnoreCase("1")) break;
        }
        dayButton = dayButton + day - 1;
        listButtonsInCalendarDate.get(dayButton).click();//кликаем нужную дату
        Thread.sleep(200);
        //System.out.println(" fill");
        fillPayment(payment);
    }

    private void extractExactHotel(Integer kod) throws InterruptedException {
        kodHotel=kod;
        driver.get("https://www.travelline.ru/secure/SwitchUserContext.ashx?ReturnUrl=%2fsecure%2fwebpms%2fextranet%2f&App=Management&Context="+kod);
        WebElement dynamicElement = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='provider-id ng-binding']")));
        findItogo();
        findFilterButton();
        LocalDate nowDate = now();
        //System.out.println("-----");
        long resultDays = ChronoUnit.DAYS.between(beginDate, endDate);
        if (resultDays>-1) {
            if (resultDays == 1) {
                if ((endDate.getDayOfMonth() == 1) && (endDate.getMonth() == nowDate.getMonth())) {
                    chooseDateAndFill(0, 1, 1, new Payment(kodHotel,endDate));
                    chooseDateAndFill(1, beginDate.getDayOfMonth(),1, new Payment(kodHotel,beginDate));
                } else if (endDate.getMonth() == nowDate.getMonth()) {
                    chooseDateAndFill(0, beginDate.getDayOfMonth(), 1,new Payment(kodHotel,beginDate));
                    chooseDateAndFill(0, endDate.getDayOfMonth(),0, new Payment(kodHotel,endDate));
                } else {
                    System.out.println("нельзя по 2 дня в прошлых месяцах выбирать");
                }
            } else if (endDate.getMonth() != beginDate.getMonth()) {
                System.out.println("месяц начала и конца не соответствуют");
            } else {
                Integer betweenMonth = (nowDate.getYear() - beginDate.getYear()) * 12 - beginDate.getMonthValue() + nowDate.getMonthValue();
                chooseDateAndFill(betweenMonth, beginDate.getDayOfMonth(),1, new Payment(kodHotel,beginDate));
                for (int i = beginDate.getDayOfMonth() + 1; i <= endDate.getDayOfMonth(); i++) {
                    chooseDateAndFill(0, i, 0, new Payment(kodHotel, beginDate.plusDays(i-beginDate.getDayOfMonth())));
                }
            }
        } else {
            System.out.println("начальная дата больше конечной");
        }
    }

    public Integer extractData(LocalDate beginDate, LocalDate endDate) throws InterruptedException {
        logger = LoggerFactory.getLogger(Main.class);
        try {
            this.beginDate = beginDate;
            this.endDate = endDate;
            init();
            WebElement dynamicElement = (new WebDriverWait(driver, 10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='provider-id ng-binding']")));
            //System.out.println("Первоначальный номер гостинницы: "+dynamicElement.getText());
            List<Hotel> hotels = new ArrayList<>();
            service.getAll().map(e -> hotels.add(e)).subscribe();
            payments = new ArrayList<>();
            paymentTypeService.getAll().map(e -> payments.add(e)).subscribe();
            Thread.sleep(100);
            for (Hotel hotel : hotels) {
                logger.info("Заполняем сатистику для отеля "+hotel.getName());
                extractExactHotel(hotel.getKod());
            }
            //Thread.sleep(200000);
            System.out.println(" finish");
        } catch (Exception e){
            logger.error("!!!ERROR "+e.toString());
        }
        finally {
            driver.close();
            return exitStatus = 200;
        }
    }
}
