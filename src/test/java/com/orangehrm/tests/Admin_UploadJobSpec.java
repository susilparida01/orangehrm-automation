package com.orangehrm.tests;

import com.orangehrm.pages.AdminJobTitlesPage;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class Admin_UploadJobSpec extends BaseTest {

    @Test(description = "Admin -> Job -> Job Titles: Add job title with Job Specification upload (AutoIt)")
    public void uploadJobSpecification_UsingAutoIt() {
        // 1) Login
        LoginPage login = new LoginPage(driver);
        DashboardPage dashboard = login.loginValidUser(driver,
                prop.getProperty("username"), prop.getProperty("password"));
        Assert.assertTrue(dashboard.getDashboardHeader().contains("Dashboard"));

        // 2) Navigate to Admin -> Job -> Job Titles
        AdminJobTitlesPage adminJob = new AdminJobTitlesPage(driver);
        adminJob.openAdminModule();
        adminJob.navigateToJobTitles();

        // 3) Add job title + upload file (AutoIt if dialog opens)
        String jobTitle = "QA_Auto_" + UUID.randomUUID().toString().substring(0, 6);
        String fileRelPath = prop.getProperty("uploadFile", "src/test/resources/data/sample.pdf");

        adminJob.addJobTitleWithSpec_AutoIt(jobTitle, fileRelPath);
        // If we reach here without exception, the success toast was visible
    }
}
