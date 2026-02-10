package com.backendsyndicate.smashclub.payment;

import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.constant.PaymentMethodConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.payment.dto.request.ReqPaymentTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespCreateTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespRefundTransactionDTO;
import com.backendsyndicate.smashclub.payment.service.PaymentService;
import com.backendsyndicate.smashclub.util.DataGenerator;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestPaymentService extends AbstractTestNGSpringContextTests {
    private Random rand;
    private DataGenerator dataGenerator;
    private PaymentService paymentService;

    // Create Trx
    private String customerId;
    private BigDecimal totalPrice;
    private String referenceCode;
    private int transactionType;

    // Payment Trx
    private String transactionCode;
    private Map<String, Object> paymentData;
    private int paymentMethodId;

    // Refund Trx
    private String notes;

    private boolean isContinue;

    @BeforeClass
    public void init() {
        RestAssured.baseURI = "http://localhost:8080";
        rand = new Random();
        dataGenerator = new DataGenerator();
        isContinue = false;

        customerId = "123";
        totalPrice = BigDecimal.valueOf(200000);
        referenceCode = "AAA";
        transactionType = TransactionTypeConstant.COURT_BOOKING;

        paymentMethodId = PaymentMethodConstant.VA_BCA;

        notes = "Cancel transaksi";
    }

    @Test(priority = 0)
    public void transactionOrder() {
        RespCreateTransactionDTO response;

        try {
            response = paymentService.createTransaction(customerId, totalPrice, referenceCode, transactionType, 0);
            transactionCode = response.getTransactionCode();
            paymentData = response.getPaymentData();

            isContinue = transactionCode != null && !transactionCode.isBlank();

            Assert.assertTrue(isContinue, "Transaction code is required!");
            Assert.assertTrue(paymentData.containsKey("invoiceUrl"), "Payment link is required!");
        } catch(Exception e) {
            Logging.handleException("TestTransactionController", "transactionList", 42, "TEST-PYMT-001", e.getMessage());
            Assert.assertNotNull(null);
        }
    }

    @Test(priority = 10)
    public void transactionPayment() {
        if( !isContinue ) {
            Assert.assertNotNull(null);
        }

        ReqPaymentTransactionDTO request = new ReqPaymentTransactionDTO();
        Response response;

        try {
            isContinue = false;
            request.setPaymentMethodId(paymentMethodId);
            response = given().header("Content-Type", "application/json").header("accept", "application/json").body(request).request(Method.POST, "/transaction/payment/" + transactionCode);
            JsonPath jPath = response.jsonPath();
            transactionCode = jPath.getString("data.transactionCode");
            isContinue = transactionCode != null && !transactionCode.isEmpty();

            Assert.assertTrue(isContinue, "Failed to process payment!");
        } catch(Exception e) {
            Logging.handleException("TestTransactionController", "transactionPayment", 86, "TEST-PYMT-002", e.getMessage());
            Assert.assertNotNull(null);
        }
    }

    @Test(priority = 20)
    public void transactionRefund() {
        if( !isContinue ) {
            Assert.assertNotNull(null);
        }

        RespRefundTransactionDTO response;

        try {
            response = paymentService.refundTransaction(transactionCode, notes);
            isContinue = response.isRequested();

            Assert.assertTrue(isContinue, "Transaction code is required!");
        } catch(Exception e) {
            Logging.handleException("TestTransactionController", "transactionRefund", 109, "TEST-PYMT-003", e.getMessage());
            Assert.assertNotNull(null);
        }
    }
}
