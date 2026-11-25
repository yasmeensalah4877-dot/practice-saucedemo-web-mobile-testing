package com.aos.web.tests.feature;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;

public class mohamed {

    private WebDriver driver;
    private WebDriverWait wait;

    private final By menuWrapLocator = By.cssSelector(".bm-menu-wrap");
    private final By burgerMenuBtn = By.cssSelector("#react-burger-menu-btn");
    private final By closeMenuBtn = By.cssSelector("#react-burger-cross-btn");
    private final By logoutLink = By.cssSelector("[data-test='logout-sidebar-link']");
    private final By allItemsLink = By.cssSelector("[data-test='inventory-sidebar-link']");
    private final By aboutLink = By.cssSelector("[data-test='about-sidebar-link']");
    private final By resetLink = By.cssSelector("[data-test='reset-sidebar-link']");
    private final By inventoryList = By.cssSelector("[data-test='inventory-list']");
    private final By pageTitle = By.cssSelector("[data-test='title']");
    private final By cartLink = By.cssSelector("[data-test='shopping-cart-link']");
    private final By cartBadge = By.cssSelector("[data-test='shopping-cart-badge']");
    private final By addBackpackBtn = By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']");
    private final By removeBackpackBtn = By.cssSelector("[data-test='remove-sauce-labs-backpack']");
    private final By inventoryItems = By.cssSelector("[data-test='inventory-item']");

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(8));

        driver.get("https://www.saucedemo.com/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='username']")))
                .sendKeys("standard_user");
        driver.findElement(By.cssSelector("[data-test='password']")).sendKeys("secret_sauce");
        driver.findElement(By.cssSelector("[data-test='login-button']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryList));
    }

    @BeforeMethod
    public void resetStateBeforeEachTest() {
        if (!driver.findElements(logoutLink).isEmpty() && driver.findElement(logoutLink).isDisplayed()) {
            wait.until(ExpectedConditions.elementToBeClickable(closeMenuBtn)).click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(logoutLink));
        }

        if (!driver.getCurrentUrl().contains("inventory")) {
            driver.get("https://www.saucedemo.com/inventory.html");
            wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryList));
        }

        if (!driver.findElements(removeBackpackBtn).isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(removeBackpackBtn)).click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(cartBadge));
        }
    }

    private WebElement openMenuAndWait() {
        wait.until(ExpectedConditions.elementToBeClickable(burgerMenuBtn)).click();
        WebElement menuWrap = wait.until(ExpectedConditions.presenceOfElementLocated(menuWrapLocator));
        wait.until(ExpectedConditions.attributeToBe(menuWrap, "aria-hidden", "false"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(allItemsLink));
        return menuWrap;
    }

    // TC1: التحقق من روابط القائمة الرئيسية وظهورها مع أيقونة السلة
    @Test
    public void testCase1() {
        SoftAssert soft = new SoftAssert();

        WebElement menuWrap = openMenuAndWait();
        soft.assertEquals(menuWrap.getAttribute("aria-hidden"), "false");

        WebElement allItems = driver.findElement(allItemsLink);
        WebElement about = driver.findElement(aboutLink);
        WebElement logout = driver.findElement(logoutLink);
        WebElement reset = driver.findElement(resetLink);
        WebElement closeBtn = driver.findElement(closeMenuBtn);

        soft.assertTrue(allItems.isDisplayed() && about.isDisplayed() && logout.isDisplayed()
                && reset.isDisplayed() && closeBtn.isDisplayed());
        soft.assertEquals(allItems.getText().trim(), "All Items");
        soft.assertEquals(about.getText().trim(), "About");
        soft.assertEquals(logout.getText().trim(), "Logout");
        soft.assertEquals(reset.getText().trim(), "Reset App State");
        soft.assertTrue(driver.findElement(cartLink).isDisplayed());

        soft.assertAll();
    }

    // TC2: التحقق من إغلاق القائمة بزر X ثم إضافة منتج للسلة
    @Test
    public void testCase2() {
        SoftAssert soft = new SoftAssert();

        WebElement menuWrap = openMenuAndWait();
        wait.until(ExpectedConditions.elementToBeClickable(closeMenuBtn)).click();
        wait.until(ExpectedConditions.attributeToBe(menuWrap, "aria-hidden", "true"));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(logoutLink));

        soft.assertEquals(menuWrap.getAttribute("aria-hidden"), "true");
        soft.assertTrue(driver.findElement(inventoryList).isDisplayed());
        soft.assertEquals(driver.findElement(pageTitle).getText().trim(), "Products");

        wait.until(ExpectedConditions.elementToBeClickable(addBackpackBtn)).click();
        WebElement badge = wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge));
        soft.assertEquals(badge.getText().trim(), "1");
        soft.assertEquals(driver.findElements(removeBackpackBtn).size(), 1);

        soft.assertAll();
    }

    // TC3: التحقق من فتح القائمة والضغط على All Items وبقاء المنتج في السلة
    @Test
    public void testCase3() {
        SoftAssert soft = new SoftAssert();

        wait.until(ExpectedConditions.elementToBeClickable(addBackpackBtn)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge));

        WebElement menuWrap = openMenuAndWait();
        wait.until(ExpectedConditions.elementToBeClickable(allItemsLink)).click();
        wait.until(ExpectedConditions.attributeToBe(menuWrap, "aria-hidden", "true"));

        soft.assertEquals(menuWrap.getAttribute("aria-hidden"), "true");
        soft.assertTrue(driver.getCurrentUrl().contains("inventory"));
        soft.assertEquals(driver.findElements(inventoryItems).size(), 6);
        soft.assertEquals(driver.findElement(cartBadge).getText().trim(), "1");

        soft.assertAll();
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}