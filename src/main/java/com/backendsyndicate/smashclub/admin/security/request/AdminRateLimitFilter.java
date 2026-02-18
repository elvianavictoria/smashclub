package com.backendsyndicate.smashclub.admin.security.request;

import com.backendsyndicate.smashclub.common.handler.ResponseHandler;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.util.RequestCapture;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AdminRateLimitFilter extends OncePerRequestFilter {
    @Autowired
    private AdminRateLimitUtility adminRateLimitUtility;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String clientIp = request.getRemoteAddr();
            Bucket bucket = buckets.computeIfAbsent(clientIp, ip -> adminRateLimitUtility.generateBucket(20, Duration.ofMinutes(1)));

            if( bucket.tryConsume(1) ) {
                filterChain.doFilter(request, response);
            } else {
                Logging.printConsole("Too many request from IP: " + clientIp);
                response.setHeader("Content-Type","application/json");
                Map<String, Object> data = new ResponseHandler().constructResponseFormat(
                        "Too many request!",
                        HttpStatus.TOO_MANY_REQUESTS,
                        "X01429",
                        null,
                        request
                );
                response.getOutputStream().println(new JSONObject(data).toString());
            }
        } catch(Exception e) {
            Logging.handleException("AdminRateLimitFilter","doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) ", 40, "RTLMT-010", e.getMessage() + "; Request: " + RequestCapture.allRequest(request));

            response.setHeader("Content-Type","application/json");
            Map<String, Object> data = new ResponseHandler().constructResponseFormat(
                    "Internal server error!",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "X01500",
                    null,
                    request
            );
            response.getOutputStream().println(new JSONObject(data).toString());
        }

    }
}
