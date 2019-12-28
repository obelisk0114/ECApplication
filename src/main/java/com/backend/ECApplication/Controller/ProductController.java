package com.backend.ECApplication.Controller;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

import com.backend.ECApplication.Model.Product;
import com.backend.ECApplication.Service.ProductService;

/**
 * Since we are deploying our Angular frontend to http://localhost:4200 and our Boot 
 * backend to http://localhost:8080, the browser would otherwise deny requests from 
 * one to the other.
 *
 * @author CTC
 */
@Validated
@CrossOrigin(origins = "http://localhost:4200", maxAge = 1800)
@Controller // This means that this class is a Controller

//This means URL's start with /api/product (after Application path)
@RequestMapping(path="/api/product", produces="application/json;charset=UTF-8")
public class ProductController {
	
	@Autowired
    private ProductService productService;
	
	@PostMapping("")
	public ResponseEntity<?> addNewProduct(@Valid @RequestBody Product product) {
		Product result = productService.addNewProduct(product);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}
	
	@PostMapping(path="/add") // Map ONLY POST Requests
	@ResponseBody
	public Product addNewProduct(
			@RequestParam(required = false, defaultValue = "") String type, 
			@RequestParam String name, @RequestParam int price,
			@RequestParam(required = false) String imageUrl,
			@RequestParam(required = false, defaultValue = "1") int quantity) {
		
		// @ResponseBody means the returned String is the response, not a view name
		
	    // @RequestParam means it is a parameter from the GET or POST request
		// When the parameter isn't specified, the method parameter is bound to null.
		
		return productService.addNewProduct(type, name, price, imageUrl, quantity);
	}
	
	@GetMapping(path="/all")
	@ResponseBody
	public Iterable<Product> getProducts() {
		return productService.getProducts();
	}
	
	@GetMapping(path="/all/count")
	@ResponseBody
	public Long getProductsCount() {
		return productService.getProductsCount();
	}
	
	@GetMapping("/{someType}")
	public @ResponseBody List<Product> getProductsByType(
			@PathVariable(value="someType") String type) {
		return productService.getProductsByType(type);
	}
	
	@GetMapping(path="/{someType}/count")
	@ResponseBody
	public Long getProductsCountByType(@PathVariable(value="someType") String type) {
		return productService.getProductsCountByType(type);
	}
	
	@GetMapping("")
	public ResponseEntity<Product> getProduct(@RequestParam Integer id) {
		Product entity = productService.getProduct(id);
		return ResponseEntity.ok(entity);
	}
	
	@PutMapping("/update")
	@ResponseBody
	public Product updateProduct(@RequestParam Integer id, 
			@RequestParam(required = false) String type,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer price,
			@RequestParam(required = false) String imageUrl,
			@RequestParam(required = false) Integer quantity) {
		
		return productService.updateProduct(id, type, name, 
				price, imageUrl, quantity);
	}
	
	@PutMapping(path = "/{id}")
	public ResponseEntity<Product> updateProduct(@Valid @RequestBody Product product, 
			@PathVariable(value = "id") int id) {
		Product result = productService.updateProduct(product, id);
        return ResponseEntity.ok(result);
	}
	
	@PutMapping(path = "")
	public ResponseEntity<List<Product>> updateProducts
	  (@RequestBody List<@Valid Product> products) {
		List<Product> results = productService.updateProducts(products);
        return ResponseEntity.ok().body(results);
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteAllProducts() {
		productService.deleteAllProducts();
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{someID}")
	@ResponseBody
	public String deleteProduct(@PathVariable(value="someID") Integer id) {
		return productService.deleteProduct(id);
	}
	
}
