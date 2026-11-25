package com.aos.web.tests.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class   toqa {

    private static final String BASE_URL = "https://www.saucedemo.com/";

    WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        // reset session + cart before every test
        driver.navigate().to(BASE_URL);
        driver.manage().deleteAllCookies();
        ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.clear(); window.sessionStorage.clear();");
        driver.navigate().to(BASE_URL);
        Thread.sleep(1000);

        // login
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
        Thread.sleep(1500);
    }

    // TC1: Default sort is "Name (A to Z)" and Z to A reverses it (positive)
    @Test
    public void testCase1() throws InterruptedException {
        Thread.sleep(1000);
        Assert.assertEquals(
                driver.findElement(By.cssSelector("[data-test='active-option']")).getText(),
                "Name (A to Z)");

        List<WebElement> nameEls = driver.findElements(By.cssSelector("[data-test='inventory-item-name']"));
        List<String> actual = new ArrayList<>();
        for (WebElement e : nameEls) {
            actual.add(e.getText());
        }
        List<String> expected = new ArrayList<>(actual);
        Collections.sort(expected);
        Assert.assertEquals(actual, expected, "Default order is not A to Z");

        new Select(driver.findElement(By.cssSelector("[data-test='product-sort-container']")))
                .selectByValue("za");
        Thread.sleep(1000);
        actual.clear();
        for (WebElement e : driver.findElements(By.cssSelector("[data-test='inventory-item-name']"))) {
            actual.add(e.getText());
        }
        Collections.reverse(expected);
        Assert.assertEquals(actual, expected, "Z to A sort is incorrect");
    }

    // TC2: Price sorting low to high and high to low (bug detection)
    @Test
    public void testCase2() throws InterruptedException {
        Thread.sleep(1000);
        Select sort = new Select(driver.findElement(By.cssSelector("[data-test='product-sort-container']")));

        sort.selectByValue("lohi");
        Thread.sleep(1000);
        List<Double> prices = new ArrayList<>();
        for (WebElement e : driver.findElements(By.cssSelector("[data-test='inventory-item-price']"))) {
            prices.add(Double.parseDouble(e.getText().replace("$", "")));
        }
        List<Double> expectedAsc = new ArrayList<>(prices);
        Collections.sort(expectedAsc);
        Assert.assertEquals(prices, expectedAsc, "Price (low to high) is not ascending");

        sort = new Select(driver.findElement(By.cssSelector("[data-test='product-sort-container']")));
        sort.selectByValue("hilo");
        Thread.sleep(1000);
        prices.clear();
        for (WebElement e : driver.findElements(By.cssSelector("[data-test='inventory-item-price']"))) {
            prices.add(Double.parseDouble(e.getText().replace("$", "")));
        }
        List<Double> expectedDesc = new ArrayList<>(expectedAsc);
        Collections.reverse(expectedDesc);
        Assert.assertEquals(prices, expectedDesc, "Price (high to low) is not descending");
    }

    // TC3: Add items to cart updates badge, button text and cart contents (positive)
    @Test
    public void testCase3() throws InterruptedException {
        Thread.sleep(1000);
        Assert.assertEquals(driver.findElements(By.cssSelector("[data-test='shopping-cart-badge']")).size(), 0,
                "Cart badge should not exist when cart is empty");

        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();
        Thread.sleep(500);
        Assert.assertEquals(driver.findElement(By.cssSelector("[data-test='shopping-cart-badge']")).getText(), "1");
        Assert.assertEquals(driver.findElement(By.cssSelector("[data-test='remove-sauce-labs-backpack']")).getText(),
                "Remove");

        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-bike-light']")).click();
        Thread.sleep(500);
        Assert.assertEquals(driver.findElement(By.cssSelector("[data-test='shopping-cart-badge']")).getText(), "2");

        driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
        Thread.sleep(1000);
        Assert.assertTrue(driver.getCurrentUrl().contains("cart"), "Did not navigate to cart page");
        Assert.assertEquals(driver.findElements(By.cssSelector("[data-test='inventory-item']")).size(), 2,
                "Cart should contain exactly the 2 added items");
    }

    // TC4: Remove from the inventory page reverts button and badge (negative / bug detection)
    @Test
    public void testCase4() throws InterruptedException {
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();
        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-bike-light']")).click();
        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-onesie']")).click();
        Thread.sleep(500);
        Assert.assertEquals(driver.findElement(By.cssSelector("[data-test='shopping-cart-badge']")).getText(), "3");

        driver.findElement(By.cssSelector("[data-test='remove-sauce-labs-onesie']")).click();
        Thread.sleep(500);
        Assert.assertEquals(driver.findElement(By.cssSelector("[data-test='shopping-cart-badge']")).getText(), "2",
                "Badge not decremented after remove");
        Assert.assertEquals(driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-onesie']")).getText(),
                "Add to cart", "Button did not revert to 'Add to cart'");

        driver.findElement(By.cssSelector("[data-test='remove-sauce-labs-backpack']")).click();
        driver.findElement(By.cssSelector("[data-test='remove-sauce-labs-bike-light']")).click();
        Thread.sleep(500);
        Assert.assertEquals(driver.findElements(By.cssSelector("[data-test='shopping-cart-badge']")).size(), 0,
                "Badge should disappear when cart is empty");
    }

    // TC5: Every product opens its own details page with matching name and price (bug detection)
    @Test
    public void testCase5() throws InterruptedException {
        Thread.sleep(1000);
        int count = driver.findElements(By.cssSelector("[data-test='inventory-item']")).size();
        Assert.assertEquals(count, 6, "Unexpected number of products");

        for (int i = 0; i < count; i++) {
            WebElement item = driver.findElements(By.cssSelector("[data-test='inventory-item']")).get(i);
            String listName = item.findElement(By.cssSelector("[data-test='inventory-item-name']")).getText();
            String listPrice = item.findElement(By.cssSelector("[data-test='inventory-item-price']")).getText();

            item.findElement(By.cssSelector("a[data-test$='-title-link']")).click();
            Thread.sleep(1000);

            Assert.assertTrue(driver.getCurrentUrl().contains("inventory-item"),
                    "Did not open details page for: " + listName);
            Assert.assertEquals(driver.findElement(By.cssSelector("[data-test='inventory-item-name']")).getText(),
                    listName, "Details page shows wrong product name");
            Assert.assertEquals(driver.findElement(By.cssSelector("[data-test='inventory-item-price']")).getText(),
                    listPrice, "Details page shows wrong price for: " + listName);

            driver.findElement(By.cssSelector("[data-test='back-to-products']")).click();
            Thread.sleep(1000);
        }
    }

    @AfterClass
    public void afterClass() {
        if (driver != null) {
            driver.quit();
        }
    }
}