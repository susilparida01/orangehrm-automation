package com.orangehrm.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.orangehrm.factory.DriverFactory;
import com.orangehrm.utils.ReportManager;
import com.orangehrm.utils.ReportPortal;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TestNG listener:
 * - Initializes & flushes ExtentReports per suite
 * - Creates an ExtentTest per method
 * - On failure: attaches screenshot + HTML page source
 * - On skip: logs reason + optional screenshot
 * Thread-safe with ReportPortal (ThreadLocal<ExtentTest>)
 */
public class TestListener implements ITestListener, ISuiteListener {

    private ExtentReports extent;

    /* ===================== Suite level ===================== */

    @Override
    public void onStart(ISuite suite) {
        extent = ReportManager.getReporter();
    }

    @Override
    public void onFinish(ISuite suite) {
        if (extent != null) {
            extent.flush();
            System.out.println("Extent report generated at: " + ReportManager.getReportPath());
        }
    }

    /* ===================== Test method level ===================== */

    @Override
    public void onTestStart(ITestResult result) {
        String method = result.getMethod().getMethodName();
        String className = result.getTestClass().getRealClass().getSimpleName();
        ExtentTest test = extent.createTest(className + " :: " + method);
        ReportPortal.setTest(test);
        ReportPortal.getTest().info("Starting test: " + method);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest t = safeTestRef(result);
        t.pass("PASSED");
        ReportPortal.remove();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest t = safeTestRef(result);
        if (result.getThrowable() != null) {
            t.fail(result.getThrowable());
        } else {
            t.fail("Test failed without throwable.");
        }

        WebDriver driver = DriverFactory.getDriver();
        if (driver != null) {
            try {
                String shot = takeScreenshot(driver, result.getMethod().getMethodName());
                t.addScreenCaptureFromPath("../screenshots/" + new File(shot).getName());
            } catch (Exception e) {
                t.warning("Failed to capture screenshot: " + e.getMessage());
            }
            try {
                String htmlPath = savePageSource(driver, result.getMethod().getMethodName());
                t.info("Saved page source: " + htmlPath);
            } catch (Exception e) {
                t.warning("Failed to save page source: " + e.getMessage());
            }
        } else {
            t.warning("WebDriver was null; artifacts not captured.");
        }

        ReportPortal.remove();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // Ensure we always have an ExtentTest to log into
        ExtentTest t = ReportPortal.getTest();
        if (t == null) {
            String method = result.getMethod().getMethodName();
            String className = result.getTestClass().getRealClass().getSimpleName();
            t = extent.createTest(className + " :: " + method);
            ReportPortal.setTest(t);
        }

        String reason = (result.getThrowable() != null) ? result.getThrowable().getMessage() : "No skip reason provided";
        t.skip("SKIPPED: " + reason);

        // Optional: screenshot on skip
        WebDriver driver = DriverFactory.getDriver();
        if (driver != null) {
            try {
                String shot = takeScreenshot(driver, result.getMethod().getMethodName() + "_SKIPPED");
                t.addScreenCaptureFromPath("../screenshots/" + new File(shot).getName());
            } catch (Exception e) {
                t.warning("Failed to capture skip screenshot: " + e.getMessage());
            }
        }

        ReportPortal.remove();
    }

    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) { /* no-op */ }
    @Override public void onStart(ITestContext context) { /* no-op (suite-level init used) */ }
    @Override public void onFinish(ITestContext context) { /* no-op (suite-level flush used) */ }

    /* ===================== Helpers ===================== */

    private ExtentTest safeTestRef(ITestResult result) {
        ExtentTest t = ReportPortal.getTest();
        if (t == null) {
            String method = result.getMethod().getMethodName();
            String className = result.getTestClass().getRealClass().getSimpleName();
            t = extent.createTest(className + " :: " + method);
            ReportPortal.setTest(t);
        }
        return t;
    }

    private String takeScreenshot(WebDriver driver, String methodName) throws IOException {
        ensureDirs();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = methodName + "_" + timestamp + ".png";
        File dest = new File("target/screenshots/" + fileName);

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(src, dest);
        return dest.getAbsolutePath();
    }

    private String savePageSource(WebDriver driver, String methodName) throws IOException {
        ensureDirs();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = methodName + "_" + timestamp + ".html";
        File dest = new File("target/screenshots/" + fileName);

        String html = driver.getPageSource();
        FileUtils.writeStringToFile(dest, html, StandardCharsets.UTF_8, false);
        return dest.getAbsolutePath();
    }

    private void ensureDirs() {
        File dir = new File("target/screenshots");
        if (!dir.exists()) dir.mkdirs();
    }
}
