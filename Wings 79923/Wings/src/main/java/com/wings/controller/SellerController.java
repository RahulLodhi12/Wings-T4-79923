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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
	    Optional<UserInfo> seller = userRepo.findByUsername(principal.getName());
	    if(seller.isEmpty()) return ResponseEntity.status(404).body("User not found");

	    
	    Optional<Category> categoryOpt = categoryRepo.findByCategoryName(product.getCategory().getCategoryName());
	    if(categoryOpt.isEmpty()) {
	    	Category category = new Category();
	    	category.setCategoryName(product.getCategory().getCategoryName());
	    	categoryRepo.save(category);
	    }
	    
	    product.setCategory(categoryOpt.get()); //categoryOpt.isPresent()
	    product.setPrice(product.getPrice());
	    product.setProductId(product.getProductId());
	    product.setProductName(product.getProductName());
	    product.setSeller(seller.get());

	    productRepo.save(product);

	    URI location = ServletUriComponentsBuilder
	            .fromCurrentContextPath()
	            .path("/api/auth/seller/product/{productId}")
	            .buildAndExpand(product.getProductId())
	            .toUri();

	    return ResponseEntity.created(location).build();
	}
	
	@GetMapping("/product")
	public ResponseEntity<Object> getAllProducts(Principal principal){
//		return null;
		
		Optional<UserInfo> user = userRepo.findByUsername(principal.getName()); //seller
		
		return ResponseEntity.ok(productRepo.findBySellerUserId(user.get().getUserId()));
	}
	
	@GetMapping("/product/{productId}")
	public ResponseEntity<Object> getProduct(Principal principal, @PathVariable Integer productId){
		
		Optional<UserInfo> user = userRepo.findByUsername(principal.getName()); //seller
		
		//combo: user_id + product_id
		Optional<Product> product = productRepo.findBySellerUserIdAndProductId(user.get().getUserId(), productId);
		
		if (product.isPresent()) {
		    return ResponseEntity.ok(product.get());
		} else {
		    return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping("/product")
	public ResponseEntity<Object> putProduct(Principal principal, @RequestBody Product updatedProduct){
//		return null;
		
		Optional<UserInfo> user = userRepo.findByUsername(principal.getName()); //seller
		if(user.isEmpty()) return ResponseEntity.status(404).body("User not found");
		
		
		//combo: user_id + product_id
		Optional<Product> product = productRepo.findBySellerUserIdAndProductId(user.get().getUserId(), updatedProduct.getProductId());
		if(product.isEmpty()) return ResponseEntity.status(404).body("Product not found");
		
		
		Optional<Category> categoryOpt = categoryRepo.findByCategoryName(updatedProduct.getCategory().getCategoryName());
	    if(categoryOpt.isEmpty()) {
	    	Category category = new Category();
	    	category.setCategoryName(updatedProduct.getCategory().getCategoryName());
	    	categoryRepo.save(category);
	    }
	
		product.get().setCategory(categoryOpt.get());
		product.get().setPrice(updatedProduct.getPrice());
		product.get().setProductId(updatedProduct.getProductId());
		product.get().setProductName(updatedProduct.getProductName());
		product.get().setSeller(updatedProduct.getSeller());
		
		productRepo.save(product.get());
		
		return ResponseEntity.ok().body("Updated..");
		
	}
	
	@DeleteMapping("/product/{productId}")
	public ResponseEntity<Product> deleteProduct(Principal principal, @PathVariable Integer productId){
//		return null;
		
		Optional<UserInfo> user = userRepo.findByUsername(principal.getName()); //seller
		if(user.isEmpty()) return ResponseEntity.status(404).build();
		
		//combo: user_id + product_id
		Optional<Product> product = productRepo.findBySellerUserIdAndProductId(user.get().getUserId(), productId);
		if(product.isEmpty()) return ResponseEntity.status(404).build();
		
		productRepo.deleteById(productId);
		return ResponseEntity.ok().build();
	}
}
