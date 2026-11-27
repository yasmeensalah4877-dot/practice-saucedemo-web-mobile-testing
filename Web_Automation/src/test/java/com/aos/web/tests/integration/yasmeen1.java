//yas111
package com.aos.web.tests.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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

public class yasmeen1 {

    private WebDriver driver;

    // عدادات وقائمة لنتائج التنفيذ (Summary Report)
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private final List<String> detectedBugs = new ArrayList<>();

    // محددات صفحة تسجيل الدخول والكارت والانتقال للـ Checkout
    private static final By USERNAME_INPUT = By.cssSelector("input[data-test='username']#user-name");
    private static final By PASSWORD_INPUT = By.cssSelector("input[data-test='password']#password");
    private static final By LOGIN_BUTTON = By.cssSelector("input[data-test='login-button']#login-button");
    private static final By ADD_TO_CART_BTN = By.cssSelector("button[data-test^='add-to-cart']");
    private static final By CART_LINK = By.cssSelector("a[data-test='shopping-cart-link'].shopping_cart_link");
    private static final By CHECKOUT_BUTTON = By.cssSelector("button[data-test='checkout']#checkout");

    // محددات صفحة Checkout Step One
    private static final By FIRST_NAME_INPUT = By.cssSelector("input[data-test='firstName']#first-name");
    private static final By LAST_NAME_INPUT = By.cssSelector("input[data-test='lastName']#last-name");
    private static final By POSTAL_CODE_INPUT = By.cssSelector("input[data-test='postalCode']#postal-code");
    private static final By CONTINUE_BUTTON = By.cssSelector("input[data-test='continue']#continue");
    private static final By ERROR_MESSAGE = By.cssSelector("#checkout_info_container .error-message-container");
    private static final By PAGE_TITLE = By.cssSelector("span[data-test='title'].title");

    // انتظار واضح + طباعة اسم الخطوة لتتبعها واحدة واحدة في الـ Console
    private void stepPause(String stepDescription, long millis) throws InterruptedException {
        System.out.println("[STEP] " + stepDescription);
        Thread.sleep(millis);
    }

    // خطوات الدخول للموقع وإضافة منتج والوصول إلى Checkout Step One خطوة بخطوة
    private void navigateStepByStepToCheckout() throws InterruptedException {
        stepPause("1. فتح موقع SauceDemo...", 1500);
        driver.navigate().to("https://www.saucedemo.com/");
        stepPause("تم فتح صفحة تسجيل الدخول", 1200);

        stepPause("2. كتابة اسم المستخدم (standard_user)...", 800);
        driver.findElement(USERNAME_INPUT).clear();
        driver.findElement(USERNAME_INPUT).sendKeys("standard_user");
        stepPause("تمت كتابة اسم المستخدم", 1000);

        stepPause("3. كتابة كلمة المرور...", 800);
        driver.findElement(PASSWORD_INPUT).clear();
        driver.findElement(PASSWORD_INPUT).sendKeys("secret_sauce");
        stepPause("تمت كتابة كلمة المرور", 1000);

        stepPause("4. الضغط على زر Login...", 800);
        driver.findElement(LOGIN_BUTTON).click();
        stepPause("تم تسجيل الدخول وظهور المنتجات", 1500);

        stepPause("5. إضافة منتج إلى سلة التسوق...", 800);
        driver.findElement(ADD_TO_CART_BTN).click();
        stepPause("تمت إضافة المنتج للسلة", 1000);

        stepPause("6. فتح صفحة سلة التسوق (Cart)...", 800);
        driver.findElement(CART_LINK).click();
        stepPause("تم فتح صفحة Cart", 1200);

        stepPause("7. الضغط على زر Checkout للوصول إلى Step One...", 800);
        driver.findElement(CHECKOUT_BUTTON).click();
        stepPause("أصبحت الآن في صفحة Checkout: Your Information", 1500);
    }

    // تعبئة بيانات الـ Checkout والضغط على Continue خطوة بخطوة
    private void fillCheckoutStepByStep(String firstName, String lastName, String postalCode)
            throws InterruptedException {
        stepPause("إدخال First Name: [" + firstName + "]", 900);
        driver.findElement(FIRST_NAME_INPUT).clear();
        driver.findElement(FIRST_NAME_INPUT).sendKeys(firstName);
        stepPause("تم إدخال First Name", 1000);

        stepPause("إدخال Last Name: [" + lastName + "]", 900);
        driver.findElement(LAST_NAME_INPUT).clear();
        driver.findElement(LAST_NAME_INPUT).sendKeys(lastName);
        stepPause("تم إدخال Last Name", 1000);

        stepPause("إدخال Postal Code: [" + postalCode + "]", 900);
        driver.findElement(POSTAL_CODE_INPUT).clear();
        driver.findElement(POSTAL_CODE_INPUT).sendKeys(postalCode);
        stepPause("تم إدخال Postal Code", 1000);

        stepPause("الضغط على زر Continue...", 900);
        driver.findElement(CONTINUE_BUTTON).click();
        stepPause("انتظار ظهور النتيجة بعد الضغط على Continue", 1800);
    }

    @BeforeClass
    public void beforeClass() {
        System.out.println("[BEFORE CLASS] تشغيل متصفح Chrome وتكبير النافذة...");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        System.out.println("\n--------------------------------------------------------");
        System.out.println("[BEFORE METHOD] بدء تجهيز الخطوات قبل الاختبار...");
        navigateStepByStepToCheckout();
    }

    /*
     * BUG REPORT - TC1
     * Title: Valid checkout information does not proceed to Checkout Overview
     * Steps: Login -> Add to cart -> Cart -> Checkout -> Enter First Name="Ahmed", Last Name="Ali", Postal Code="12345" -> Click Continue
     * Expected: Navigate to checkout-step-two.html with title "Checkout: Overview"
     * Actual (if bug): Remains on Step One or shows wrong title
     * Severity: High
     */
    @Test
    public void testCase1() throws InterruptedException {
        System.out.println("[TEST CASE 1] سيناريو إيجابي: بيانات صحيحة");
        fillCheckoutStepByStep("Ahmed", "Ali", "12345");

        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-two.html"),
                "Expected checkout-step-two.html, got: " + driver.getCurrentUrl());
        Assert.assertEquals(driver.findElement(PAGE_TITLE).getText().trim(), "Checkout: Overview",
                "Expected 'Checkout: Overview' title.");
    }

    /*
     * BUG REPORT - TC2
     * Title: Empty checkout fields do not show required error or stay on Step One
     * Steps: Leave First Name, Last Name, Postal Code empty -> Click Continue
     * Expected: Show error message and stay on checkout-step-one.html
     * Actual (if bug): No error displayed or navigated away from Step One
     * Severity: High
     */
    @Test
    public void testCase2() throws InterruptedException {
        System.out.println("[TEST CASE 2] سيناريو سلبي: ترك جميع الحقول فارغة");
        fillCheckoutStepByStep("", "", "");

        Assert.assertFalse(driver.findElement(ERROR_MESSAGE).getText().trim().isEmpty(),
                "Expected an error message when all fields are empty.");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"),
                "Expected to remain on checkout-step-one.html.");
    }

    /*
     * BUG REPORT - TC3
     * Title: Invalid Postal Code ("abc@#$") accepted without error
     * Steps: Enter First Name="Ahmed", Last Name="Ali", Postal Code="abc@#$" -> Click Continue
     * Expected: Reject invalid Postal Code with error and stay on Step One
     * Actual (if bug): Accepted without error or navigated to next step
     * Severity: Medium
     */
    @Test
    public void testCase3() throws InterruptedException {
        System.out.println("[TEST CASE 3] كشف Bug: Postal Code غير صالح (abc@#$)");
        fillCheckoutStepByStep("Ahmed", "Ali", "abc@#$");

        Assert.assertFalse(driver.findElement(ERROR_MESSAGE).getText().trim().isEmpty(),
                "Bug Detected: Invalid Postal Code 'abc@#$' was accepted without error.");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"),
                "Bug Detected: Navigated away with invalid Postal Code 'abc@#$'.");
    }

    /*
     * BUG REPORT - TC4
     * Title: Whitespace-only fields ("   ") accepted without error
     * Steps: Enter "   " in First Name, Last Name, and Postal Code -> Click Continue
     * Expected: Reject spaces-only input with error and stay on Step One
     * Actual (if bug): Accepted spaces-only input or navigated to Overview
     * Severity: Medium
     */
    @Test
    public void testCase4() throws InterruptedException {
        System.out.println("[TEST CASE 4] كشف Bug: مسافات فقط في الحقول");
        fillCheckoutStepByStep("   ", "   ", "   ");

        Assert.assertFalse(driver.findElement(ERROR_MESSAGE).getText().trim().isEmpty(),
                "Bug Detected: Whitespace-only fields were accepted without error.");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"),
                "Bug Detected: Navigated to Overview with whitespace-only fields.");
    }

    /*
     * BUG REPORT - TC5
     * Title: Overlong First/Last Name (260 chars) accepted without validation error
     * Steps: Enter 260 'A' chars in First Name & Last Name, Postal Code="12345" -> Click Continue
     * Expected: Reject overlong input with validation error and stay on Step One
     * Actual (if bug): Accepted 260-char input and allowed progression
     * Severity: Medium
     */
    @Test
    public void testCase5() throws InterruptedException {
        System.out.println("[TEST CASE 5] كشف Bug: نص طويل جداً (260 حرف)");
        String longValue = "A".repeat(260);
        fillCheckoutStepByStep(longValue, longValue, "12345");

        Assert.assertFalse(driver.findElement(ERROR_MESSAGE).getText().trim().isEmpty(),
                "Bug Detected: 260-character input was accepted without error.");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"),
                "Bug Detected: Progressed with 260-character names.");
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
            String msg = result.getThrowable() != null ? result.getThrowable().getMessage() : "Failed";
            detectedBugs.add(methodName + ": " + msg);
            System.out.println("[AFTER METHOD] " + methodName + " -> FAILED | " + msg);
        }
        stepPause("انتظار قصير قبل الانتقال للتالي...", 1000);
    }

    @AfterClass
    public void afterClass() {
        // SUMMARY REPORT
        System.out.println("\n==================== SUMMARY REPORT ====================");
        System.out.println("Class Name   : yasmeen1");
        System.out.println("Total Tests  : " + totalTests);
        System.out.println("Passed       : " + passedTests);
        System.out.println("Failed       : " + failedTests);
        System.out.println("Bugs Found   : " + detectedBugs.size());
        for (int i = 0; i < detectedBugs.size(); i++) {
            System.out.println((i + 1) + ". " + detectedBugs.get(i));
        }
        System.out.println("========================================================\n");

        // لو عايزة تشوفي الصفحة في آخر تنفيذ قبل ما تقفل، تقدري تعلّقي السطر اللي تحت مؤقتاً
        if (driver != null) {
            driver.quit();
        }
    }
}