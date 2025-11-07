package com.orangehrm.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private int attempt = 0;
    private static final int maxRetry = Integer.parseInt(System.getProperty("retry.max", "1"));

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < maxRetry) {
            attempt++;
            System.out.println("Retrying " + result.getName() + " (attempt " + attempt + ")");
            return true;
        }
        return false;
    }
}
