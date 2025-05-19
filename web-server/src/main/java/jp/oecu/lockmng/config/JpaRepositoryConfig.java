package jp.oecu.lockmng.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "jp.oecu.lockmng.repository.jpa",
    transactionManagerRef = "jpaTransactionManager"
)
public class JpaRepositoryConfig {
    
}
