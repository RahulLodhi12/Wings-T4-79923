package com.wings.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wings.models.Cart;

public interface CartRepo extends JpaRepository<Cart, Integer> {

}
