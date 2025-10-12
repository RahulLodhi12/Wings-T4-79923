package com.wings.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wings.models.CartProduct;
import com.wings.models.Product;
import com.wings.repository.CartProductRepo;
import com.wings.repository.CartRepo;
import com.wings.repository.ProductRepo;
import com.wings.repository.UserInfoRepository;

@RequestMapping("/api/auth/consumer")
public class ConsumerController {
	
	ProductRepo productRepo;
	
	CartRepo cartRepo;
	
	CartProductRepo cpRepo;
	
	UserInfoRepository userRepo;
	
	@GetMapping("/cart")
	public ResponseEntity<Object> getCart(Principal principal){
		return null;
	}
	
	@PostMapping("/cart")
	public ResponseEntity<Object> postCart(Principal principal, Product product){
		return null;
	}
	
	@PutMapping("/cart")
	public ResponseEntity<Object> putCart(Principal principal, CartProduct cp){
		return null;
	}
	
	@DeleteMapping("/cart")
	public ResponseEntity<Object> deleteCart(Principal principal, Product product){
		return null;
	}
	
	

}
