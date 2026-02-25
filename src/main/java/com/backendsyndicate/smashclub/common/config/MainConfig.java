package com.backendsyndicate.smashclub.common.config;

import com.backendsyndicate.smashclub.common.security.Crypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:application.properties")
public class MainConfig {

    @Autowired
    private Environment env;
    private static int cacheLimit;

    private static String appBackendUrl;
    private static String appFrontendUrl;

    @Primary
    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dBuild = DataSourceBuilder.create();

        dBuild.driverClassName(env.getProperty("spring.datasource.driverClassName"));
        dBuild.url(Crypto.performDecrypt(env.getProperty("spring.datasource.url")));
        dBuild.username(Crypto.performDecrypt(env.getProperty("spring.datasource.username")));
        dBuild.password(Crypto.performDecrypt(env.getProperty("spring.datasource.password")));

        return dBuild.build();
    }

    public static int getCacheLimit() {
        return cacheLimit;
    }

    @Value("${spring.request.cache.limit}")
    public void setCacheLimit(int cacheLimit) {
        this.cacheLimit = cacheLimit;
    }

    public static String getAppBackendUrl() {
        return appBackendUrl;
    }

    @Value("${app.backend.url}")
    public void setAppBackendUrl(String appBackendUrl) {
        this.appBackendUrl = appBackendUrl;
    }

    public static String getAppFrontendUrl() {
        return appFrontendUrl;
    }

    @Value("${app.frontend.url}")
    public void setAppFrontendUrl(String appFrontendUrl) {
        this.appFrontendUrl = appFrontendUrl;
    }
}
