package com.easyplan._global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;
	
	private final CsrfCookieFilter csrfCookieFilter;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		CsrfTokenRequestAttributeHandler csrfRequestHandler = new CsrfTokenRequestAttributeHandler();
		csrfRequestHandler.setCsrfRequestAttributeName(null);
		
		http
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			
			.formLogin(AbstractHttpConfigurer::disable)
			
			.httpBasic(AbstractHttpConfigurer::disable)
			
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			
			.csrf(csrf -> csrf
					.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
					.csrfTokenRequestHandler(csrfRequestHandler)
			)
			
			.addFilterAfter(csrfCookieFilter, BasicAuthenticationFilter.class)
			
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/api/csrf").permitAll()
					
					.requestMatchers("/api/auth/**").permitAll()
					
					.requestMatchers("/api/members", "/api/members/check-email", "/api/members/check-nickname", "/api/members/verify").permitAll()
					
					.anyRequest().authenticated())
			;
		
		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		
		configuration.addAllowedOrigin("http://localhost");
		configuration.addAllowedOrigin("http://localhost:80");
    configuration.addAllowedOrigin("https://localhost:5173");
    
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
	}
	
}
