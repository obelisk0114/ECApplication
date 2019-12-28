package com.backend.ECApplication;

import java.util.Set;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.json.JSONObject;
import org.json.JSONArray;

import com.backend.ECApplication.Model.Product;
import com.backend.ECApplication.Dao.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
class EcApplicationTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ProductRepository productRepository;
	
	private HttpHeaders httpHeaders;
	
	@BeforeEach
	public void init() {
		httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
	}
	
	@AfterEach
	public void clear() {
		productRepository.deleteAll();
	}
	
	@Test
	public void testAddNewProduct() throws Exception {
		JSONObject request = new JSONObject();
		request.put("type", "new");
		request.put("name", "泰勒展開");
		request.put("price", 1776);
		request.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request.put("quantity", 19);
		
		HttpHeaders httpHeader1 = new HttpHeaders();
		httpHeader1.add("Content-Type", "application/json");
		
		RequestBuilder requestBuilder = 
				MockMvcRequestBuilders.post("/api/product")
				    .headers(httpHeader1).content(request.toString());
		
		MvcResult result = mockMvc.perform(requestBuilder).andDo(print()).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
        JSONObject resBody = new JSONObject(response.getContentAsString());
        
        Integer productId = resBody.getInt("id");
        assertNotNull(productId);
        
        assertEquals(request.getString("type"), resBody.getString("type"));
        assertEquals(request.getString("name"), resBody.getString("name"));
        assertEquals(request.getInt("price"), resBody.getInt("price"));
        assertEquals(request.getString("imageUrl"), resBody.getString("imageUrl"));
        assertEquals(request.getInt("quantity"), resBody.getInt("quantity"));
        
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals("application/json;charset=UTF-8", response.getContentType());
        
        assertEquals(1, productRepository.count());
        assertTrue(productRepository.findById(productId).isPresent());
	}
	
	@Test
	public void testAddNewProduct3() throws Exception {
		String name = "Tesla";
		int price = 1943;
		String imageUrl = "brooke-lark-230642-unsplash.jpg";
		
		mockMvc.perform(post("/api/product/add")
                .headers(httpHeaders)
                .param("name", name)
                .param("price", price + "")
                .param("imageUrl", imageUrl))
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.id").hasJsonPath())
		        .andExpect(jsonPath("$.type").value(""))
		        .andExpect(jsonPath("$.quantity").value(1))
		        .andExpect(jsonPath("$.imageUrl").value(imageUrl))
		        .andExpect(jsonPath("$.name").value(name))
		        .andExpect(jsonPath("$.price").value(price));
	}
	
	@Test
	public void testAddNewProduct5() throws Exception {
		String type = "Wireless";
		String name = "Tesla";
		int price = 1943;
		String imageUrl = "brooke-lark-230642-unsplash.jpg";
		int quantity = 86;
		
		mockMvc.perform(post("/api/product/add")
                .headers(httpHeaders)
                .param("type", type)
                .param("name", name)
                .param("price", price + "")
                .param("imageUrl", imageUrl)
                .param("quantity", quantity + ""))
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.id").hasJsonPath())
		        .andExpect(jsonPath("$.type").value(type))
		        .andExpect(jsonPath("$.name").value(name))
		        .andExpect(jsonPath("$.imageUrl").value(imageUrl))
		        .andExpect(jsonPath("$.quantity").value(quantity))
		        .andExpect(jsonPath("$.price").value(price));
	}
	
	@Test
	public void testGetProducts() throws Exception {
		Product product1 = new Product();
		product1.setType("開屁的使者");
        product1.setName("混蛋士兵");
        product1.setPrice(3000);
        product1.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product1.setQuantity(2500);
                
        Product product2 = new Product();
		product2.setType("屍魂界");
        product2.setName("浦原喜助");
        product2.setPrice(115);
        product2.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product2.setQuantity(12);
        
        productRepository.save(product1);
        productRepository.save(product2);
        
        Set<Integer> requestSet = new HashSet<>();
        requestSet.add(product1.getId());
        requestSet.add(product2.getId());
        
        MvcResult result = mockMvc.perform(get("/api/product/all")
                .headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        
        MockHttpServletResponse response = result.getResponse();
        JSONArray resBody = new JSONArray(response.getContentAsString());
        
        assertEquals(2, resBody.length());
        
        Set<Integer> responseSet = new HashSet<>();
        for (int i = 0 ; i < resBody.length(); i++) {
        	JSONObject element = resBody.getJSONObject(i);
        	
        	int id = element.getInt("id");
        	if (id == product1.getId()) {
        		assertEquals(product1.getType(), element.getString("type"));
        		assertEquals(product1.getName(), element.getString("name"));
        		assertEquals(product1.getPrice(), element.getInt("price"));
        		assertEquals(product1.getImageUrl(), element.getString("imageUrl"));
        		assertEquals(product1.getQuantity(), element.getInt("quantity"));
        	}
        	else {
        		assertEquals(product2.getType(), element.getString("type"));
        		assertEquals(product2.getName(), element.getString("name"));
        		assertEquals(product2.getPrice(), element.getInt("price"));
        		assertEquals(product2.getImageUrl(), element.getString("imageUrl"));
        		assertEquals(product2.getQuantity(), element.getInt("quantity"));
        	}
        	
        	responseSet.add(id);
        }
        
        assertEquals(requestSet, responseSet);
	}
	
	@Test
    public void testGetProductsByType() throws Exception {
		Product product1 = new Product();
		product1.setType("Analysis");
        product1.setName("Real Analysis");
        product1.setPrice(59);
        product1.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product1.setQuantity(39);
        
        Product product2 = new Product();
		product2.setType("Algebra");
        product2.setName("群論");
        product2.setPrice(59);
        product2.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product2.setQuantity(319);
        
        Product product3 = new Product();
		product3.setType("Analysis");
        product3.setName("Complex Analysis");
        product3.setPrice(79);
        product3.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product3.setQuantity(319);
        
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        
        Set<Integer> requestSet = new HashSet<>();
        requestSet.add(product1.getId());
        requestSet.add(product3.getId());

        MvcResult result = mockMvc.perform(get("/api/product/" + product1.getType())
                .headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        
        MockHttpServletResponse response = result.getResponse();
        JSONArray resBody = new JSONArray(response.getContentAsString());
        
        assertEquals(2, resBody.length());
        
        Set<Integer> responseSet = new HashSet<>();
        for (int i = 0 ; i < resBody.length(); i++) {
            JSONObject element = resBody.getJSONObject(i);
            String elementType = element.getString("type");
            assertEquals(elementType, product1.getType());
            
            responseSet.add(element.getInt("id"));
        }
        assertEquals(requestSet, responseSet);
    }
	
	@Test
	public void testGetProductsCountByType() throws Exception {
		Product product1 = new Product();
		product1.setType("Analysis");
        product1.setName("Real Analysis");
        product1.setPrice(59);
        product1.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product1.setQuantity(39);
        
        Product product2 = new Product();
		product2.setType("Algebra");
        product2.setName("群論");
        product2.setPrice(59);
        product2.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product2.setQuantity(319);
        
        Product product3 = new Product();
		product3.setType("Analysis");
        product3.setName("Complex Analysis");
        product3.setPrice(79);
        product3.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product3.setQuantity(319);
        
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        
        MvcResult result = 
        		mockMvc.perform(get("/api/product/" + product1.getType() + "/count")
                       .headers(httpHeaders))
                       .andExpect(status().isOk())
                       .andReturn();
        
        MockHttpServletResponse response = result.getResponse();
        int resBody = Integer.parseInt(response.getContentAsString());
        
        int count = 0;
        for (Product p : productRepository.findAll()) {
            if (p.getType().equals(product1.getType())) {
            	count++;
            }
        }
        assertEquals(count, resBody);
	}
	
	@Test
    public void testGetProduct() throws Exception {
		Product product = new Product();
		product.setType("Analysis");
        product.setName("Complex Analysis");
        product.setPrice(59);
        product.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product.setQuantity(39);
        
        productRepository.save(product);

        mockMvc.perform(get("/api/product?id=" + product.getId())
                .headers(httpHeaders))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.type").value(product.getType()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.price").value(product.getPrice()))
                .andExpect(jsonPath("$.imageUrl").value(product.getImageUrl()))
                .andExpect(jsonPath("$.quantity").value(product.getQuantity()));
    }
	
	@Test
    public void testGetProductsWhoseTypesDontExist() throws Exception {
		Product product1 = new Product();
		product1.setType("Analysis");
        product1.setName("Real Analysis");
        product1.setPrice(59);
        product1.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product1.setQuantity(39);
        
        Product product2 = new Product();
		product2.setType("Algebra");
        product2.setName("群論");
        product2.setPrice(59);
        product2.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product2.setQuantity(319);
        
        Product product3 = new Product();
		product3.setType("Analysis");
        product3.setName("Complex Analysis");
        product3.setPrice(79);
        product3.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product3.setQuantity(319);
        
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        
        MvcResult result = mockMvc.perform(get("/api/product/沒有這個 type 吧")
                .headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        
        MockHttpServletResponse response = result.getResponse();
        JSONArray resBody = new JSONArray(response.getContentAsString());
        assertEquals(resBody.length(), 0);
    }
	
	@Test
    public void testGetAProductDoesntExist() throws Exception {
		Product product = new Product();
		product.setType("Analysis");
        product.setName("Complex Analysis");
        product.setPrice(159);
        product.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product.setQuantity(139);
        
        productRepository.save(product);

        mockMvc.perform(get("/api/product?id=" + (product.getId() + 1))
                .headers(httpHeaders))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Can't find product."));
    }
	
	@Test
	public void testUpdateProduct() throws Exception {
		Product product = new Product();
		product.setType("Quantom");
        product.setName("Schrodinger");
        product.setPrice(1900);
        product.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product.setQuantity(51);
        
        productRepository.save(product);
		
        JSONObject request = new JSONObject();
		request.put("id", product.getId());
		request.put("type", "Black-body radiation");
		request.put("name", "Planck");
		request.put("price", 1899);
		request.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request.put("quantity", 89);
        
		mockMvc.perform(put("/api/product/" + product.getId())
                .headers(httpHeaders).content(request.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.type").value(request.getString("type")))
                .andExpect(jsonPath("$.name").value(request.getString("name")))
                .andExpect(jsonPath("$.price").value(request.getInt("price")))
                .andExpect(jsonPath("$.imageUrl").value(request.getString("imageUrl")))
                .andExpect(jsonPath("$.quantity").value(request.getInt("quantity")));
	}
	
	@Test
	public void testUpdateProduct1() throws Exception {
		Product product = new Product();
		product.setType("Quantom");
        product.setName("Schrodinger");
        product.setPrice(1900);
        product.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product.setQuantity(51);
        
        productRepository.save(product);
		
        String imageUrl = "amy-shamblen-784211-unsplash.jpg";
		
		mockMvc.perform(put("/api/product/update?id=" + product.getId())
				.param("imageUrl", imageUrl)
                .headers(httpHeaders))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.type").value("Quantom"))
                .andExpect(jsonPath("$.name").value("Schrodinger"))
                .andExpect(jsonPath("$.price").value(1900))
                .andExpect(jsonPath("$.quantity").value(51))
                .andExpect(jsonPath("$.imageUrl").value(imageUrl));
	}
	
	@Test
	public void testUpdateProduct2() throws Exception {
		Product product = new Product();
		product.setType("Quantom");
        product.setName("Schrodinger");
        product.setPrice(1900);
        product.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product.setQuantity(51);
        
        productRepository.save(product);
		
        String name = "Heisenberg";
        String imageUrl = "amy-shamblen-784211-unsplash.jpg";
		
		mockMvc.perform(put("/api/product/update?id=" + product.getId() + "&name="
				    + name + "&imageUrl=" + imageUrl)
                .headers(httpHeaders))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.type").value("Quantom"))
                .andExpect(jsonPath("$.price").value(1900))
                .andExpect(jsonPath("$.quantity").value(51))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.imageUrl").value(imageUrl));
	}
	
	@Test
	public void testUpdateProduct3() throws Exception {
		Product product = new Product();
		product.setType("Quantom");
        product.setName("Schrodinger");
        product.setPrice(1900);
        product.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product.setQuantity(51);
        
        productRepository.save(product);
        
        String type = "CAT";
        int price = 1926;
        int quantity = 61;
        
		mockMvc.perform(put("/api/product/update")
                .headers(httpHeaders)
                .param("id", product.getId().toString())
                .param("type", type)
                .param("price", price + "")
                .param("quantity", quantity + ""))
		        .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Schrodinger"))
                .andExpect(jsonPath("$.imageUrl").value("brooke-lark-230642-unsplash.jpg"))
                .andExpect(jsonPath("$.type").value(type))
                .andExpect(jsonPath("$.price").value(price))
                .andExpect(jsonPath("$.quantity").value(quantity));
	}
	
	@Test
	public void testUpdateProduct4() throws Exception {
		Product product = new Product();
		product.setType("Quantom");
        product.setName("Schrodinger");
        product.setPrice(1900);
        product.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product.setQuantity(51);
        
        productRepository.save(product);
        
        String type = "CAT";
        String name = "Memory";
        int price = 1926;
        int quantity = 61;
        
		mockMvc.perform(put("/api/product/update")
                .headers(httpHeaders)
                .param("id", product.getId().toString())
                .param("type", type)
                .param("name", name)
                .param("price", price + "")
                .param("quantity", quantity + ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.imageUrl").value("brooke-lark-230642-unsplash.jpg"))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.type").value(type))
                .andExpect(jsonPath("$.price").value(price))
                .andExpect(jsonPath("$.quantity").value(quantity));
	}
	
	@Test
	public void testUpdateAProductDoesntExist() throws Exception {
		Product product = new Product();
		product.setType("Nuclear");
        product.setName("Atom");
        product.setPrice(1896);
        product.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product.setQuantity(20);
        
        productRepository.save(product);
        
        Integer id = product.getId() + 2;
        String type = "hydrogen";
        String name = "Rutherford";
        int price = 1920;
        String imageUrl = "amy-shamblen-784211-unsplash.jpg";
        int quantity = 21;
        
        mockMvc.perform(put("/api/product/update")
                .headers(httpHeaders)
                .param("id", id.toString())
                .param("type", type)
                .param("name", name)
                .param("price", price + "")
                .param("imageUrl", imageUrl)
                .param("quantity", quantity + ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId() + 1))
                .andExpect(jsonPath("$.type").value(type))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.price").value(price))
                .andExpect(jsonPath("$.imageUrl").value(imageUrl))
                .andExpect(jsonPath("$.quantity").value(quantity));
	}
	
	@Test
	public void testUpdateProducts() throws Exception {
		Product product1 = new Product();
		product1.setType("Quantom");
        product1.setName("Schrodinger");
        product1.setPrice(1900);
        product1.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product1.setQuantity(51);
                
        Product product2 = new Product();
		product2.setType("Calculus");
        product2.setName("Leibniz");
        product2.setPrice(1672);
        product2.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product2.setQuantity(70);
        
        productRepository.save(product1);
        productRepository.save(product2);
        int id1 = product1.getId();
        int id2 = product2.getId();
		
		JSONArray requests = new JSONArray();
		
		JSONObject request1 = new JSONObject();
		request1.put("id", id1);
		request1.put("type", "many worlds interpretation");
		request1.put("name", "Everett");
		request1.put("price", 1899);
		request1.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request1.put("quantity", 51);
		
		JSONObject request2 = new JSONObject();
		request2.put("id", id2);
		request2.put("type", "Computer Science");
		request2.put("name", "von Neumann");
		request2.put("price", 1672);
		request2.put("imageUrl", "amy-shamblen-784211-unsplash.jpg");
		request2.put("quantity", 53);
		
		requests.put(request1);
		requests.put(request2);
		
		MvcResult result = mockMvc.perform(put("/api/product")
				.headers(httpHeaders).content(requests.toString()))
				.andDo(print())
                .andExpect(status().isOk())
                .andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		JSONArray resBody = new JSONArray(response.getContentAsString());
		
		Set<String> requestSet = new HashSet<>();
		requestSet.add(request1.toString());
		requestSet.add(request2.toString());
		        
		Set<String> responseSet = new HashSet<>();
		for (int i = 0; i < resBody.length(); i++) {
			JSONObject element = resBody.getJSONObject(i);			
			responseSet.add(element.toString());
		}
		
		assertEquals(requestSet, responseSet);
	}
	
	@Test
	public void testUpdateProducts2() throws Exception {
		JSONArray requests = new JSONArray();
		
		JSONObject request1 = new JSONObject();
		request1.put("type", "Black-body radiation");
		request1.put("name", "Planck");
		request1.put("price", 1899);
		request1.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request1.put("quantity", 89);
		
		JSONObject request2 = new JSONObject();
		request2.put("type", "Nuclear");
		request2.put("name", "Atom");
		request2.put("price", 1896);
		request2.put("imageUrl", "amy-shamblen-784211-unsplash.jpg");
		request2.put("quantity", 20);
		
		requests.put(request1);
		requests.put(request2);
		
		MvcResult result = mockMvc.perform(put("/api/product")
				.headers(httpHeaders).content(requests.toString()))
                .andExpect(status().isOk())
                .andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		JSONArray resBody = new JSONArray(response.getContentAsString());
		
		Set<String> requestSet = new HashSet<>();
		requestSet.add(request1.toString());
		requestSet.add(request2.toString());
		        
		Set<String> responseSet = new HashSet<>();
		for (int i = 0; i < resBody.length(); i++) {
			JSONObject element = resBody.getJSONObject(i);
			element.remove("id");
			
			responseSet.add(element.toString());
		}
		
		assertEquals(requestSet, responseSet);
	}
	
	@Test
	public void testDeleteAllProducts() throws Exception {
		Product product1 = new Product();
		product1.setType("Temporal paradox");
        product1.setName("平行宇宙");
        product1.setPrice(1905);
        product1.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product1.setQuantity(26);
                
        Product product2 = new Product();
		product2.setType("new");
        product2.setName("Alcubierre drive");
        product2.setPrice(1994);
        product2.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product2.setQuantity(3);
        
        productRepository.save(product1);
        productRepository.save(product2);
        
        mockMvc.perform(delete("/api/product/delete")
                .headers(httpHeaders))
                .andExpect(status().isNoContent());
        
        assertEquals(productRepository.count(), 0);
	}
	
	@Test
	public void testDeleteProduct() throws Exception {
		Product product = new Product();
		product.setType("Electromagnetism");
        product.setName("Maxwell");
        product.setPrice(1856);
        product.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product.setQuantity(8);
        
        productRepository.save(product);
        int id = product.getId();
        
        mockMvc.perform(delete("/api/product/" + id)
                .headers(httpHeaders))
                .andExpect(status().isOk())
                .andExpect(content().string("Delete product id = " + id));
        
        assertThrows(RuntimeException.class, () -> {
			productRepository.findById(id).orElseThrow(RuntimeException::new);
        });
	}
	
	@Test
	public void get400WhenAddProductWithoutName() throws Exception {
		JSONObject request = new JSONObject();
		request.put("type", "new");
		request.put("price", 1776);
		request.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request.put("quantity", 19);
		
		mockMvc.perform(post("/api/product")
				.headers(httpHeaders).content(request.toString()))
		        .andDo(print())
		        .andExpect(status().isBadRequest())
		        .andExpect(jsonPath("$.errors[*].message").value("Product name is required."));
	}
	
	@Test
	public void get400WhenAddProductWithoutPrice() throws Exception {
		JSONObject request = new JSONObject();
		request.put("name", "Laplace");
		request.put("type", "water");
		request.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request.put("quantity", 19);
		
		mockMvc.perform(post("/api/product")
				.headers(httpHeaders).content(request.toString()))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void get400WhenAddProductWithNegativePrice() throws Exception {
		JSONObject request = new JSONObject();
		request.put("name", "Laplace");
		request.put("type", "water");
		request.put("price", -2);
		request.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request.put("quantity", 19);
		
		mockMvc.perform(post("/api/product")
				.headers(httpHeaders).content(request.toString()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[*].message").value("Price must be larger than -1. -2 is too small"));
	}
	
	@Test
	public void get400WhenAddProductWithoutImageUrl() throws Exception {
		JSONObject request = new JSONObject();
		request.put("name", "Laplace");
		request.put("type", "water");
		request.put("price", 176);
		request.put("quantity", 19);
		
		mockMvc.perform(post("/api/product")
				.headers(httpHeaders).content(request.toString()))
		        .andExpect(status().isBadRequest())
		        .andExpect(jsonPath("$.errors[0].message").value("image_url is required."));
	}
	
	@Test
	public void get400WhenAddProductWithNegativeQuantity() throws Exception {
		JSONObject request = new JSONObject();
		request.put("name", "Laplace");
		request.put("type", "water");
		request.put("price", 176);
		request.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request.put("quantity", -19);
		
		mockMvc.perform(post("/api/product")
				.headers(httpHeaders).content(request.toString()))
		        .andExpect(status().isBadRequest())
		        .andExpect(jsonPath("$.errors[0].message").value("Availability must be larger than -1."));
	}
	
	@Test
	public void get400WhenAddProductWithoutImageUrlWithNegativePriceNegativeQuantity() 
			throws Exception {
		
		JSONObject request = new JSONObject();
		int price = -2;
		request.put("name", "Laplace");
		request.put("type", "signal");
		request.put("price", price);
		request.put("quantity", -29);
		
		MvcResult result = mockMvc.perform(post("/api/product")
				.headers(httpHeaders).content(request.toString()))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		JSONObject resBody = new JSONObject(response.getContentAsString());
		
		JSONArray resTarget = resBody.getJSONArray("errors");
        
		for (int i = 0; i < resTarget.length(); i++) {
			JSONObject element = resTarget.getJSONObject(i);
			String elementMessage = element.getString("message");

			switch (element.getString("code")) {
			case "price":
				assertEquals(elementMessage, "Price must be larger than -1. " + price + " is too small");
				break;
			case "imageUrl":
				assertEquals(elementMessage, "image_url is required.");
				break;
			case "quantity":
				assertEquals(elementMessage, "Availability must be larger than -1.");
				break;
			}
		}
	}
	
	@Test
	public void get400WhenUpdateProductWithoutName() throws Exception {
		
		Product product = new Product();
		product.setType("FFT");
        product.setName("Fourier");
        product.setPrice(200);
        product.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product.setQuantity(29);
        
        productRepository.save(product);
		
		JSONObject request = new JSONObject();
		request.put("id", product.getId());
		request.put("type", "FFT");
		request.put("price", 2);
		request.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request.put("quantity", 29);
		
		mockMvc.perform(put("/api/product/" + product.getId())
				.headers(httpHeaders).content(request.toString()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].message").value("Product name is required."));		
	}
	
	@Test
	public void get400WhenUpdateProductWithoutImageUrl() throws Exception {
		
		Product product = new Product();
		product.setType("FFT");
        product.setName("Fourier");
        product.setPrice(200);
        product.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product.setQuantity(29);
        
        productRepository.save(product);
		
		JSONObject request = new JSONObject();
		request.put("id", product.getId());
		request.put("type", "FFT");
		request.put("name", "SinCos");
		request.put("price", 2);
		request.put("quantity", 29);
		
		mockMvc.perform(put("/api/product/" + product.getId())
				.headers(httpHeaders).content(request.toString()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].message").value("image_url is required."));		
	}
	
	@Test
	public void get400WhenUpdateProductWithNegativePriceAndNegativeQuantity() 
			throws Exception {
		
		Product product = new Product();
		product.setType("FFT");
        product.setName("Fourier");
        product.setPrice(200);
        product.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product.setQuantity(29);
        
        productRepository.save(product);
		
		JSONObject request = new JSONObject();
		int price = -2;
		request.put("id", product.getId());
		request.put("type", "FFT");
		request.put("name", "Fourier");
		request.put("price", price);
		request.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request.put("quantity", -29);
		
		MvcResult result = mockMvc.perform(put("/api/product/" + product.getId())
				.headers(httpHeaders).content(request.toString()))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		JSONObject resBody = new JSONObject(response.getContentAsString());
		
		JSONArray resTarget = resBody.getJSONArray("errors");
        
		for (int i = 0; i < resTarget.length(); i++) {
			JSONObject element = resTarget.getJSONObject(i);
			String elementMessage = element.getString("message");

			switch (element.getString("code")) {
			case "price":
				assertEquals(elementMessage, "Price must be larger than -1. " + price + " is too small");
				break;
			case "quantity":
				assertEquals(elementMessage, "Availability must be larger than -1.");
				break;
			}
		}
	}
	
	@Test
	public void get422WhenUpdateProductWithWrongId() throws Exception {
		Product product = new Product();
		product.setType("FFT");
        product.setName("Fourier");
        product.setPrice(200);
        product.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product.setQuantity(29);
        
        productRepository.save(product);
		
		JSONObject request = new JSONObject();
		request.put("id", product.getId());
		request.put("type", "FFT");
		request.put("name", "Fourier");
		request.put("price", 2);
		request.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request.put("quantity", 9);
				
		mockMvc.perform(put("/api/product/" + (product.getId() + 1))
				.headers(httpHeaders).content(request.toString()))
				.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void get400WhenUpdateProductsWithNegativePrice() throws Exception {
		Product product1 = new Product();
		product1.setType("Super string");
        product1.setName("M-theory");
        product1.setPrice(910);
        product1.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product1.setQuantity(521);
                
        Product product2 = new Product();
		product2.setType("White hole");
        product2.setName("Gravity");
        product2.setPrice(2019);
        product2.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product2.setQuantity(70);
        
        productRepository.save(product1);
        productRepository.save(product2);
        int id1 = product1.getId();
        int id2 = product2.getId();
		
		JSONArray requests = new JSONArray();
		
		int price1 = -1995;
		JSONObject request1 = new JSONObject();
		request1.put("id", id1);
		request1.put("type", "String theory");
		request1.put("name", "M-theory");
		request1.put("price", price1);
		request1.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request1.put("quantity", 5);
		
		JSONObject request2 = new JSONObject();
		request2.put("id", id2);
		request2.put("type", "Black hole");
		request2.put("name", "singularity");
		request2.put("price", 2019);
		request2.put("imageUrl", "amy-shamblen-784211-unsplash.jpg");
		request2.put("quantity", 87);
		
		requests.put(request1);
		requests.put(request2);
		
		mockMvc.perform(put("/api/product")
				.headers(httpHeaders).content(requests.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Price must be larger than -1. " + price1 + " is too small"));
	}
	
	@Test
	public void get400WhenUpdateProductsWithNegativePrice2() throws Exception {
		Product product1 = new Product();
		product1.setType("Super string");
        product1.setName("M-theory");
        product1.setPrice(910);
        product1.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product1.setQuantity(521);
                
        Product product2 = new Product();
		product2.setType("White hole");
        product2.setName("Gravity");
        product2.setPrice(2019);
        product2.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product2.setQuantity(70);
        
        productRepository.save(product1);
        productRepository.save(product2);
        int id1 = product1.getId();
        int id2 = product2.getId();
		
		JSONArray requests = new JSONArray();
		
		int price1 = -1995;
		JSONObject request1 = new JSONObject();
		request1.put("id", id1);
		request1.put("type", "String theory");
		request1.put("name", "M-theory");
		request1.put("price", price1);
		request1.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request1.put("quantity", 5);
		
		int price2 = -2019;
		JSONObject request2 = new JSONObject();
		request2.put("id", id2);
		request2.put("type", "Black hole");
		request2.put("name", "singularity");
		request2.put("price", price2);
		request2.put("imageUrl", "amy-shamblen-784211-unsplash.jpg");
		request2.put("quantity", 87);
		
		requests.put(request1);
		requests.put(request2);
		
		String s1 = "Price must be larger than -1. " + price1 + " is too small";
		String s2 = "Price must be larger than -1. " + price2 + " is too small";
		
		Set<String> requestSet = new HashSet<>();
		requestSet.add(s1);
		requestSet.add(s2);
		
		MvcResult result = mockMvc.perform(put("/api/product")
				.headers(httpHeaders).content(requests.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		JSONObject resBody = new JSONObject(response.getContentAsString());
		
		JSONArray resTarget = resBody.getJSONArray("errors");
        
		Set<String> responseSet = new HashSet<>();
		for (int i = 0; i < resTarget.length(); i++) {
			JSONObject element = resTarget.getJSONObject(i);
			String elementMessage = element.getString("message");

			responseSet.add(elementMessage);
		}
		
		assertEquals(responseSet, requestSet);
	}
	
	@Test
	public void get400WhenUpdateProductsWithNegativePriceNegativeQuantity() 
			throws Exception {
		
		Product product1 = new Product();
		product1.setType("Super string");
        product1.setName("M-theory");
        product1.setPrice(910);
        product1.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product1.setQuantity(521);
                
        Product product2 = new Product();
		product2.setType("White hole");
        product2.setName("Gravity");
        product2.setPrice(2019);
        product2.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product2.setQuantity(70);
        
        productRepository.save(product1);
        productRepository.save(product2);
        int id1 = product1.getId();
        int id2 = product2.getId();
		
		JSONArray requests = new JSONArray();
		
		int price1 = -1995;
		int quantity1 = -5;
		JSONObject request1 = new JSONObject();
		request1.put("id", id1);
		request1.put("type", "String theory");
		request1.put("name", "M-theory");
		request1.put("price", price1);
		request1.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request1.put("quantity", quantity1);
		
		JSONObject request2 = new JSONObject();
		request2.put("id", id2);
		request2.put("type", "Black hole");
		request2.put("name", "singularity");
		request2.put("price", 2019);
		request2.put("imageUrl", "amy-shamblen-784211-unsplash.jpg");
		request2.put("quantity", 87);
		
		requests.put(request1);
		requests.put(request2);
		
		String s1 = "Price must be larger than -1. " + price1 + " is too small";
		String s2 = "Availability must be larger than -1.";
		
		Set<String> requestSet = new HashSet<>();
		requestSet.add(s1);
		requestSet.add(s2);
		
		MvcResult result = mockMvc.perform(put("/api/product")
				.headers(httpHeaders).content(requests.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		JSONObject resBody = new JSONObject(response.getContentAsString());
		
		JSONArray resTarget = resBody.getJSONArray("errors");
        
		Set<String> responseSet = new HashSet<>();
		for (int i = 0; i < resTarget.length(); i++) {
			JSONObject element = resTarget.getJSONObject(i);
			String elementMessage = element.getString("message");

			responseSet.add(elementMessage);
		}
		
		assertEquals(responseSet, requestSet);
	}
	
	@Test
	public void get400WhenUpdateProductsWithNegativePriceNegativeQuantity2() 
			throws Exception {
		
		Product product1 = new Product();
		product1.setType("Super string");
        product1.setName("M-theory");
        product1.setPrice(910);
        product1.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product1.setQuantity(521);
                
        Product product2 = new Product();
		product2.setType("White hole");
        product2.setName("Gravity");
        product2.setPrice(2019);
        product2.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product2.setQuantity(70);
        
        productRepository.save(product1);
        productRepository.save(product2);
        int id1 = product1.getId();
        int id2 = product2.getId();
		
		JSONArray requests = new JSONArray();
		
		int price1 = -1995;
		JSONObject request1 = new JSONObject();
		request1.put("id", id1);
		request1.put("type", "String theory");
		request1.put("name", "M-theory");
		request1.put("price", price1);
		request1.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request1.put("quantity", 5);
		
		int quantity2 = -87;
		JSONObject request2 = new JSONObject();
		request2.put("id", id2);
		request2.put("type", "Black hole");
		request2.put("name", "singularity");
		request2.put("price", 2019);
		request2.put("imageUrl", "amy-shamblen-784211-unsplash.jpg");
		request2.put("quantity", quantity2);
		
		requests.put(request1);
		requests.put(request2);
		
		String s1 = "Price must be larger than -1. " + price1 + " is too small";
		String s2 = "Availability must be larger than -1.";
		
		Set<String> requestSet = new HashSet<>();
		requestSet.add(s1);
		requestSet.add(s2);
		
		MvcResult result = mockMvc.perform(put("/api/product")
				.headers(httpHeaders).content(requests.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		JSONObject resBody = new JSONObject(response.getContentAsString());
		
		JSONArray resTarget = resBody.getJSONArray("errors");
        
		Set<String> responseSet = new HashSet<>();
		for (int i = 0; i < resTarget.length(); i++) {
			JSONObject element = resTarget.getJSONObject(i);
			String elementMessage = element.getString("message");

			responseSet.add(elementMessage);
		}
		
		assertEquals(responseSet, requestSet);
	}
	
	@Test
	public void get400WhenUpdateProductsWithNegativePriceNegativeQuantity3() 
			throws Exception {
		
		Product product1 = new Product();
		product1.setType("Super string");
        product1.setName("M-theory");
        product1.setPrice(910);
        product1.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product1.setQuantity(521);
                
        Product product2 = new Product();
		product2.setType("White hole");
        product2.setName("Gravity");
        product2.setPrice(2019);
        product2.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product2.setQuantity(70);
        
        productRepository.save(product1);
        productRepository.save(product2);
        int id1 = product1.getId();
        int id2 = product2.getId();
		
		JSONArray requests = new JSONArray();
		
		int price1 = -1995;
		JSONObject request1 = new JSONObject();
		request1.put("id", id1);
		request1.put("type", "String theory");
		request1.put("name", "M-theory");
		request1.put("price", price1);
		request1.put("imageUrl", "brooke-lark-230642-unsplash.jpg");
		request1.put("quantity", 5);
		
		int price2 = -2019;
		int quantity2 = -87;
		JSONObject request2 = new JSONObject();
		request2.put("id", id2);
		request2.put("type", "Black hole");
		request2.put("name", "singularity");
		request2.put("price", price2);
		request2.put("imageUrl", "amy-shamblen-784211-unsplash.jpg");
		request2.put("quantity", quantity2);
		
		requests.put(request1);
		requests.put(request2);
		
		String s1 = "Price must be larger than -1. " + price1 + " is too small";
		String s2 = "Availability must be larger than -1.";
		String s3 = "Price must be larger than -1. " + price2 + " is too small";
		
		Set<String> requestSet = new HashSet<>();
		requestSet.add(s1);
		requestSet.add(s2);
		requestSet.add(s3);
		
		MvcResult result = mockMvc.perform(put("/api/product")
				.headers(httpHeaders).content(requests.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		JSONObject resBody = new JSONObject(response.getContentAsString());
		
		JSONArray resTarget = resBody.getJSONArray("errors");
        
		Set<String> responseSet = new HashSet<>();
		for (int i = 0; i < resTarget.length(); i++) {
			JSONObject element = resTarget.getJSONObject(i);
			String elementMessage = element.getString("message");

			responseSet.add(elementMessage);
		}
		
		assertEquals(responseSet, requestSet);
	}
	
	@Test
	public void get400UpdateProductsWithNegativePriceQuantityWithoutNameImageUrl() 
			throws Exception {
		
		Product product1 = new Product();
		product1.setType("Super string");
        product1.setName("M-theory");
        product1.setPrice(910);
        product1.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product1.setQuantity(521);
                
        Product product2 = new Product();
		product2.setType("White hole");
        product2.setName("Gravity");
        product2.setPrice(2019);
        product2.setImageUrl("brooke-lark-230642-unsplash.jpg");
        product2.setQuantity(70);
        
        productRepository.save(product1);
        productRepository.save(product2);
        int id1 = product1.getId();
        int id2 = product2.getId();
		
		JSONArray requests = new JSONArray();
		
		int price1 = -1995;
		JSONObject request1 = new JSONObject();
		request1.put("id", id1);
		request1.put("type", "String theory");
		request1.put("name", "M-theory");
		request1.put("price", price1);
		request1.put("quantity", 5);
		
		int price2 = -2019;
		int quantity2 = -87;
		JSONObject request2 = new JSONObject();
		request2.put("id", id2);
		request2.put("type", "Black hole");
		request2.put("price", price2);
		request2.put("imageUrl", "amy-shamblen-784211-unsplash.jpg");
		request2.put("quantity", quantity2);
		
		requests.put(request1);
		requests.put(request2);
		
		String s1 = "Price must be larger than -1. " + price1 + " is too small";
		String s2 = "Availability must be larger than -1.";
		String s3 = "Price must be larger than -1. " + price2 + " is too small";
		String s4 = "image_url is required.";
		String s5 = "Product name is required.";
		
		Set<String> requestSet = new HashSet<>();
		requestSet.add(s1);
		requestSet.add(s2);
		requestSet.add(s3);
		requestSet.add(s4);
		requestSet.add(s5);
		
		MvcResult result = mockMvc.perform(put("/api/product")
				.headers(httpHeaders).content(requests.toString()))
				.andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		JSONObject resBody = new JSONObject(response.getContentAsString());
		
		JSONArray resTarget = resBody.getJSONArray("errors");
        
		Set<String> responseSet = new HashSet<>();
		for (int i = 0; i < resTarget.length(); i++) {
			JSONObject element = resTarget.getJSONObject(i);
			String elementMessage = element.getString("message");

			responseSet.add(elementMessage);
		}
		
		System.out.print("\n123456 : requestSet = ");
		for (String s : requestSet) {
			System.out.print(s + " ; ");
		}
		System.out.print("\n123456 : responseSet = ");
		for (String s : responseSet) {
			System.out.print(s + " ; ");
		}
		
		assertEquals(responseSet, requestSet);
	}

	@Test
	void contextLoads() {
	}

}
