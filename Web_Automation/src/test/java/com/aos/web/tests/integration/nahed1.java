package com.aos.web.tests.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;

public class nahed1 {

    WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void beforeMethod(ITestResult result) throws InterruptedException {
        driver.manage().deleteAllCookies();
        driver.navigate().to("https://www.saucedemo.com/");
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        Thread.sleep(1000);

        String methodName = result.getMethod().getMethodName();

        if (!methodName.equals("testCase1") && !methodName.equals("testCase2")) {
            driver.findElement(By.id("user-name")).sendKeys("standard_user");
            driver.findElement(By.id("password")).sendKeys("secret_sauce");
            driver.findElement(By.id("login-button")).click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[data-test='inventory-list']")
            ));
        }
    }

    @Test
    public void testCase1() throws InterruptedException {
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test='inventory-list']")
        ));

        WebElement pageTitle = driver.findElement(By.cssSelector("[data-test='title']"));
        assert pageTitle.getText().equals("Products");
    }

    @Test
    public void testCase2() throws InterruptedException {
        driver.manage().deleteAllCookies();
        driver.navigate().to("https://www.saucedemo.com/inventory.html");

        Thread.sleep(2000);

        assert driver.getCurrentUrl().contains("saucedemo.com") &&
                !driver.getCurrentUrl().contains("inventory.html");
    }

    @Test
    public void testCase3() throws InterruptedException {
        WebElement productName = driver.findElement(
                By.cssSelector("[data-test='inventory-item-name']")
        );

        WebElement productPrice = driver.findElement(
                By.cssSelector("[data-test='inventory-item-price']")
        );

        String homeProductName = productName.getText();
        String homeProductPrice = productPrice.getText();

        productName.click();

        Thread.sleep(1500);

        WebElement detailsName = driver.findElement(
                By.cssSelector("[data-test='inventory-item-name']")
        );

        WebElement detailsPrice = driver.findElement(
                By.cssSelector("[data-test='inventory-item-price']")
        );

        assert detailsName.getText().equals(homeProductName);
        assert detailsPrice.getText().equals(homeProductPrice);
    }

    @Test
    public void testCase4() throws InterruptedException {
        driver.findElement(By.cssSelector("[data-test='inventory-item-name']")).click();

        Thread.sleep(1500);

        driver.findElement(By.cssSelector("[data-test='add-to-cart']")).click();

        Thread.sleep(1000);

        driver.findElement(By.cssSelector("[data-test='back-to-products']")).click();

        Thread.sleep(1500);

        WebElement cartBadge = driver.findElement(
                By.cssSelector("[data-test='shopping-cart-badge']")
        );

        assert cartBadge.getText().equals("1");
    }

    @Test
    public void testCase5() throws InterruptedException {
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();
        driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();

        Thread.sleep(1500);

        WebElement cartItems = driver.findElement(
                By.cssSelector("[data-test='cart-list']")
        );

        assert cartItems.findElements(
                By.cssSelector("[data-test='inventory-item']")
        ).size() == 2;
    }

    @AfterMethod
    public void afterMethod() {
    }

    @AfterClass
    public void afterClass() {
        driver.quit();
    }
}