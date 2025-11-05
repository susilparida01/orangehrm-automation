package com.orangehrm.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class DashboardPage extends BasePage {

    @FindBy(css = "h6.oxd-text.oxd-text--h6.oxd-topbar-header-breadcrumb-module")
    public WebElement dashboardHeader;

    public DashboardPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public String getDashboardHeader() {
        waitForVisibility(dashboardHeader);
        return getText(dashboardHeader);
    }
}
