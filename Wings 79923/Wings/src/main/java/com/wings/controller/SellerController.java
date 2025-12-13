package com.wings.controller;

import java.net.URI;
//import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.wings.models.Cart;
import com.wings.models.Category;
//import com.wings.models.Category;
import com.wings.models.Product;
import com.wings.repository.CategoryRepo;
import com.wings.repository.ProductRepo;
import com.wings.repository.UserInfoRepository;
import com.wings.models.UserInfo;

@RestController
@RequestMapping("/api/auth/seller")
public class SellerController {
	
	@Autowired
	ProductRepo productRepo;
	
	@Autowired
	UserInfoRepository userRepo;
	
	@Autowired
	CategoryRepo categoryRepo;
	
	@PostMapping("/product")
	public ResponseEntity<Object> postProduct(Principal principal, @RequestBody Product product){
		try {
			
			String username = principal.getName();
        	
        	Optional<UserInfo> userInfo = userRepo.findByUsername(username);
        	
        	//Sanity Check #1 -> check user null or not
        	if(userInfo.isEmpty()) {
        		return ResponseEntity.status(404).body("User not found");
        	}
        	
        	//Sanity Check #2 -> check role
        	if(!userInfo.get().getRoles().equals("SELLER")) {
        		return ResponseEntity.status(403).body("You don't have access");
        	}
        	
        	//Logic
        	Optional<Category> catOpt = categoryRepo.findByCategoryName(product.getCategory().getCategoryName());
        	
        	product.setCategory(catOpt.get());
        	product.setSeller(userInfo.get());
        	
        	Product savedProduct = productRepo.save(product);
        	
        	String location = "http://localhost:8000/api/auth/seller/product/" + savedProduct.getProductId();
        	
        	
        	return ResponseEntity.created(URI.create(location)).body(savedProduct);
			
		} catch(Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}
	
	@GetMapping("/product")
	public ResponseEntity<Object> getAllProducts(Principal principal){	
		try {
			
			String username = principal.getName();
        	
        	Optional<UserInfo> userInfo = userRepo.findByUsername(username);
        	
        	//Sanity Check #1 -> check user null or not
        	if(userInfo.isEmpty()) {
        		return ResponseEntity.status(404).body("User not found");
        	}
        	
        	//Sanity Check #2 -> check role
        	if(!userInfo.get().getRoles().equals("SELLER")) {
        		return ResponseEntity.status(403).body("You don't have access");
        	}
        	
        	//Logic
        	List<Product> list = productRepo.findBySellerUserId(userInfo.get().getUserId());
        	
        	return ResponseEntity.ok(list);
			
		} catch(Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}
	
	@GetMapping("/product/{productId}")
	public ResponseEntity<Object> getProduct(Principal principal, @PathVariable Integer productId){
		try {
			
			String username = principal.getName();
        	
        	Optional<UserInfo> userInfo = userRepo.findByUsername(username);
        	
        	//Sanity Check #1 -> check user null or not
        	if(userInfo.isEmpty()) {
        		return ResponseEntity.status(404).body("User not found");
        	}
        	
        	//Sanity Check #2 -> check role
        	if(!userInfo.get().getRoles().equals("SELLER")) {
        		return ResponseEntity.status(403).body("You don't have access");
        	}
        	
        	//Logic
        	Optional<Product> prodOpt = productRepo.findBySellerUserIdAndProductId(userInfo.get().getUserId(), productId);
        	if(prodOpt.isEmpty()) {
        		return ResponseEntity.status(404).body(null);
        	}
        	
        	return ResponseEntity.ok(prodOpt.get());
        	
			
		} catch(Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
		
	}
	
	@PutMapping("/product")
	public ResponseEntity<Object> putProduct(Principal principal, @RequestBody Product updatedProduct){
		try {
			
			String username = principal.getName();
        	
        	Optional<UserInfo> userInfo = userRepo.findByUsername(username);
        	
        	//Sanity Check #1 -> check user null or not
        	if(userInfo.isEmpty()) {
        		return ResponseEntity.status(404).body("User not found");
        	}
        	
        	//Sanity Check #2 -> check role
        	if(!userInfo.get().getRoles().equals("SELLER")) {
        		return ResponseEntity.status(403).body("You don't have access");
        	}
        	
        	//Logic
        	Optional<Product> prodOpt = productRepo.findById(updatedProduct.getProductId());
        	if(prodOpt.isEmpty()) {
        		return ResponseEntity.status(404).body(null);
        	}
        	
        	Optional<Category> catOpt = categoryRepo.findByCategoryName(updatedProduct.getCategory().getCategoryName());
        	
        	updatedProduct.setCategory(catOpt.get());
        	updatedProduct.setSeller(userInfo.get());
        	
        	Product savedProduct = productRepo.save(updatedProduct);
        	
        	return ResponseEntity.ok(savedProduct);
        	
			
		} catch(Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
		
	}
	
	@DeleteMapping("/product/{productId}")
	public ResponseEntity<Product> deleteProduct(Principal principal, @PathVariable Integer productId){
		try {
			
			String username = principal.getName();
        	
        	Optional<UserInfo> userInfo = userRepo.findByUsername(username);
        	
        	//Sanity Check #1 -> check user null or not
        	if(userInfo.isEmpty()) {
        		return ResponseEntity.status(404).body(null);
        	}
        	
        	//Sanity Check #2 -> check role
        	if(!userInfo.get().getRoles().equals("SELLER")) {
        		return ResponseEntity.status(403).body(null);
        	}
        	
        	//Logic
        	Optional<Product> prodOpt = productRepo.findBySellerUserIdAndProductId(userInfo.get().getUserId(), productId);
        	if(prodOpt.isEmpty()) {
        		return ResponseEntity.status(404).body(null);
        	}
        	
        	productRepo.delete(prodOpt.get());
        	
        	return ResponseEntity.ok().build();
        	
			
		} catch(Exception e) {
			return ResponseEntity.status(500).body(null);
		}
	}
}
