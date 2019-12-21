package com.backend.ECApplication.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;

@Entity(name = "products") // This tells Hibernate to make a table out of this class
public class Product {     // If name is not set, class name will become table name.
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL")
	private String type;
	
	@NotNull(message = "Product name is required.")
	@Column(name = "product_name", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci")
	private String name;
	
	@Min(value = -1, message = "Price must be larger than {value}. ${validatedValue} is too small")
	private int price;
	
	@NotNull(message = "image_url is required.")
	private String imageUrl;
	
	@Min(value = -1, message = "Availability must be larger than {value}.")
	private int quantity;
	
	public Product() {}
	
	public Product(String type, String name, int price, String imageUrl) {
		this.type = type;
		this.name = name;
		this.price = price;
		this.imageUrl = imageUrl;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
}
