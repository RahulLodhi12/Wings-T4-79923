//package com.wings.controller;
//
//import java.security.Principal;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import com.wings.models.CartProduct;
//import com.wings.models.Product;
//import com.wings.repository.CartProductRepo;
//import com.wings.repository.CartRepo;
//import com.wings.repository.ProductRepo;
//import com.wings.repository.UserInfoRepository;
//
//@RequestMapping("/api/auth/consumer")
//public class ConsumerController {
//	
//	ProductRepo productRepo;
//	
//	CartRepo cartRepo;
//	
//	CartProductRepo cpRepo;
//	
//	UserInfoRepository userRepo;
//	
//	@GetMapping("/cart")
//	public ResponseEntity<Object> getCart(Principal principal){
//		return null;
//	}
//	
//	@PostMapping("/cart")
//	public ResponseEntity<Object> postCart(Principal principal, Product product){
//		return null;
//	}
//	
//	@PutMapping("/cart")
//	public ResponseEntity<Object> putCart(Principal principal, CartProduct cp){
//		return null;
//	}
//	
//	@DeleteMapping("/cart")
//	public ResponseEntity<Object> deleteCart(Principal principal, Product product){
//		return null;
//	}
//	
//	
//
//}





package com.wings.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
	public ResponseEntity<Object> postCart(Principal principal, @RequestBody Product product){
		Optional<UserInfo> user = userRepo.findByUsername(principal.getName());
		Optional<Product> dbProduct = productRepo.findById(product.getProductId());
		
		if (user.isEmpty()) return ResponseEntity.badRequest().body("User not found");
		if(dbProduct.isEmpty()) return ResponseEntity.badRequest().body("Product not found");
		
		Optional<Cart> cartOpt = cartRepo.findByUserUsername(principal.getName());
	
		
		Cart cart;

		if (cartOpt.isPresent()) {
		    cart = cartOpt.get();
		} else {
		    Cart newCart = new Cart();
		    newCart.setUser(user.get());
		    cart = cartRepo.save(newCart);
		}

		
		CartProduct cp = new CartProduct();
		cp.setCart(cart);
		cp.setProduct(dbProduct.get());
		cp.setQuantity(1);
		cp.setCartId(cart.getCartId());
		cp.setProductId(dbProduct.get().getProductId());
		cpRepo.save(cp);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("Product added to cart");
	}
	
	@PutMapping("/cart")
	public ResponseEntity<Object> putCart(Principal principal, @RequestBody CartProduct cp){
		// Fetch user from Principal
	    Optional<UserInfo> user = userRepo.findByUsername(principal.getName());
	    if (user.isEmpty()) return ResponseEntity.badRequest().body("User not found");

	    // Fetch the cart for this user
	    Optional<Cart> cartOpt = cartRepo.findByUserUsername(principal.getName());
	    if (cartOpt.isEmpty()) return ResponseEntity.badRequest().body("Cart not found");

	    Cart cart = cartOpt.get();

	    // Fetch product from DB
	    Optional<Product> productOpt = productRepo.findById(cp.getProduct().getProductId());
	    if (productOpt.isEmpty()) return ResponseEntity.badRequest().body(cp.getProduct().getProductId()+cp.getProductId()+"Product not found");

	    Product product = productOpt.get();

	    // Find existing CartProduct for this user + product
	    Optional<CartProduct> existing = cpRepo.findByCartUserUserIdAndProductProductId(
	            user.get().getUserId(), product.getProductId());

	    if (existing.isPresent()) {
	        CartProduct existingCP = existing.get();

	        if (cp.getQuantity() == 0) {
	            cpRepo.delete(existingCP);
	            return ResponseEntity.ok("Product removed from cart");
	        }

	        existingCP.setQuantity(cp.getQuantity());
	        cpRepo.save(existingCP);
	        return ResponseEntity.ok("Cart updated");
	    }
	    else {
	    	CartProduct newCP = new CartProduct();
	        newCP.setCart(cart);
	        newCP.setProduct(cp.getProduct());
	        newCP.setQuantity(cp.getQuantity());
	        newCP.setCartId(cart.getCartId());
	        newCP.setProductId(cp.getProductId());
	        cpRepo.save(newCP);
	        return ResponseEntity.ok("New Product added to cart");
	    }
	    
//	    return ResponseEntity.badRequest().body("Error..");
	    
	}
	
	@DeleteMapping("/cart")
	public ResponseEntity<Object> deleteCart(Principal principal, @RequestBody Product product){
		Optional<UserInfo> user = userRepo.findByUsername(principal.getName());
		if (user.isEmpty()) return ResponseEntity.badRequest().body("User not found");
		
		//combo -> user_id + product_id
		cpRepo.deleteByCartUserUserIdAndProductProductId(user.get().getUserId(), product.getProductId());
		return ResponseEntity.ok("Product removed from cart");
	}
}


