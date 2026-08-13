package com.cctns.apprehend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cctns.apprehend.utility.EncryptionUtil;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@SpringBootApplication
@EnableFeignClients
public class CctnsApprehendServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CctnsApprehendServiceApplication.class, args);
    }

    private final EncryptionUtil encryptionUtil;
    private String password;


    //Change the name of the class and the constructor
    public CctnsApprehendServiceApplication(EncryptionUtil encryptionUtil, @Value("${spring.datasource.password}") String password) {
        this.encryptionUtil = encryptionUtil;
        this.password = password;
    }

    @PostConstruct
    public void init() {
        if (!encryptionUtil.isBase64Encoded(password)) {
            password = encryptionUtil.encrypt(password);
            log.info("Encrypted Password: {}", password);
        }
    }
}
