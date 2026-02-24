package com.backendsyndicate.smashclub.admin.internal;

import com.backendsyndicate.smashclub.admin.TestAdminAuthController;
import com.backendsyndicate.smashclub.common.util.Logging;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestAdminPermissionController extends AbstractTestNGSpringContextTests {
    private TestAdminAuthController testAdminAuthController;
    private String accessToken;
    private final int STATUS_OK = HttpStatus.OK.value();

    @BeforeClass
    public void init() {
        RestAssured.baseURI = "http://localhost:8080";
        testAdminAuthController = new TestAdminAuthController();
        accessToken = "";
    }

    private void setAccessToken() {
        try {
            accessToken = testAdminAuthController.getAccessToken("admin", "admin");
            if( accessToken == null ) accessToken = "";
        } catch(Exception e) {
            Logging.handleException("TestAdminPermissionController", "setAccessToken()", 33, "TAPC-00E010", e.getMessage());
            accessToken = "";
        }
    }

    @Test(priority=3)
    public void adminPermissionList() {
        if( accessToken.isEmpty() ) {
            setAccessToken();
//            Assert.fail("Access token is empty! Cannot get permission list!");
        }

        Response response;

        try {
            Logging.printConsole("Bearer " + accessToken);
            response = given()
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                    .request(Method.GET, "api/v1/admin/permissions");
            int statusCode = response.getStatusCode();
            Assert.assertEquals(statusCode, STATUS_OK);
        } catch(Exception e) {
            Logging.handleException("TestAdminPermissionController", "adminPermissionList()", 33, "TAPC-01E010", e.getMessage());
            Assert.fail(e.getMessage());
        }
    }
}
