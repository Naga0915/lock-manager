package jp.oecu.lockmng.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@EnableJdbcRepositories(
    basePackages = "org.springframework.session",
    transactionManagerRef = "jdbcTransactionManager"
)
public class JdbcRepositoryConfig {

}
