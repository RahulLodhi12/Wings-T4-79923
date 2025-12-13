package com.wings.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wings.models.Cart;
import com.wings.models.CartProduct;
import com.wings.models.Product;
import com.wings.models.UserInfo;
import com.wings.repository.CartProductRepo;
import com.wings.repository.CartRepo;
import com.wings.repository.ProductRepo;
import com.wings.repository.UserInfoRepository;
//import com.wings1SS5.model.UserModel;

@RestController
@RequestMapping("/api/auth/consumer")
public class ConsumerController {
	
	@Autowired
	ProductRepo productRepo;
	
	@Autowired
	CartRepo cartRepo;
	
	@Autowired
	CartProductRepo cpRepo;
	
	@Autowired
	UserInfoRepository userRepo;
	
	@GetMapping("/cart")
	public ResponseEntity<Object> getCart(Principal principal){
		try {
			
			String username = principal.getName();
        	
        	Optional<UserInfo> userInfo = userRepo.findByUsername(username);
        	
        	//Sanity Check #1 -> check user null or not
        	if(userInfo.isEmpty()) {
        		return ResponseEntity.status(404).body("User not found");
        	}
        	
        	//Sanity Check #2 -> check role
        	if(!userInfo.get().getRoles().equals("CONSUMER")) {
        		return ResponseEntity.status(403).body("You don't have access");
        	}
        	
        	//Logic
        	Optional<Cart> cartOpt = cartRepo.findByUserUsername(username);
        	
        	if(cartOpt.isEmpty()) {
        		return ResponseEntity.status(404).body("not found");
        	}
        	
        	return ResponseEntity.status(200).body(cartOpt.get());
			
		} catch(Exception e) {
			return ResponseEntity.status(500).body("Internal Server error");
		}
	}
	
	@PostMapping("/cart")
	public ResponseEntity<Object> postCart(Principal principal, @RequestBody Product product) {
		try {
			System.out.println("1");
			String username = principal.getName();
        	
        	Optional<UserInfo> userInfo = userRepo.findByUsername(username);
        	
        	//Sanity Check #1 -> check user null or not
        	if(userInfo.isEmpty()) {
        		return ResponseEntity.status(404).body("User not found");
        	}
        	
        	//Sanity Check #2 -> check role
        	if(!userInfo.get().getRoles().equals("CONSUMER")) {
        		return ResponseEntity.status(403).body("You don't have access");
        	}
        	
        	//Logic
        	//Check - duplicates
        	Optional<CartProduct> cpOpt = cpRepo.findByCartUserUserIdAndProductProductId(userInfo.get().getUserId(), product.getProductId());
        	System.out.println("2");
        	if(cpOpt.isPresent()) {
        		return ResponseEntity.status(409).body("product already present");
        	}
        	
        	Optional<Cart> cartOpt = cartRepo.findByUserUsername(username);
        	System.out.println("3");
        	//cart exist
        	if(cartOpt.isPresent()) {
        		CartProduct cp = new CartProduct();
        		cp.setCart(cartOpt.get());
        		cp.setProduct(product);
        		cp.setQuantity(1);
        		        	
        		CartProduct savedCp = cpRepo.save(cp);

        		return ResponseEntity.status(200).body(savedCp);
        		
        	} //cart not exist -> don't have test case for this
        	else {
        		
        		Cart newCart = new Cart();
        		newCart.setUser(userInfo.get());
        		newCart.setTotalAmount(product.getPrice());

        		Cart savedCart = cartRepo.save(newCart); // ✅ save first

        		CartProduct cp = new CartProduct();
        		cp.setCart(savedCart);
        		cp.setProduct(product);
        		cp.setQuantity(1);

        		CartProduct savedCp = cpRepo.save(cp);
        		
        		return ResponseEntity.status(200).body(savedCp);
        	}
			
		} catch(Exception e) {
			return ResponseEntity.status(500).body("Internal Server error");
		}
		
	}
	
	@PutMapping("/cart")
	public ResponseEntity<Object> putCart(Principal principal, @RequestBody CartProduct cp){
		try {
			
			String username = principal.getName();
        	
        	Optional<UserInfo> userInfo = userRepo.findByUsername(username);
        	
        	//Sanity Check #1 -> check user null or not
        	if(userInfo.isEmpty()) {
        		return ResponseEntity.status(404).body("User not found");
        	}
        	
        	//Sanity Check #2 -> check role
        	if(!userInfo.get().getRoles().equals("CONSUMER")) {
        		return ResponseEntity.status(403).body("You don't have access");
        	}
        	
        	//Logic
        	Optional<CartProduct> cpOpt = cpRepo.findByCartUserUserIdAndProductProductId(userInfo.get().getUserId(), cp.getProduct().getProductId());
        	
        	if(cpOpt.isEmpty()) {
        		
        		Optional<Cart> cartOpt = cartRepo.findByUserUsername(username);
        		
        		CartProduct cproduct = new CartProduct();
        		cproduct.setCart(cartOpt.get());
        		cproduct.setProduct(cp.getProduct());
        		cproduct.setQuantity(cp.getQuantity());
        		
        		cpRepo.save(cproduct);
        		
        		return ResponseEntity.status(200).body("updated");
        	}
        	
        	if(cp.getQuantity()==0) {
        		cpRepo.delete(cpOpt.get());
        		return ResponseEntity.status(200).body("updated..55");
        	}
        	
        	cpOpt.get().setQuantity(cp.getQuantity());
        	cpRepo.save(cpOpt.get());
        	return ResponseEntity.status(200).body("updated..22");
        	
			
		} catch(Exception e) {
			return ResponseEntity.status(500).body("Internal Server error");
		}

	}
	
	@DeleteMapping("/cart")
	public ResponseEntity<Object> deleteCart(Principal principal, @RequestBody Product product){
		try {
			
			String username = principal.getName();
        	
        	Optional<UserInfo> userInfo = userRepo.findByUsername(username);
        	
        	//Sanity Check #1 -> check user null or not
        	if(userInfo.isEmpty()) {
        		return ResponseEntity.status(404).body("User not found");
        	}
        	
        	//Sanity Check #2 -> check role
        	if(!userInfo.get().getRoles().equals("CONSUMER")) {
        		return ResponseEntity.status(403).body("You don't have access");
        	}
        	
        	//Logic
        	cpRepo.deleteByCartUserUserIdAndProductProductId(userInfo.get().getUserId(), product.getProductId());
        	
        	return ResponseEntity.status(200).body("deleted..");
        	
			
		} catch(Exception e) {
			return ResponseEntity.status(500).body("Internal Server error");
		}
	}
}


