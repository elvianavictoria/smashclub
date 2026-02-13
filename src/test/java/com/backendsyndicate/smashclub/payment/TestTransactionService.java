package com.backendsyndicate.smashclub.payment;

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

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestTransactionService extends AbstractTestNGSpringContextTests {
    private JSONObject req;
    private String transactionCode;
    private boolean isContinue;

    @BeforeClass
    public void init() {
        RestAssured.baseURI = "http://localhost:8080";
        req = new JSONObject();
        isContinue = false;
        transactionCode = "260112-ABCD-001";
    }

    @Test(priority = 0)
    public void transactionList() {
        Response response;

        try {
            response = given()
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .params("startDate", LocalDate.now().toString())
                    .params("endDate", LocalDate.now().toString())
                    .params("page", 0)
                    .params("size", 25)
                    .request(Method.GET, "transaction");

            Logging.handleException("TestTransactionController", "transactionList", 47, "TEST", response.getBody().toString());
            int statusCode = response.getStatusCode();
            int badRequestCode = HttpStatus.BAD_REQUEST.value();
            Assert.assertEquals(statusCode, badRequestCode);
            isContinue = true;
        } catch(Exception e) {
            Logging.handleException("TestTransactionController", "transactionList", 42, "TEST-TRX-001", e.getMessage());
//            Assert.assertNotNull(null);
            Assert.assertTrue(false, e.getMessage());
        }
    }

    @Test(priority = 10)
    public void transactionDetail() {
        if( !isContinue ) {
//            Assert.assertNotNull(null);
            Assert.assertTrue(false, "Failed to call transaction list!");
        }

        req = new JSONObject();
        Response response;

        try {
            isContinue = false;

            response = given()
                    .header("Content-Type", "application/json")
                    .header("accept", "*/*")
                    .request(Method.GET, "transaction/" + transactionCode);
            Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST.value());
        } catch(Exception e) {
            Logging.handleException("TestTransactionController", "transactionDetail", 52, "TEST-TRX-002", e.getMessage());
//            Assert.assertNotNull(null);
            Assert.assertTrue(false, e.getMessage());
        }
    }
}
