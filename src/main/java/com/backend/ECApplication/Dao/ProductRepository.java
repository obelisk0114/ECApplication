package com.backend.ECApplication.Dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.backend.ECApplication.Model.Product;

// This will be AUTO IMPLEMENTED by Spring into a Bean called productRepository
// CRUD refers Create, Read, Update, Delete

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {
	List<Product> findByType(String type);
	
	Long countByType(String type);

}
