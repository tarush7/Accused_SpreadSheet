package com.cctns.apprehend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This Class represents the Cors Config Apply CORS for
 * <a href="http://localhost:3000">Cors-End-Point</a> Allowed HTTP methods : GET
 * ,POST ,PUT ,DELETE All Headers are allowed Credentials are Cached for 1 Hour
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${cors.url}")
	private String url;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**")
			.allowedOriginPatterns(url)
			.allowedMethods("GET", "POST", "PUT", "DELETE")
			.allowedHeaders("*")
			.allowCredentials(true)
			.maxAge(3600);
	}
}
