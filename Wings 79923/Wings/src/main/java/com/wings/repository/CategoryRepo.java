package com.wings.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wings.models.Category;

public interface CategoryRepo extends JpaRepository<Category, Integer> {

}
