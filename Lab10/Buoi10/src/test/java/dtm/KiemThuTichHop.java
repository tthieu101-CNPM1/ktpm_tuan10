package dtm;

import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

import static io.restassured.RestAssured.given;

public class KiemThuTichHop extends KiemThuNenTangApi {

    private WebDriver driver;
    private boolean isApiAlive = false;

    @BeforeMethod
    public void thietLapTrinhDuyet() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void dongTrinhDuyet() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Phần A: API Precondition
    @Test
    public void tienQuyetApiLogin() {
        String reqBody = "{\"email\":\"eve.holt@reqres.in\",\"password\":\"cityslicka\"}";

        Response response = given(requestSpec)
                .body(reqBody)
                .when()
                .post("/login");

        Assert.assertEquals(response.statusCode(), 200, "API login thất bại");
        String token = response.jsonPath().getString("token");
        System.out.println("Token lấy được từ API: " + token);
    }

    // Phần A: UI Verification
    @Test(dependsOnMethods = "tienQuyetApiLogin")
    public void kiemThuUiSauTienQuyet() {
        driver.get("https://www.saucedemo.com/");

        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "URL không chứa 'inventory'");
        Assert.assertEquals(driver.getTitle(), "Swag Labs", "Title trang không khớp");
    }

    // Phần B: Luồng tích hợp đầy đủ
    @Test
    public void luongTichHopDayDuApiVaUi() {
        // Bước 1: API check
        Response response = given(requestSpec).when().get("/users");
        if (response.statusCode() == 200) {
            isApiAlive = true;
        }

        if (!isApiAlive) {
            throw new SkipException("API reqres.in không hoạt động, SKIP test UI");
        }

        // Bước 2: UI action - Đăng nhập
        driver.get("https://www.saucedemo.com/");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Bước 3: UI action - Thêm 2 sản phẩm
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();

        // Bước 4: Assertion - Kiểm tra badge = 2
        String badgeCount = driver.findElement(By.className("shopping_cart_badge")).getText();
        Assert.assertEquals(badgeCount, "2", "Số lượng trên badge giỏ hàng không đúng");

        // Bước 5: UI action - Vào giỏ hàng
        driver.findElement(By.className("shopping_cart_link")).click();

        // Bước 6: Assertion - Xác nhận 2 sản phẩm
        int soLuongSanPhamTrongGio = driver.findElements(By.className("cart_item")).size();
        Assert.assertEquals(soLuongSanPhamTrongGio, 2, "Số lượng sản phẩm trong giỏ hàng không đúng");
    }
}