package Search;

import Main.Util;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SearchTest {
    // Create a new instance of the ChromeDriver for browser automation
    WebDriver driver = new ChromeDriver();

    @BeforeTest
    public void setUp() {
        // Initialize a WebDriverWait to wait for certain conditions
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Navigate to the base URL specified in the Util class
        driver.get(Util.BaseUrl);

        // Maximize the browser window to ensure all elements are visible
        driver.manage().window().maximize();

        // Set implicit wait for the driver, allowing time for elements to be found before throwing an exception
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

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

        // Click to apply the container type filter for "Bag"
        driver.findElement(By.xpath("//span[text()='Bag']")).click();

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
        // Initialize a WebDriverWait for up to 20 seconds for conditions to be met
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

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
        List<WebElement> productTitles = driver.findElements(By.cssSelector(".s-title-text"));

        // Verify that each product title contains the specified container type "Bag"
        for (WebElement title : productTitles) {
            assert title.getText().contains(Util.ContainerType) : "Product does not contain 'Bag' in the title.";
        }

        // Print confirmation that all products are correctly filtered
        System.out.println("All products are correctly filtered with container type: Bag.");
    }
}

