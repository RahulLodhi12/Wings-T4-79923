package com.wings.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.wings.models.Cart;
import com.wings.models.CartProduct;
import com.wings.models.Category;
import com.wings.models.Product;
import com.wings.models.UserInfo;
import com.wings.repository.CartProductRepo;
import com.wings.repository.CartRepo;
import com.wings.repository.CategoryRepo;
import com.wings.repository.ProductRepo;
import com.wings.repository.UserInfoRepository;

@Component
public class DataLoader implements CommandLineRunner {

	@Autowired
	private UserInfoRepository userRepo;
	
	@Autowired
	private CategoryRepo categoryRepo;
	
	@Autowired
	private CartRepo cartRepo;
	
	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
	private CartProductRepo cartProductRepo;
	
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		loadUserData();
		loadData();
	}
	
	private void loadUserData() {
		//Create and save UserInfo entities
		userRepo.save(new UserInfo("jack",passwordEncoder.encode("pass_word"),"CONSUMER"));
		userRepo.save(new UserInfo("bob",passwordEncoder.encode("pass_word"),"CONSUMER"));
		userRepo.save(new UserInfo("apple",passwordEncoder.encode("pass_word"),"SELLER"));
		userRepo.save(new UserInfo("glaxo",passwordEncoder.encode("pass_word"),"SELLER"));
		
		System.out.println("User Data got loaded in DB");
	}
	
	private void loadData() {
		//Create and save Category entities
		Category category1 = new Category("Fashion");
		Category category2 = new Category("Electronics");
		Category category3 = new Category("Books");
		Category category4 = new Category("Groceries");
		Category category5 = new Category("Medicines");
		
		//Save categories to the repository
		categoryRepo.save(category1);
		categoryRepo.save(category2);
		categoryRepo.save(category3);
		categoryRepo.save(category4);
		categoryRepo.save(category5);
		
		System.out.println("Category Data got loaded in DB");
		
		//Fetch UserInfo for users (jack and bob) using userId
		UserInfo user3 = userRepo.findById(3).orElseThrow(()-> new RuntimeException("User not found"));
		UserInfo user4 = userRepo.findById(4).orElseThrow(()-> new RuntimeException("User not found"));
		
		Category cate1 = categoryRepo.findById(2).orElseThrow(()-> new RuntimeException("Category not found"));
		Category cate2 = categoryRepo.findById(5).orElseThrow(()-> new RuntimeException("Category not found"));
		
		//Create and save Product entities
		Product product1 = new Product("Apple iPad 10.2 8th Gen Wifi iOS Tablet",29190,user3,cate1);
		Product product2 = new Product("Crocin pain relief tablet",10,user4,cate2);
		
		//Ensure the product is saved correctly
		productRepo.save(product1);
		productRepo.save(product2);
		
		System.out.println("Product Data got loaded in DB");
		
		UserInfo user1 = userRepo.findById(1).orElseThrow(()-> new RuntimeException("User not found"));
		UserInfo user2 = userRepo.findById(2).orElseThrow(()-> new RuntimeException("User not found"));
		
		//Create and save Cart entities
		Cart cart1 = new Cart(20.0, user1); //Pass UserInfo object
		Cart cart2 = new Cart(0.0, user2);	//Pass UserInfo object
		
		cartRepo.save(cart1);
		cartRepo.save(cart2);
		
		System.out.println("Cart Data got loaded in DB");
		
		//Fetch Products for CartProduct
		Product savedProduct1 = productRepo.findById(product1.getProductId()).orElseThrow(()-> new RuntimeException("Product not Found"));
		Product savedProduct2 = productRepo.findById(product2.getProductId()).orElseThrow(()-> new RuntimeException("Product not Found"));
	
		//Create and save CartProduct entities
		CartProduct cartProduct1 = new CartProduct(cart1, savedProduct2, 2); // Product 1 in Cart 1
		
		cartProductRepo.save(cartProduct1);
		
		System.out.println("Cart Product Data got loaded in DB");
	}

}
