package dtm;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import java.util.HashMap;
import java.util.Map;

public class KiemThuXacThuc extends KiemThuNenTangApi {

    @Test
    public void kiemThuLoginThanhCong() {
        Map<String, String> body = new HashMap<>();
        body.put("email", "eve.holt@reqres.in");
        body.put("password", "cityslicka");

        given(requestSpec)
                .body(body)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("token", not(emptyString()));
    }

    @Test
    public void kiemThuLoginThieuPassword() {
        Map<String, String> body = new HashMap<>();
        body.put("email", "eve.holt@reqres.in");

        given(requestSpec)
                .body(body)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    @Test
    public void kiemThuLoginThieuEmail() {
        Map<String, String> body = new HashMap<>();
        body.put("password", "cityslicka");

        given(requestSpec)
                .body(body)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing email or username"));
    }

    @Test
    public void kiemThuRegisterThanhCong() {
        Map<String, String> body = new HashMap<>();
        body.put("email", "eve.holt@reqres.in");
        body.put("password", "pistol");

        given(requestSpec)
                .body(body)
                .when()
                .post("/register")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("token", notNullValue());
    }

    @Test
    public void kiemThuRegisterThieuPassword() {
        Map<String, String> body = new HashMap<>();
        body.put("email", "eve.holt@reqres.in");

        given(requestSpec)
                .body(body)
                .when()
                .post("/register")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    @DataProvider(name = "loginScenarios")
    public Object[][] kichBanLogin() {
        return new Object[][] {
                {"eve.holt@reqres.in", "cityslicka", 200, null},
                {"eve.holt@reqres.in", "", 400, "Missing password"},
                {"", "cityslicka", 400, "Missing email or username"},
                {"notexist@reqres.in", "wrongpass", 400, "user not found"},
                {"invalid-email", "pass123", 400, "user not found"}
        };
    }

    @Test(dataProvider = "loginScenarios")
    public void kiemThuKichBanLogin(String email, String password, int expectedStatus, String expectedError) {
        Map<String, String> body = new HashMap<>();
        if (!email.isEmpty()) body.put("email", email);
        if (!password.isEmpty()) body.put("password", password);

        io.restassured.response.ValidatableResponse response = given(requestSpec)
                .body(body)
                .when()
                .post("/login")
                .then()
                .statusCode(expectedStatus);

        if (expectedError != null) {
            response.body("error", containsString(expectedError));
        }
    }
}