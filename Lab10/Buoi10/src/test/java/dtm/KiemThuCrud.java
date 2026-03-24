package dtm;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class KiemThuCrud extends KiemThuNenTangApi {
    private String taoId;

    @Test(priority = 1)
    public void kiemThuPostTaoUser() {
        YeuCauTaoNguoiDung yeuCau = new YeuCauTaoNguoiDung("Nguyen Van A", "QA Engineer");

        PhanHoiNguoiDung phanHoi = given(requestSpec)
                .body(yeuCau)
                .when()
                .post("/users")
                .then()
                .spec(responseSpec)
                .statusCode(201)
                .body("name", equalTo("Nguyen Van A"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue())
                .extract().as(PhanHoiNguoiDung.class);

        this.taoId = phanHoi.getId();
    }

    @Test(priority = 2)
    public void kiemThuPutCapNhatUser() {
        YeuCauTaoNguoiDung yeuCau = new YeuCauTaoNguoiDung("Nguyen Van A", "Senior QA");

        given(requestSpec)
                .body(yeuCau)
                .when()
                .put("/users/2")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("job", equalTo("Senior QA"))
                .body("updatedAt", notNullValue());
    }

    @Test(priority = 3)
    public void kiemThuPatchCapNhatMotPhan() {
        String jsonBody = "{\"job\": \"Lead QA\"}";

        given(requestSpec)
                .body(jsonBody)
                .when()
                .patch("/users/2")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("job", equalTo("Lead QA"))
                .body("updatedAt", notNullValue());
    }

    @Test(priority = 4)
    public void kiemThuDeleteXoaUser() {
        given(requestSpec)
                .when()
                .delete("/users/2")
                .then()
                .statusCode(204)
                .body(emptyOrNullString());
    }

    @Test(priority = 5)
    public void kiemThuPostGetXacNhan() {
        YeuCauTaoNguoiDung yeuCau = new YeuCauTaoNguoiDung("Xac Nhan", "Test");

        Response postResponse = given(requestSpec)
                .body(yeuCau)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract().response();

        String newId = postResponse.jsonPath().getString("id");

        // Ghi chú: Mock API reqres.in không lưu dữ liệu, bước này sẽ trả về 404 trong thực tế
        // Mã dưới đây được viết theo đúng yêu cầu đề bài.
        given(requestSpec)
                .when()
                .get("/users/" + newId)
                .then()
                .statusCode(200)
                .body("data.name", equalTo("Xac Nhan"));
    }
}