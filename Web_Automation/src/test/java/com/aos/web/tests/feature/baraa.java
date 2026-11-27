package com.aos.web.tests.feature;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
- =========================================================================
- BUG REPORT (تقرير الأخطاء المرصودة على موقع SauceDemo)
- =========================================================================
- Bug ID   : BUG-CART-001
- Title    : إمكانية الضغط على زر Checkout والانتقال لصفحة الدفع والسلة فارغة
- Severity : Medium
- Steps    :
-   1. تسجيل الدخول بحساب standard_user.
-   2. الدخول إلى صفحة السلة (Your Cart) وهي فارغة.
-   3. الضغط على زر Checkout (#checkout).
- Expected : بقاء المستخدم في صفحة السلة (/cart.html) أو منع إتمام الشراء لعدم وجود منتجات.
- Actual   : يسمح النظام بالانتقال إلى صفحة الخطوة الأولى للدفع (/checkout-step-one.html) رغم أن السلة فارغة.
- =========================================================================
 */

public class baraa {

    private WebDriver driver;
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private int stepCounter = 0;
    private final List<String> bugLogs = new ArrayList<>();

    // مدة الانتظار بين الخطوات (3 ثواني لكل خطوة لتوضيح المتابعة البصرية)
    private static final int STEP_PAUSE = 3000;

    private void pause(int ms) throws InterruptedException {
        Thread.sleep(ms);
    }

    // عرض رقم واسم الخطوة في الـ Console وفي شريط علوي/سفلي داخل صفحة المتصفح
    private void logStep(String stepMessage) {
        stepCounter++;
        String formatted = "#" + stepCounter + " | " + stepMessage;
        System.out.println("   -> [STEP " + formatted + "]");
        showStepBanner(formatted);
    }

    private void showStepBanner(String text) {
        try {
            if (driver == null) return;
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String safeText = text.replace("'", "\\'");
            String script =
                    "var b = document.getElementById('selenium-step-banner');" +
                            "if (!b) {" +
                            "  b = document.createElement('div');" +
                            "  b.id = 'selenium-step-banner';" +
                            "  b.style.position = 'fixed';" +
                            "  b.style.bottom = '12px';" +
                            "  b.style.right = '12px';" +
                            "  b.style.zIndex = '999999';" +
                            "  b.style.backgroundColor = '#1e293b';" +
                            "  b.style.color = '#ffffff';" +
                            "  b.style.padding = '10px 16px';" +
                            "  b.style.borderRadius = '8px';" +
                            "  b.style.borderLeft = '5px solid #f59e0b';" +
                            "  b.style.fontFamily = 'Arial, sans-serif';" +
                            "  b.style.fontSize = '14px';" +
                            "  b.style.fontWeight = 'bold';" +
                            "  b.style.boxShadow = '0 4px 12px rgba(0,0,0,0.35)';" +
                            "  b.style.maxWidth = '85vw';" +
                            "  b.dir = 'rtl';" +
                            "  document.body.appendChild(b);" +
                            "}" +
                            "b.textContent = '" + safeText + "';";
            js.executeScript(script);
        } catch (Exception ignored) {
        }
    }

    // تمييز العنصر بصريًا على الصفحة قبل الضغط أو الكتابة عليه
    private void highlight(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            js.executeScript("arguments[0].style.border='4px solid #dc2626'; arguments[0].style.backgroundColor='#fef08a'; arguments[0].style.color='#000000';", element);
        } catch (Exception ignored) {
        }
    }

    private void recordPass(String testName) {
        totalTests++;
        passedTests++;
        System.out.println("[PASS] " + testName + " نجح بالكامل.\n");
        showStepBanner("PASS: " + testName);
    }

    private void recordFail(String testName, String reason) {
        totalTests++;
        failedTests++;
        String bugEntry = testName + " | السبب: " + reason;
        bugLogs.add(bugEntry);
        System.out.println("[FAIL / BUG DETECTED] " + bugEntry + "\n");
        showStepBanner("BUG / FAIL: " + testName);
    }

    // محاولة إغلاق نافذة Google Password Manager ("Change your password" / OK) إذا ظهرت
    private void dismissPasswordPopupIfPresent() {
        try {
            new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            Thread.sleep(600);

            try {
                driver.switchTo().alert().accept();
                logStep("تم الضغط على OK في نافذة التنبيه");
            } catch (Exception ignoredAlert) {
            }
        } catch (Exception ignored) {
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
        pause(STEP_PAUSE);
    }

    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        stepCounter = 0;
        System.out.println("------------------------------------------");
        System.out.println("الدخول إلى (@BeforeMethod) - تجهيز جلسة الدخول خطوة بخطوة:");

        logStep("الخطوة 1: فتح الرابط https://www.saucedemo.com");
        driver.navigate().to("https://www.saucedemo.com");
        pause(STEP_PAUSE);

        logStep("الخطوة 2: مسح Cookies وتحديث الصفحة لضمان حالة نظيفة");
        driver.manage().deleteAllCookies();
        driver.navigate().refresh();
        pause(STEP_PAUSE);

        logStep("الخطوة 3: إدخال اسم المستخدم (standard_user)");
        WebElement userField = driver.findElement(By.cssSelector("#user-name"));
        highlight(userField);
        userField.sendKeys("standard_user");
        pause(STEP_PAUSE);

        logStep("الخطوة 4: إدخال كلمة المرور (secret_sauce)");
        WebElement passField = driver.findElement(By.cssSelector("#password"));
        highlight(passField);
        passField.sendKeys("secret_sauce");
        pause(STEP_PAUSE);

        logStep("الخطوة 5: الضغط على زر Login (#login-button)");
        WebElement loginBtn = driver.findElement(By.cssSelector("#login-button"));
        highlight(loginBtn);
        pause(1200);
        loginBtn.click();
        pause(STEP_PAUSE);

        logStep("الخطوة 6: التحقق من نافذة Change your password / OK ومعالجتها إن ظهرت");
        dismissPasswordPopupIfPresent();
        pause(1500);

        logStep("الخطوة 7: التحقق من الدخول إلى صفحة المنتجات (" + driver.getCurrentUrl() + ")");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "فشل تسجيل الدخول والوصول لصفحة المنتجات");
        System.out.println("تم الدخول بنجاح إلى النظام والبدء في الاختبار:");
        pause(STEP_PAUSE);
    }

    // TC1: التحقق من فتح صفحة السلة وعناصرها الأساسية
    @Test(priority = 1)
    public void testCase1_OpenCartPage() throws InterruptedException {
        String testName = "TC1 - فتح صفحة السلة والتحقق من العناوين والحاوية";
        System.out.println("تشغيل: " + testName);
        try {
            dismissPasswordPopupIfPresent();

            logStep("TC1: الضغط على أيقونة السلة");
            WebElement cartLink = driver.findElement(By.cssSelector("[data-test='shopping-cart-link']"));
            highlight(cartLink);
            pause(1200);
            cartLink.click();
            pause(STEP_PAUSE);

            logStep("TC1: التحقق من رابط صفحة السلة");
            Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/cart.html", "رابط صفحة السلة غير صحيح");
            pause(STEP_PAUSE);

            logStep("TC1: التحقق من عنوان الصفحة Your Cart");
            WebElement pageTitle = driver.findElement(By.cssSelector("[data-test='title']"));
            highlight(pageTitle);
            Assert.assertEquals(pageTitle.getText(), "Your Cart", "عنوان السلة غير صحيح");
            pause(STEP_PAUSE);

            logStep("TC1: التحقق من ظهور حاوية السلة وعناوين QTY و Description");
            WebElement cartContainer = driver.findElement(By.cssSelector("[data-test='cart-contents-container']"));
            highlight(cartContainer);
            Assert.assertTrue(cartContainer.isDisplayed(), "حاوية السلة غير ظاهرة");
            pause(STEP_PAUSE);

            logStep("TC1: التحقق من نص عمود QTY وعمود Description");
            WebElement qtyLabel = driver.findElement(By.cssSelector("[data-test='cart-quantity-label']"));
            WebElement descLabel = driver.findElement(By.cssSelector("[data-test='cart-desc-label']"));
            highlight(qtyLabel);
            highlight(descLabel);
            Assert.assertEquals(qtyLabel.getText(), "QTY");
            Assert.assertEquals(descLabel.getText(), "Description");
            pause(STEP_PAUSE);

            recordPass(testName);
            pause(STEP_PAUSE);
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

            logStep("TC2: التقاط اسم ووصف وسعر منتج Sauce Labs Backpack قبل الإضافة");
            WebElement product = driver.findElement(By.cssSelector(".inventory_item:has(#add-to-cart-sauce-labs-backpack)"));
            highlight(product);
            String expectedName = product.findElement(By.cssSelector(".inventory_item_name")).getText();
            String expectedDescription = product.findElement(By.cssSelector(".inventory_item_desc")).getText();
            String expectedPrice = product.findElement(By.cssSelector(".inventory_item_price")).getText();
            pause(STEP_PAUSE);

            logStep("TC2: الضغط على زر Add to cart للمنتج");
            WebElement addBtn = driver.findElement(By.cssSelector("#add-to-cart-sauce-labs-backpack"));
            highlight(addBtn);
            pause(1200);
            addBtn.click();
            pause(STEP_PAUSE);

            logStep("TC2: التحقق من ظهور العداد 1 على السلة");
            WebElement badge = driver.findElement(By.cssSelector(".shopping_cart_badge"));
            highlight(badge);
            Assert.assertEquals(badge.getText(), "1", "عداد السلة يجب أن يساوي 1");
            pause(STEP_PAUSE);

            logStep("TC2: الانتقال إلى صفحة السلة ومطابقة بيانات المنتج");
            WebElement cartLink = driver.findElement(By.cssSelector("[data-test='shopping-cart-link']"));
            highlight(cartLink);
            pause(1200);
            cartLink.click();
            pause(STEP_PAUSE);

            logStep("TC2: التحقق من عدد العناصر وبيانات المنتج داخل السلة");
            WebElement cartItem = driver.findElement(By.cssSelector(".cart_item"));
            highlight(cartItem);
            Assert.assertEquals(driver.findElements(By.cssSelector(".cart_item")).size(), 1, "عدد العناصر في السلة يجب أن يكون 1");
            Assert.assertEquals(driver.findElement(By.cssSelector(".inventory_item_name")).getText(), expectedName, "اسم المنتج غير مطابق");
            Assert.assertEquals(driver.findElement(By.cssSelector(".inventory_item_desc")).getText(), expectedDescription, "وصف المنتج غير مطابق");
            Assert.assertEquals(driver.findElement(By.cssSelector(".inventory_item_price")).getText(), expectedPrice, "سعر المنتج غير مطابق");
            Assert.assertEquals(driver.findElement(By.cssSelector(".cart_quantity")).getText(), "1", "كمية المنتج غير مطابقة");
            pause(STEP_PAUSE);

            recordPass(testName);
            pause(STEP_PAUSE);
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

            logStep("TC3: إضافة منتج Backpack");
            WebElement addBackpack = driver.findElement(By.cssSelector("#add-to-cart-sauce-labs-backpack"));
            highlight(addBackpack);
            pause(1200);
            addBackpack.click();
            pause(STEP_PAUSE);

            logStep("TC3: إضافة منتج Bike Light والتحقق من أن العداد أصبح 2");
            WebElement addBikeLight = driver.findElement(By.cssSelector("#add-to-cart-sauce-labs-bike-light"));
            highlight(addBikeLight);
            pause(1200);
            addBikeLight.click();
            pause(STEP_PAUSE);
            Assert.assertEquals(driver.findElement(By.cssSelector(".shopping_cart_badge")).getText(), "2");

            logStep("TC3: الدخول للسلة وحذف المنتج الأول (Backpack)");
            driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
            pause(STEP_PAUSE);
            WebElement removeBackpack = driver.findElement(By.cssSelector("#remove-sauce-labs-backpack"));
            highlight(removeBackpack);
            pause(1200);
            removeBackpack.click();
            pause(STEP_PAUSE);

            logStep("TC3: التحقق من بقاء Bike Light وتحديث العداد إلى 1");
            highlight(driver.findElement(By.cssSelector(".cart_item")));
            Assert.assertEquals(driver.findElements(By.cssSelector(".cart_item")).size(), 1, "يجب أن يتبقى عنصر واحد");
            Assert.assertEquals(driver.findElement(By.cssSelector(".inventory_item_name")).getText(), "Sauce Labs Bike Light", "المنتج المتبقي غير صحيح");
            Assert.assertEquals(driver.findElement(By.cssSelector(".shopping_cart_badge")).getText(), "1", "العداد لم يتحدث إلى 1");
            pause(STEP_PAUSE);

            logStep("TC3: حذف المنتج الثاني والتحقق من أن السلة فارغة واختفاء العداد");
            WebElement removeBikeLight = driver.findElement(By.cssSelector("#remove-sauce-labs-bike-light"));
            highlight(removeBikeLight);
            pause(1200);
            removeBikeLight.click();
            pause(STEP_PAUSE);
            Assert.assertTrue(driver.findElements(By.cssSelector(".cart_item")).isEmpty(), "السلة يجب أن تصبح فارغة");
            Assert.assertTrue(driver.findElements(By.cssSelector(".shopping_cart_badge")).isEmpty(), "عداد السلة يجب أن يختفي");
            pause(STEP_PAUSE);

            recordPass(testName);
            pause(STEP_PAUSE);
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

            logStep("TC4: إضافة منتج والانتقال إلى السلة");
            WebElement addBackpack = driver.findElement(By.cssSelector("#add-to-cart-sauce-labs-backpack"));
            highlight(addBackpack);
            pause(1200);
            addBackpack.click();
            pause(STEP_PAUSE);

            driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
            pause(STEP_PAUSE);

            logStep("TC4: الضغط على زر Continue Shopping");
            WebElement continueBtn = driver.findElement(By.cssSelector("#continue-shopping"));
            highlight(continueBtn);
            pause(1200);
            continueBtn.click();
            pause(STEP_PAUSE);

            logStep("TC4: التحقق من العودة إلى inventory.html وبقاء العداد 1");
            highlight(driver.findElement(By.cssSelector(".shopping_cart_badge")));
            Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/inventory.html", "فشل الرجوع لصفحة المنتجات");
            Assert.assertEquals(driver.findElement(By.cssSelector(".shopping_cart_badge")).getText(), "1", "لم يتم الاحتفاظ بعداد السلة");
            pause(STEP_PAUSE);

            logStep("TC4: العودة للسلة وعمل Refresh للصفحة للتحقق من بقاء المنتج");
            driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
            pause(STEP_PAUSE);
            driver.navigate().refresh();
            pause(STEP_PAUSE);

            logStep("TC4: التحقق من بقاء المنتج بعد Refresh");
            highlight(driver.findElement(By.cssSelector(".cart_item")));
            Assert.assertEquals(driver.findElements(By.cssSelector(".cart_item")).size(), 1, "لم يتم الاحتفاظ بعناصر السلة بعد Refresh");
            Assert.assertEquals(driver.findElement(By.cssSelector(".inventory_item_name")).getText(), "Sauce Labs Backpack", "المنتج المحفوظ غير مطابق");
            pause(STEP_PAUSE);

            recordPass(testName);
            pause(STEP_PAUSE);
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

            logStep("TC5: الانتقال إلى السلة والتأكد أنها فارغة");
            driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
            pause(STEP_PAUSE);
            Assert.assertTrue(driver.findElements(By.cssSelector(".cart_item")).isEmpty(), "السلة يجب أن تكون فارغة قبل الاختبار");
            pause(STEP_PAUSE);

            logStep("TC5: الضغط على زر Checkout (#checkout) إذا كان مفعّلًا");
            WebElement checkoutButton = driver.findElement(By.cssSelector("#checkout"));
            highlight(checkoutButton);
            pause(1500);
            if (checkoutButton.isEnabled()) {
                checkoutButton.click();
            }
            pause(STEP_PAUSE);

            logStep("TC5: التحقق من البقاء في صفحة السلة وعدم السماح بالانتقال للدفع");
            Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/cart.html",
                    "BUG-CART-001: Checkout is allowed with an empty cart");
            pause(STEP_PAUSE);

            recordPass(testName);
            pause(STEP_PAUSE);
        } catch (AssertionError e) {
            recordFail(testName, e.getMessage());
            throw e;
        }
    }

    @AfterMethod
    public void afterMethod() throws InterruptedException {
        logStep("تنظيف Cookies بعد الاختبار (@AfterMethod)");
        driver.manage().deleteAllCookies();
        pause(1500);
    }

    @AfterClass
    public void afterClass() throws InterruptedException {
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

        showStepBanner("انتهى التنفيذ (15 ثانية) | الإجمالي: " + totalTests + " | نجح: " + passedTests + " | أخطاء/فشل: " + failedTests);
        pause(15000);

        if (driver != null) {
            driver.quit();
        }
    }
}