# Automation Testing Project (Amazon)

This project is an automation testing suite for verifying various functionalities of a web application using Selenium WebDriver and TestNG. The tests are written in Java and are designed to run in a Chrome browser.

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Apache Maven
- Google Chrome browser
- ChromeDriver executable compatible with your Chrome browser version

## Setup

1. **Clone the repository:**
    ```sh
    git clone <repository-url>
    cd <repository-directory>
    ```

2. **Install dependencies:**
    ```sh
    mvn clean install
    ```

3. **Configure the `Util` class:**
    Update the `Util.java` file located in `src/main/java/Main/` with the appropriate values for `BaseUrl`, `UserID`, `Password`, `ProductName`, and `ContainerType`.

## Running Tests

1. **Run all tests:**
    ```sh
    mvn test
    ```

2. **Run specific tests:**
    Update the `TestNG.xml` file to include or exclude specific test methods.

## Test Descriptions

### `SearchTest.java`

- **`testSearchAdvanced`**: Verifies that the search suggestions contain the specified product name.
- **`testSearchWithFilters`**: Verifies that the search results are filtered correctly based on price and container type.
- **`verifyPriceSorting`**: Ensures that the product prices are sorted in ascending order.
- **`verifyContainerType`**: Checks that each product title contains the specified container type "Bag".

### Utility Class

- **`Util.java`**: Contains utility constants and methods used across the tests.

## Example TestNG Configuration

The `TestNG.xml` file is used to configure and run the tests. Below is an example configuration:

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >
<suite name="SuiteName">
    <test name="TestName">
        <classes>
            <class name="Search.SearchTest">
                <methods>
                    <include name="testSearchAdvanced"/>
                    <include name="testSearchWithFilters"/>
                </methods>
            </class>
        </classes>
    </test>
</suite>

```