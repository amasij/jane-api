package com.jane;

import com.jane.configuration.CustomConfiguration;
import com.jane.configuration.WebConfiguration;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EntityScan("com.jane")
@Import({WebConfiguration.class, CacheAutoConfiguration.class, CustomConfiguration.class})
public class JaneApplication extends SpringBootServletInitializer {

	@Inject
	private Logger logger;

	public static void main(String[] args) {
		SpringApplication.run(JaneApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			logger.info("----------------------*-------------------------");
			logger.info("|                                              |");
			logger.info("|           Started Jane                       |");
			logger.info("|                                              |");
			logger.info("----------------------*-------------------------");
		};
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

		return application.sources(JaneApplication.class);
	}




}
