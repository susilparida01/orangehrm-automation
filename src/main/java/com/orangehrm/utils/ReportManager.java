package com.orangehrm.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportManager {

    private static ExtentReports extent;
    private static String reportPath;

    public static synchronized ExtentReports getReporter() {
        if (extent == null) {
            // Create output folders
            File extentDir = new File("target/extent");
            if (!extentDir.exists()) extentDir.mkdirs();
            File shotDir = new File("target/screenshots");
            if (!shotDir.exists()) shotDir.mkdirs();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            reportPath = "target/extent/ExtentReport_" + timestamp + ".html";

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setReportName("OrangeHRM Automation Report");
            spark.config().setDocumentTitle("Test Execution Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("Project", "OrangeHRM Demo");
            extent.setSystemInfo("Framework", "Selenium + TestNG");
            extent.setSystemInfo("Java", System.getProperty("java.version"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
        }
        return extent;
    }

    public static String getReportPath() {
        return reportPath;
    }
}
