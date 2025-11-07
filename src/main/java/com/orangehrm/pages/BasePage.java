package com.orangehrm.pages;

import com.orangehrm.utils.Constants;
import com.orangehrm.utils.Log;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

/**
 * BasePage class holds all reusable WebDriver actions and utilities.
 * Every Page Object extends this class to use these helper methods.
 */
public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Constants.DEFAULT_TIMEOUT));
        this.actions = new Actions(driver);
    }

    // ======================
    // Generic reusable methods
    // ======================

    public void click(WebElement element) {
        waitForVisibility(element);
        waitForElementToBeClickable(element);
        Log.step("Click: " + debugName(element));
        element.click();
    }

    public void typeText(WebElement element, String text) {
        waitForVisibility(element);
        Log.step("Type: '" + text + "' into " + debugName(element));
        element.clear();
        element.sendKeys(text);
    }

    public String getText(WebElement element) {
        waitForVisibility(element);
        String t = element.getText().trim();
        Log.step("Get text from " + debugName(element) + " -> '" + t + "'");
        return t;
    }

    public boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void waitForVisibility(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitForUrlContains(String keyword) {
        wait.until(ExpectedConditions.urlContains(keyword));
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void hoverOverElement(WebElement element) {
        actions.moveToElement(element).perform();
    }

    public void safeClick(WebElement element) {
        try {
            click(element);
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    public void safeType(WebElement element, String value) {
        try {
            typeText(element, value);
        } catch (StaleElementReferenceException e) {
            waitForVisibility(element);
            element.clear();
            element.sendKeys(value);
        }
    }
    
    private String debugName(WebElement el) {
        try {
            String tag = el.getTagName();
            String id = el.getAttribute("id");
            String name = el.getAttribute("name");
            if (id != null && !id.isBlank()) return "<" + tag + "#"+ id + ">";
            if (name != null && !name.isBlank()) return "<" + tag + "[name="+ name +"]>";
            return "<" + tag + ">";
       } catch (Exception e) {
            return "<element>";
       }
   }
}
