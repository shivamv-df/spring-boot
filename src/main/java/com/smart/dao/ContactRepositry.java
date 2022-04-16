package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entites.Contact;

public interface ContactRepositry extends JpaRepository<Contact, Integer>{
   @Query("from Contact as c where c.user.id= :userId")
   public Page<Contact> findContactsByUser(@Param("userId")int userId, Pageable pePageable);
	public List<Contact> findContactsByUser(@Param("userId") int userId);

}