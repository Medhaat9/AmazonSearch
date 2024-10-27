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
    WebDriver driver = new ChromeDriver();
    @BeforeTest
    public void setUp()
    {
        /*Wait until the page reload*/
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get(Util.BaseUrl);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        /*Click on Sign in */
        driver.findElement(By.xpath("//span[@class='nav-line-2 ']")).click();
        /*Insert Mail*/
        driver.findElement(By.xpath("//input[@id='ap_email']")).click();
        driver.findElement(By.xpath("//input[@id='ap_email']")).clear();
        driver.findElement(By.xpath("//input[@id='ap_email']")).sendKeys(Util.UserID);
        driver.findElement(By.xpath("//input[@id='continue']")).click();
        /*Insert Password*/
        driver.findElement(By.xpath("//input[@id='ap_password']")).click();
        driver.findElement(By.xpath("//input[@id='ap_password']")).clear();
        driver.findElement(By.xpath("//input[@id='ap_password']")).sendKeys(Util.Password);
        driver.findElement(By.xpath("//input[@id='signInSubmit']")).click();
        /*Navigate to language button*/
        driver.findElement(By.xpath("//span[@class='nav-icon nav-arrow']")).click();
        /*Choose English*/
        driver.findElement(By.xpath("//*[@id=\"icp-language-settings\"]/div[3]/div/label/i")).click();
        /*Press Save */
        driver.findElement(By.xpath("//input[@class='a-button-input']")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//input[@class='a-button-input']")));

    }
    @Test (priority = 2)
    public void testSearchWithFilters() throws InterruptedException {
         /*Search on Rice Product*/
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys(Util.ProductName);
        driver.findElement(By.id("nav-search-submit-button")).click();
        /*Apply Low Price Filter*/
        driver.findElement(By.xpath("//span[@class='a-button-text a-declarative']")).click();
        driver.findElement(By.xpath("//li[@aria-labelledby='s-result-sort-select_1']")).click();
        /*Apply Container Type : "Bag " Filter*/
        driver.findElement(By.xpath("//span[text()='Bag']")).click();
        /*Verify Types*/
        verifyPriceSorting();
        verifyContainerType();

        /*Clear Search Box After Finish*/
        driver.findElement(By.id("twotabsearchtextbox")).clear();
        System.out.println("Done");
    }

    @Test (priority = 1)
    public void testSearchadvanced() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        String searchTerm = "rice";
        By suggestionBoxLocator = By.className("s-suggestion-container");
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys(searchTerm);

        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestionBoxLocator));
        // Retrieve all suggestions from the suggestion box
        Thread.sleep(200);
        List<WebElement> suggestions = driver.findElements(By.className("s-suggestion-container"));
        System.out.println(suggestions.size());
        // Verify each suggestion contains the term "rice"
        for (WebElement suggestion : suggestions) {
            String suggestionText = suggestion.getText().toLowerCase();
            Assert.assertTrue(suggestionText.contains(searchTerm), "Suggestion does not contain keyword: " + suggestionText);
        }
        driver.findElement(By.id("twotabsearchtextbox")).clear();
        System.out.println("Done");
    }
    @AfterTest
    void EndSequance()
    {
        driver.quit();
    }

    private void verifyPriceSorting() {
        List<WebElement> prices = driver.findElements(By.cssSelector(".a-price-whole"));
        List<Double> priceValues = new ArrayList<>();

        for (WebElement price : prices) {
            String priceText = price.getText().replace(",", "");
            if (!priceText.isEmpty()) {
                priceValues.add(Double.parseDouble(priceText));
            }
        }

        for (int i = 0; i < priceValues.size() - 1; i++) {
            Assert.assertTrue(priceValues.get(i) <= priceValues.get(i + 1), "Prices are not sorted correctly.");

        }

    }
    private void verifyContainerType() {
        List<WebElement> productTitles = driver.findElements(By.cssSelector(".s-title-text"));

        for (WebElement title : productTitles) {
            assert title.getText().contains("Bag") : "Product does not contain 'Bag' in the title.";
        }
        System.out.println("All products are correctly filtered with container type: Bag.");
      }

}
