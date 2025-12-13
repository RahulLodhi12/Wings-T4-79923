package com.wings.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler {

	//In-build Exception
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<?> handleUserNotFoundException(Exception ex) {
		System.out.println("chutiye : " + ex.getMessage());
		return ResponseEntity.status(404).body("chutiye : " + ex.getMessage());
	}
	
	
	//Custom Exception -> Method in UserInfoUserDetailsService class
	@ExceptionHandler(CustomUsernameNotFoundException.class)
	public ResponseEntity<?> handleCustomUsernameNotFoundException(CustomUsernameNotFoundException ex){
		return ResponseEntity.status(420).body("again chutiye : " + ex.getMessage());
	}
}
