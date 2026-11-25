package com.aos.web.tests.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class E2E_testing {

    private WebDriver driver;
    private WebDriverWait wait;

    // أجزاء الاسم الكامل في مسار E2E: الأول، الثاني، الثالث، والرابع
    private static final String FIRST_NAME = "Yasmeen";
    private static final String SECOND_NAME = "Salah";
    private static final String THIRD_NAME = "Abdelrazek";
    private static final String FOURTH_NAME = "Youssef";

    private static final String FULL_FIRST_FIELD = FIRST_NAME + " " + SECOND_NAME; // Yasmeen Salah
    private static final String FULL_LAST_FIELD = THIRD_NAME + " " + FOURTH_NAME;  // Abdelrazek Youssef
    private static final String FULL_NAME = FIRST_NAME + " " + SECOND_NAME + " " + THIRD_NAME + " " + FOURTH_NAME;
    private static final String POSTAL_CODE = "32511";

    // عدادات وقائمة لنتائج التنفيذ (Summary Report)
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private final List<String> detectedBugs = new ArrayList<>();

    // محددات تسجيل الدخول وإضافة منتج والانتقال لصفحة Checkout
    private static final By USERNAME_INPUT = By.cssSelector("input[data-test='username']#user-name");
    private static final By PASSWORD_INPUT = By.cssSelector("input[data-test='password']#password");
    private static final By LOGIN_BUTTON = By.cssSelector("input[data-test='login-button']#login-button");
    private static final By ADD_TO_CART_BTN = By.cssSelector("button[data-test^='add-to-cart']");
    private static final By CART_LINK = By.cssSelector("a[data-test='shopping-cart-link'].shopping_cart_link");
    private static final By CHECKOUT_BUTTON = By.cssSelector("button[data-test='checkout']#checkout");

    // محددات صفحة Checkout Step One و Step Two و Complete
    private static final By FIRST_NAME_INPUT = By.cssSelector("input[data-test='firstName']#first-name");
    private static final By LAST_NAME_INPUT = By.cssSelector("input[data-test='lastName']#last-name");
    private static final By POSTAL_CODE_INPUT = By.cssSelector("input[data-test='postalCode']#postal-code");
    private static final By CONTINUE_BUTTON = By.cssSelector("input[data-test='continue']#continue");
    private static final By FINISH_BUTTON = By.cssSelector("button[data-test='finish']#finish");
    private static final By COMPLETE_HEADER = By.cssSelector("h2[data-test='complete-header'].complete-header");
    private static final By CANCEL_BUTTON = By.cssSelector("button[data-test='cancel']#cancel");
    private static final By ERROR_CONTAINER = By.cssSelector("#checkout_info_container div.error-message-container");
    private static final By PAGE_TITLE = By.cssSelector("span[data-test='title'].title");
    private static final By BURGER_MENU_BTN = By.cssSelector("button#react-burger-menu-btn");
    private static final By MENU_WRAP = By.cssSelector("div.bm-menu-wrap");
    private static final By ABOUT_SIDEBAR_LINK = By.cssSelector("a[data-test='about-sidebar-link']#about_sidebar_link");

    private int stepCounter = 1;

    // طباعة كل خطوة + انتظار بـ Thread.sleep لمتابعة التنفيذ بوضوح
    private void showStep(String description, long sleepMillis) throws InterruptedException {
        System.out.println(String.format("[E2E STEP %02d] %s", stepCounter++, description));
        Thread.sleep(sleepMillis);
    }

    // انتظار عنصر حتى يظهر بوضوح
    private WebElement waitForVisible(By locator, String elementName) {
        System.out.println("   -> انتظار ظهور: " + elementName);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // انتظار عنصر حتى يصبح قابلاً للضغط ثم الضغط عليه
    private void waitAndClick(By locator, String elementName, long sleepAfterMillis) throws InterruptedException {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        el.click();
        showStep("تم الضغط على: " + elementName, sleepAfterMillis);
    }

    // كتابة جزء من الاسم تدريجياً داخل حقل لإظهار الخطوة بوضوح
    private void appendNamePartGradually(WebElement input, String fieldName, String partLabel, String partText,
                                         long charSleepMillis) throws InterruptedException {
        showStep("كتابة [" + partLabel + "] داخل [" + fieldName + "]: \"" + partText + "\"", 600);
        for (int i = 0; i < partText.length(); i++) {
            input.sendKeys(String.valueOf(partText.charAt(i)));
            Thread.sleep(charSleepMillis);
        }
        showStep("تمت كتابة [" + partLabel + "] -> القيمة الحالية في [" + fieldName + "]: \""
                + input.getAttribute("value") + "\"", 750);
    }

    // كتابة نص كامل خطوة بخطوة داخل حقل
    private void typeGradually(By locator, String fieldName, String value, long charSleepMillis)
            throws InterruptedException {
        WebElement input = waitForVisible(locator, fieldName);
        showStep("بدء الكتابة في [" + fieldName + "]: \"" + value + "\"", 600);
        input.clear();
        for (int i = 0; i < value.length(); i++) {
            input.sendKeys(String.valueOf(value.charAt(i)));
            Thread.sleep(charSleepMillis);
        }
        showStep("اكتملت الكتابة في [" + fieldName + "] -> القيمة الحالية: \"" + input.getAttribute("value") + "\"", 750);
    }

    // كتابة الاسم الأول + الثاني في حقل First Name خطوة بخطوة
    private void enterFirstAndSecondNameStepByStep() throws InterruptedException {
        WebElement firstInput = waitForVisible(FIRST_NAME_INPUT, "First Name");
        firstInput.clear();
        appendNamePartGradually(firstInput, "First Name", "الاسم الأول", FIRST_NAME, 75);
        firstInput.sendKeys(" ");
        appendNamePartGradually(firstInput, "First Name", "الاسم الثاني", SECOND_NAME, 75);
    }

    // كتابة الاسم الثالث + الرابع في حقل Last Name خطوة بخطوة
    private void enterThirdAndFourthNameStepByStep() throws InterruptedException {
        WebElement lastInput = waitForVisible(LAST_NAME_INPUT, "Last Name");
        lastInput.clear();
        appendNamePartGradually(lastInput, "Last Name", "الاسم الثالث", THIRD_NAME, 75);
        lastInput.sendKeys(" ");
        appendNamePartGradually(lastInput, "Last Name", "الاسم الرابع", FOURTH_NAME, 75);
    }

    // فتح الموقع -> تسجيل الدخول -> إضافة منتج -> فتح السلة -> الضغط على Checkout
    private void navigateToCheckoutStepOne() throws InterruptedException {
        showStep("فتح موقع SauceDemo (https://www.saucedemo.com/)", 1200);
        driver.get("https://www.saucedemo.com/");
        showStep("تم فتح الموقع | URL الحالي: " + driver.getCurrentUrl(), 1000);

        typeGradually(USERNAME_INPUT, "Username", "standard_user", 70);
        typeGradually(PASSWORD_INPUT, "Password", "secret_sauce", 70);

        showStep("الضغط على زر Login", 600);
        waitAndClick(LOGIN_BUTTON, "Login Button", 1300);

        showStep("إضافة منتج إلى السلة (Add to Cart)", 600);
        waitAndClick(ADD_TO_CART_BTN, "Add to Cart Button", 1100);

        showStep("الانتقال إلى سلة التسوق (Cart)", 600);
        waitAndClick(CART_LINK, "Shopping Cart Link", 1200);

        showStep("الضغط على زر Checkout للدخول إلى Step One", 600);
        waitAndClick(CHECKOUT_BUTTON, "Checkout Button", 1400);

        String title = waitForVisible(PAGE_TITLE, "Checkout Page Title").getText().trim();
        showStep("داخل صفحة Checkout Step One | العنوان: \"" + title + "\" | URL: "
                + driver.getCurrentUrl(), 1200);
    }

    // تعبئة بيانات الـ Checkout والضغط على Continue خطوة بخطوة
    private void fillCheckoutInfoAndContinue(String firstName, String lastName, String postalCode)
            throws InterruptedException {
        if (FULL_FIRST_FIELD.equals(firstName)) {
            enterFirstAndSecondNameStepByStep();
        } else {
            typeGradually(FIRST_NAME_INPUT, "First Name", firstName, 75);
        }

        if (FULL_LAST_FIELD.equals(lastName)) {
            enterThirdAndFourthNameStepByStep();
        } else {
            typeGradually(LAST_NAME_INPUT, "Last Name", lastName, 75);
        }

        typeGradually(POSTAL_CODE_INPUT, "Postal Code", postalCode, 85);

        showStep("الضغط على زر Continue للانتقال إلى Checkout: Overview", 700);
        waitAndClick(CONTINUE_BUTTON, "Continue Button", 1600);
        showStep("بعد الضغط على Continue | URL الحالي: " + driver.getCurrentUrl(), 1100);
    }

    // إتمام الأوردر بالكامل من صفحة Overview بالضغط على Finish
    private void completeOrderUntilEnd() throws InterruptedException {
        String overviewTitle = waitForVisible(PAGE_TITLE, "Checkout: Overview Title").getText().trim();
        showStep("وصلنا لصفحة Overview | العنوان: \"" + overviewTitle + "\" | URL: " + driver.getCurrentUrl(), 1300);

        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-two.html"),
                "Expected /checkout-step-two.html, got: " + driver.getCurrentUrl());
        Assert.assertEquals(overviewTitle, "Checkout: Overview",
                "Expected page title 'Checkout: Overview'.");

        showStep("الضغط على زر Finish لإتمام الأوردر باسم: " + FULL_NAME, 800);
        waitAndClick(FINISH_BUTTON, "Finish Button", 1700);

        String completeHeader = waitForVisible(COMPLETE_HEADER, "Order Complete Header").getText().trim();
        String completeTitle = waitForVisible(PAGE_TITLE, "Checkout: Complete Title").getText().trim();
        showStep("تم إتمام الأوردر بنجاح حتى النهاية | العنوان: \"" + completeTitle + "\" | الرسالة: \""
                + completeHeader + "\"", 1800);
    }

    @BeforeClass
    public void beforeClass() {
        System.out.println("\n================ [BEFORE CLASS] بدء تشغيل E2E_testing ================");
        System.out.println("Tester Name : " + FULL_NAME);
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        stepCounter = 1;
        System.out.println("\n================ [BEFORE METHOD] تجهيز مسار E2E خطوة بخطوة ================");
        navigateToCheckoutStepOne();
    }

    /*
     * BUG REPORT - TC1
     * Title: Full E2E order flow fails to write full name, reach Overview, or complete order on Finish
     * Steps: Login -> Add to cart -> Cart -> Checkout -> Enter First Name="Yasmeen Salah", Last Name="Abdelrazek Youssef", Postal Code="32511" -> Continue -> Finish
     * Expected: Reach Checkout: Overview, click Finish, reach checkout-complete.html with "Thank you for your order!"
     * Actual (if bug): Fails to advance to Overview, fails on Finish, or does not show confirmation message
     * Severity: High
     */
    @Test
    public void testCase1() throws InterruptedException {
        System.out.println("\n--- [TEST CASE 1] مسار E2E كامل وإتمام الأوردر باسم: " + FULL_NAME + " ---");
        fillCheckoutInfoAndContinue(FULL_FIRST_FIELD, FULL_LAST_FIELD, POSTAL_CODE);
        completeOrderUntilEnd();

        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-complete.html"),
                "Expected /checkout-complete.html after Finish, got: " + driver.getCurrentUrl());
        Assert.assertEquals(driver.findElement(PAGE_TITLE).getText().trim(), "Checkout: Complete!",
                "Expected title 'Checkout: Complete!'.");
        Assert.assertEquals(driver.findElement(COMPLETE_HEADER).getText().trim(), "Thank you for your order!",
                "Expected confirmation 'Thank you for your order!'.");
    }

    /*
     * BUG REPORT - TC2
     * Title: Missing First Name does not block checkout or show 'Error: First Name is required'
     * Steps: Leave First Name empty, enter Last Name="Abdelrazek Youssef" and Postal Code="32511" -> Click Continue
     * Expected: Show 'Error: First Name is required' and stay on Step One
     * Actual (if bug): Wrong/missing error message or user navigates away from Step One
     * Severity: High
     */
    @Test
    public void testCase2() throws InterruptedException {
        System.out.println("\n--- [TEST CASE 2] ترك First Name فارغاً ---");
        fillCheckoutInfoAndContinue("", FULL_LAST_FIELD, POSTAL_CODE);

        String actualError = waitForVisible(ERROR_CONTAINER, "Error Message").getText().trim();
        showStep("رسالة الخطأ الظاهرة: \"" + actualError + "\"", 1100);

        Assert.assertEquals(actualError, "Error: First Name is required",
                "Expected 'Error: First Name is required' when First Name is empty, got: '" + actualError + "'");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"),
                "Expected user to remain on Step One when First Name is missing.");
    }

    /*
     * BUG REPORT - TC3
     * Title: Invalid Postal Code ("abc@#$") accepted without validation error
     * Steps: Enter First Name="Yasmeen Salah", Last Name="Abdelrazek Youssef", Postal Code="abc@#$" -> Click Continue
     * Expected: Reject invalid Postal Code with error message and stay on Step One
     * Actual (if bug): Accepted without error or navigated away from Step One
     * Severity: Medium
     */
    @Test
    public void testCase3() throws InterruptedException {
        System.out.println("\n--- [TEST CASE 3] إدخال حروف ورموز في Postal Code (abc@#$) ---");
        fillCheckoutInfoAndContinue(FULL_FIRST_FIELD, FULL_LAST_FIELD, "abc@#$");

        String actualError = waitForVisible(ERROR_CONTAINER, "Error Message").getText().trim();
        showStep("رسالة الخطأ بعد إدخال Postal Code غير صالح: \"" + actualError + "\"", 1100);

        Assert.assertFalse(actualError.isEmpty(),
                "Bug Detected: Invalid Postal Code 'abc@#$' was accepted without a validation error message.");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"),
                "Bug Detected: Application navigated away from Step One despite invalid Postal Code 'abc@#$'.");
    }

    /*
     * BUG REPORT - TC4
     * Title: Whitespace-only checkout fields ("   ") accepted without error
     * Steps: Enter "   " in First Name, Last Name, and Postal Code -> Click Continue
     * Expected: Reject spaces-only input with error message and stay on Step One
     * Actual (if bug): No error displayed or navigated to Step Two
     * Severity: Medium
     */
    @Test
    public void testCase4() throws InterruptedException {
        System.out.println("\n--- [TEST CASE 4] إدخال مسافات فقط (\"   \") في الحقول ---");
        fillCheckoutInfoAndContinue("   ", "   ", "   ");

        String actualError = waitForVisible(ERROR_CONTAINER, "Error Message").getText().trim();
        showStep("رسالة الخطأ بعد إدخال مسافات فقط: \"" + actualError + "\"", 1100);

        Assert.assertFalse(actualError.isEmpty(),
                "Bug Detected: Whitespace-only checkout fields were accepted without a required-field error.");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"),
                "Bug Detected: Application progressed to Step Two when all fields contained spaces only.");
    }

    /*
     * BUG REPORT - TC5
     * Title: Sidebar menu does not open cleanly or Cancel button does not return to cart.html
     * Steps: Click Burger Menu -> Check aria-hidden="false" and About link visibility -> Click Cancel
     * Expected: Sidebar menu opens with About link visible, and Cancel returns to cart.html
     * Actual (if bug): Menu stays aria-hidden="true", About link not visible, or URL is not cart.html
     * Severity: Medium
     */
    @Test
    public void testCase5() throws InterruptedException {
        System.out.println("\n--- [TEST CASE 5] فتح القائمة الجانبية والضغط على زر Cancel ---");

        showStep("الضغط على زر القائمة الجانبية (Burger Menu)", 600);
        waitAndClick(BURGER_MENU_BTN, "Burger Menu Button", 1300);

        String ariaHidden = waitForVisible(MENU_WRAP, "Menu Wrap").getAttribute("aria-hidden");
        boolean aboutVisible = waitForVisible(ABOUT_SIDEBAR_LINK, "About Link").isDisplayed();
        showStep("حالة القائمة aria-hidden=" + ariaHidden + " | ظهور رابط About=" + aboutVisible, 1100);

        Assert.assertEquals(ariaHidden, "false",
                "Bug Detected: Sidebar menu wrap remained aria-hidden='true' after clicking the menu button.");
        Assert.assertTrue(aboutVisible,
                "Bug Detected: 'About' sidebar link is not visible when sidebar menu is opened.");

        showStep("الضغط على زر Cancel للعودة إلى السلة", 600);
        waitAndClick(CANCEL_BUTTON, "Cancel Button", 1400);
        showStep("بعد الضغط على Cancel | URL الحالي: " + driver.getCurrentUrl(), 1000);

        Assert.assertTrue(driver.getCurrentUrl().contains("cart.html"),
                "Bug Detected: Clicking Cancel did not return to /cart.html; current URL is: " + driver.getCurrentUrl());
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
            String msg = result.getThrowable() != null ? result.getThrowable().getMessage() : "Assertion failed";
            detectedBugs.add(methodName + ": " + msg);
            System.out.println("[AFTER METHOD] " + methodName + " -> FAILED | " + msg);
        }
        Thread.sleep(900);
    }

    @AfterClass
    public void afterClass() {
        // SUMMARY REPORT
        System.out.println("\n==================== SUMMARY REPORT ====================");
        System.out.println("Class Name   : E2E_testing");
        System.out.println("Executed By  : " + FULL_NAME);
        System.out.println("Total Tests  : " + totalTests);
        System.out.println("Passed       : " + passedTests);
        System.out.println("Failed       : " + failedTests);
        System.out.println("Bugs Found   : " + detectedBugs.size());
        for (int i = 0; i < detectedBugs.size(); i++) {
            System.out.println((i + 1) + ". " + detectedBugs.get(i));
        }
        System.out.println("========================================================\n");

        if (driver != null) {
            driver.quit();
        }
    }
}