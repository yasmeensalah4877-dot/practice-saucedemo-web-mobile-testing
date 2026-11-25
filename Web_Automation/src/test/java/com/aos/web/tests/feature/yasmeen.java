//yasmena


package com.aos.web.tests.feature;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class yasmeen {

    WebDriver driver;

    // Execution counters for Summary Report
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private final List<String> bugFindings = new ArrayList<>();

    // Login & Cart / Checkout Navigation Selectors
    private static final By USERNAME_INPUT = By.cssSelector("input[data-test='username']#user-name");
    private static final By PASSWORD_INPUT = By.cssSelector("input[data-test='password']#password");
    private static final By LOGIN_BUTTON = By.cssSelector("input[data-test='login-button']#login-button");
    private static final By ADD_TO_CART_BTN = By.cssSelector("button[data-test^='add-to-cart']");
    private static final By CART_LINK = By.cssSelector("a[data-test='shopping-cart-link'].shopping_cart_link");
    private static final By CHECKOUT_BUTTON = By.cssSelector("button[data-test='checkout']#checkout");

    // Checkout Step One Selectors
    private static final By FIRST_NAME_INPUT = By.cssSelector("input[data-test='firstName']#first-name");
    private static final By LAST_NAME_INPUT = By.cssSelector("input[data-test='lastName']#last-name");
    private static final By POSTAL_CODE_INPUT = By.cssSelector("input[data-test='postalCode']#postal-code");
    private static final By CONTINUE_BUTTON = By.cssSelector("input[data-test='continue']#continue");
    private static final By ERROR_CONTAINER = By.cssSelector("#checkout_info_container div.error-message-container");
    private static final By PAGE_TITLE = By.cssSelector("span[data-test='title'].title");

    private void pause(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    private void openCheckoutStepOne() throws InterruptedException {
        driver.navigate().to("https://www.saucedemo.com/");
        pause(1200);

        driver.findElement(USERNAME_INPUT).clear();
        driver.findElement(USERNAME_INPUT).sendKeys("standard_user");
        pause(600);

        driver.findElement(PASSWORD_INPUT).clear();
        driver.findElement(PASSWORD_INPUT).sendKeys("secret_sauce");
        pause(600);

        driver.findElement(LOGIN_BUTTON).click();
        pause(1200);

        driver.findElement(ADD_TO_CART_BTN).click();
        pause(800);

        driver.findElement(CART_LINK).click();
        pause(1000);

        driver.findElement(CHECKOUT_BUTTON).click();
        pause(1200);
    }

    @BeforeClass
    public void beforeClass() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        openCheckoutStepOne();
    }

    /*
     * =========================================================================
     * BUG REPORT - TC1 (If Checkout Step Two navigation or title fails)
     * - Title: Checkout Step One does not advance to Checkout: Overview with valid inputs
     * - Steps: Login -> Add item -> Cart -> Checkout -> Enter First Name="Ahmed", Last Name="Ali", Postal Code="12345" -> Click Continue
     * - Expected Result: Navigate to checkout-step-two.html with title "Checkout: Overview"
     * - Actual Result (if bug occurs): Remains on step one or wrong page title is displayed
     * - Severity: High
     * =========================================================================
     */
    @Test
    public void testCase1() throws InterruptedException {
        // Positive Flow: Valid First Name, Last Name, and Postal Code proceed to Checkout Overview
        driver.findElement(FIRST_NAME_INPUT).clear();
        driver.findElement(FIRST_NAME_INPUT).sendKeys("Ahmed");
        pause(700);

        driver.findElement(LAST_NAME_INPUT).clear();
        driver.findElement(LAST_NAME_INPUT).sendKeys("Ali");
        pause(700);

        driver.findElement(POSTAL_CODE_INPUT).clear();
        driver.findElement(POSTAL_CODE_INPUT).sendKeys("12345");
        pause(700);

        driver.findElement(CONTINUE_BUTTON).click();
        pause(1500);

        String currentUrl = driver.getCurrentUrl();
        String titleText = driver.findElement(PAGE_TITLE).getText().trim();
        Assert.assertTrue(currentUrl.contains("checkout-step-two"),
                "Expected navigation to Checkout Step Two (Overview), but stayed on: " + currentUrl);
        Assert.assertEquals(titleText, "Checkout: Overview",
                "Expected Overview page title after valid submission.");
    }

    /*
     * =========================================================================
     * BUG REPORT - TC2 (Missing Required First Name Validation)
     * - Title: Missing First Name allows progression or shows wrong error message
     * - Steps: On Checkout Step One, leave First Name empty, fill Last Name="Ali" and Postal Code="12345", click Continue
     * - Expected Result: Stay on checkout-step-one and display "Error: First Name is required"
     * - Actual Result (if bug occurs): Advances to step two or displays missing/incorrect error message
     * - Severity: High
     * =========================================================================
     */
    @Test
    public void testCase2() throws InterruptedException {
        // Negative Validation: Empty First Name must display 'Error: First Name is required' and stay on Step One
        driver.findElement(FIRST_NAME_INPUT).clear();
        pause(600);

        driver.findElement(LAST_NAME_INPUT).clear();
        driver.findElement(LAST_NAME_INPUT).sendKeys("Ali");
        pause(700);

        driver.findElement(POSTAL_CODE_INPUT).clear();
        driver.findElement(POSTAL_CODE_INPUT).sendKeys("12345");
        pause(700);

        driver.findElement(CONTINUE_BUTTON).click();
        pause(1500);

        WebElement errorEl = driver.findElement(ERROR_CONTAINER);
        Assert.assertTrue(errorEl.getText().contains("Error: First Name is required"),
                "Expected 'Error: First Name is required', but got: '" + errorEl.getText() + "'");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one"),
                "User should remain on Checkout Step One when First Name is empty.");
    }

    /*
     * =========================================================================
     * BUG REPORT - TC3 (Numeric-Only First Name Accepted)
     * - Title: Application accepts numeric-only First Name ("12345") without validation error
     * - Steps: On Checkout Step One, enter First Name="12345", Last Name="Ali", Postal Code="12345", click Continue
     * - Expected Result: Reject numeric-only First Name with a validation error and stay on step one
     * - Actual Result (if bug occurs): Accepts "12345" without error and proceeds to Checkout Overview
     * - Severity: Medium
     * =========================================================================
     */
    @Test
    public void testCase3() throws InterruptedException {
        // Bug Detection: Numeric-only First Name ("12345") must be rejected with an error message
        driver.findElement(FIRST_NAME_INPUT).clear();
        driver.findElement(FIRST_NAME_INPUT).sendKeys("12345");
        pause(700);

        driver.findElement(LAST_NAME_INPUT).clear();
        driver.findElement(LAST_NAME_INPUT).sendKeys("Ali");
        pause(700);

        driver.findElement(POSTAL_CODE_INPUT).clear();
        driver.findElement(POSTAL_CODE_INPUT).sendKeys("12345");
        pause(700);

        driver.findElement(CONTINUE_BUTTON).click();
        pause(1500);

        String errorText = driver.findElement(ERROR_CONTAINER).getText().trim();
        Assert.assertFalse(errorText.isEmpty(),
                "Bug Detected: Numeric-only First Name '12345' was accepted without an error message.");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one"),
                "Bug Detected: Application navigated away from Step One despite invalid numeric First Name.");
    }

    /*
     * =========================================================================
     * BUG REPORT - TC4 (Whitespace-Only Last Name Accepted)
     * - Title: Application accepts spaces-only Last Name ("   ") without required validation error
     * - Steps: On Checkout Step One, enter First Name="Ahmed", Last Name="   ", Postal Code="12345", click Continue
     * - Expected Result: Reject whitespace-only Last Name with a validation error and stay on step one
     * - Actual Result (if bug occurs): Treats spaces as valid input and proceeds to Checkout Overview
     * - Severity: Medium
     * =========================================================================
     */
    @Test
    public void testCase4() throws InterruptedException {
        // Bug Detection: Spaces-only Last Name ("   ") must be rejected with a required-field error
        driver.findElement(FIRST_NAME_INPUT).clear();
        driver.findElement(FIRST_NAME_INPUT).sendKeys("Ahmed");
        pause(700);

        driver.findElement(LAST_NAME_INPUT).clear();
        driver.findElement(LAST_NAME_INPUT).sendKeys("   ");
        pause(700);

        driver.findElement(POSTAL_CODE_INPUT).clear();
        driver.findElement(POSTAL_CODE_INPUT).sendKeys("12345");
        pause(700);

        driver.findElement(CONTINUE_BUTTON).click();
        pause(1500);

        String errorText = driver.findElement(ERROR_CONTAINER).getText().trim();
        Assert.assertFalse(errorText.isEmpty(),
                "Bug Detected: Whitespace-only Last Name was accepted without a required-field error.");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one"),
                "Bug Detected: Application progressed with whitespace-only Last Name.");
    }

    /*
     * =========================================================================
     * BUG REPORT - TC5 (Non-Numeric Postal Code Accepted)
     * - Title: Application accepts non-numeric Zip/Postal Code ("abcde") without validation error
     * - Steps: On Checkout Step One, enter First Name="Ahmed", Last Name="Ali", Postal Code="abcde", click Continue
     * - Expected Result: Reject invalid Postal Code "abcde" with an error message and stay on step one
     * - Actual Result (if bug occurs): Accepts "abcde" without error and proceeds to Checkout Overview
     * - Severity: Medium
     * =========================================================================
     */
    @Test
    public void testCase5() throws InterruptedException {
        // Bug Detection: Non-numeric Postal Code ("abcde") must be rejected with an error message
        driver.findElement(FIRST_NAME_INPUT).clear();
        driver.findElement(FIRST_NAME_INPUT).sendKeys("Ahmed");
        pause(700);

        driver.findElement(LAST_NAME_INPUT).clear();
        driver.findElement(LAST_NAME_INPUT).sendKeys("Ali");
        pause(700);

        driver.findElement(POSTAL_CODE_INPUT).clear();
        driver.findElement(POSTAL_CODE_INPUT).sendKeys("abcde");
        pause(700);

        driver.findElement(CONTINUE_BUTTON).click();
        pause(1500);

        String errorText = driver.findElement(ERROR_CONTAINER).getText().trim();
        Assert.assertFalse(errorText.isEmpty(),
                "Bug Detected: Invalid Postal Code 'abcde' was accepted without an error message.");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one"),
                "Bug Detected: Application navigated to Overview with invalid Postal Code 'abcde'.");
    }

    @AfterMethod
    public void afterMethod(ITestResult result) throws InterruptedException {
        totalTests++;
        String methodName = result.getMethod().getMethodName();
        if (result.getStatus() == ITestResult.SUCCESS) {
            passedTests++;
            System.out.println("[AFTER METHOD] " + methodName + " -> PASSED");
        } else if (result.getStatus() == ITestResult.FAILURE) {
            failedTests++;
            String reason = result.getThrowable() != null ? result.getThrowable().getMessage() : "Assertion failed";
            bugFindings.add(methodName + ": " + reason);
            System.out.println("[AFTER METHOD] " + methodName + " -> FAILED | " + reason);
        }
        pause(800);
    }

    @AfterClass
    public void afterClass() {
        // =====================================================================
        // SUMMARY REPORT
        // =====================================================================
        System.out.println("\n==================== SUMMARY REPORT ====================");
        System.out.println("Test Class   : yasmeen");
        System.out.println("Total Tests  : " + totalTests);
        System.out.println("Passed Tests : " + passedTests);
        System.out.println("Failed Tests : " + failedTests);
        System.out.println("Bugs Found   : " + bugFindings.size());
        if (!bugFindings.isEmpty()) {
            System.out.println("--- Detected Bugs / Failures ---");
            for (int i = 0; i < bugFindings.size(); i++) {
                System.out.println((i + 1) + ". " + bugFindings.get(i));
            }
        }
        System.out.println("========================================================\n");

        // Uncomment driver.quit() once you finish watching the browser execution
        // driver.quit();
    }
}