package com.backendsyndicate.smashclub.external.callback;

import com.backendsyndicate.smashclub.external.dto.XenditWebhookDTO;
import com.backendsyndicate.smashclub.external.service.payment.XenditCallbackService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/callback/xendit")
public class XenditCallbackController {
    @Autowired
    private XenditCallbackService xenditCallbackService;

    @PostMapping
    public ResponseEntity<Object> callback(@Valid @RequestBody XenditWebhookDTO dto, HttpServletRequest request) {
        return xenditCallbackService.callback(dto, request);
    }
}
