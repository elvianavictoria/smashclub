package com.backendsyndicate.smashclub.payment;

import com.backendsyndicate.smashclub.common.util.Logging;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;

public class TestTransactionService {
    private JSONObject req;
    private String transactionCode;
    private boolean isContinue;

    @BeforeClass
    public void init() {
        RestAssured.baseURI = "http://localhost:8080";
        req = new JSONObject();
        isContinue = false;
    }

    @Test(priority = 0)
    public void transactionList() {
        Response response;

        try {
            req.put("startDate", LocalDate.now());
            req.put("endDate", LocalDate.now());
            req.put("page", 0);
            req.put("size", 25);

            response = given().header("Content-Type", "application/json").header("accept", "*/*").body(req).request(Method.GET, "transaction/");
            JsonPath jPath = response.jsonPath();

            Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
            isContinue = true;
        } catch(Exception e) {
            Logging.handleException("TestTransactionController", "transactionList", 42, "TEST-TRX-001", e.getMessage());
            Assert.assertNotNull(null);
        }
    }

    @Test(priority = 10)
    public void transactionDetail() {
        if( !isContinue ) {
            Assert.assertNotNull(null);
        }

        Response response;

        try {
            isContinue = false;

            response = given().header("Content-Type", "application/json").header("accept", "*/*").body(req).request(Method.GET, "transaction/" + transactionCode);
            Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
        } catch(Exception e) {
            Logging.handleException("TestTransactionController", "transactionDetail", 52, "TEST-TRX-002", e.getMessage());
            Assert.assertNotNull(null);
        }
    }
}
