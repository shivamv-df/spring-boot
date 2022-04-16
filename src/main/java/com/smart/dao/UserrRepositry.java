package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entites.Contact;
import com.smart.entites.User;

public interface UserrRepositry extends JpaRepository<User, Integer>{
	@Query("select  u from User u where u.email= :email")
	public User getUserByUserName(@Param("email") String email);
}
