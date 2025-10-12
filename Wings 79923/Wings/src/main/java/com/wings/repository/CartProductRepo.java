package com.wings.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wings.models.CartProduct;

public interface CartProductRepo extends JpaRepository<CartProduct, Integer> {

}
