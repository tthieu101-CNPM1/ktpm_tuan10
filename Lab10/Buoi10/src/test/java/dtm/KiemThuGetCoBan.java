package dtm;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import java.util.Map;

public class KiemThuGetCoBan extends KiemThuNenTangApi {

    @Test
    public void kiemThuGetPage1() {
        // Test 1: GET /api/users?page=1 -> status 200, page=1, total_pages > 0, data.size() >= 1
        given(requestSpec)
                .queryParam("page", 1)
                .when()
                .get("/users")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("page", equalTo(1))
                .body("total_pages", greaterThan(0))
                .body("data.size()", greaterThanOrEqualTo(1));
    }

    @Test
    public void kiemThuGetPage2() {
        // Test 2: GET /api/users?page=2 -> page=2, data[] có id, email, first_name, last_name, avatar
        given(requestSpec)
                .queryParam("page", 2)
                .when()
                .get("/users")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("page", equalTo(2))
                .body("data.id", everyItem(notNullValue()))
                .body("data.email", everyItem(notNullValue()))
                .body("data.first_name", everyItem(notNullValue()))
                .body("data.last_name", everyItem(notNullValue()))
                .body("data.avatar", everyItem(notNullValue()));
    }

    @Test
    public void kiemThuGetUser3() {
        // Test 3: GET /api/users/3 -> id=3, email đúng định dạng @reqres.in, first_name không rỗng
        given(requestSpec)
                .when()
                .get("/users/3")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("data.id", equalTo(3))
                .body("data.email", endsWith("@reqres.in"))
                .body("data.first_name", not(emptyOrNullString()));
    }

    @Test
    public void kiemThuGetUser9999() {
        // Test 4: GET /api/users/9999 -> status 404, body là object rỗng {}
        given(requestSpec)
                .when()
                .get("/users/9999")
                .then()
                // Không gán responseSpec ở đây vì 404 Not Found không trả về JSON hợp lệ cho expectContentType
                .statusCode(404)
                .body("$", anEmptyMap());
    }
}