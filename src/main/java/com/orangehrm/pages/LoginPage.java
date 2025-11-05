package com.orangehrm.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends BasePage {

    /* Login Page Locators */
    @FindBy(name = "username")
    public WebElement login_username;

    @FindBy(name = "password")
    public WebElement login_password;

    @FindBy(tagName = "button")
    public WebElement login_button;

    @FindBy(xpath = "//div[@role='alert']/div[1]/p")
    public WebElement login_invalid_credentials_label;

    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // Page actions using BasePage methods
    public void enterUsername(String username) {
        safeType(login_username, username);
    }

    public void enterPassword(String password) {
        safeType(login_password, password);
    }

    public void clickLogin() {
        safeClick(login_button);
    }

    public DashboardPage loginValidUser(WebDriver driver, String user, String pass) {
        enterUsername(user);
        enterPassword(pass);
        clickLogin();
        waitForUrlContains("/dashboard");
        return new DashboardPage(driver);
    }

    public String loginInvalidUser(String user, String pass) {
        enterUsername(user);
        enterPassword(pass);
        clickLogin();
        waitForVisibility(login_invalid_credentials_label);
        return getText(login_invalid_credentials_label);
    }
}
