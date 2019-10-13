package cz.vse.selenium;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.UUID;

/**
 * Unit test for simple App.
 */
public class AppTest {
    private ChromeDriver driver;

    @Before
    public void init() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @After
    public void tearDown() {
        driver.close();
    }

    @Test
    public void shouldNotLoginUsingInvalidPassword() {
        // given
        driver.get("https://opensource-demo.orangehrmlive.com/");

        // when
        WebElement usernameInput = driver.findElement(By.id("txtUsername"));
        usernameInput.sendKeys("admin");
        WebElement passwordInput = driver.findElement(By.id("txtPassword"));
        passwordInput.sendKeys("admin");
        WebElement loginButton = driver.findElement(By.id("btnLogin"));
        loginButton.click();

        // then
        WebElement errorMessageSpan = driver.findElement(By.id("spanMessage"));
        Assert.assertEquals("Invalid credentials", errorMessageSpan.getText());

        // validation error exists
        // url changed to https://opensource-demo.orangehrmlive.com/index.php/auth/validateCredentials
        // there is no menu
    }


    @Test
    public void shouldLoginUsingValidCredentials() {
        // given
        driver.get("http://demo.churchcrm.io/master/");

        // when
        WebElement usernameInput = driver.findElement(By.id("UserBox"));
        usernameInput.sendKeys("admin");
        WebElement passwordInput = driver.findElement(By.id("PasswordBox"));
        passwordInput.sendKeys("changeme");
        WebElement loginButton = driver.findElement(By.className("btn-primary"));
        loginButton.click();
    }

    @Test
    public void shouldCreateNewUser() {
        // Given
        shouldLoginUsingValidCredentials();

        // When
        driver.get("http://demo.churchcrm.io/master/PersonEditor.php");

        WebElement genderSelectElement = driver.findElement(By.name("Gender"));
        Select genderSelect = new Select(genderSelectElement);
        genderSelect.selectByVisibleText("Male");

        WebElement firstNameInput = driver.findElement(By.id("FirstName"));
        firstNameInput.sendKeys("John");
        WebElement lastNameInput = driver.findElement(By.id("LastName"));
        String uuid = UUID.randomUUID().toString();
        lastNameInput.sendKeys("Doe " + uuid);
        WebElement emailInput = driver.findElement(By.name("Email"));
        emailInput.sendKeys("john.doe@gmail.com");

        WebElement classificationSelectElement = driver.findElement(By.name("Classification"));
        Select classificationSelect = new Select(classificationSelectElement);
        classificationSelect.selectByIndex(4);

        WebElement personSaveButton = driver.findElement(By.id("PersonSaveButton"));
        personSaveButton.click();

        // Then
        driver.get("http://demo.churchcrm.io/master/v2/people");

        WebElement searchInput = driver.findElement(By.cssSelector("#members_filter input"));
        searchInput.sendKeys(uuid);

        // UKOL...opravit, doplnit tak, aby se provedla verifikace ze kontakt, ktery jsme vytvorili opravdu existuje
        //    (jde vyhledat a zobrazi se v tabulce)
        //    doporucuji radek tabulky s danou osobou projit (traverzovat), nebo jinym zpusobem v nem najit retezec UUID, ktery jednoznacne identifikuje pridanou osobu
        WebElement personTableRow = driver.findElement(By.cssSelector("table#members tr"));


    }
}
