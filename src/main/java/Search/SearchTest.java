package Search;

import Main.Util;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SearchTest {
    // Create a new instance of the ChromeDriver for browser automation
    WebDriver driver = new ChromeDriver();
    // Initialize a WebDriverWait to wait for certain conditions
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    @BeforeTest
    public void setUp() {

        // Navigate to the base URL specified in the Util class
        driver.get(Util.BaseUrl);

        // Maximize the browser window to ensure all elements are visible
        driver.manage().window().maximize();

        // Set implicit wait for the driver, allowing time for elements to be found before throwing an exception
        //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // Click on the 'Sign in' button
        driver.findElement(By.xpath("//span[@class='nav-line-2 ']")).click();

        // Locate the email input field, click it, clear any existing text, and enter the email from Util
        driver.findElement(By.xpath("//input[@id='ap_email']")).click();
        driver.findElement(By.xpath("//input[@id='ap_email']")).clear();
        driver.findElement(By.xpath("//input[@id='ap_email']")).sendKeys(Util.UserID);

        // Click the 'Continue' button to proceed with login
        driver.findElement(By.xpath("//input[@id='continue']")).click();

        // Locate the password input field, click it, clear any existing text, and enter the password from Util
        driver.findElement(By.xpath("//input[@id='ap_password']")).click();
        driver.findElement(By.xpath("//input[@id='ap_password']")).clear();
        driver.findElement(By.xpath("//input[@id='ap_password']")).sendKeys(Util.Password);

        // Click the 'Sign In' button to complete the login process
        driver.findElement(By.xpath("//input[@id='signInSubmit']")).click();

        // Click on the language button to change the language settings
        driver.findElement(By.xpath("//span[@class='nav-icon nav-arrow']")).click();

        // Choose 'English' from the language options
        driver.findElement(By.xpath("//*[@id=\"icp-language-settings\"]/div[3]/div/label/i")).click();

        // Click the 'Save' button to apply the selected language
        driver.findElement(By.xpath("//input[@class='a-button-input']")).click();

        // Wait until the 'Save' button disappears, indicating the operation is complete
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//input[@class='a-button-input']")));
    }

    @Test(priority = 2)
    public void testSearchWithFilters() throws InterruptedException {
        // Enter the product name into the search box
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys(Util.ProductName);

        // Click the search button to execute the search
        driver.findElement(By.id("nav-search-submit-button")).click();

        // Click the button to apply sorting by low price
        driver.findElement(By.xpath("//span[@class='a-button-text a-declarative']")).click();
        driver.findElement(By.xpath("//li[@aria-labelledby='s-result-sort-select_1']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Bag']")));
        // Click to apply the container type filter for "Bag"
        driver.findElement(By.xpath("//span[text()='Bag']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Bag']")));

        // Verify that the prices are sorted correctly
        verifyPriceSorting();

        // Verify that the products displayed are of the selected container type
        verifyContainerType();

        // Clear the search box after the test is complete
        driver.findElement(By.id("twotabsearchtextbox")).clear();
        System.out.println("Done");
    }

    @Test(priority = 1)
    public void testSearchAdvanced() throws InterruptedException {

        // Locator for the suggestion box
        By suggestionBoxLocator = By.className("s-suggestion-container");

        // Type the search term into the search box
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys(Util.ProductName);

        // Wait until the suggestion box becomes visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestionBoxLocator));

        // Introduce a short delay to allow suggestions to load
        Thread.sleep(200);

        // Retrieve all suggestions from the suggestion box
        List<WebElement> suggestions = driver.findElements(By.className("s-suggestion-container"));

        // Print the number of suggestions retrieved
        System.out.println(suggestions.size());

        // Verify that each suggestion contains the search term "rice"
        for (WebElement suggestion : suggestions) {
            String suggestionText = suggestion.getText().toLowerCase();
            Assert.assertTrue(suggestionText.contains(Util.ProductName), "Suggestion does not contain keyword: " + suggestionText);
        }

        // Clear the search box after the test is complete
        driver.findElement(By.id("twotabsearchtextbox")).clear();
        System.out.println("Done");
    }

    @AfterTest
    void EndSequence() {
        // Close the browser and end the WebDriver session
        driver.quit();
    }

    private void verifyPriceSorting() {
        // Locate all price elements on the page
        List<WebElement> prices = driver.findElements(By.cssSelector(".a-price-whole"));

        // Create a list to store price values as doubles
        List<Double> priceValues = new ArrayList<>();

        // Extract and parse price values from the web elements
        for (WebElement price : prices) {
            String priceText = price.getText().replace(",", ""); // Remove commas from price text
            if (!priceText.isEmpty()) {
                priceValues.add(Double.parseDouble(priceText)); // Convert to Double and add to list
            }
        }

        // Verify that prices are sorted in ascending order
        for (int i = 0; i < priceValues.size() - 1; i++) {
            Assert.assertTrue(priceValues.get(i) <= priceValues.get(i + 1), "Prices are not sorted correctly.");
        }
    }

    private void verifyContainerType() {
        // Locate all product title elements on the page
        List<WebElement> productTitles = driver.findElements(By.xpath("//h2[@class='a-size-mini a-spacing-none a-color-base s-line-clamp-4']"));
        String ActualResult ;
        SoftAssert Assert = new SoftAssert();
        // Create an instance of Actions class
        Actions actions = new Actions(driver);
        // Store the current window handle to return to it later
        String parentWindow = driver.getWindowHandle();

        // Verify that each product title contains the specified container type "Bag"
        for (WebElement title : productTitles) {
            //Get each element URL
            WebElement linkElement = title.findElement(By.xpath(".//a"));
            String url = linkElement.getAttribute("href");
            // Use JavaScript to open the link in a new tab
            ((JavascriptExecutor) driver).executeScript("window.open();");
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(parentWindow)) {
                    //Switch to new tab with the new element
                    driver.switchTo().window(handle);
                    driver.get(url);
                    break;
                }
            }
            //Wait unit category appears
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Bag']")));
            //Get category text
            ActualResult = driver.findElement(By.xpath("//span[text()='Bag']")).getText();
            //Assert the results
            Assert.assertEquals(ActualResult, Util.ContainerType);
            //Close the current tab for the current element
            driver.close();
            //Re switch to parent window
            driver.switchTo().window(parentWindow);
        }

        // Report all assertions
        Assert.assertAll();
        // Print confirmation that all products are correctly filtered
        System.out.println("All products are correctly filtered with container type: Bag.");
    }
}

