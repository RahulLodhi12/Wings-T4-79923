package com.wings.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wings.models.Cart;

public interface CartRepo extends JpaRepository<Cart, Integer> {
	Optional<Cart> findByUserUsername(String username);
}
