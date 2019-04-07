package com.github.rudylucky.auth.dao;

import com.github.rudylucky.auth.dao.entity.UserDbo;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackageClasses = DaoConfig.class)
@EnableJpaRepositories(basePackageClasses = DaoConfig.class)
@EntityScan(basePackageClasses = UserDbo.class)
public class DaoConfig {
    public interface LIMIT{
        int LENGTH_ROLE_REMARK = 1000;
        int LENGTH_DEFAULT = 255;
    }

}
