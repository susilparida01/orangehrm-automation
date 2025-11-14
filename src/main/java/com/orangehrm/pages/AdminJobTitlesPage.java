package com.orangehrm.pages;

import com.orangehrm.utils.AutoItUtil;
import com.orangehrm.utils.Log;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.io.File;
import java.nio.file.Paths;

public class AdminJobTitlesPage extends BasePage {

    // Left menu / navigation
    @FindBy(xpath = "//span[normalize-space()='Admin']")
    public WebElement adminMenu;

    // Top bar menu might require clicking Job, then Job Titles
    @FindBy(xpath = "//span[normalize-space()='Job']")
    public WebElement jobDropdown;

    @FindBy(xpath = "//a[normalize-space()='Job Titles']")
    public WebElement jobTitlesLink;

    // Job Titles page
    @FindBy(xpath = "//button[normalize-space()='Add']")
    public WebElement addButton;

    @FindBy(xpath = "//label[normalize-space()='Job Title']/../following-sibling::div//input")
    public WebElement jobTitleInput;
    
    @FindBy(xpath = "//textarea[@placeholder='Type description here']")
    public WebElement jobDescriptionTextArea;

    // Upload control (two styles: a visible "Browse" label or hidden input[type=file])
    @FindBy(xpath = "//label[normalize-space()='Job Specification']/..//input[@type='file']")
    public WebElement jobSpecInputHidden;

    @FindBy(xpath = "//label[normalize-space()='Job Specification']/../following-sibling::div//i | //label[normalize-space()='Job Specification']/..//span[contains(.,'Browse')]")
    public WebElement jobSpecBrowseButton;

    @FindBy(xpath = "//button[.=' Save ' or normalize-space()='Save']")
    public WebElement saveButton;

    @FindBy(css = "div.oxd-toast.oxd-toast--success")
    public WebElement successToast;

    public AdminJobTitlesPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /** Go directly to Admin module (works on OrangeHRM demo after login) */
    public void openAdminModule() {
    	
        safeClick(adminMenu);
        waitForUrlContains("/admin");
    }

    /** Navigate to Admin → Job → Job Titles */
    public void navigateToJobTitles() {
    	
        safeClick(jobDropdown);
        safeClick(jobTitlesLink);
        waitForUrlContains("/admin/viewJobTitleList");
    }

    /** Add a new Job Title and upload Job Specification file via AutoIt (Windows) */
    public void addJobTitleWithSpec_AutoIt(String jobTitle, String relativeFileToUpload) {
    	
        safeClick(addButton);
        typeText(jobTitleInput, jobTitle);
        typeText(jobDescriptionTextArea, jobTitle );

        String absoluteFile = Paths.get(relativeFileToUpload).toAbsolutePath().toString();

        try {
            // If input[type=file] is visible, prefer standard Selenium sendKeys.
            if (jobSpecInputHidden.isDisplayed()) {
                Log.step("File input is visible; using sendKeys");
                jobSpecInputHidden.sendKeys(absoluteFile);
            } else {
                // Otherwise, click Browse to open native dialog and use AutoIt
                Log.step("File input is hidden; using AutoIt");
                safeClick(jobSpecBrowseButton);
                AutoItUtil.uploadWithAutoIt(absoluteFile);
            }
        } catch (Exception e) {
            // Robust fallback: click Browse and AutoIt anyway
            Log.warn("Direct sendKeys failed or input not visible. Falling back to AutoIt. Reason: " + e.getMessage());
            safeClick(jobSpecBrowseButton);
            AutoItUtil.uploadWithAutoIt(absoluteFile);
        }

        safeClick(saveButton);
        waitForVisibility(successToast);
        Log.pass("Job Title saved with uploaded spec: " + new File(absoluteFile).getName());
    }
}



