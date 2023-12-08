package com.cksiow.ai.advisor;


import com.universal.core.library.configuration.BaseRepositoryFactoryBean;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.SneakyThrows;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan(basePackages = {
        "com.universal.core.library.pagination",
        "com.cksiow",
        "com.universal.core.library.configuration",
        "com.universal.core.library.snowflake",
        "com.universal.core.library.exception",
        "com.universal.core.library.configure.cors",
        "com.universal.core.library.google"
})
@EnableJpaRepositories(repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EnableScheduling
@EnableAsync
public class AIAdvisorApplication extends SpringBootServletInitializer {


    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(AIAdvisorApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AIAdvisorApplication.class);
    }

    @Bean
    public GroupedOpenApi publicApi() {

        return GroupedOpenApi.builder()
                .group("cksiow")
                .packagesToScan("com.cksiow")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("AI Advisor Application")
                        .description("AI Advisor Application")
                        .version("1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("AI Advisor Wiki Documentation")
                )
                ;
    }
}
