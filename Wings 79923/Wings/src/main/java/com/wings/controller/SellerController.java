package com.wings.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wings.models.Product;
import com.wings.repository.CategoryRepo;
import com.wings.repository.ProductRepo;
import com.wings.repository.UserInfoRepository;

@RequestMapping("/api/auth/seller")
public class SellerController {
	ProductRepo productRepo;
	
	UserInfoRepository userRepo;
	
	CategoryRepo categoryRepo;
	
	@PostMapping("/product")
	public ResponseEntity<Object> postProduct(Principal principal, Product product){
		return null;
	}
	
	@GetMapping("/product")
	public ResponseEntity<Object> getAllProducts(Principal principal){
		return null;
	}
	
	@GetMapping("/product/{productId}")
	public ResponseEntity<Object> getProduct(Principal principal, Integer productId){
		return null;
	}
	
	@PutMapping("/product")
	public ResponseEntity<Object> putProduct(Principal principal, Product updatedProduct){
		return null;
	}
	
	@DeleteMapping("/product/{productId}")
	public ResponseEntity<Product> deleteProduct(Principal principal, Integer productId){
		return null;
	}
}
