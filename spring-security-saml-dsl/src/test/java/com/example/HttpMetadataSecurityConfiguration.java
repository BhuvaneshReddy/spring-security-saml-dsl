package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static com.example.dsl.OktaConfigurer.okta;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@Profile("http-metadata")
public class HttpMetadataSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${okta.metadata.path}")
    private String httpMetadataPath;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SecurityConfigurer securityConfigurerAdapter =
            okta()
                .keyStore()
                    .storeFilePath("saml/colombia.jks")
                    .password("colombia-password")
                    .keyname("colombia")
                    .keyPassword("colombia-password")
                    .and()
                .metadataFilePath(httpMetadataPath)
                .protocol("https")
                .hostname("localhost:8443")
                .basePath("/")
                .entityId("com:example");

        http.apply(securityConfigurerAdapter);

        http
            .requiresChannel()
            .anyRequest().requiresSecure();

        http
            .authorizeRequests()
            .antMatchers("/saml/**").permitAll()
            .antMatchers("/health").permitAll()
            .antMatchers("/error").permitAll()
            .anyRequest().authenticated();
    }
}
