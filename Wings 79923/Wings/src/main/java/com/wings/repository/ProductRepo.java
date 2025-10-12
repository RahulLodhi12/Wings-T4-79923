package com.wings.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wings.models.Product;

public interface ProductRepo extends JpaRepository<Product, Integer> {

}
