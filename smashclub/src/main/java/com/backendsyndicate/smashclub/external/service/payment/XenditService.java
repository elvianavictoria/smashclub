package com.backendsyndicate.smashclub.external.service.payment;

import com.backendsyndicate.smashclub.common.constant.PaymentMethodConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.external.config.XenditConfig;
import com.backendsyndicate.smashclub.external.dto.XenditResponseDTO;
import com.xendit.XenditClient;
import com.xendit.enums.BankCode;
import com.xendit.exception.XenditException;
import com.xendit.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class XenditService {
    private XenditClient client;
    private String secretKey;
    private String publicKey;

    private String currency;
    private String eWalletCheckoutMethod;

    private ModelMapper modelMapper = new ModelMapper();

    public XenditService() {
        this.secretKey = XenditConfig.getSecretKey();
        this.publicKey = XenditConfig.getPublicKey();

        this.currency = "IDR";
        this.eWalletCheckoutMethod = "ONE_TIME_PAYMENT";
        initClient();
    }

    private void initClient() {
        this.client = new XenditClient.Builder()
                .setApikey(this.secretKey)
                .build();
    }

    private String generateErrorCode(String methodNo, String errorNo) {
        return "XEN-" + methodNo + "E" + errorNo;
    }

    public XenditResponseDTO createPayment(String externalId, BigDecimal amount, String payerEmail, String description) {
        XenditResponseDTO response = new XenditResponseDTO();

        String paymentUrl = this.createInvoice(externalId, amount, payerEmail, description);
        response.setExternalId(externalId);
        response.setInvoiceUrl(paymentUrl);

        return response;
    }

    public XenditResponseDTO createPayment(String externalId, BigDecimal amount, String payerEmail, String description, int paymentMethodId) {
        XenditResponseDTO response = new XenditResponseDTO();

        int paymentMethodCategoryId = (int) PaymentMethodConstant.getPaymentMethod(paymentMethodId).get("categoryId");

        if( paymentMethodCategoryId == PaymentMethodConstant.CATEGORY_VIRTUAL_ACCOUNT ) {
            String bankCode = "";
            switch(paymentMethodId) {
                case PaymentMethodConstant.VA_BCA:
                    bankCode = "BCA";
                    break;
                case PaymentMethodConstant.VA_MANDIRI:
                    bankCode = "MANDIRI";
                    break;
                case PaymentMethodConstant.VA_BRI:
                    bankCode = "BRI";
                    break;
                case PaymentMethodConstant.VA_BNI:
                    bankCode = "BNI";
                    break;
                default:
                    break;
            }

            if( bankCode.isEmpty() ) {
                return null;
            }

            FixedVirtualAccount va = this.createClosedVA(externalId, bankCode, "SMASHCLUB", amount);
            response = modelMapper.map(va, XenditResponseDTO.class);
            response.setName("SMASHCLUB");
        } else if( paymentMethodCategoryId == PaymentMethodConstant.CATEGORY_EWALLET ) {
            String channelCode = "";

            switch(paymentMethodId) {
                case PaymentMethodConstant.EW_DANA:
                    channelCode = "ID_DANA";
                    break;
                case PaymentMethodConstant.EW_SHOPEEPAY:
                    channelCode = "ID_SHOPEEPAY";
                    break;
                case PaymentMethodConstant.EW_OVO:
                    channelCode = "ID_OVO";
                    break;
                default:
                    break;
            }

            if( channelCode.isEmpty() ) {
                return null;
            }

            EWalletCharge ew = createEWalletInvoice(externalId, amount, channelCode);
            response = modelMapper.map(ew, XenditResponseDTO.class);
        } else if( paymentMethodCategoryId == PaymentMethodConstant.CATEGORY_QRIS ) {
            QRCode qr = createQR(externalId, amount);
            response = modelMapper.map(qr, XenditResponseDTO.class);
        }

        return response;
    }

    public String createInvoice(String externalId, BigDecimal amount, String payerEmail, String description) {
        String invoiceUrl = null;

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("external_id", externalId);
            params.put("amount", amount);
            params.put("payer_email", payerEmail);
            params.put("description", description);

            Invoice invoice = client.invoice.create(params);
            invoiceUrl = invoice.getInvoiceUrl();
        } catch(XenditException xe) {
            Logging.handleException("XenditService", "createInvoice(String externalId, BigDecimal amount, String payerEmail, String description)", 126, generateErrorCode("01", "010"), xe.getMessage());
        }

        return invoiceUrl;
    }

    public FixedVirtualAccount createClosedVA(String externalId, String bankCode, String name, BigDecimal amount) {
        FixedVirtualAccount virtualAccount = null;

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("external_id", externalId);
            params.put("bank_code", BankCode.fromString(bankCode));
            params.put("name", name);
            params.put("amount", amount);

            virtualAccount = client.fixedVirtualAccount.createOpen(params);
        } catch(XenditException xe) {
            Logging.handleException("XenditService", "createClosedVA(String externalId, String bankCode, String name, BigDecimal amount)", 136, generateErrorCode("02", "010"), xe.getMessage());
        }

        return virtualAccount;
    }

    public EWalletCharge createEWalletInvoice(String referenceId, BigDecimal amount, String channelCode) {
        EWalletCharge charge = null;

        try {
            Map<String, String> channelProperties = new HashMap<>();
            channelProperties.put("success_redirect_url", XenditConfig.getSuccessRedirectUrl());

            Map<String, Object> params = new HashMap<>();
            params.put("reference_id", referenceId);
            params.put("currency", this.currency);
            params.put("amount", amount);
            params.put("checkout_method", this.eWalletCheckoutMethod);
            params.put("channel_code", channelCode);
            params.put("channel_properties", channelProperties);
            params.put("source", channelCode);

            charge = client.eWallet.createEWalletCharge(params);
        } catch(XenditException xe) {
            Logging.handleException("XenditService", "createEWalletInvoice(String referenceId, BigDecimal amount, String channelCode, String customerId)", 154, generateErrorCode("03", "010"), xe.getMessage());
        }

        return charge;
    }

    public QRCode createQR(String referenceId, BigDecimal amount) {
        QRCode qr = null;

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("reference_id", referenceId);
            params.put("type", QRCode.QRCodeType.DYNAMIC);
            params.put("currency", this.currency);
            params.put("amount", amount);

            qr = client.qrCode.createQRCode(params);
        } catch(XenditException xe) {
            Logging.handleException("XenditService", "createQR(String referenceId, BigDecimal amount)", 177, generateErrorCode("04", "010"), xe.getMessage());
        }

        return qr;
    }
}
