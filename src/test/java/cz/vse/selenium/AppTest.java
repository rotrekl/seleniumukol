package cz.vse.selenium;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
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
//        driver.close();
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
        //driver.get("http://demo.churchcrm.io/master/");
        driver.get("http://digitalnizena.cz/church/");

        // when
        WebElement usernameInput = driver.findElement(By.id("UserBox"));
        usernameInput.sendKeys("church");
        WebElement passwordInput = driver.findElement(By.id("PasswordBox"));
        passwordInput.sendKeys("church12345");
        WebElement loginButton = driver.findElement(By.className("btn-primary"));
        loginButton.click();
    }

    @Test
    public void shouldCreateNewUser() {
        // Given
        shouldLoginUsingValidCredentials();

        // When
        driver.get("http://digitalnizena.cz/church/PersonEditor.php");

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
        driver.get("http://digitalnizena.cz/church/v2/people");

        WebElement searchInput = driver.findElement(By.cssSelector("#members_filter input"));
        searchInput.sendKeys(uuid);

        // UKOL...opravit, doplnit tak, aby se provedla verifikace ze kontakt, ktery jsme vytvorili opravdu existuje
        //    (jde vyhledat a zobrazi se v tabulce)
        //    doporucuji radek tabulky s danou osobou projit (traverzovat), nebo jinym zpusobem v nem najit retezec UUID, ktery jednoznacne identifikuje pridanou osobu
        WebElement personTableRow = driver.findElement(By.cssSelector("table#members tr"));


        // option1
        Assert.assertTrue(personTableRow.getText().contains(uuid));


        // option2

        List<WebElement> cells = personTableRow.findElements(By.tagName("td"));
        Assert.assertEquals(9, cells.size());
        for (int i = 0; i < cells.size(); i++) {
            System.out.println(cells.get(i).getText() + "        // " + cells.get(i));
        }

    }


    @Test
    public void insertDeposit() throws InterruptedException {
        shouldLoginUsingValidCredentials();

        driver.get("http://digitalnizena.cz/church/FindDepositSlip.php");

        WebElement depositCommentInput = driver.findElement(By.cssSelector("#depositComment"));
        String depositComment = "deposit-PavelG-" + UUID.randomUUID().toString();
        depositCommentInput.sendKeys(depositComment);

        WebElement depositTypeElement = driver.findElement(By.cssSelector("#depositType"));
        Select depositTypeSelect = new Select(depositTypeElement);
        //depositTypeSelect.selectByVisibleText("Credit Card");

        WebElement depositDateInput = driver.findElement(By.cssSelector("#depositDate"));
        depositDateInput.click();
        depositDateInput.clear();
        depositDateInput.sendKeys("2018-10-30");

        WebElement addDepositButton = driver.findElement(By.cssSelector("#addNewDeposit"));
        addDepositButton.click();

        Thread.sleep(2000);       // FIXME   task za 5 bludistaku, sleep se NIKDY NIKDY nepouziva, prosim odstrante ho a nahradte lepsi konstrukci

        List<WebElement> depositRows = driver.findElements(By.cssSelector("#depositsTable_wrapper #depositsTable tbody tr"));
        WebElement firstRow = depositRows.get(0);
        String innerHTML = firstRow.getAttribute("innerHTML");
        Assert.assertTrue(innerHTML.contains("10-30-18"));    // TODO pozor jiny format
        Assert.assertTrue(innerHTML.contains(depositComment));


        for (WebElement row : depositRows) {
            row.click();
        }

        WebElement deleteButton = driver.findElement(By.cssSelector("#deleteSelectedRows"));
        deleteButton.click();

        //TODO compare this WebElement confirmDeleteButton = driver.findElement(By.cssSelector(".modal-dialog .btn-primary"));
        WebElement confirmDeleteButton = driver.findElement(By.cssSelector(".modal-content > .modal-footer .btn-primary"));
        WebDriverWait wait = new WebDriverWait(driver, 1);
        wait.until(ExpectedConditions.visibilityOf(confirmDeleteButton));
        confirmDeleteButton.click();

        // actually the application behaves incorrect => when delete all rows, Delete button should be disabled
        // we have our test correct, so it good that test fails!
        Assert.assertFalse(deleteButton.isEnabled());
    }


}
