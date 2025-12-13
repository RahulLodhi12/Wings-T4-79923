package com.wings.repository;

import java.util.List;
import java.util.Optional;

//import com.fresco.ecommerce.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wings.models.Product;

// Import required annotations to make use of the Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {
	List<Product> findByProductNameContainingIgnoreCaseOrCategoryCategoryNameContainingIgnoreCase(String productName,
			String categoryName);

	List<Product> findBySellerUserId(Integer sellerId);

	Optional<Product> findBySellerUserIdAndProductId(Integer sellerId, Integer productId);
	
//	List<Product> findTop3ByCategoryOrderByPrice();
}