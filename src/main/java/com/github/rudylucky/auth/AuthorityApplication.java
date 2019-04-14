package com.github.rudylucky.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableJpaRepositories
//@EntityScan
//@EnableWebMvc
//@EnableCaching
//@EnableScheduling
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthorityApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorityApplication.class, args);
    }

}
