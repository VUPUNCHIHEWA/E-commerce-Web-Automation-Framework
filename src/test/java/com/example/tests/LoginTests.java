package com.example.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.example.pages.LoginPage;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;

public class LoginTests {
    WebDriver driver;
    LoginPage loginPage;
    static ExtentReports extent;
    ExtentTest test;

    @BeforeSuite
    public void setupReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter("target/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");
        loginPage = new LoginPage(driver);
    }

    @Test(priority = 1)
    public void testValidLogin() {
        test = extent.createTest("Valid Login Test");
        loginPage.login("standard_user", "secret_sauce");
        
        String title = driver.findElement(By.className("title")).getText();
        Assert.assertEquals(title, "Products");
        test.pass("Login successful!");
    }

    @Test(priority = 2)
    public void testInvalidLogin() {
        test = extent.createTest("Invalid Login Test");
        loginPage.login("standard_user", "wrong_password");
        
        String error = loginPage.getErrorMessage();
        Assert.assertTrue(error.contains("Username and password do not match"));
        test.pass("Error message verified.");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String screenshotPath = captureScreenshot(result.getName());
            test.fail("Test Failed: " + result.getThrowable(), 
                MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        }
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite
    public void flushReport() {
        extent.flush();
    }

    public String captureScreenshot(String name) {
        String path = System.getProperty("user.dir") + "/target/" + name + ".png";
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(src, new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name + ".png"; 
    }
}