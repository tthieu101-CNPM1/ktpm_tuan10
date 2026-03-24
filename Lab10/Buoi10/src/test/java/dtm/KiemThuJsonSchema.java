package dtm;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class KiemThuJsonSchema extends KiemThuNenTangApi {

    @Test
    public void kiemThuSchemaDanhSachUser() {
        given(requestSpec)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-list-schema.json"));
    }

    @Test
    public void kiemThuSchemaMotUser() {
        given(requestSpec)
                .when()
                .get("/users/2")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

    @Test
    public void kiemThuSchemaTaoUser() {
        YeuCauTaoNguoiDung yeuCau = new YeuCauTaoNguoiDung("Schema Test", "Tester");
        given(requestSpec)
                .body(yeuCau)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/create-user-schema.json"));
    }
}