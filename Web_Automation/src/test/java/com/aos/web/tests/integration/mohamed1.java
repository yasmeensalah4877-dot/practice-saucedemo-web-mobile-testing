package com.aos.web.tests.feature;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

public class mohamed1 {

    WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        driver.navigate().to("https://www.saucedemo.com/");
        Thread.sleep(2000);
    }

    // TC1: Verify Main Menu Links Navigation
    @Test
    public void testCase1() throws InterruptedException {
        SoftAssert soft = new SoftAssert();

        // Steps 1-2: open site and log in
        driver.findElement(By.cssSelector("[data-test='username']")).sendKeys("standard_user");
        driver.findElement(By.cssSelector("[data-test='password']")).sendKeys("secret_sauce");
        driver.findElement(By.cssSelector("[data-test='login-button']")).click();
        Thread.sleep(2000);
        soft.assertTrue(driver.getCurrentUrl().contains("inventory"), "Login must land on the inventory page");

        // Step 3: click the menu icon
        driver.findElement(By.cssSelector("#react-burger-menu-btn")).click();
        Thread.sleep(1000);

        // Expected: menu opens and shows the 4 links + X button
        WebElement menuWrap = driver.findElement(By.cssSelector(".bm-menu-wrap"));
        soft.assertEquals(menuWrap.getAttribute("aria-hidden"), "false", "Opened menu must have aria-hidden='false'");

        WebElement allItems = driver.findElement(By.cssSelector("[data-test='inventory-sidebar-link']"));
        WebElement about = driver.findElement(By.cssSelector("[data-test='about-sidebar-link']"));
        WebElement logout = driver.findElement(By.cssSelector("[data-test='logout-sidebar-link']"));
        WebElement reset = driver.findElement(By.cssSelector("[data-test='reset-sidebar-link']"));
        WebElement closeBtn = driver.findElement(By.cssSelector("#react-burger-cross-btn"));

        soft.assertTrue(allItems.isDisplayed(), "'All Items' must be visible");
        soft.assertTrue(about.isDisplayed(), "'About' must be visible");
        soft.assertTrue(logout.isDisplayed(), "'Logout' must be visible");
        soft.assertTrue(reset.isDisplayed(), "'Reset App State' must be visible");
        soft.assertTrue(closeBtn.isDisplayed(), "Close (X) button must be visible");

        soft.assertEquals(allItems.getText().trim(), "All Items");
        soft.assertEquals(about.getText().trim(), "About");
        soft.assertEquals(logout.getText().trim(), "Logout");
        soft.assertEquals(reset.getText().trim(), "Reset App State");

        // Integration: the menu must be usable together with the rest of the page header (cart icon still present)
        soft.assertTrue(driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).isDisplayed(),
                "Cart icon must remain in the header while the menu is open");

        soft.assertAll();
        Thread.sleep(1000);
    }

    // TC2: Verify Sidebar Menu Closes Successfully via 'X' Button
    @Test
    public void testCase2() throws InterruptedException {
        SoftAssert soft = new SoftAssert();

        // Steps 1-2: open site and log in
        driver.findElement(By.cssSelector("[data-test='username']")).sendKeys("standard_user");
        driver.findElement(By.cssSelector("[data-test='password']")).sendKeys("secret_sauce");
        driver.findElement(By.cssSelector("[data-test='login-button']")).click();
        Thread.sleep(2000);

        // Step 3: open the menu, then click X
        driver.findElement(By.cssSelector("#react-burger-menu-btn")).click();
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("#react-burger-cross-btn")).click();
        Thread.sleep(1000);

        // Expected: menu closes, products page remains visible
        WebElement menuWrap = driver.findElement(By.cssSelector(".bm-menu-wrap"));
        soft.assertEquals(menuWrap.getAttribute("aria-hidden"), "true", "Menu must be aria-hidden='true' after X");
        soft.assertFalse(driver.findElement(By.cssSelector("[data-test='logout-sidebar-link']")).isDisplayed(),
                "Menu links must be hidden after closing");
        soft.assertTrue(driver.findElement(By.cssSelector("[data-test='inventory-list']")).isDisplayed(),
                "Products list must remain visible");
        soft.assertEquals(driver.findElement(By.cssSelector("[data-test='title']")).getText(), "Products");
        soft.assertTrue(driver.getCurrentUrl().contains("inventory"), "User must stay on the inventory page");

        // Integration: full interactivity restored -> add to cart works and updates the header badge
        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();
        Thread.sleep(500);
        List<WebElement> badge = driver.findElements(By.cssSelector("[data-test='shopping-cart-badge']"));
        soft.assertEquals(badge.size(), 1, "Cart badge must appear after adding an item");
        if (badge.size() == 1) {
            soft.assertEquals(badge.get(0).getText(), "1");
        }
        soft.assertEquals(driver.findElements(By.cssSelector("[data-test='remove-sauce-labs-backpack']")).size(), 1,
                "Button must switch to 'Remove' after adding");

        soft.assertAll();
        Thread.sleep(1000);
    }

    // TC3: Verify 'All Items' Link Navigation from Homepage
    @Test
    public void testCase3() throws InterruptedException {
        SoftAssert soft = new SoftAssert();

        // Steps 1-2: open site and log in
        driver.findElement(By.cssSelector("[data-test='username']")).sendKeys("standard_user");
        driver.findElement(By.cssSelector("[data-test='password']")).sendKeys("secret_sauce");
        driver.findElement(By.cssSelector("[data-test='login-button']")).click();
        Thread.sleep(2000);

        // Integration setup: add one item so we can verify cart state survives the navigation
        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();
        Thread.sleep(500);

        // Steps 3-4: open the menu and click 'All Items'
        driver.findElement(By.cssSelector("#react-burger-menu-btn")).click();
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("[data-test='inventory-sidebar-link']")).click();
        Thread.sleep(1500);

        // Expected: menu closes and the full inventory is displayed
        WebElement menuWrap = driver.findElement(By.cssSelector(".bm-menu-wrap"));
        soft.assertEquals(menuWrap.getAttribute("aria-hidden"), "true", "Menu must close after clicking 'All Items'");
        soft.assertTrue(driver.getCurrentUrl().contains("inventory"), "URL must remain on the inventory page");
        soft.assertTrue(driver.findElement(By.cssSelector("[data-test='inventory-list']")).isDisplayed(),
                "Inventory list must be displayed");
        soft.assertEquals(driver.findElements(By.cssSelector("[data-test='inventory-item']")).size(), 6,
                "All 6 products must be listed");

        // Integration: navigating via 'All Items' must NOT clear the cart
        List<WebElement> badge = driver.findElements(By.cssSelector("[data-test='shopping-cart-badge']"));
        soft.assertEquals(badge.size(), 1, "Cart badge must still be present after 'All Items'");
        if (badge.size() == 1) {
            soft.assertEquals(badge.get(0).getText(), "1", "Cart count must be preserved");
        }

        soft.assertAll();
        Thread.sleep(1000);
    }

    @AfterMethod
    public void afterMethod() {
    }

    @AfterClass
    public void afterClass() {
        driver.quit();
    }
}