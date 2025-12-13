package com.wings.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wings.config.UserInfoUserDetailsService;
import com.wings.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@Component
//public class JwtAuthFilter extends OncePerRequestFilter{
//
//	@Autowired
//	private JwtService jwtService;
//	
//	@Autowired
//	private UserInfoUserDetailsService userDetailsService;
//	
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		
//		 // 1️. Extract JWT token from the Authorization header
//        String authHeader = request.getHeader("Authorization");
//        String token = null;
//        String username = null;
//
//        //Token and Username
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//            username = jwtService.extractUsername(token);
//        }
//
//        // 2️. Validate and set authentication object
//        //User not authenticated
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//            
//            //Validate token
//            if (jwtService.validateToken(token, userDetails)) {
//            	//creates an authenticated user object(authToken) that Spring Security will store inside the SecurityContext
//                UsernamePasswordAuthenticationToken authTokenObj =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); 
//                
//              //setting extra information about the request (like IP address, session ID) to the authentication token
//                authTokenObj.setDetails(new WebAuthenticationDetails(request));
//                
//              //store the current logged-in user's details
//                SecurityContextHolder.getContext().setAuthentication(authTokenObj); 
//            }
//        }
//
//        // 3️. Continue the remaining filter
//        filterChain.doFilter(request, response);
//	}
//
//}

@Component
public class JwtAuthFilter extends OncePerRequestFilter{

	@Autowired
	JwtService jwtService;
	
	@Autowired
	UserInfoUserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String authHeader = request.getHeader("Authorization");
		String token=null;
		String username=null;
		
		if(authHeader!=null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			username = jwtService.extractUsername(token);
		}
		
		
		if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			
			if(jwtService.validateToken(token, userDetails)) {
				//create -> set -> set
				
				UsernamePasswordAuthenticationToken authObj = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				
				authObj.setDetails(new WebAuthenticationDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authObj);
				
			}
		}
		
		
		filterChain.doFilter(request, response);
		
	}
	
}
