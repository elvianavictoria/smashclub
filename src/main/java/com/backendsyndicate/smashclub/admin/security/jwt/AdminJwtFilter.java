package com.backendsyndicate.smashclub.admin.security.jwt;

import com.backendsyndicate.smashclub.admin.service.AdminAuthService;
import com.backendsyndicate.smashclub.admin.service.AdminSessionService;
import com.backendsyndicate.smashclub.common.config.AdminJwtConfig;
import com.backendsyndicate.smashclub.common.security.Crypto;
import com.backendsyndicate.smashclub.common.security.CustomHttpServletRequestWrapper;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.util.RequestCapture;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AdminJwtFilter extends OncePerRequestFilter {
    @Autowired
    private AdminJwtUtility jwtUtility;

    @Autowired
    private AdminAuthService adminAuthService;
    @Autowired
    private AdminSessionService adminSessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        authorization = authorization == null ? "" : authorization;
        Logging.printConsole("Content -Type : "+request.getContentType());
        String token = "";
        String username = "";

        try{
            if(!"".equals(authorization) &&
                    authorization.startsWith("Bearer ") &&
                    authorization.length() > 7){

                Logging.printConsole("[AdminJwtFilter] 46 - Passed invalid checking!");
                Logging.printConsole("[AdminJwtFilter] 47 - Authorization: " + authorization);

                token = authorization.substring(7);//hilangkan Bearer
                if(AdminJwtConfig.getEnableEncrypt().equals("y")){
                    token = Crypto.performDecrypt(token);
                }
                Logging.printConsole("[AdminJwtFilter] 56 - Check token if it is clean: " + token);
                username = jwtUtility.getUsernameFromToken(token);
                Logging.printConsole("[AdminJwtFilter] 58 - Got username from token!");

                String strContentType = request.getContentType()==null?"":request.getContentType();
                if(!strContentType.startsWith("multipart/form-data") || "".equals(strContentType)){
                    request = new CustomHttpServletRequestWrapper(request);
                }
                if(username != null && SecurityContextHolder.getContext().getAuthentication()== null){

                    Logging.printConsole("[AdminJwtFilter] 66 - Validating token!");
                    if(jwtUtility.validateToken(token)){
                        Logging.printConsole("[AdminJwtFilter] 68 - Check if session valid!");
                        if( adminSessionService.isSessionValid(token) ) {
                            Logging.printConsole("[AdminJwtFilter] 70 - Loading username!");
                            UserDetails userDetails = adminAuthService.loadUserByUsername(username);
                            /** persiapan konteks permission / izin / hak ases nya saat di dorong ke controller nantinya */
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }

                    }
                }
            }
        }catch (Exception e){
            Logging.handleException("JwtFilter","doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) ", 42, "JWT-010", e.getMessage() + "; Request: " + RequestCapture.allRequest(request));
        }
        filterChain.doFilter(request, response);
    }
}
