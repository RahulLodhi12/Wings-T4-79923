package com.wings.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

@Component
public class JwtAuthFilter extends OncePerRequestFilter{

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserInfoUserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		 // 1️. Extract JWT token from the Authorization header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        //Token and Username
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }

        // 2️. Validate and set authentication object
        //User not authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            //Validate token
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authTokenObj =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); //creates an authenticated user object(authToken) that Spring Security will store inside the SecurityContext

                authTokenObj.setDetails(new WebAuthenticationDetails(request)); //setting extra information about the request (like IP address, session ID) to the authentication token
                
                SecurityContextHolder.getContext().setAuthentication(authTokenObj); //store the current logged-in user's details
            }
        }

        // 3️. Continue the remaining filter
        filterChain.doFilter(request, response);
	}
	
	

}
