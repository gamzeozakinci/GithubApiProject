package com.apitests.github.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Writes the test results to target/extent-report-{suite name}.html.
 */
public class ExtentTestListener implements ITestListener {

    private ExtentReports extent;
    private ExtentTest test;

    @Override
    public void onStart(ITestContext context) {
        String suiteName = context.getSuite().getName();
        ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-report-" + suiteName + ".html");
        spark.config().setReportName("GitHub REST API Tests - " + suiteName);
        spark.config().setDocumentTitle("GitHub REST API Tests");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("API", "https://api.github.com");
        extent.setSystemInfo("Suite", suiteName);
        extent.setSystemInfo("Java", System.getProperty("java.version"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
    }

    @Override
    public void onTestStart(ITestResult result) {
        String name = result.getMethod().getDescription();
        if (name == null) {
            name = result.getMethod().getMethodName();
        }
        test = extent.createTest(name);
        test.assignCategory(result.getMethod().getGroups());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.skip(result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
