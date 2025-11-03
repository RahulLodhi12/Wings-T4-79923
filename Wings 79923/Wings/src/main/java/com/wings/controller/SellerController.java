package com.wings.controller;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wings.models.Category;
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
//		return null;
		Category category = new Category();
		category.setCategoryId(product.getCategory().getCategoryId());
		category.setCategoryName(product.getCategory().getCategoryName());
		categoryRepo.save(category);
		
		productRepo.save(product);
		
		// Build the URI for the new product
	    URI location = URI.create("/product/" + product.getProductId());
		
		return ResponseEntity.created(location).body("Product Added Successfully");
	
	}
	
	@GetMapping("/product")
	public List<Product> getAllProducts(Principal principal){
//		return null;
		
		Optional<UserInfo> user = userRepo.findByUsername(principal.getName()); //seller
		
		return productRepo.findBySellerUserId(user.get().getUserId());
	}
	
	@GetMapping("/product/{productId}")
	public Optional<Product> getProduct(Principal principal, @PathVariable Integer productId){
//		return null;
//		Optional<Product> product = productRepo.findById(productId);
//		if(product.isEmpty()) return ResponseEntity.badRequest().body("Product not found");
		
		Optional<UserInfo> user = userRepo.findByUsername(principal.getName()); //seller
//		Optional<Product> product = productRepo.findById(productId);
		
		
		//combo: user_id + product_id
		return productRepo.findBySellerUserIdAndProductId(user.get().getUserId(), productId);
	}
	
	@PutMapping("/product")
	public ResponseEntity<Object> putProduct(Principal principal, @RequestBody Product updatedProduct){
//		return null;
		
		Optional<UserInfo> user = userRepo.findByUsername(principal.getName()); //seller
		if(user.isEmpty()) return ResponseEntity.badRequest().body("User not found");
		
		
		//combo: user_id + product_id
		Optional<Product> product = productRepo.findBySellerUserIdAndProductId(user.get().getUserId(), updatedProduct.getProductId());
		if(product.isEmpty()) return ResponseEntity.badRequest().body("Product not found");
		
	
		product.get().setCategory(updatedProduct.getCategory());
		product.get().setPrice(updatedProduct.getPrice());
		product.get().setProductId(updatedProduct.getProductId());
		product.get().setProductName(updatedProduct.getProductName());
		product.get().setSeller(updatedProduct.getSeller());
		
		productRepo.save(product.get());
		
		return ResponseEntity.ok().body("Updated..");
		
	}
	
	@DeleteMapping("/product/{productId}")
	public ResponseEntity<Object> deleteProduct(Principal principal, @PathVariable Integer productId){
//		return null;
		
		Optional<UserInfo> user = userRepo.findByUsername(principal.getName()); //seller
		if(user.isEmpty()) return ResponseEntity.badRequest().body("User not found");
		
		//combo: user_id + product_id
		Optional<Product> product = productRepo.findBySellerUserIdAndProductId(user.get().getUserId(), productId);
		if(product.isEmpty()) return ResponseEntity.badRequest().body("Product not found");
		
		productRepo.deleteById(productId);
		return ResponseEntity.ok().body("Delete from Product Table..");
	}
}
