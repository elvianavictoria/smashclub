package com.backendsyndicate.smashclub.admin;

import com.backendsyndicate.smashclub.common.util.Logging;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestAdminAuthController extends AbstractTestNGSpringContextTests {
    private String username;
    private String fullname;
    private String password;
    private String accessToken;
    private String changedUsername;
    private String changedFullname;

    private final int STATUS_OK = HttpStatus.OK.value();

    @BeforeClass
    public void init() {
        RestAssured.baseURI = "http://localhost:8080";
        username = "admin";
        fullname = "Administrator";
        password = "admin";
        accessToken = "";
        changedUsername = "administrator";
        changedFullname = "Admin";
    }

    @Test(priority = 1)
    public void authenticate() {
        if( accessToken.isEmpty() ) {
            Assert.fail("Cannot authenticate before login!");
        }

        Response response;

        try {
            response = given()
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .request(Method.POST, "api/v1/admin/auth");
            int statusCode = response.getStatusCode();
            Assert.assertEquals(statusCode, STATUS_OK);

            JsonPath jsonPath = response.jsonPath();
            username = jsonPath.getString("data.username");
            fullname = jsonPath.getString("data.fullname");
            Assert.assertNotNull(username, "Username is empty!");
            Assert.assertNotNull(fullname, "Fullname is empty!");
            Assert.assertNotNull(jsonPath.getString("data.adminRole"), "Role is empty!");
        } catch(Exception e) {
            Logging.handleException("TestAdminAuthController", "authenticate()", 42, "TAAS-02E010", e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 0)
    public void login() {
        Response response;

        try {
            response = loginAPICall(username, password);
            accessToken = response.jsonPath().getString("data.accessToken");
            int statusCode = response.getStatusCode();

            Assert.assertEquals(statusCode, STATUS_OK);
            Assert.assertFalse(accessToken.isEmpty(), "Access token is empty!");
        } catch(Exception e) {
            Logging.handleException("TestAdminAuthController", "login()", 54, "TAAS-01E010", e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 2)
    public void updateProfile() {
        if( accessToken.isEmpty() ) {
            Assert.fail("Cannot update profile before login!");
        }

        Response response;

        try {
            response = given()
//                    .header("Content-Type", "multipart/form-data")
//                    .header("accept", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .params("username", changedUsername)
                    .params("fullname", changedFullname)
                    .params("profilePicture", null)
                    .request(Method.PUT, "api/v1/admin/auth/update");
            Logging.printConsole(response.getBody().toString());
            int statusCode = response.getStatusCode();
            Assert.assertEquals(statusCode, STATUS_OK);

            JsonPath jsonPath = response.jsonPath();
//            Logging.printConsole(jsonPath.getString("data"));
            Assert.assertNotNull(jsonPath.getString("data.username"), "Username is empty!");
            Assert.assertNotNull(jsonPath.getString("data.fullname"), "Fullname is empty!");
            Assert.assertEquals(jsonPath.getString("data.username"), changedUsername, "Username is not changed!");
            Assert.assertEquals(jsonPath.getString("data.fullname"), changedFullname, "Fullname is not changed!");
        } catch(Exception e) {
            Logging.handleException("TestAdminAuthController", "updateProfile()", 73, "TAAS-03E010", e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

//    @Test(priority = 3)
    public void logout() {
        if( accessToken.isEmpty() ) {
            Assert.fail("Cannot logout before login!");
        }

        Response response;

        try {
            response = given()
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .request(Method.POST, "api/v1/admin/auth/logout");
            int statusCode = response.getStatusCode();
            Assert.assertEquals(statusCode, STATUS_OK);

            JsonPath jsonPath = response.jsonPath();
            String isLoggedOut = jsonPath.getString("data.loggedOut");
            Assert.assertEquals(isLoggedOut, "true", "Logout failed!");
        }
        catch(Exception e) {
            Logging.handleException("TestAdminAuthController", "logout()", 42, "TAAS-04E010", e.getMessage());
            Assert.fail(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Response loginAPICall(String username, String password) throws Exception {
        return given()
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .body(Map.of("username", username, "password", password))
                .request(Method.POST, "api/v1/admin/auth/login");
    }

    public String getAccessToken(String username, String password) throws Exception {
        Response response = loginAPICall(username, password);
        return response.jsonPath().getString("data.accessToken");
    }
}
