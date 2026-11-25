package com.aos.web.tests.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

    // محددات تسجيل الدخول
    private static final By USERNAME_INPUT = By.cssSelector("input[data-test='username']#user-name");
    private static final By PASSWORD_INPUT = By.cssSelector("input[data-test='password']#password");
    private static final By LOGIN_BUTTON = By.cssSelector("input[data-test='login-button']#login-button");
    private static final By LOGIN_ERROR_MSG = By.cssSelector("h3[data-test='error']");

    // محددات المنتجات والسلة
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

    // تمييز العنصر بإطار واضح داخل المتصفح لتتبع الخطوة بصرياً
    private void highlightElement(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].style.border='3px dashed #e11d48'; arguments[0].style.backgroundColor='#fff1f2';", element);
        } catch (Exception ignored) {
        }
    }

    // انتظار عنصر حتى يظهر بوضوح وتظليله
    private WebElement waitForVisible(By locator, String elementName) {
        System.out.println("   -> انتظار ظهور: " + elementName);
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        highlightElement(el);
        return el;
    }

    // انتظار عنصر حتى يصبح قابلاً للضغط ثم تظليله والضغط عليه
    private void waitAndClick(By locator, String elementName, long sleepAfterMillis) throws InterruptedException {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        highlightElement(el);
        Thread.sleep(450);
        el.click();
        showStep("تم الضغط على: " + elementName, sleepAfterMillis);
    }

    // كتابة جزء من الاسم تدريجياً داخل حقل لإظهار الخطوة بوضوح
    private void appendNamePartGradually(WebElement input, String fieldName, String partLabel, String partText,
                                         long charSleepMillis) throws InterruptedException {
        highlightElement(input);
        showStep("الآن أكتب [" + partLabel + "] في [" + fieldName + "]: \"" + partText + "\"", 800);
        for (int i = 0; i < partText.length(); i++) {
            input.sendKeys(String.valueOf(partText.charAt(i)));
            Thread.sleep(charSleepMillis);
        }
        showStep("انتهت كتابة [" + partLabel + "] -> القيمة الآن داخل [" + fieldName + "]: \""
                + input.getAttribute("value") + "\"", 950);
    }

    // كتابة نص كامل خطوة بخطوة داخل حقل
    private void typeGradually(By locator, String fieldName, String value, long charSleepMillis)
            throws InterruptedException {
        WebElement input = waitForVisible(locator, fieldName);
        showStep("الآن أكتب في [" + fieldName + "]: \"" + value + "\"", 800);
        input.clear();
        for (int i = 0; i < value.length(); i++) {
            input.sendKeys(String.valueOf(value.charAt(i)));
            Thread.sleep(charSleepMillis);
        }
        showStep("اكتملت الكتابة في [" + fieldName + "] -> القيمة الحالية: \"" + input.getAttribute("value") + "\"", 950);
    }

    // كتابة الاسم الأول + الثاني في حقل First Name خطوة بخطوة
    private void enterFirstAndSecondNameStepByStep() throws InterruptedException {
        WebElement firstInput = waitForVisible(FIRST_NAME_INPUT, "First Name");
        firstInput.clear();
        appendNamePartGradually(firstInput, "First Name", "الاسم الأول", FIRST_NAME, 120);
        firstInput.sendKeys(" ");
        appendNamePartGradually(firstInput, "First Name", "الاسم الثاني", SECOND_NAME, 120);
    }

    // كتابة الاسم الثالث + الرابع في حقل Last Name خطوة بخطوة
    private void enterThirdAndFourthNameStepByStep() throws InterruptedException {
        WebElement lastInput = waitForVisible(LAST_NAME_INPUT, "Last Name");
        lastInput.clear();
        appendNamePartGradually(lastInput, "Last Name", "الاسم الثالث", THIRD_NAME, 120);
        lastInput.sendKeys(" ");
        appendNamePartGradually(lastInput, "Last Name", "الاسم الرابع", FOURTH_NAME, 120);
    }

    // دالة تسجيل الدخول (تُستخدم للدخول الناجح أو الفاشل)
    private void attemptLogin(String username, String password) throws InterruptedException {
        typeGradually(USERNAME_INPUT, "Username", username, 110);
        typeGradually(PASSWORD_INPUT, "Password", password, 110);
        showStep("الآن أضغط على زر Login", 700);
        waitAndClick(LOGIN_BUTTON, "Login Button", 1500);
    }

    // تسجيل الدخول بنجاح -> اختيار عنصر -> فتح السلة -> الضغط على Checkout
    private void loginAndNavigateToCheckoutStepOne() throws InterruptedException {
        attemptLogin("standard_user", "secret_sauce");

        String productsTitle = waitForVisible(PAGE_TITLE, "Products Page Title").getText().trim();
        showStep("تم الدخول بنجاح! نحن الآن في صفحة: " + productsTitle, 1200);

        showStep("الآن أختار عنصراً وأضيفه إلى السلة (Add to Cart)", 800);
        waitAndClick(ADD_TO_CART_BTN, "Add to Cart Button", 1400);

        showStep("الآن أنتقل إلى سلة التسوق (Cart)", 800);
        waitAndClick(CART_LINK, "Shopping Cart Link", 1400);

        showStep("الآن أضغط على زر Checkout للدخول إلى Step One", 800);
        waitAndClick(CHECKOUT_BUTTON, "Checkout Button", 1500);

        String title = waitForVisible(PAGE_TITLE, "Checkout Page Title").getText().trim();
        showStep("وصلنا إلى صفحة Checkout Step One | العنوان: \"" + title + "\"", 1200);
    }

    // تعبئة بيانات الـ Checkout والضغط على Continue خطوة بخطوة
    private void fillCheckoutInfoAndContinue(String firstName, String lastName, String postalCode)
            throws InterruptedException {
        if (FULL_FIRST_FIELD.equals(firstName)) {
            enterFirstAndSecondNameStepByStep();
        } else {
            typeGradually(FIRST_NAME_INPUT, "First Name", firstName, 110);
        }

        if (FULL_LAST_FIELD.equals(lastName)) {
            enterThirdAndFourthNameStepByStep();
        } else {
            typeGradually(LAST_NAME_INPUT, "Last Name", lastName, 110);
        }

        typeGradually(POSTAL_CODE_INPUT, "Postal Code", postalCode, 120);

        showStep("الآن أضغط على زر Continue للانتقال إلى Checkout: Overview", 900);
        waitAndClick(CONTINUE_BUTTON, "Continue Button", 1800);
        showStep("بعد الضغط على Continue | URL الحالي: " + driver.getCurrentUrl(), 1300);
    }

    // إتمام الأوردر بالكامل من صفحة Overview بالضغط على Finish
    private void completeOrderUntilEnd() throws InterruptedException {
        String overviewTitle = waitForVisible(PAGE_TITLE, "Checkout: Overview Title").getText().trim();
        showStep("نحن الآن في صفحة Overview | العنوان: \"" + overviewTitle + "\" | URL: " + driver.getCurrentUrl(), 1500);

        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-two.html"),
                "Expected /checkout-step-two.html, got: " + driver.getCurrentUrl());
        Assert.assertEquals(overviewTitle, "Checkout: Overview",
                "Expected page title 'Checkout: Overview'.");

        showStep("الآن أضغط على زر Finish لإتمام الأوردر باسم: " + FULL_NAME, 1000);
        waitAndClick(FINISH_BUTTON, "Finish Button", 1800);

        String completeHeader = waitForVisible(COMPLETE_HEADER, "Order Complete Header").getText().trim();
        String completeTitle = waitForVisible(PAGE_TITLE, "Checkout: Complete Title").getText().trim();
        showStep("تم إتمام الأوردر بنجاح! | العنوان: \"" + completeTitle + "\" | الرسالة: \""
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
        System.out.println("\n================ [BEFORE METHOD] فتح رابط الموقع ================");
        showStep("أفتح الآن موقع SauceDemo (https://www.saucedemo.com/)", 1300);
        driver.get("https://www.saucedemo.com/");
        showStep("تم فتح الموقع | URL الحالي: " + driver.getCurrentUrl(), 1000);
    }

    @Test(priority = 1)
    public void testCase1_FailedLogin() throws InterruptedException {
        System.out.println("\n--- [TEST CASE 1] محاولة الدخول ببيانات غير صحيحة (ميدخلش) ---");
        attemptLogin("standard_user", "wrong_password_123");

        String errorText = waitForVisible(LOGIN_ERROR_MSG, "Login Error Message").getText().trim();
        showStep("لم يدخل وظهرت رسالة الخطأ: \"" + errorText + "\"", 1400);

        Assert.assertTrue(errorText.contains("Username and password do not match"),
                "Expected login failure message, got: " + errorText);
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/",
                "Expected user to stay on Login page.");
    }

    @Test(priority = 2)
    public void testCase2_SuccessfulLoginAndFullPurchase() throws InterruptedException {
        System.out.println("\n--- [TEST CASE 2] الدخول بنجاح وشراء منتج باسم: " + FULL_NAME + " ---");
        loginAndNavigateToCheckoutStepOne();
        fillCheckoutInfoAndContinue(FULL_FIRST_FIELD, FULL_LAST_FIELD, POSTAL_CODE);
        completeOrderUntilEnd();

        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-complete.html"),
                "Expected /checkout-complete.html after Finish, got: " + driver.getCurrentUrl());
        Assert.assertEquals(driver.findElement(PAGE_TITLE).getText().trim(), "Checkout: Complete!",
                "Expected title 'Checkout: Complete!'.");
        Assert.assertEquals(driver.findElement(COMPLETE_HEADER).getText().trim(), "Thank you for your order!",
                "Expected confirmation 'Thank you for your order!'.");
    }

    @Test(priority = 3)
    public void testCase3_MissingFirstName() throws InterruptedException {
        System.out.println("\n--- [TEST CASE 3] ترك First Name فارغاً ---");
        loginAndNavigateToCheckoutStepOne();
        fillCheckoutInfoAndContinue("", FULL_LAST_FIELD, POSTAL_CODE);

        String actualError = waitForVisible(ERROR_CONTAINER, "Error Message").getText().trim();
        showStep("رسالة الخطأ الظاهرة: \"" + actualError + "\"", 1200);

        Assert.assertEquals(actualError, "Error: First Name is required",
                "Expected 'Error: First Name is required', got: '" + actualError + "'");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"),
                "Expected user to remain on Step One.");
    }

    @Test(priority = 4)
    public void testCase4_InvalidPostalCode() throws InterruptedException {
        System.out.println("\n--- [TEST CASE 4] إدخال حروف ورموز في Postal Code (abc@#$) ---");
        loginAndNavigateToCheckoutStepOne();
        fillCheckoutInfoAndContinue(FULL_FIRST_FIELD, FULL_LAST_FIELD, "abc@#$");

        String actualError = waitForVisible(ERROR_CONTAINER, "Error Message").getText().trim();
        showStep("رسالة الخطأ بعد إدخال Postal Code غير صالح: \"" + actualError + "\"", 1200);

        Assert.assertFalse(actualError.isEmpty(),
                "Bug Detected: Invalid Postal Code 'abc@#$' was accepted without validation error.");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"),
                "Bug Detected: Navigated away from Step One despite invalid Postal Code.");
    }

    private static final By CLOSE_MENU_BTN = By.cssSelector("button#react-burger-cross-btn");

    @Test(priority = 5)
    public void testCase5_SidebarAndCancel() throws InterruptedException {
        System.out.println("\n--- [TEST CASE 5] فتح القائمة الجانبية والضغط على زر Cancel ---");

        try {
            loginAndNavigateToCheckoutStepOne();

            showStep("الخطوة الأخيرة (1): أضغط الآن على زر القائمة الجانبية (Burger Menu)", 900);
            waitAndClick(BURGER_MENU_BTN, "Burger Menu Button", 1500);

            WebElement menuWrap = wait.until(ExpectedConditions.presenceOfElementLocated(MENU_WRAP));
            highlightElement(menuWrap);
            String ariaHidden = menuWrap.getAttribute("aria-hidden");
            boolean aboutVisible = waitForVisible(ABOUT_SIDEBAR_LINK, "About Link").isDisplayed();
            showStep("الخطوة الأخيرة (2): حالة القائمة aria-hidden=" + ariaHidden + " | ظهور رابط About=" + aboutVisible, 1400);

            Assert.assertEquals(ariaHidden, "false",
                    "Bug Detected: Sidebar menu remained hidden (aria-hidden='true').");
            Assert.assertTrue(aboutVisible,
                    "Bug Detected: 'About' link is not visible inside sidebar.");

            showStep("الخطوة الأخيرة (3): أغلق القائمة الجانبية الآن حتى يظهر زر Cancel بوضوح", 900);
            waitAndClick(CLOSE_MENU_BTN, "Close Sidebar Button", 1400);

            showStep("الخطوة الأخيرة (4): أضغط الآن على زر Cancel للعودة إلى السلة (cart.html)", 900);
            waitAndClick(CANCEL_BUTTON, "Cancel Button", 1600);

            String currentUrl = driver.getCurrentUrl();
            showStep("الخطوة الأخيرة (5 - النهاية): فحص الرابط بعد Cancel -> URL الحالي: " + currentUrl, 1600);

            Assert.assertTrue(currentUrl.contains("cart.html"),
                    "FAILED AT LAST STEP: Expected URL to contain 'cart.html' after clicking Cancel, but got: " + currentUrl);

            showStep("نتيجة آخر خطوة: نجحت وعادت إلى صفحة السلة بنجاح!", 1500);

        } catch (AssertionError | Exception e) {
            System.out.println("==========================================================");
            System.out.println("[LAST STEP FAILURE DETAIL] توقفت/فشلت آخر خطوة في testCase5!");
            System.out.println("Current URL : " + driver.getCurrentUrl());
            System.out.println("Reason      : " + e.getMessage());
            System.out.println("==========================================================");
            Thread.sleep(1500);
            throw e;
        }
    }
    }