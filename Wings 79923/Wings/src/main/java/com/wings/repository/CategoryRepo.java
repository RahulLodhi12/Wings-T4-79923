package com.wings.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wings.models.Category;

public interface CategoryRepo extends JpaRepository<Category, Integer> {
	Optional<Category> findByCategoryName(String category);
}
