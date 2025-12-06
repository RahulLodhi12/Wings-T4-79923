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
		Optional<Cart> cart = cartRepo.findByUserUsername(principal.getName());
		if (cart.isPresent()) {
			return ResponseEntity.ok(cart.get());
		}
		return ResponseEntity.notFound().build();
	}
	
	@PostMapping("/cart")
	public ResponseEntity<Object> postCart(Principal principal, @RequestBody Product product) {
		Optional<Cart> cartOpt = cartRepo.findByUserUsername(principal.getName());

		if (cartOpt.isPresent()) {
			
			// check duplicate - user + product
			Optional<CartProduct> cpExist = cpRepo.findByCartUserUserIdAndProductProductId(cartOpt.get().getCartId(),
					product.getProductId());
			if (cpExist.isPresent()) {
				return ResponseEntity.status(409).body("Product already exist in Cart");
			}
		}
		
		CartProduct cp = new CartProduct();
		
		cp.setCart(cartOpt.get());
		cp.setProduct(product);
		cp.setQuantity(1);
		cpRepo.save(cp);

		return ResponseEntity.status(200).body("Product added to cart");
	}
	
	@PutMapping("/cart")
	public ResponseEntity<Object> putCart(Principal principal, @RequestBody CartProduct cp){
		Optional<Cart> cartOpt = cartRepo.findByUserUsername(principal.getName());

	    Cart cart = cartOpt.get();

	    // Find existing CartProduct for this user + product
	    Optional<CartProduct> cpExist = cpRepo.findByCartUserUserIdAndProductProductId(
	            cart.getCartId(), cp.getProduct().getProductId());

	    if (cp.getQuantity() == 0) {
	    	cpRepo.delete(cpExist.get());
	    	return ResponseEntity.ok("Product removed from cart");
	    }
	    
	    cpExist.get().setQuantity(cp.getQuantity());
	    cpRepo.save(cpExist.get());
	    return ResponseEntity.ok("Cart updated");

	}
	
	@DeleteMapping("/cart")
	public ResponseEntity<Object> deleteCart(Principal principal, @RequestBody Product product){
		Optional<UserInfo> user = userRepo.findByUsername(principal.getName());
		
		//combo -> user_id + product_id
		cpRepo.deleteByCartUserUserIdAndProductProductId(user.get().getUserId(), product.getProductId());
		return ResponseEntity.ok("Product removed from cart");
	}
}


