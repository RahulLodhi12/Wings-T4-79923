package com.wings.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.wings.filter.JwtAuthFilter;

@Configuration
public class SecurityConfig {
	
	@Autowired
	private JwtAuthFilter authFilter;
	
	//----------XXXXXXXXXX------------
	@Autowired
	private UserInfoUserDetailsService userInfoUserDetailsService;
	
	@Bean
	UserDetailsService userDetailsService() {
		return userInfoUserDetailsService;
	}
	//-----------OR----------------
	
//	@Autowired
//	private UserDetailsService userDetailsService;
	
	
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
	}
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		//step 1. disable csrf
		http.csrf(csrf -> csrf.disable());
		//step 2: change the session management policy
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		//step 3: register custom filter [JWTAuthFilter]
		http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
		//step 4: register url's
		http.authenticationProvider(authenticationProvider());
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/public/**").permitAll()
				.requestMatchers("/api/auth/consumer/**").hasAuthority("CONSUMER")
				.requestMatchers("/api/auth/seller/**").hasAuthority("SELLER")
				.anyRequest().authenticated());

     return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
//		return new NoOpPasswordEncoder.getInstance(); //works with springboot 2
	}
	
	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); //This provider works with DB
	    provider.setUserDetailsService(userDetailsService());
	    provider.setPasswordEncoder(passwordEncoder());
	    return provider;
	}
	
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
//		return null;
		return config.getAuthenticationManager();
	}

}
