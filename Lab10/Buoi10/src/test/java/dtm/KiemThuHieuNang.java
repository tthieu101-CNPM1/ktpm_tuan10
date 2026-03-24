package dtm;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class KiemThuHieuNang extends KiemThuNenTangApi {

    @DataProvider(name = "slaData")
    public Object[][] duLieuSla() {
        return new Object[][]{
                {"GET", "/users", 2000L, 200},
                {"GET", "/users/2", 1500L, 200},
                {"POST", "/users", 3000L, 201},
                {"POST", "/login", 2000L, 200},
                {"DELETE", "/users/2", 1000L, 204}
        };
    }

    @Test(dataProvider = "slaData")
    public void kiemThuSlaCacEndpoint(String method, String endpoint, long maxMs, int expectedStatus) {
        thucHienGoiApiVaKiemTraSla(method, endpoint, maxMs, expectedStatus);
    }

    @Step("Gọi {method} {endpoint} - SLA: {maxMs}ms")
    public void thucHienGoiApiVaKiemTraSla(String method, String endpoint, long maxMs, int expectedStatus) {
        long startTime = System.currentTimeMillis();
        Response response = null;

        Map<String, Object> bodyPost = new HashMap<>();
        if (method.equals("POST") && endpoint.equals("/users")) {
            bodyPost.put("name", "morpheus");
            bodyPost.put("job", "leader");
        } else if (method.equals("POST") && endpoint.equals("/login")) {
            bodyPost.put("email", "eve.holt@reqres.in");
            bodyPost.put("password", "cityslicka");
        }

        switch (method) {
            case "GET":
                response = given(requestSpec).when().get(endpoint);
                break;
            case "POST":
                response = given(requestSpec).body(bodyPost).when().post(endpoint);
                break;
            case "DELETE":
                response = given(requestSpec).when().delete(endpoint);
                break;
        }

        response.then()
                .statusCode(expectedStatus)
                .time(lessThan(maxMs));

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("Thời gian phản hồi " + method + " " + endpoint + ": " + elapsed + "ms");

        if (endpoint.equals("/users") && method.equals("GET")) {
            response.then().body("data.size()", greaterThanOrEqualTo(1));
        } else if (endpoint.equals("/users/2") && method.equals("GET")) {
            response.then().body("data.id", equalTo(2));
        } else if (endpoint.equals("/users") && method.equals("POST")) {
            response.then().body("id", notNullValue());
        } else if (endpoint.equals("/login") && method.equals("POST")) {
            response.then().body("token", notNullValue());
        }
    }

    @Test
    public void kiemThuMonitoringLapLai() {
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;
        long totalTime = 0;
        int soLanLap = 10;

        for (int i = 0; i < soLanLap; i++) {
            long startTime = System.currentTimeMillis();
            given(requestSpec).when().get("/users?page=1").then().statusCode(200);
            long elapsed = System.currentTimeMillis() - startTime;

            if (elapsed < minTime) minTime = elapsed;
            if (elapsed > maxTime) maxTime = elapsed;
            totalTime += elapsed;
        }

        System.out.println("Kết quả monitoring 10 lần gọi GET /users?page=1:");
        System.out.println("Thời gian trung bình: " + (totalTime / soLanLap) + "ms");
        System.out.println("Thời gian nhỏ nhất: " + minTime + "ms");
        System.out.println("Thời gian lớn nhất: " + maxTime + "ms");
    }
}