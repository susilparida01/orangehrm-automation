package com.orangehrm.factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.edge.*;
import org.openqa.selenium.firefox.*;

import java.time.Duration;

public class DriverFactory {

    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public WebDriver initDriver(String browser, boolean headless) {
        if (browser == null) browser = "chrome";

        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions co = new ChromeOptions();
                if (headless) co.addArguments("--headless=new");
                co.addArguments("--start-maximized");
                tlDriver.set(new ChromeDriver(co));
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions eo = new EdgeOptions();
                if (headless) eo.addArguments("--headless=new");
                eo.addArguments("--start-maximized");
                tlDriver.set(new EdgeDriver(eo));
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions fo = new FirefoxOptions();
                if (headless) fo.addArguments("-headless");
                tlDriver.set(new FirefoxDriver(fo));
                getDriver().manage().window().maximize();
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        return getDriver();
    }

    public static WebDriver getDriver() {
        return tlDriver.get();
    }

    public void quitDriver() {
        if (getDriver() != null) {
            getDriver().quit();
            tlDriver.remove();
        }
    }
}
