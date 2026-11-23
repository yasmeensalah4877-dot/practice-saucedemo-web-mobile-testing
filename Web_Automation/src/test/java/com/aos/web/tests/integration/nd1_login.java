package com.aos.web.tests.integration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.Objects;

public class nd1_login {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void beforeClass() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }
    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        driver.manage().deleteAllCookies();
        driver.navigate().to("https://www.saucedemo.com/");
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        driver.navigate().refresh();
        Thread.sleep(2000);
    }

    // Test Case 1: Verify Logout Integration
    @Test
    public void testCase1() {

        driver.findElement(By.cssSelector("input[data-test='username']"))
                .sendKeys("standard_user");

        driver.findElement(By.cssSelector("input[data-test='password']"))
                .sendKeys("secret_sauce");

        driver.findElement(By.cssSelector("input[data-test='login-button']"))
                .click();

        wait.until(ExpectedConditions.urlContains("inventory.html"));

        driver.findElement(By.id("react-burger-menu-btn")).click();

        WebElement logout = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("logout_sidebar_link")
                )
        );

        logout.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[data-test='username']")
        ));

        Assert.assertTrue(
                Objects.requireNonNull(driver.getCurrentUrl()).contains("saucedemo.com"),
                "User should be redirected to login page after logout"
        );

    }

    // Test Case 2: Verify Access to Internal Pages Without Login
    @Test
    public void testCase2() {

        driver.navigate().to(
                "https://www.saucedemo.com/inventory.html"
        );

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[data-test='username']")
        ));

        Assert.assertTrue(
                driver.getCurrentUrl().contains("saucedemo.com"),
                "Unauthenticated user should be redirected to login page"
        );

        Assert.assertFalse(
                driver.getCurrentUrl().contains("inventory.html"),
                "Unauthenticated user should not access inventory page"
        );

    }

    // Test Case 3: Verify Browser Back Button After Login
    @Test
    public void testCase3()  {

        driver.findElement(By.cssSelector("input[data-test='username']"))
                .sendKeys("standard_user");

        driver.findElement(By.cssSelector("input[data-test='password']"))
                .sendKeys("secret_sauce");

        driver.findElement(By.cssSelector("input[data-test='login-button']"))
                .click();

        wait.until(ExpectedConditions.urlContains("inventory.html"));

        driver.navigate().back();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[data-test='username']")
        ));

        Assert.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("saucedemo.com"),
                "User should return to the login page");

    }

    // Test Case 4: Verify Login Form After Logout
    @Test
    public void testCase4()  {

        driver.findElement(By.cssSelector("input[data-test='username']"))
                .sendKeys("standard_user");

        driver.findElement(By.cssSelector("input[data-test='password']"))
                .sendKeys("secret_sauce");


        driver.findElement(By.cssSelector("input[data-test='login-button']"))
                .click();

        wait.until(ExpectedConditions.urlContains("inventory.html"));

        driver.findElement(By.id("react-burger-menu-btn")).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("logout_sidebar_link")
        )).click();

        WebElement username = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[data-test='username']")
                )
        );

        WebElement password = driver.findElement(
                By.cssSelector("input[data-test='password']")
        );

        Assert.assertTrue(username.isDisplayed(),
                "Username field should appear after logout");

        Assert.assertTrue(password.isDisplayed(),
                "Password field should appear after logout");

    }

    // Test Case 5: Verify Integration Between Login Fields and Login Button
    @Test
    public void testCase5() {

        WebElement username = driver.findElement(
                By.cssSelector("input[data-test='username']")
        );

        WebElement password = driver.findElement(
                By.cssSelector("input[data-test='password']")
        );

        WebElement loginButton = driver.findElement(
                By.cssSelector("input[data-test='login-button']")
        );

        username.sendKeys("standard_user");
        password.sendKeys("secret_sauce");
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("inventory.html"));

        Assert.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("inventory.html"), "Valid credentials should be submitted and user should access inventory page");

    }

    @AfterMethod
    public void afterMethod() {
    }

    @AfterClass
    public void afterClass() {
        driver.quit();
    }
}
