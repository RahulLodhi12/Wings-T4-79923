package com.wings.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wings.models.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer>{
	Optional<UserInfo> findByUsername(String username);
}
