package com.dream;

import com.dream.interceptor.LoginInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
public class WebMvcConfiguration {

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	public WebMvcConfigurerAdapter webMvcConfigurerAdapter() {

		return new WebMvcConfigurerAdapter() {

			@Override
			public void configureMessageConverters(
					List<HttpMessageConverter<?>> converters) {

				converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));

				MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
				converter.setObjectMapper(objectMapper);
				converter.setPrettyPrint(true);

				converters.add(converter);
			}

			@Override
			public void addArgumentResolvers(
					List<HandlerMethodArgumentResolver> argumentResolvers) {

				argumentResolvers.add(new PageableHandlerMethodArgumentResolver());
			}

			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor(new LoginInterceptor());
			}

		};
	}

}
