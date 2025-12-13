package com.wings.config;

import java.applet.AudioClip;

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
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import com.wings.filter.JwtAuthFilter;

//@Configuration
//public class SecurityConfig {
//	
//	@Autowired
//	private JwtAuthFilter authFilter;
//	
//	//----------XXXXXXXXXX------------
//	@Autowired
//	private UserInfoUserDetailsService userInfoUserDetailsService;
//	
//	@Bean
//	UserDetailsService userDetailsService() {
//		return userInfoUserDetailsService;
//	}
//	//-----------OR----------------
//	
////	@Autowired
////	private UserDetailsService userDetailsService;
//	
//	
//	@Bean
//	WebSecurityCustomizer webSecurityCustomizer() {
////		return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
//		
//		return web -> web
//				.ignoring()
//				.requestMatchers("/h2-console/**");
//	}
//	
//	
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
//		
//		//step 1. disable csrf
//		http.csrf(csrf -> csrf.disable());
//		//step 2: change the session management policy
//		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//		//step 3: register custom filter [JWTAuthFilter]
//		http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
//		//step 4: register url's
//		http.authenticationProvider(authenticationProvider());
//		http.authorizeHttpRequests(auth -> auth
//				.requestMatchers("/api/public/**").permitAll()
//				.requestMatchers("/api/auth/consumer/**").hasAuthority("CONSUMER")
//				.requestMatchers("/api/auth/seller/**").hasAuthority("SELLER")
//				.anyRequest().authenticated());
//
//     return http.build();
//	}
//	
//	@Bean
//	PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
////		return NoOpPasswordEncoder.getInstance();
//	}
//	
//	@Bean
//	AuthenticationProvider authenticationProvider() {
//		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); //This provider works with DB
//	    provider.setUserDetailsService(userDetailsService());
//	    provider.setPasswordEncoder(passwordEncoder());
//	    return provider;
//	}
//	
//	
//	@Bean
//	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
////		return null;
//		return config.getAuthenticationManager();
//	}
//
//}

@Component
public class SecurityConfig {
	
	@Autowired
	JwtAuthFilter authFilter;
	
	@Autowired
	UserInfoUserDetailsService userInfoUserDetailsService;
	
	@Bean
	UserDetailsService userDetailsService() {
		return userInfoUserDetailsService;
	}
	
	@Autowired
	AuthEntryPoint authEntryPoint;
	
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web
				.ignoring()
				.requestMatchers("/h2-console/**");
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.csrf(csrf -> csrf.disable());
		
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
		
		http.authenticationProvider(authenticationProvider());
		
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/public/**").permitAll()
				.requestMatchers("/api/auth/consumer/**").hasAuthority("CONSUMER")
				.requestMatchers("/api/auth/seller/**").hasAuthority("SELLER")
				.anyRequest().authenticated()
				);
		
		
		http.exceptionHandling(ex->ex.authenticationEntryPoint(authEntryPoint));
		
		return http.build();
	}
	
	
	@Bean
	PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
		return NoOpPasswordEncoder.getInstance();
	}
	
	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
}

