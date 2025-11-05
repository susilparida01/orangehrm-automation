package com.orangehrm.tests;

import com.orangehrm.factory.DriverFactory;
import com.orangehrm.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import java.util.Properties;

public class BaseTest {

    protected DriverFactory driverFactory;
    protected WebDriver driver;
    protected Properties prop;

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "headless"})
    public void setUp(@Optional String browser, @Optional String headless) {
        ConfigReader config = new ConfigReader();
        prop = config.initProp();

        String br = (browser != null && !browser.isEmpty()) ? browser : prop.getProperty("browser", "chrome");
        boolean isHeadless = (headless != null)
                ? Boolean.parseBoolean(headless)
                : Boolean.parseBoolean(prop.getProperty("headless", "false"));

        driverFactory = new DriverFactory();
        driver = driverFactory.initDriver(br, isHeadless);
        driver.get(prop.getProperty("baseUrl"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driverFactory != null) driverFactory.quitDriver();
    }
}
