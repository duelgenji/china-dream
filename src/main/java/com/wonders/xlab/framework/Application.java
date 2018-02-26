package com.wonders.xlab.framework;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.wonders.xlab.framework.repository.MyRepositoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(
        basePackages = {"com.dream"},
        repositoryBaseClass = MyRepositoryImpl.class
)
@EnableScheduling
@EnableAsync
@EntityScan({"${app.basePackages}"})
@ComponentScan({"com.dream"})
public class Application extends SpringBootServletInitializer {

    @Value("${jackson.indent.output}")
    private boolean jacksonIndentOutput = false;

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("HEAD", "GET", "POST", "PUT", "DELETE");
            }
        };
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {

        Hibernate4Module hibernateModule = new Hibernate4Module();
        hibernateModule.disable(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION);

        return Jackson2ObjectMapperBuilder.json()
                .modules(hibernateModule, new JodaModule())
                .simpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .indentOutput(jacksonIndentOutput)
                .serializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.profiles(new String[]{"production"}).sources(new Class[]{Application.class});
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
