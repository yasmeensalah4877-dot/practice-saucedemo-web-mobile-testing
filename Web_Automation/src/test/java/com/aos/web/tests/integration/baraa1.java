package com.swaglabs.qa.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import java.util.List;

public class baraa1 {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://www.saucedemo.com/";

    // Robust CSS Selectors extracted from provided HTML
    private static final By HTML_LANG_EN = By.cssSelector("html[lang='en']");
    private static final By META_DESCRIPTION = By.cssSelector("meta[name='description'][content='Sauce Labs Swag Labs app']");
    private static final By FAVICON_LINK = By.cssSelector("link[rel='icon'][href='/favicon.ico']");
    private static final By MODULE_BUNDLE_SCRIPT = By.cssSelector("script[type='module'][src^='/assets/index-']");
    private static final By MAIN_STYLESHEET_LINK = By.cssSelector("link[rel='stylesheet'][href^='/assets/index-']");
    private static final By BODY_ELEMENT = By.cssSelector("body");

    private void logStep(String message) {
        System.out.println("[TEST STEP] " + message);
        Reporter.log(message, true);
    }

    @BeforeMethod
    public void setUp() {
        logStep("Initializing ChromeDriver in visible mode...");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterMethod
    public void tearDown() {
        logStep("Closing browser session...");
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * TC-01 (Positive): Verify document language, title, and core metadata are properly defined.
     */
    @Test(priority = 1, description = "Verify HTML lang attribute, title, and description metadata")
    public void verifyDocumentMetadataAndLanguage() {
        logStep("Step 1: Navigate to " + BASE_URL);
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(BODY_ELEMENT));

        logStep("Step 2: Verify <html lang='en'> attribute is present");
        List<WebElement> htmlLangMatches = driver.findElements(HTML_LANG_EN);
        Assert.assertFalse(htmlLangMatches.isEmpty(), "Expected <html lang='en'> to be present.");

        logStep("Step 3: Verify page title is 'Swag Labs'");
        String actualTitle = driver.getTitle();
        Assert.assertEquals(actualTitle, "Swag Labs", "Page title does not match expected 'Swag Labs'.");

        logStep("Step 4: Verify meta description content");
        WebElement descriptionMeta = driver.findElement(META_DESCRIPTION);
        String content = descriptionMeta.getAttribute("content");
        Assert.assertEquals(content, "Sauce Labs Swag Labs app", "Meta description content mismatch.");
    }

    /**
     * TC-02 (Positive / Asset Integrity): Verify module JS bundle and stylesheet are referenced and loaded.
     */
    @Test(priority = 2, description = "Verify SPA module script and stylesheet assets load properly")
    public void verifyModuleScriptAndStylesheetAreLoaded() {
        logStep("Step 1: Navigate to " + BASE_URL);
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(BODY_ELEMENT));

        logStep("Step 2: Verify module script source");
        WebElement moduleScript = driver.findElement(MODULE_BUNDLE_SCRIPT);
        String scriptSrc = moduleScript.getAttribute("src");
        Assert.assertTrue(scriptSrc != null && scriptSrc.contains("/assets/index-"),
                "Module script src is missing or malformed: " + scriptSrc);

        logStep("Step 3: Verify main stylesheet href");
        WebElement mainCss = driver.findElement(MAIN_STYLESHEET_LINK);
        String cssHref = mainCss.getAttribute("href");
        Assert.assertTrue(cssHref != null && cssHref.contains("/assets/index-"),
                "Main stylesheet href is missing or malformed: " + cssHref);

        logStep("Step 4: Verify module script elements are present and active in DOM");
        Boolean isScriptComplete = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "const scripts = Array.from(document.querySelectorAll(\"script[type='module'][src*='/assets/index-']\"));" +
                        "return scripts.length > 0 && scripts.every(s => s.src && s.src.length > 0);"
        );
        Assert.assertTrue(Boolean.TRUE.equals(isScriptComplete), "Expected module script element to be present and active.");
    }

    /**
     * TC-03 (Bug Detection / SPA Hydration): Catch blank/unmounted body if JS bundle fails to render UI.
     */
    @Test(priority = 3, description = "Verify SPA renders visible content inside body rather than remaining blank")
    public void verifySpaRendersVisibleContentInBody() {
        logStep("Step 1: Navigate to " + BASE_URL);
        driver.get(BASE_URL);

        logStep("Step 2: Wait until body renders non-empty visible text");
        wait.until(d -> {
            String bodyText = d.findElement(BODY_ELEMENT).getText().trim();
            return !bodyText.isEmpty();
        });

        logStep("Step 3: Assert body text is not blank");
        String renderedText = driver.findElement(BODY_ELEMENT).getText().trim();
        Assert.assertFalse(renderedText.isEmpty(),
                "BUG DETECTED: <body> rendered no visible text; SPA bundle may have failed to mount or execute.");
    }

    /**
     * TC-04 (Negative / Deep-Link Routing): Verify SPA redirect script handles encoded deep links cleanly.
     */
    @Test(priority = 4, description = "Verify encoded slash deep-link URLs decode cleanly")
    public void verifySpaEncodedDeepLinkHandling() {
        String encodedDeepLink = BASE_URL + "?/inventory~and~item_id=4";
        logStep("Step 1: Navigate to encoded deep link " + encodedDeepLink);
        driver.get(encodedDeepLink);

        logStep("Step 2: Wait for body to become visible");
        wait.until(ExpectedConditions.visibilityOfElementLocated(BODY_ELEMENT));

        logStep("Step 3: Verify URL has no leftover '?/' or '~and~' tokens");
        String currentUrl = driver.getCurrentUrl();
        Assert.assertFalse(currentUrl.contains("?/"),
                "BUG DETECTED: SPA redirect query prefix '?/' was not normalized in URL: " + currentUrl);
        Assert.assertFalse(currentUrl.contains("~and~"),
                "BUG DETECTED: Encoded '~and~' delimiter was not decoded back to '&' in URL: " + currentUrl);
    }

    /**
     * TC-05 (Negative / Favicon & Resource Check): Verify favicon link is valid and page has no obvious error text.
     */
    @Test(priority = 5, description = "Verify favicon link attribute and ensure no unhandled error screen is shown")
    public void verifyFaviconAndNoUnhandledErrorState() {
        logStep("Step 1: Navigate to " + BASE_URL);
        driver.get(BASE_URL);

        logStep("Step 2: Wait for body visibility and verify favicon href");
        wait.until(ExpectedConditions.visibilityOfElementLocated(BODY_ELEMENT));
        WebElement favicon = driver.findElement(FAVICON_LINK);
        String faviconHref = favicon.getAttribute("href");
        Assert.assertTrue(faviconHref != null && faviconHref.endsWith("/favicon.ico"),
                "Expected favicon href ending with '/favicon.ico', but found: " + faviconHref);

        logStep("Step 3: Verify no 404 / Not Found / Unexpected Error text appears in body");
        String bodyTextLower = driver.findElement(BODY_ELEMENT).getText().toLowerCase();
        Assert.assertFalse(bodyTextLower.contains("404") || bodyTextLower.contains("not found") || bodyTextLower.contains("unexpected error"),
                "BUG DETECTED: Page body contains error/not-found text: " + bodyTextLower);
    }
}