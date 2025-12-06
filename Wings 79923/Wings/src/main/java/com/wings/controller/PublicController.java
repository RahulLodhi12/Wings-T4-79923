package com.wings.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wings.models.Product;
import com.wings.repository.ProductRepo;
import com.wings.repository.UserInfoRepository;

@RestController
@RequestMapping("/api/public")
public class PublicController {
	
	@Autowired
	ProductRepo productRepo;
	
	@Autowired
	UserInfoRepository userRepo;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@GetMapping("/product/search")
	public ResponseEntity<Object> getProducts(@RequestParam(required = false) String keyword){
//		@RequestParam String keyword means: keyword is required = true by default
//  	If the client does not send the parameter
//		Spring MVC automatically throws: MissingServletRequestParameterException
//		Spring Boot translates this exception to: HTTP 400 Bad Request
		
//		return productRepo.findByProductNameContainingIgnoreCaseOrCategoryCategoryNameContainingIgnoreCase(keyword, keyword);
		
		List<Product> list = productRepo.findByProductNameContainingIgnoreCaseOrCategoryCategoryNameContainingIgnoreCase(keyword, keyword);
		if(list.isEmpty()) {
			return ResponseEntity.status(400).build();
		}
		
		return ResponseEntity.status(200).body(list);
	}
}
