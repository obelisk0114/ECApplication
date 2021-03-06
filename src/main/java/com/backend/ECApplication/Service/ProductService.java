package com.backend.ECApplication.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import com.backend.ECApplication.Dao.ProductRepository;
import com.backend.ECApplication.Model.Product;
import com.backend.ECApplication.Exception.UnprocessableException;

@Service
public class ProductService {
	
	@Autowired
	// This means to get the bean called productRepository
	// Which is auto-generated by Spring, we will use it to handle the data
	private ProductRepository productRepository;
	
	public Product addNewProduct(Product product) {
		return productRepository.save(product);
	}
	
	public Product addNewProduct(String type, String name, int price, 
			String imageUrl, int quantity) {
		
		Product p = new Product();
		p.setType(type);
	    p.setName(name);
	    p.setPrice(price);
	    p.setImageUrl(imageUrl);
	    p.setQuantity(quantity);
	    
	    return productRepository.save(p);
	}
	
	public Iterable<Product> getProducts() {
		// This returns a JSON or XML with the users
		return productRepository.findAll();
	}
	
	public Long getProductsCount() {
		return productRepository.count();
	}
	
	public List<Product> getProductsByType(String type) {
		return productRepository.findByType(type);
	}
	
	public Long getProductsCountByType(String type) {
		return productRepository.countByType(type);
	}
	
	public Product getProduct(Integer id) {
		// Optional<Product> entities = productRepository.findById(id);
		// Product entity = entities.get();
		
		return productRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Can't find product."));
	}
	
	public Product updateProduct(Integer id, String type, String name, Integer price, 
			String imageUrl, Integer quantity) {
		
		return productRepository.findById(id).map(product -> {
			setUpdatedProduct(product, type, name, price, imageUrl, quantity);
			return productRepository.save(product);
		}).orElseGet(() -> {
			return this.addNewProduct(type, name, price, imageUrl, quantity);
		});
	}
	
	public Product updateProduct(Product product, int id) {
		if (id != product.getId()) {
			throw new UnprocessableException("Id didn't match with product.");
		}
		return productRepository.save(product);
	}
	
	public List<Product> updateProducts(List<Product> products) {
		List<Product> results = new ArrayList<>();
		for (Product product : products) {
			Product result = productRepository.save(product);
			results.add(result);
		}
		return results;
	}
	
	public void deleteAllProducts() {
		productRepository.deleteAll();
	}
	
	public String deleteProduct(Integer id) {
		productRepository.deleteById(id);
		return "Delete product id = " + id;
	}
	
	private void setUpdatedProduct(Product product, String type, String name,
			Integer price, String imageUrl, Integer quantity) {
		if (type != null) {
			product.setType(type);
		}
		if (name != null) {
			product.setName(name);
		}
		if (price != null) {
			product.setPrice(price);
		}
		if (imageUrl != null) {
			product.setImageUrl(imageUrl);				
		}
		if (quantity != null) {
			product.setQuantity(quantity);				
		}
	}
	
}
