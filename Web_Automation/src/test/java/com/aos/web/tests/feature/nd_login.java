package com.aos.web.tests.feature;


import org.openqa.selenium.WebDriver;
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
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.HashMap;
import java.util.Map;
public class nd_login{

        WebDriver driver;
        WebDriverWait wait;

    @BeforeClass
    public void beforeClass() {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-features=PasswordLeakDetection,SafetyCheck,PasswordManagerOnboarding");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

        @BeforeMethod
        public void beforeMethod() throws InterruptedException {
            driver.navigate().to("https://www.saucedemo.com/");
            Thread.sleep(2000);
        }

        // Test Case 1: Successful Login
        @Test
        public void testCase1()  {

            driver.findElement(By.cssSelector("input[data-test='username']"))
                    .sendKeys("standard_user");

            driver.findElement(By.cssSelector("input[data-test='password']"))
                    .sendKeys("secret_sauce");

            driver.findElement(By.cssSelector("input[data-test='login-button']"))
                    .click();

            wait.until(ExpectedConditions.urlContains("inventory.html"));

            Assert.assertTrue(
                    Objects.requireNonNull(driver.getCurrentUrl()).contains("inventory.html"),
                    "User should be successfully redirected to the inventory page"
            );

        }

        // Test Case 2: Login Using Locked Out User
        @Test
        public void testCase2()  {

            driver.findElement(By.cssSelector("input[data-test='username']"))
                    .sendKeys("locked_out_user");

            driver.findElement(By.cssSelector("input[data-test='password']"))
                    .sendKeys("secret_sauce");

            driver.findElement(By.cssSelector("input[data-test='login-button']"))
                    .click();

            WebElement errorMessage = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".error-message-container")
                    )
            );

            Assert.assertTrue(
                    errorMessage.getText().contains("locked out"),
                    "A locked-out error message should appear"
            );

            Assert.assertFalse(
                    Objects.requireNonNull(driver.getCurrentUrl()).contains("inventory.html"),
                    "Locked-out user should not access the inventory page"
            );

        }

        // Test Case 3: Login With Empty Username
        @Test
        public void testCase3()  {

            driver.findElement(By.cssSelector("input[data-test='password']"))
                    .sendKeys("secret_sauce");

            driver.findElement(By.cssSelector("input[data-test='login-button']"))
                    .click();

            WebElement errorMessage = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".error-message-container")
                    )
            );

            Assert.assertTrue(
                    errorMessage.getText().contains("Username is required"),
                    "Username required error message should appear"
            );

            Assert.assertFalse(
                    Objects.requireNonNull(driver.getCurrentUrl()).contains("inventory.html"),
                    "User should remain on the login page"
            );

        }

        // Test Case 4: Login With Empty Password
        @Test
        public void testCase4()  {

            driver.findElement(By.cssSelector("input[data-test='username']"))
                    .sendKeys("standard_user");

            driver.findElement(By.cssSelector("input[data-test='login-button']"))
                    .click();


            WebElement errorMessage = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".error-message-container")
                    )
            );

            Assert.assertTrue(
                    errorMessage.getText().contains("Password is required"),
                    "Password required error message should appear"
            );

            Assert.assertFalse(
                    Objects.requireNonNull(driver.getCurrentUrl()).contains("inventory.html"),
                    "User should remain on the login page"
            );

        }

        // Test Case 5: Login With Incorrect Password
        @Test
        public void testCase5()  {

            driver.findElement(By.cssSelector("input[data-test='username']"))
                    .sendKeys("standard_user");

            driver.findElement(By.cssSelector("input[data-test='password']"))
                    .sendKeys("wrong_password");

            driver.findElement(By.cssSelector("input[data-test='login-button']"))
                    .click();

            WebElement errorMessage = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".error-message-container")
                    )
            );

            Assert.assertTrue(
                    errorMessage.getText().contains(
                            "Username and password do not match any user in this service"
                    ),
                    "Invalid credentials error message should appear"
            );

            Assert.assertFalse(
                    Objects.requireNonNull(driver.getCurrentUrl()).contains("inventory.html"),
                    "User should not access the inventory page with incorrect credentials"
            );

        }

        @AfterMethod
        public void afterMethod() {
        }

        @AfterClass
        public void afterClass() {
            driver.quit();
        }
}
