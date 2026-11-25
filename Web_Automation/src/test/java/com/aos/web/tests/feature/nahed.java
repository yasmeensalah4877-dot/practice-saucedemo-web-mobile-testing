package com.aos.web.tests.feature;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class nahed {

    WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
    }

    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        driver.navigate().to("https://www.saucedemo.com/inventory.html");
        Thread.sleep(2000);
    }

    // TC1: Verify Home Page initial state and products display
    @Test
    public void testCase1() throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test='inventory-list']")));

        Assert.assertEquals(
                driver.findElement(By.cssSelector("[data-test='title']")).getText(),
                "Products",
                "Home page title is incorrect"
        );

        List<WebElement> products = driver.findElements(
                By.cssSelector("[data-test='inventory-item-name']"));

        Assert.assertEquals(products.size(), 6,
                "The Home page should display exactly six products");

        List<String> actualNames = new ArrayList<>();

        for (WebElement product : products) {
            actualNames.add(product.getText());
        }

        List<String> expectedNames = Arrays.asList(
                "Sauce Labs Backpack",
                "Sauce Labs Bike Light",
                "Sauce Labs Bolt T-Shirt",
                "Sauce Labs Fleece Jacket",
                "Sauce Labs Onesie",
                "Test.allTheThings() T-Shirt (Red)"
        );

        Assert.assertEquals(actualNames, expectedNames,
                "Product names or default order are incorrect");

        Assert.assertTrue(
                driver.findElements(By.cssSelector(".shopping_cart_badge")).isEmpty(),
                "Cart badge should not be displayed when the cart is empty"
        );
    }

    // TC2: Verify sorting products by Price (low to high)
    @Test
    public void testCase2() throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement sortDropdown = driver.findElement(
                By.cssSelector("select[data-test='product-sort-container']"));

        Select select = new Select(sortDropdown);

        select.selectByValue("lohi");

        wait.until(ExpectedConditions.textToBe(
                By.cssSelector("[data-test='active-option']"),
                "Price (low to high)"
        ));

        List<WebElement> prices = driver.findElements(
                By.cssSelector("[data-test='inventory-item-price']"));

        List<Double> actualPrices = new ArrayList<>();

        for (WebElement price : prices) {
            actualPrices.add(
                    Double.parseDouble(price.getText().replace("$", ""))
            );
        }

        List<Double> expectedPrices = new ArrayList<>(actualPrices);

        Collections.sort(expectedPrices);

        Assert.assertEquals(actualPrices, expectedPrices,
                "Products are not sorted by price in ascending order");

        Assert.assertEquals(prices.size(), 6,
                "Sorting should not remove any products");
    }

    // TC3: Verify adding a product does not affect other products
    @Test
    public void testCase3() throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement backpackButton = driver.findElement(
                By.cssSelector(
                        "button[data-test='add-to-cart-sauce-labs-backpack']"));

        backpackButton.click();

        WebElement removeBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("button[data-test='remove-sauce-labs-backpack']")));

        Assert.assertEquals(removeBtn.getText(), "Remove",
                "Selected product button should change to Remove");
        Assert.assertEquals(
                driver.findElement(By.cssSelector(".shopping_cart_badge")).getText(),
                "1",
                "Cart badge should display 1"
        );

        List<WebElement> productCards = driver.findElements(
                By.cssSelector("[data-test='inventory-item']"));

        for (WebElement card : productCards) {

            String productName = card.findElement(
                            By.cssSelector("[data-test='inventory-item-name']"))
                    .getText();

            String buttonText = card.findElement(
                            By.cssSelector("button.btn_inventory"))
                    .getText();

            if (productName.equals("Sauce Labs Backpack")) {

                Assert.assertEquals(buttonText, "Remove",
                        "Selected product should display Remove");

            } else {

                Assert.assertEquals(buttonText, "Add to cart",
                        "Other products should remain Add to cart");
            }
        }
    }

    // TC4: Verify removing the last product updates the cart correctly
    @Test
    public void testCase4() throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement onesieCard = driver.findElement(
                By.xpath("//div[@data-test='inventory-item']" +
                        "[.//div[@data-test='inventory-item-name' " +
                        "and normalize-space()='Sauce Labs Onesie']]")
        );

        WebElement onesieButton = onesieCard.findElement(
                By.cssSelector("button.btn_inventory"));

        // تصفير السلة في بداية testCase4 عشان تبدأ فاضية من أثر testCase3
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        driver.navigate().refresh();

        // 1. الضغط على Add to cart لمنتج Onesie
        WebElement addOnesieBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[data-test='add-to-cart-sauce-labs-onesie']")));
        addOnesieBtn.click();

        // 2. انتظار ظهور زرار Remove الجديد والتأكد منه
        WebElement removeOnesieBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("button[data-test='remove-sauce-labs-onesie']")));
        Assert.assertEquals(removeOnesieBtn.getText(), "Remove",
                "Selected product button should change to Remove");

        // 3. التأكد إن رقم السلة بقى 1
        Assert.assertEquals(
                driver.findElement(By.cssSelector(".shopping_cart_badge")).getText(),
                "1",
                "Cart badge should display 1 after adding the product"
        );

        // 4. الضغط على زرار Remove الجديد لحذف المنتج
        removeOnesieBtn.click();

        // 5. انتظار رجوع زرار Add to cart الجديد والتأكد منه
        WebElement addOnesieBtnAgain = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("button[data-test='add-to-cart-sauce-labs-onesie']")));
        Assert.assertEquals(addOnesieBtnAgain.getText(), "Add to cart",
                "Product button should return to Add to cart");

        // 6. التأكد إن علامة السلة اختفت تماماً
        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector(".shopping_cart_badge"), 0
        ));

        Assert.assertTrue(
                driver.findElements(By.cssSelector(".shopping_cart_badge")).isEmpty(),
                "Cart badge should disappear after removing the last product"
        );
    }

    // TC5: Verify opening and closing the side menu
    @Test
    public void testCase5() throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement menuButton = driver.findElement(
                By.cssSelector("#react-burger-menu-btn"));

        menuButton.click();

        WebElement menu = driver.findElement(
                By.cssSelector(".bm-menu-wrap"));

        wait.until(ExpectedConditions.visibilityOf(menu));

        Assert.assertTrue(menu.isDisplayed(),
                "Side menu should be visible after clicking the menu button");

        WebElement allItemsLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test='inventory-sidebar-link']")));

        Assert.assertTrue(allItemsLink.isDisplayed(),
                "All Items option should be displayed");
        Assert.assertTrue(
                driver.findElement(
                                By.cssSelector("[data-test='logout-sidebar-link']"))
                        .isDisplayed(),
                "Logout option should be displayed");

        WebElement closeButton = driver.findElement(
                By.cssSelector("#react-burger-cross-btn"));

        closeButton.click();

        wait.until(ExpectedConditions.invisibilityOf(menu));

        Assert.assertFalse(menu.isDisplayed(),
                "Side menu should be hidden after clicking the close button");
    }

    @AfterMethod
    public void afterMethod() {
    }

    @AfterClass
    public void afterClass() {
        driver.quit();
    }
}
