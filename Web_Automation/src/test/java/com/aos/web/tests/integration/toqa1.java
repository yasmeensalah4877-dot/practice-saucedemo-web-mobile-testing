
package com.aos.web.tests.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class toqa1 {

    WebDriver driver;
    WebDriverWait wait;

    String URL = "https://www.saucedemo.com";

    By inventoryPage =
            By.cssSelector("[data-test='inventory-container']");

    By cartBadge =
            By.cssSelector("[data-test='shopping-cart-badge']");

    By cartLink =
            By.cssSelector("[data-test='shopping-cart-link']");

    By backpackTitle =
            By.cssSelector("a[data-test='item-4-title-link']");

    By backpackAddButton =
            By.cssSelector(
                    "button[data-test='add-to-cart-sauce-labs-backpack']"
            );

    By detailsAddButton =
            By.cssSelector("button[data-test='add-to-cart']");

    By detailsRemoveButton =
            By.cssSelector("button[data-test='remove']");

    By backToProducts =
            By.cssSelector("button[data-test='back-to-products']");


    @BeforeClass
    public void beforeClass() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(
                driver, Duration.ofSeconds(10)
        );
    }


    @BeforeMethod
    public void beforeMethod() {

        driver.navigate().to(URL);

        // Login
        if (!driver.findElements(
                By.id("user-name")).isEmpty()) {

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("user-name")
            )).sendKeys("standard_user");

            driver.findElement(By.id("password"))
                    .sendKeys("secret_sauce");

            driver.findElement(By.id("login-button"))
                    .click();
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                inventoryPage
        ));

        // Reset App State
        driver.findElement(By.id("react-burger-menu-btn"))
                .click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("reset_sidebar_link")
        )).click();

        // Close menu if still open
        if (!driver.findElements(
                By.id("react-burger-cross-btn")).isEmpty()
                && driver.findElement(
                By.id("react-burger-cross-btn")).isDisplayed()) {

            driver.findElement(
                    By.id("react-burger-cross-btn")).click();
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                inventoryPage
        ));
    }


    // Test Case 1
    // Add product from Details Page
    // and verify it on Products Page

    @Test
    public void testCase1() {

        driver.findElement(backpackTitle).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                detailsAddButton
        )).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                cartBadge
        ));

        driver.findElement(backToProducts).click();

        WebElement removeButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "button[data-test='remove-sauce-labs-backpack']"
                        )
                )
        );

        Assert.assertEquals(
                removeButton.getText(),
                "Remove",
                "Product should be removed from Add to cart state"
        );

        Assert.assertEquals(
                driver.findElement(cartBadge).getText(),
                "1",
                "Cart badge should display 1"
        );
    }


    // Test Case 2
    // Add product from Products Page
    // and verify it on Product Details Page

    @Test
    public void testCase2() {

        wait.until(ExpectedConditions.elementToBeClickable(
                backpackAddButton
        )).click();

        Assert.assertEquals(
                driver.findElement(cartBadge).getText(),
                "1"
        );

        driver.findElement(backpackTitle).click();

        WebElement removeButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        detailsRemoveButton
                )
        );

        Assert.assertEquals(
                removeButton.getText(),
                "Remove",
                "Product Details Page should reflect cart state"
        );
    }


    // Test Case 3
    // Remove product from Details Page
    // and verify removal on Products Page

    @Test
    public void testCase3() {

        driver.findElement(backpackTitle).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                detailsAddButton
        )).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                cartBadge
        ));

        wait.until(ExpectedConditions.elementToBeClickable(
                detailsRemoveButton
        )).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                cartBadge
        ));

        driver.findElement(backToProducts).click();

        WebElement addButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        backpackAddButton
                )
        );

        Assert.assertEquals(
                addButton.getText(),
                "Add to cart",
                "Product should return to Add to cart state"
        );

        Assert.assertTrue(
                driver.findElements(cartBadge).isEmpty(),
                "Cart badge should disappear after removing the product"
        );
    }


    // Test Case 4
    // Add product from Details Page
    // and verify its data on Cart Page

    @Test
    public void testCase4() {

        driver.findElement(backpackTitle).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                detailsAddButton
        )).click();

        driver.findElement(cartLink).click();

        WebElement productName = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "[data-test='inventory-item-name']"
                        )
                )
        );

        WebElement productPrice = driver.findElement(
                By.cssSelector(
                        "[data-test='inventory-item-price']"
                )
        );

        WebElement quantity = driver.findElement(
                By.cssSelector(
                        "[data-test='item-quantity']"
                )
        );

        Assert.assertEquals(
                productName.getText(),
                "Sauce Labs Backpack",
                "Cart should display the correct product name"
        );

        Assert.assertEquals(
                productPrice.getText(),
                "$29.99",
                "Cart should display the correct product price"
        );

        Assert.assertEquals(
                quantity.getText(),
                "1",
                "Cart should display the correct quantity"
        );
    }


    // Test Case 5
    // Remove product from Cart Page
    // and verify removal on Details Page

    @Test
    public void testCase5() {

        driver.findElement(backpackAddButton).click();

        driver.findElement(cartLink).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(
                        "button[data-test='remove-sauce-labs-backpack']"
                )
        )).click();

        // Verify cart is empty
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                cartBadge
        ));

        // Return to Products Page
        driver.findElement(
                By.cssSelector("button[data-test='continue-shopping']")
        ).click();

        // Open the same product details
        driver.findElement(backpackTitle).click();

        WebElement addButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        detailsAddButton
                )
        );

        Assert.assertEquals(
                addButton.getText(),
                "Add to cart",
                "Product should return to Add to cart state"
        );

        Assert.assertTrue(
                driver.findElements(cartBadge).isEmpty(),
                "Cart badge should disappear after removing the product"
        );
    }


    @AfterMethod
    public void afterMethod() {

    }


    @AfterClass
    public void afterClass() {

        if (driver != null) {
            driver.quit();
        }
    }
}
