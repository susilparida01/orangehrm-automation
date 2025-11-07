package com.orangehrm.tests;

import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class Login_AdminUser extends BaseTest {

    @Test(description = "Valid Admin login should navigate to Dashboard")
    public void testValidLogin() {
        LoginPage login = new LoginPage(driver);
        DashboardPage dashboard = login.loginValidUser(driver,
                prop.getProperty("username"), prop.getProperty("password"));

        Assert.assertTrue(dashboard.getDashboardHeader().contains("Dashboard"),
                "Login failed! Dashboard header not found.");
    }

    @Test(description = "Invalid login should display error message")
    public void testInvalidLogin() {
        LoginPage login = new LoginPage(driver);
        String errorMessage = login.loginInvalidUser("Admin", "wrongPassword!");

        Assert.assertEquals(errorMessage, "Invalid wwwwcredentials",
                "Expected 'Invalid credentials' message, but got: " + errorMessage);
    }
}
