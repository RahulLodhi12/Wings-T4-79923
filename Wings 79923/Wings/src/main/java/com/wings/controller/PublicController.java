package com.wings.controller;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wings.models.Product;
import com.wings.repository.ProductRepo;
import com.wings.repository.UserInfoRepository;

@RequestMapping("/api/public")
public class PublicController {
	ProductRepo productRepo;
	
	UserInfoRepository userRepo;
	
	private AuthenticationManager authenticationManager;
	
	@GetMapping("/product/search")
	public List<Product> getProducts(@RequestParam String keyword){
		return null;
	}
}
