package com.backendsyndicate.smashclub.admin;

import com.backendsyndicate.smashclub.common.util.Logging;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Map;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestAdminAuthService extends AbstractTestNGSpringContextTests {
    private JSONObject req;
    private String username;
    private String password;
    private String accessToken;

    @BeforeClass
    public void init() {
        RestAssured.baseURI = "http://localhost:8080";
        req = new JSONObject();
        username = "admin";
        password = "admin";
        accessToken = "";
    }

    @Test(priority = 0)
    public void login() {
        Response response;

        try {
            response = given()
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .body(Map.of("username", username, "password", password))
                    .request(Method.POST, "api/v1/admin/auth/login");

            int statusCode = response.getStatusCode();
            int okStatusCode = HttpStatus.OK.value();
            Assert.assertEquals(statusCode, okStatusCode);
            String accessToken = response.jsonPath().getString("data.accessToken");
            Assert.assertFalse(accessToken.isEmpty(), "Access token is required!");
            Logging.printConsole(accessToken);
        } catch(Exception e) {
            Logging.handleException("TestAdminAuthService", "login()", 36, "TAAS-01E010", e.getMessage());
            Assert.assertTrue(false, e.getMessage());
        }
    }
}
