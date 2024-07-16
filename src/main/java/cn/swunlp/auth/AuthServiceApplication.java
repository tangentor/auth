package cn.swunlp.auth;

import cn.swunlp.backend.base.security.annotation.EnableAuthConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author TangXi
 */
@SpringBootApplication
@EnableFeignClients
@EnableAuthConfiguration(delayRegister = true,debug = true)
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

}
