package com.aos.web.tests.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class baraa1 {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://www.saucedemo.com/";

    // Robust CSS Selectors for Swag Labs elements
    private static final By USERNAME_INPUT = By.cssSelector("#user-name");
    private static final By PASSWORD_INPUT = By.cssSelector("#password");
    private static final By LOGIN_BUTTON = By.cssSelector("#login-button");
    private static final By ERROR_MESSAGE_CONTAINER = By.cssSelector("[data-test='error']");
    private static final By INVENTORY_CONTAINER = By.cssSelector("#inventory_container");
    private static final By ADD_BACKPACK_BUTTON = By.cssSelector("#add-to-cart-sauce-labs-backpack");
    private static final By REMOVE_BACKPACK_BUTTON = By.cssSelector("#remove-sauce-labs-backpack");
    private static final By SHOPPING_CART_BADGE = By.cssSelector(".shopping_cart_badge");
    private static final By SHOPPING_CART_LINK = By.cssSelector(".shopping_cart_link");
    private static final By CART_ITEM_NAME = By.cssSelector(".inventory_item_name");
    private static final By CART_ITEM_PRICE = By.cssSelector(".inventory_item_price");
    private static final By CHECKOUT_BUTTON = By.cssSelector("#checkout");
    private static final By FIRST_NAME_INPUT = By.cssSelector("#first-name");
    private static final By LAST_NAME_INPUT = By.cssSelector("#last-name");
    private static final By POSTAL_CODE_INPUT = By.cssSelector("#postal-code");
    private static final By CONTINUE_BUTTON = By.cssSelector("#continue");
    private static final By FINISH_BUTTON = By.cssSelector("#finish");
    private static final By COMPLETE_HEADER = By.cssSelector(".complete-header");

    private void logStep(String message) {
        System.out.println("[STEP] " + message);
        Reporter.log(message, true);
    }

    // Visual pause so you can clearly watch each action happen in the browser
    private void visualPause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void loginAsStandardUser() {
        logStep("Opening Swag Labs login page: " + BASE_URL);
        driver.get(BASE_URL);

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_INPUT));
        logStep("Entering username: standard_user");
        usernameField.clear();
        usernameField.sendKeys("standard_user");
        visualPause(600);

        logStep("Entering password: secret_sauce");
        WebElement passwordField = driver.findElement(PASSWORD_INPUT);
        passwordField.clear();
        passwordField.sendKeys("secret_sauce");
        visualPause(600);

        logStep("Clicking Login button");
        driver.findElement(LOGIN_BUTTON).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(INVENTORY_CONTAINER));
        visualPause(800);
    }

    @BeforeMethod
    public void setUp() {
        logStep("Launching visible Chrome browser with password manager & leak check disabled...");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--incognito");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-features=PasswordLeakDetection,SafetyCheck,PasswordManagerOnboarding");

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterMethod
    public void tearDown() {
        visualPause(1000);
        logStep("Closing browser...");
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * TC-01 (Positive): Verify valid login redirects to Inventory page.
     */
    @Test(priority = 1, description = "Verify valid user login opens inventory page")
    public void verifyValidLogin() {
        loginAsStandardUser();

        logStep("Verifying current URL contains 'inventory.html'");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"),
                "Expected URL to contain inventory.html after login.");
    }

    /**
     * TC-02 (Negative): Verify locked_out_user receives an error message and cannot log in.
     */
    @Test(priority = 2, description = "Verify locked out user cannot log in and sees error message")
    public void verifyLockedOutUserLoginError() {
        logStep("Opening Swag Labs login page");
        driver.get(BASE_URL);

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_INPUT));
        logStep("Entering locked_out_user credentials");
        usernameField.sendKeys("locked_out_user");
        visualPause(600);
        driver.findElement(PASSWORD_INPUT).sendKeys("secret_sauce");
        visualPause(600);

        logStep("Clicking Login button");
        driver.findElement(LOGIN_BUTTON).click();

        WebElement errorBanner = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE_CONTAINER));
        String errorText = errorBanner.getText();
        logStep("Verifying error message displayed: " + errorText);
        Assert.assertTrue(errorText.contains("Sorry, this user has been locked out."),
                "Expected locked out error message, but got: " + errorText);
        visualPause(1000);
    }

    /**
     * TC-03 (Positive): Verify adding a product updates cart badge and displays the exact item inside cart.
     */
    @Test(priority = 3, description = "Verify adding Sauce Labs Backpack updates cart badge and cart page")
    public void verifyAddProductToCartIntegration() {
        loginAsStandardUser();

        logStep("Clicking 'Add to cart' for Sauce Labs Backpack");
        wait.until(ExpectedConditions.elementToBeClickable(ADD_BACKPACK_BUTTON)).click();
        visualPause(800);

        logStep("Verifying cart badge displays '1'");
        WebElement badge = wait.until(ExpectedConditions.visibilityOfElementLocated(SHOPPING_CART_BADGE));
        Assert.assertEquals(badge.getText().trim(), "1", "Cart badge count should be 1.");

        logStep("Opening shopping cart page");
        driver.findElement(SHOPPING_CART_LINK).click();
        visualPause(800);

        logStep("Verifying product name and price in cart");
        String itemName = wait.until(ExpectedConditions.visibilityOfElementLocated(CART_ITEM_NAME)).getText();
        String itemPrice = driver.findElement(CART_ITEM_PRICE).getText();
        Assert.assertEquals(itemName, "Sauce Labs Backpack", "Product name in cart mismatch.");
        Assert.assertEquals(itemPrice, "$29.99", "Product price in cart mismatch.");
    }

    /**
     * TC-04 (Positive): Verify removing a product from inventory clears the cart badge and resets button.
     */
    @Test(priority = 4, description = "Verify removing product from inventory updates button and clears cart")
    public void verifyRemoveProductFromInventory() {
        loginAsStandardUser();

        logStep("Adding Sauce Labs Backpack to cart");
        wait.until(ExpectedConditions.elementToBeClickable(ADD_BACKPACK_BUTTON)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(SHOPPING_CART_BADGE));
        visualPause(800);

        logStep("Clicking 'Remove' button on inventory page");
        wait.until(ExpectedConditions.elementToBeClickable(REMOVE_BACKPACK_BUTTON)).click();
        visualPause(800);

        logStep("Verifying button reverted to 'Add to cart' and cart badge disappeared");
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(ADD_BACKPACK_BUTTON)).isDisplayed(),
                "Add to cart button should reappear after removal.");
        Assert.assertTrue(driver.findElements(SHOPPING_CART_BADGE).isEmpty(),
                "Cart badge should be cleared when no items remain.");
    }

    /**
     * TC-05 (End-to-End Positive): Complete checkout flow from cart to order confirmation.
     */
    @Test(priority = 5, description = "Verify complete checkout flow from cart to order confirmation")
    public void verifyCompleteCheckoutFlow() {
        loginAsStandardUser();

        logStep("Adding Sauce Labs Backpack and navigating to cart");
        wait.until(ExpectedConditions.elementToBeClickable(ADD_BACKPACK_BUTTON)).click();
        visualPause(600);
        driver.findElement(SHOPPING_CART_LINK).click();
        visualPause(600);

        logStep("Clicking Checkout button");
        wait.until(ExpectedConditions.elementToBeClickable(CHECKOUT_BUTTON)).click();
        visualPause(600);

        logStep("Filling checkout information (Test User, 12345)");
        wait.until(ExpectedConditions.visibilityOfElementLocated(FIRST_NAME_INPUT)).sendKeys("Test");
        visualPause(400);
        driver.findElement(LAST_NAME_INPUT).sendKeys("User");
        visualPause(400);
        driver.findElement(POSTAL_CODE_INPUT).sendKeys("12345");
        visualPause(600);

        logStep("Clicking Continue to Checkout Overview");
        driver.findElement(CONTINUE_BUTTON).click();
        visualPause(800);

        logStep("Clicking Finish to complete order");
        wait.until(ExpectedConditions.elementToBeClickable(FINISH_BUTTON)).click();
        visualPause(800);

        logStep("Verifying order confirmation message");
        WebElement confirmationHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(COMPLETE_HEADER));
        Assert.assertEquals(confirmationHeader.getText().trim(), "Thank you for your order!",
                "Order confirmation message mismatch.");
    }
}