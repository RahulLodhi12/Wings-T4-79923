package com.wings;


import org.json.JSONArray;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.json.JSONObject;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
class WingsApplicationTests {

	private MockMvc mvc;

	String c_u = "jack", s_u = "apple", p = "pass_word";

	@Autowired
	WebApplicationContext context;

	@BeforeEach
	void setMockMvc(){
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	@Order(4)
	public void productSearchStatus() throws Exception {
		//test expects the controller method to return any non-null JSON. like List, ResponseEntity<List>, Object
		mvc.perform(get("/api/public/product/search").param("keyword", "tablet")).andExpect(status().is(200))
				.andExpect(jsonPath("$", notNullValue()));	
	}

	@Test
	@Order(5)
	public void productSearchWithoutKeyword() throws Exception {
		mvc.perform(get("/api/public/product/search")).andExpect(status().is(400));
	}

@Test
@Order(6)
public void productSearchWithProductName() throws Exception {
//	expects the controller to return: A JSON Array (i.e., a List of Products)
	MvcResult res = mvc.perform(get("/api/public/product/search").param("keyword", "tablet"))
			.andExpect(status().is(200))
			.andReturn();

	// Parse response as JSONArray
	JSONArray arr = new JSONArray(res.getResponse().getContentAsString());

	// Assert size of the array
	assert (arr.length() > 0);

	// Iterate through the JSONArray
	for (int i = 0; i < arr.length(); i++) {
		JSONObject obj = arr.getJSONObject(i);
		assert (obj.getString("productName").toLowerCase().contains("tablet"));
	}
}

	@Test
	@Order(7)
	public void productSearchWithCategoryName() throws Exception {
//		expects the controller to return: A JSON Array (i.e., a List of Products)
		MvcResult res = mvc.perform(get("/api/public/product/search").param("keyword", "medicine"))
				.andExpect(status().is(200))
				.andReturn();

		// Parse response as JSONArray
		JSONArray arr = new JSONArray(res.getResponse().getContentAsString());

		// Iterate through the JSONArray
		for (int i = 0; i < arr.length(); i++) {
			JSONObject obj = arr.getJSONObject(i);

			// Assuming categoryName is inside a "category" object
			JSONObject categoryObj = obj.getJSONObject("category");
			String categoryName = categoryObj.getString("categoryName");

			// Now check if the categoryName contains "medicine"
			assert (categoryName.toLowerCase().contains("medicine"));
		}
	}

	@Test
	@Order(8)
	public void consumerLoginWithBadCreds() throws Exception {
		mvc.perform(post("/api/public/login").contentType(MediaType.APPLICATION_JSON) //body contains JSON
				.content(getJSONCreds(c_u, "password"))) //what JSON body is contains (u+p)
				.andExpect(status().is(401));
	}

	public String getJSONCreds(String u, String p) {
		Map<String, String> map = new HashMap<>();
		map.put("username", u);
		map.put("password", p);
		return new JSONObject(map).toString();
	}

	public MockHttpServletResponse loginHelper(String u, String p) throws Exception {
		return mvc
				.perform(post("/api/public/login").contentType(MediaType.APPLICATION_JSON).content(getJSONCreds(u, p)))
				.andReturn().getResponse(); //returning ["accessToken" : token]
	}

	@Test
	@Order(9)
	public void consumerLoginWithValidCreds() throws Exception {
//		test does NOT check the JSON structure, the controller return type can be many things 
//		— as long as the response body is non-empty JSON.
		assertEquals(200, loginHelper(c_u, p).getStatus());
		assertNotEquals("", loginHelper(c_u, p).getContentAsString());
	}

	@Test
	@Order(10)
	public void consumerGetCartWithValidJWT() throws Exception {
//		controller MUST return a JSON object: ResponseEntity<Cart> OR Cart OR ResponseEntity<Object>
		String responseContent = loginHelper(c_u, p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken"); //key-"accessToken", value-token


		mvc.perform(get("/api/auth/consumer/cart")
						.header("Authorization", "Bearer " + jwtToken))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.cartId", is(not(equalTo("")))))
				.andExpect(jsonPath("$.cartProducts[0].quantity", is(2)))
				.andExpect(jsonPath("$.cartProducts[0].product.productName",
						containsStringIgnoringCase("Crocin pain relief tablet")))
				.andExpect(jsonPath("$.cartProducts[0].product.category.categoryName", is("Medicines")));
	}

	@Test
	@Order(11)
	public void sellerApiWithConsumerJWT() throws Exception {
		String responseContent = loginHelper(c_u, p).getContentAsString(); //["accessToken" : token] in String form

		JSONObject jsonResponse = new JSONObject(responseContent); //convert to JSONObject
		String jwtToken = jsonResponse.getString("accessToken");


		mvc.perform(get("/api/auth/seller/product")
						.header("Authorization", "Bearer " + jwtToken))
				.andExpect(status().is(403));
	}

	@Test
	@Order(12)
	public void sellerLoginWithValidCreds() throws Exception {
//		test does NOT check the JSON structure, the controller return type can be many things 
//		— as long as the response body is non-empty JSON.
		assertEquals(200, loginHelper(s_u, p).getStatus());
		assertNotEquals("", loginHelper(s_u, p).getContentAsString());
	}

	@Test
	@Order(13)
	public void sellerGetProductsWithValidJWT() throws Exception {
		String responseContent = loginHelper(s_u, p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken");


		mvc.perform(get("/api/auth/seller/product")
						.header("Authorization", "Bearer " + jwtToken))

				.andExpect(status().is(200)).andExpect(jsonPath("$.[0].productId", is(not(equalTo("")))))
				.andExpect(jsonPath("$.[0].productName",
						containsStringIgnoringCase("Apple iPad 10.2 8th Gen WiFi iOS Tablet")))
				.andExpect(jsonPath("$.[0].category.categoryName", is("Electronics")));
	}

	@Test
	@Order(14)
	public void consumerApiWithSellerJWT() throws Exception {
		String responseContent = loginHelper(s_u, p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken");


		mvc.perform(get("/api/auth/consumer/cart")
				.header("Authorization", "Bearer " + jwtToken))

				.andExpect(status().is(403));
	}

	public JSONObject getProduct(int id, String name, Double price, int cId, String cName) {
		Map<String, String> mapC = new HashMap<>();
		mapC.put("categoryId", String.valueOf(cId));
		mapC.put("categoryName", cName);
		Map<String, Object> map = new HashMap<>();
		map.put("productId", id);
		map.put("productName", name);
		map.put("price", String.valueOf(price));
		map.put("category", mapC);
		return new JSONObject(map);
	}

	static String createdURI;

	@Test
	@Order(15)
	public void sellerAddNewProduct() throws Exception {

		String responseContent = loginHelper(s_u, p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken");


		createdURI = mvc
				.perform(post("/api/auth/seller/product")
						.header("Authorization", "Bearer " + jwtToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(getProduct(0, "iPhone 11", 49000.0, 2, "Electronics").toString()))
				.andExpect(status().is(201)).andReturn().getResponse().getRedirectedUrl();
	}

	@Test
	@Order(16)
	public void sellerCheckAddedNewProduct() throws Exception {
		String responseContent = loginHelper(s_u, p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken");


		mvc.perform(get(new URL(createdURI).getPath()).header("Authorization", "Bearer " + jwtToken))
				.andExpect(status().is(200)).andExpect(jsonPath("$.productId", is(3)))
				.andExpect(jsonPath("$.productName", is("iPhone 11"))).andExpect(jsonPath("$.price", is(49000.0)))
				.andExpect(jsonPath("$.category.categoryName", is("Electronics")));



		mvc.perform(get("/api/auth/seller/product")	.header("Authorization", "Bearer " + jwtToken))
				.andExpect(status().is(200)).andExpect(content().string(containsString("iPhone 11")));
	}

	@Test
	@Order(17)
	public void sellerCheckProductFromAnotherSeller() throws Exception {
		String responseContent = loginHelper("glaxo", p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken");


		mvc.perform(get(new URL(createdURI).getPath()).header("Authorization", "Bearer " + jwtToken))
				.andExpect(status().is(404));

		mvc.perform(get("/api/auth/seller/product").header("Authorization", "Bearer " + jwtToken))
				.andExpect(status().is(200)).andExpect(content().string(not(containsString("iPhone 11"))));
	}

	@Test
	@Order(18)
	public void sellerUpdateProduct() throws Exception {

		String responseContent = loginHelper(s_u, p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken");


		String[] arr = createdURI.split("/"); //http://localhost:8000/api/auth/seller/product/3
		mvc.perform(put("/api/auth/seller/product")	.header("Authorization", "Bearer " +jwtToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(getProduct(Integer.parseInt(arr[arr.length - 1]), "iPhone 12", 98000.0, 2, "Electronics")
								.toString()))
				.andExpect(status().is(200));

		mvc.perform(get(new URL(createdURI).getPath())	.header("Authorization", "Bearer " + jwtToken))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.productId", is(Integer.valueOf(arr[arr.length - 1]))))
				.andExpect(jsonPath("$.productName", is("iPhone 12"))).andExpect(jsonPath("$.price", is(98000.0)))
				.andExpect(jsonPath("$.category.categoryName", is("Electronics")));

		mvc.perform(get("/api/auth/seller/product").header("Authorization", "Bearer " + jwtToken))
				.andExpect(status().is(200)).andExpect(content().string(containsString("iPhone 12")));
	}

	@Test
	@Order(19)
	public void sellerUpdateProductWithWrongProductId() throws Exception {

		String responseContent = loginHelper(s_u, p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken");


		mvc.perform(put("/api/auth/seller/product")	.header("Authorization", "Bearer " + jwtToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(getProduct(30, "iPhone 12", 98000.0, 2, "Electronics").toString()))
				.andExpect(status().is(404));
	}

	@Test
	@Order(20)
	public void consumerAddProductToCart() throws Exception {
//		return type can be string OR JSON(Cart)
		String responseContent = loginHelper(c_u, p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken");


		String[] arr = createdURI.split("/");
		mvc.perform(get("/api/auth/consumer/cart")	.header("Authorization", "Bearer " + jwtToken))
				.andExpect(content().string(not(containsString("iPhone 12"))));

		mvc.perform(post("/api/auth/consumer/cart")	.header("Authorization", "Bearer " + jwtToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(getProduct(Integer.parseInt(arr[arr.length - 1]), "iPhone 12", 98000.0, 2, "Electronics")
								.toString()))
				.andExpect(status().is(200));

		mvc.perform(get("/api/auth/consumer/cart").header("Authorization", "Bearer " + jwtToken))
				.andExpect(content().string(containsString("iPhone 12")));
	}

	@Test
	@Order(21)
	public void consumerAddProductToCartAgain() throws Exception {
		String responseContent = loginHelper(c_u, p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken");


		String[] arr = createdURI.split("/");

		mvc.perform(post("/api/auth/consumer/cart")	.header("Authorization", "Bearer " + jwtToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(getProduct(Integer.parseInt(arr[arr.length - 1]), "iPhone 12", 98000.0, 2, "Electronics")
								.toString()))
				.andExpect(status().is(409));
	}

	public JSONObject getCartProduct(JSONObject product, int q) {
		Map<String, Object> map = new HashMap<>();
		map.put("product", product);
		map.put("quantity", q);
		return new JSONObject(map);
	}

	@Test
	@Order(22)
	public void consumerUpdateProductInCart() throws Exception {
		String responseContent = loginHelper(c_u, p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken");


		String[] arr = createdURI.split("/");

		mvc.perform(put("/api/auth/consumer/cart")	.header("Authorization", "Bearer " +jwtToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(getCartProduct(
								getProduct(Integer.parseInt(arr[arr.length - 1]), "iPhone 12", 98000.0, 2, "Electronics"), 3)
								.toString()))
				.andExpect(status().is(200));

		mvc.perform(get("/api/auth/consumer/cart")	.header("Authorization", "Bearer " +jwtToken))
				.andExpect(status().is(200)).andExpect(jsonPath("$.cartId", is(not(equalTo("")))))
				.andExpect(jsonPath("$.cartProducts[1].quantity", is(3)))
				.andExpect(jsonPath("$.cartProducts[1].product.productName", containsStringIgnoringCase("iphone 12")))
				.andExpect(jsonPath("$.cartProducts[1].product.category.categoryName", is("Electronics")));
	}

	@Test
	@Order(23)
	public void consumerUpdateProductInCartWithZeroQuantity() throws Exception {
		String responseContent = loginHelper(c_u, p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken");

		String[] arr = createdURI.split("/");

		mvc.perform(put("/api/auth/consumer/cart")	.header("Authorization", "Bearer " + jwtToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(getCartProduct(
								getProduct(Integer.parseInt(arr[arr.length - 1]), "iPhone 12", 98000.0, 2, "Electronics"), 0)
								.toString()))
				.andExpect(status().is(200));

		mvc.perform(get("/api/auth/consumer/cart")	.header("Authorization", "Bearer " +jwtToken))
				.andExpect(status().is(200)).andExpect(jsonPath("$.cartId", is(not(equalTo("")))))
				.andExpect(jsonPath("$.cartProducts", hasSize(1)))
				.andExpect(jsonPath("$.cartProducts[0].quantity", is(2)))
				.andExpect(jsonPath("$.cartProducts[0].product.productName",
						containsStringIgnoringCase("Crocin pain relief tablet")))
				.andExpect(jsonPath("$.cartProducts[0].product.category.categoryName", is("Medicines")));
	}

	@Test
	@Order(24)
	public void consumerDeleteProductInCart() throws Exception {
		String responseContent = loginHelper(c_u, p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken");


		mvc.perform(delete("/api/auth/consumer/cart")	.header("Authorization", "Bearer " +jwtToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(getProduct(2, "Crocin pain relief tablet", 10.0, 5, "Medicines").toString()))
				.andExpect(status().is(200));

		mvc.perform(get("/api/auth/consumer/cart")	.header("Authorization", "Bearer " + jwtToken))
				.andExpect(status().is(200)).andExpect(jsonPath("$.cartId", is(not(equalTo("")))))
				.andExpect(jsonPath("$.cartProducts", hasSize(0)));
	}

	@Test
	@Order(25)
	public void sellerDeleteProduct() throws Exception {
		String responseContent = loginHelper(s_u, p).getContentAsString();

		JSONObject jsonResponse = new JSONObject(responseContent);
		String jwtToken = jsonResponse.getString("accessToken");


		String[] arr = createdURI.split("/");
		mvc.perform(delete("/api/auth/seller/product/" + Integer.valueOf(arr[arr.length - 1]))	.header("Authorization", "Bearer " + jwtToken)).andExpect(status().is(200));

		mvc.perform(get("/api/auth/seller/product/" + Integer.valueOf(arr[arr.length - 1]))	.header("Authorization", "Bearer " + jwtToken)).andExpect(status().is(404));
	}

}