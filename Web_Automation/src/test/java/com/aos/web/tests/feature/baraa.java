package com.aos.web.tests.feature;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class baraa {

    WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        driver.navigate().to("https://www.saucedemo.com");
        driver.manage().deleteAllCookies();
        driver.navigate().refresh();
        Thread.sleep(2000);

        driver.findElement(By.cssSelector("#user-name"))
                .sendKeys("standard_user");

        driver.findElement(By.cssSelector("#password"))
                .sendKeys("secret_sauce");

        driver.findElement(By.cssSelector("#login-button"))
                .click();

        Thread.sleep(1000);
    }

    @Test
    public void testCase1() throws InterruptedException {

        // Verify that the cart page is opened successfully.

        driver.findElement(By.cssSelector(
                "[data-test='shopping-cart-link']")).click();

        Thread.sleep(1000);

        Assert.assertEquals(driver.getCurrentUrl(),
                "https://www.saucedemo.com/cart.html",
                "Incorrect cart page URL");

        Assert.assertEquals(driver.findElement(By.cssSelector(
                        "[data-test='title']")).getText(),
                "Your Cart",
                "Incorrect cart page title");

        Assert.assertTrue(driver.findElement(By.cssSelector(
                        "[data-test='cart-contents-container']")).isDisplayed(),
                "Cart contents container is not displayed");

        Assert.assertEquals(driver.findElement(By.cssSelector(
                        "[data-test='cart-quantity-label']")).getText(),
                "QTY");

        Assert.assertEquals(driver.findElement(By.cssSelector(
                        "[data-test='cart-desc-label']")).getText(),
                "Description");
    }

    @Test
    public void testCase2() throws InterruptedException {

        // Verify product details and cart badge after adding a product.

        WebElement product = driver.findElement(By.cssSelector(
                ".inventory_item:has(#add-to-cart-sauce-labs-backpack)"));

        String expectedName = product.findElement(By.cssSelector(
                ".inventory_item_name")).getText();

        String expectedDescription = product.findElement(By.cssSelector(
                ".inventory_item_desc")).getText();

        String expectedPrice = product.findElement(By.cssSelector(
                ".inventory_item_price")).getText();

        driver.findElement(By.cssSelector(
                "#add-to-cart-sauce-labs-backpack")).click();

        Assert.assertEquals(driver.findElement(By.cssSelector(
                        ".shopping_cart_badge")).getText(),
                "1",
                "Incorrect cart badge count");

        driver.findElement(By.cssSelector(
                "[data-test='shopping-cart-link']")).click();

        Thread.sleep(1000);

        Assert.assertEquals(driver.findElements(By.cssSelector(
                        ".cart_item")).size(),
                1,
                "Incorrect number of cart items");

        Assert.assertEquals(driver.findElement(By.cssSelector(
                        ".inventory_item_name")).getText(),
                expectedName,
                "Incorrect product name");

        Assert.assertEquals(driver.findElement(By.cssSelector(
                        ".inventory_item_desc")).getText(),
                expectedDescription,
                "Incorrect product description");

        Assert.assertEquals(driver.findElement(By.cssSelector(
                        ".inventory_item_price")).getText(),
                expectedPrice,
                "Incorrect product price");

        Assert.assertEquals(driver.findElement(By.cssSelector(
                        ".cart_quantity")).getText(),
                "1",
                "Incorrect product quantity");
    }

    @Test
    public void testCase3() throws InterruptedException {

        // Verify removing one product while keeping another.

        driver.findElement(By.cssSelector(
                "#add-to-cart-sauce-labs-backpack")).click();

        driver.findElement(By.cssSelector(
                "#add-to-cart-sauce-labs-bike-light")).click();

        Assert.assertEquals(driver.findElement(By.cssSelector(
                ".shopping_cart_badge")).getText(), "2");

        driver.findElement(By.cssSelector(
                "[data-test='shopping-cart-link']")).click();

        driver.findElement(By.cssSelector(
                "#remove-sauce-labs-backpack")).click();

        Thread.sleep(1000);

        Assert.assertEquals(driver.findElements(By.cssSelector(
                        ".cart_item")).size(),
                1,
                "Incorrect number of remaining products");

        Assert.assertEquals(driver.findElement(By.cssSelector(
                        ".inventory_item_name")).getText(),
                "Sauce Labs Bike Light",
                "Incorrect remaining product");

        Assert.assertEquals(driver.findElement(By.cssSelector(
                        ".shopping_cart_badge")).getText(),
                "1",
                "Cart badge was not updated");

        driver.findElement(By.cssSelector(
                "#remove-sauce-labs-bike-light")).click();

        Assert.assertTrue(driver.findElements(By.cssSelector(
                        ".cart_item")).isEmpty(),
                "Cart should be empty");

        Assert.assertTrue(driver.findElements(By.cssSelector(
                        ".shopping_cart_badge")).isEmpty(),
                "Cart badge should disappear");
    }

    @Test
    public void testCase4() throws InterruptedException {

        // Verify Continue Shopping and cart persistence.

        driver.findElement(By.cssSelector(
                "#add-to-cart-sauce-labs-backpack")).click();

        driver.findElement(By.cssSelector(
                "[data-test='shopping-cart-link']")).click();

        driver.findElement(By.cssSelector(
                "#continue-shopping")).click();

        Thread.sleep(1000);

        Assert.assertEquals(driver.getCurrentUrl(),
                "https://www.saucedemo.com/inventory.html",
                "Continue Shopping navigation failed");

        Assert.assertEquals(driver.findElement(By.cssSelector(
                        ".shopping_cart_badge")).getText(),
                "1",
                "Cart badge was not retained");

        driver.findElement(By.cssSelector(
                "[data-test='shopping-cart-link']")).click();

        driver.navigate().refresh();

        Thread.sleep(1000);

        Assert.assertEquals(driver.findElements(By.cssSelector(
                        ".cart_item")).size(),
                1,
                "Cart contents were not retained");

        Assert.assertEquals(driver.findElement(By.cssSelector(
                        ".inventory_item_name")).getText(),
                "Sauce Labs Backpack",
                "Selected product was not retained");
    }

    @Test
    public void testCase5() throws InterruptedException {

        // Verify that checkout is prevented when the cart is empty.

        driver.findElement(By.cssSelector(
                "[data-test='shopping-cart-link']")).click();

        Assert.assertTrue(driver.findElements(By.cssSelector(
                        ".cart_item")).isEmpty(),
                "Cart must be empty before testing");

        WebElement checkoutButton = driver.findElement(
                By.cssSelector("#checkout"));

        if (checkoutButton.isEnabled()) {
            checkoutButton.click();
        }

        Thread.sleep(1000);

        Assert.assertEquals(driver.getCurrentUrl(),
                "https://www.saucedemo.com/cart.html",
                "BUG: Checkout is allowed with an empty cart");
    }

    @AfterMethod
    public void afterMethod() {
        driver.manage().deleteAllCookies();
    }

    @AfterClass
    public void afterClass() {
        driver.quit();
    }
}