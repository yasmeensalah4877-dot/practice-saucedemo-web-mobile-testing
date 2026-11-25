package com.aos.web.tests.feature;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * =========================================================================
 * BUG REPORT (تقرير الأخطاء المرصودة على موقع SauceDemo)
 * =========================================================================
 * Bug ID   : BUG-CART-001
 * Title    : إمكانية الضغط على زر Checkout والانتقال لصفحة الدفع والسلة فارغة
 * Severity : Medium
 * Steps    :
 *   1. تسجيل الدخول بحساب standard_user.
 *   2. الدخول إلى صفحة السلة (Your Cart) وهي فارغة.
 *   3. الضغط على زر Checkout (#checkout).
 * Expected : بقاء المستخدم في صفحة السلة (/cart.html) أو منع إتمام الشراء لعدم وجود منتجات.
 * Actual   : يسمح النظام بالانتقال إلى صفحة الخطوة الأولى للدفع (/checkout-step-one.html) رغم أن السلة فارغة.
 * =========================================================================
 */

public class baraa {

    private WebDriver driver;
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private final List<String> bugLogs = new ArrayList<>();

    private void logStep(String stepMessage) {
        System.out.println("   -> [STEP] " + stepMessage);
    }

    private void recordPass(String testName) {
        totalTests++;
        passedTests++;
        System.out.println("[PASS] " + testName + " نجح بالكامل.\n");
    }

    private void recordFail(String testName, String reason) {
        totalTests++;
        failedTests++;
        String bugEntry = testName + " | السبب: " + reason;
        bugLogs.add(bugEntry);
        System.out.println("[FAIL / BUG DETECTED] " + bugEntry + "\n");
    }

    // محاولة إغلاق نافذة Google Password Manager ("Change your password" / OK) إذا ظهرت
    private void dismissPasswordPopupIfPresent() {
        try {
            // 1. محاولة الضغط على Escape لإغلاق النافذة المنبثقة
            new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            Thread.sleep(300);

            // 2. إذا كان هناك Alert قياسي في المتصفح
            try {
                driver.switchTo().alert().accept();
                logStep("تم الضغط على OK في نافذة التنبيه");
            } catch (Exception ignoredAlert) {
                // لا يوجد Alert قياسي، نكمل بشكل طبيعي
            }
        } catch (Exception ignored) {
            // إذا لم تظهر النافذة لا يحدث أي خطأ ويكمل الكود عادي
        }
    }

    @BeforeClass
    public void beforeClass() throws InterruptedException {
        System.out.println("==========================================");
        System.out.println("بدء تشغيل الكلاس: baraa (@BeforeClass)");
        System.out.println("==========================================");
        logStep("تجهيز إعدادات Chrome لتقليل نوافذ حفظ وفحص كلمات المرور");

        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-features=PasswordLeakDetection,SafetyCheck");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        Thread.sleep(1000);
    }

    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        System.out.println("------------------------------------------");
        System.out.println("الدخول إلى (@BeforeMethod) - تجهيز جلسة الدخول:");

        logStep("الخطوة 1: فتح الرابط https://www.saucedemo.com");
        driver.navigate().to("https://www.saucedemo.com");
        Thread.sleep(1000);

        logStep("الخطوة 2: مسح Cookies وتحديث الصفحة لضمان حالة نظيفة");
        driver.manage().deleteAllCookies();
        driver.navigate().refresh();
        Thread.sleep(1500);

        logStep("الخطوة 3: إدخال اسم المستخدم (standard_user)");
        driver.findElement(By.cssSelector("#user-name")).sendKeys("standard_user");
        Thread.sleep(500);

        logStep("الخطوة 4: إدخال كلمة المرور (secret_sauce)");
        driver.findElement(By.cssSelector("#password")).sendKeys("secret_sauce");
        Thread.sleep(500);

        logStep("الخطوة 5: الضغط على زر Login (#login-button)");
        driver.findElement(By.cssSelector("#login-button")).click();
        Thread.sleep(1200);

        logStep("الخطوة 6: التحقق من نافذة Change your password / OK ومعالجتها إن ظهرت");
        dismissPasswordPopupIfPresent();
        Thread.sleep(500);

        logStep("الخطوة 7: التحقق من الدخول إلى صفحة المنتجات (" + driver.getCurrentUrl() + ")");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "فشل تسجيل الدخول والوصول لصفحة المنتجات");
        System.out.println("تم الدخول بنجاح إلى النظام والبدء في الاختبار:");
    }

    // TC1: التحقق من فتح صفحة السلة وعناصرها الأساسية
    @Test(priority = 1)
    public void testCase1_OpenCartPage() throws InterruptedException {
        String testName = "TC1 - فتح صفحة السلة والتحقق من العناوين والحاوية";
        System.out.println("تشغيل: " + testName);
        try {
            dismissPasswordPopupIfPresent();

            logStep("الضغط على أيقونة السلة");
            driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
            Thread.sleep(1000);

            logStep("التحقق من رابط صفحة السلة");
            Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/cart.html", "رابط صفحة السلة غير صحيح");

            logStep("التحقق من عنوان الصفحة Your Cart");
            Assert.assertEquals(driver.findElement(By.cssSelector("[data-test='title']")).getText(), "Your Cart", "عنوان السلة غير صحيح");

            logStep("التحقق من ظهور حاوية السلة وعناوين QTY و Description");
            Assert.assertTrue(driver.findElement(By.cssSelector("[data-test='cart-contents-container']")).isDisplayed(), "حاوية السلة غير ظاهرة");
            Assert.assertEquals(driver.findElement(By.cssSelector("[data-test='cart-quantity-label']")).getText(), "QTY");
            Assert.assertEquals(driver.findElement(By.cssSelector("[data-test='cart-desc-label']")).getText(), "Description");
            Thread.sleep(500);

            recordPass(testName);
        } catch (AssertionError e) {
            recordFail(testName, e.getMessage());
            throw e;
        }
    }

    // TC2: التحقق من تفاصيل المنتج وعداد السلة بعد إضافة منتج
    @Test(priority = 2)
    public void testCase2_AddProductAndVerifyDetails() throws InterruptedException {
        String testName = "TC2 - إضافة منتج والتحقق من الاسم والوصف والسعر والعداد";
        System.out.println("تشغيل: " + testName);
        try {
            dismissPasswordPopupIfPresent();

            logStep("التقاط اسم ووصف وسعر منتج Sauce Labs Backpack قبل الإضافة");
            WebElement product = driver.findElement(By.cssSelector(".inventory_item:has(#add-to-cart-sauce-labs-backpack)"));
            String expectedName = product.findElement(By.cssSelector(".inventory_item_name")).getText();
            String expectedDescription = product.findElement(By.cssSelector(".inventory_item_desc")).getText();
            String expectedPrice = product.findElement(By.cssSelector(".inventory_item_price")).getText();

            logStep("الضغط على زر Add to cart للمنتج");
            driver.findElement(By.cssSelector("#add-to-cart-sauce-labs-backpack")).click();
            Thread.sleep(800);

            logStep("التحقق من ظهور العداد 1 على السلة");
            Assert.assertEquals(driver.findElement(By.cssSelector(".shopping_cart_badge")).getText(), "1", "عداد السلة يجب أن يساوي 1");

            logStep("الانتقال إلى صفحة السلة ومطابقة بيانات المنتج");
            driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
            Thread.sleep(1000);

            Assert.assertEquals(driver.findElements(By.cssSelector(".cart_item")).size(), 1, "عدد العناصر في السلة يجب أن يكون 1");
            Assert.assertEquals(driver.findElement(By.cssSelector(".inventory_item_name")).getText(), expectedName, "اسم المنتج غير مطابق");
            Assert.assertEquals(driver.findElement(By.cssSelector(".inventory_item_desc")).getText(), expectedDescription, "وصف المنتج غير مطابق");
            Assert.assertEquals(driver.findElement(By.cssSelector(".inventory_item_price")).getText(), expectedPrice, "سعر المنتج غير مطابق");
            Assert.assertEquals(driver.findElement(By.cssSelector(".cart_quantity")).getText(), "1", "كمية المنتج غير مطابقة");

            recordPass(testName);
        } catch (AssertionError e) {
            recordFail(testName, e.getMessage());
            throw e;
        }
    }

    // TC3: التحقق من حذف منتج مع بقاء الآخر ثم إفراغ السلة
    @Test(priority = 3)
    public void testCase3_RemoveProductPartialAndFull() throws InterruptedException {
        String testName = "TC3 - حذف منتج جزئيًا مع بقاء الآخر ثم تفريغ السلة";
        System.out.println("تشغيل: " + testName);
        try {
            dismissPasswordPopupIfPresent();

            logStep("إضافة منتجين (Backpack و Bike Light) والتحقق من العداد 2");
            driver.findElement(By.cssSelector("#add-to-cart-sauce-labs-backpack")).click();
            driver.findElement(By.cssSelector("#add-to-cart-sauce-labs-bike-light")).click();
            Thread.sleep(800);
            Assert.assertEquals(driver.findElement(By.cssSelector(".shopping_cart_badge")).getText(), "2");

            logStep("الدخول للسلة وحذف المنتج الأول (Backpack)");
            driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
            Thread.sleep(800);
            driver.findElement(By.cssSelector("#remove-sauce-labs-backpack")).click();
            Thread.sleep(800);

            logStep("التحقق من بقاء Bike Light وتحديث العداد إلى 1");
            Assert.assertEquals(driver.findElements(By.cssSelector(".cart_item")).size(), 1, "يجب أن يتبقى عنصر واحد");
            Assert.assertEquals(driver.findElement(By.cssSelector(".inventory_item_name")).getText(), "Sauce Labs Bike Light", "المنتج المتبقي غير صحيح");
            Assert.assertEquals(driver.findElement(By.cssSelector(".shopping_cart_badge")).getText(), "1", "العداد لم يتحدث إلى 1");

            logStep("حذف المنتج الثاني والتحقق من أن السلة فارغة واختفاء العداد");
            driver.findElement(By.cssSelector("#remove-sauce-labs-bike-light")).click();
            Thread.sleep(800);
            Assert.assertTrue(driver.findElements(By.cssSelector(".cart_item")).isEmpty(), "السلة يجب أن تصبح فارغة");
            Assert.assertTrue(driver.findElements(By.cssSelector(".shopping_cart_badge")).isEmpty(), "عداد السلة يجب أن يختفي");

            recordPass(testName);
        } catch (AssertionError e) {
            recordFail(testName, e.getMessage());
            throw e;
        }
    }

    // TC4: التحقق من زر Continue Shopping وبقاء السلة بعد Refresh
    @Test(priority = 4)
    public void testCase4_ContinueShoppingAndRefreshPersistence() throws InterruptedException {
        String testName = "TC4 - متابعة التسوق وبقاء محتويات السلة بعد Refresh";
        System.out.println("تشغيل: " + testName);
        try {
            dismissPasswordPopupIfPresent();

            logStep("إضافة منتج والانتقال إلى السلة ثم الضغط على Continue Shopping");
            driver.findElement(By.cssSelector("#add-to-cart-sauce-labs-backpack")).click();
            driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
            Thread.sleep(800);
            driver.findElement(By.cssSelector("#continue-shopping")).click();
            Thread.sleep(1000);

            logStep("التحقق من العودة إلى inventory.html وبقاء العداد 1");
            Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/inventory.html", "فشل الرجوع لصفحة المنتجات");
            Assert.assertEquals(driver.findElement(By.cssSelector(".shopping_cart_badge")).getText(), "1", "لم يتم الاحتفاظ بعداد السلة");

            logStep("العودة للسلة وعمل Refresh للصفحة للتحقق من بقاء المنتج");
            driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
            Thread.sleep(800);
            driver.navigate().refresh();
            Thread.sleep(1200);

            Assert.assertEquals(driver.findElements(By.cssSelector(".cart_item")).size(), 1, "لم يتم الاحتفاظ بعناصر السلة بعد Refresh");
            Assert.assertEquals(driver.findElement(By.cssSelector(".inventory_item_name")).getText(), "Sauce Labs Backpack", "المنتج المحفوظ غير مطابق");

            recordPass(testName);
        } catch (AssertionError e) {
            recordFail(testName, e.getMessage());
            throw e;
        }
    }

    // TC5: التحقق من منع إتمام الشراء (Checkout) والسلة فارغة
    @Test(priority = 5)
    public void testCase5_CheckoutWithEmptyCart() throws InterruptedException {
        String testName = "TC5 - محاولة إتمام الشراء والسلة فارغة (Negative / Bug Check)";
        System.out.println("تشغيل: " + testName);
        try {
            dismissPasswordPopupIfPresent();

            logStep("الانتقال إلى السلة والتأكد أنها فارغة");
            driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
            Thread.sleep(800);
            Assert.assertTrue(driver.findElements(By.cssSelector(".cart_item")).isEmpty(), "السلة يجب أن تكون فارغة قبل الاختبار");

            logStep("الضغط على زر Checkout (#checkout) إذا كان مفعّلًا");
            WebElement checkoutButton = driver.findElement(By.cssSelector("#checkout"));
            if (checkoutButton.isEnabled()) {
                checkoutButton.click();
            }
            Thread.sleep(1000);

            logStep("التحقق من البقاء في صفحة السلة وعدم السماح بالانتقال للدفع");
            Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/cart.html",
                    "BUG-CART-001: Checkout is allowed with an empty cart");

            recordPass(testName);
        } catch (AssertionError e) {
            recordFail(testName, e.getMessage());
            throw e;
        }
    }

    @AfterMethod
    public void afterMethod() throws InterruptedException {
        logStep("تنظيف Cookies بعد الاختبار (@AfterMethod)");
        driver.manage().deleteAllCookies();
        Thread.sleep(500);
    }

    @AfterClass
    public void afterClass() {
        System.out.println("\n==========================================");
        System.out.println("SUMMARY REPORT (ملخص نتائج التنفيذ)");
        System.out.println("==========================================");
        System.out.println("Total Test Cases : " + totalTests);
        System.out.println("Passed           : " + passedTests);
        System.out.println("Failed / Bugs    : " + failedTests);

        System.out.println("\n--- BUG REPORT DETAILS ---");
        if (bugLogs.isEmpty()) {
            System.out.println("لا توجد أخطاء مسجلة.");
        } else {
            for (String bug : bugLogs) {
                System.out.println("* " + bug);
            }
        }
        System.out.println("==========================================\n");

        if (driver != null) {
            driver.quit();
        }
    }
}