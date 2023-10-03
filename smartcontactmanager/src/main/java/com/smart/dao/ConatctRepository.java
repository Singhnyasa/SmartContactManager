package com.smart.dao;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.cdi.JpaRepositoryExtension;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ConatctRepository extends JpaRepository<Contact,Integer> {
     //pagination....
	
	@Query("from Contact as c where c.user.id = :userId ")
	public Page<Contact>findConatctsByUSer(@Param("userId") int userId,Pageable pageable);
          
	//search
	public List<Contact> findByNameContainingAndUser(String name,User user);
 
	
	
}
