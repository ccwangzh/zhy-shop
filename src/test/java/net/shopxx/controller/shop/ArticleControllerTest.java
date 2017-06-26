package net.shopxx.controller.shop;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;

import net.shopxx.test.base.DbUnitTest;
public class ArticleControllerTest extends DbUnitTest {
	@Before
	public void setup() throws Exception {
		super.setup();
	}
	@Test
	public void testHits() throws Exception {
		this.mockMvc.perform(get("/article/hits/1")).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				//.andDo(print())
				.andExpect(jsonPath("$").isNumber());
	}
	@Test
	public void testSearch() throws Exception {
		this.mockMvc.perform(
				 get("/article/search")
				.param("keyword", "产品")
				.param("pageNumber", "1")
				 )
		        .andExpect(status().isOk())
				.andExpect(content().contentType("text/html; charset=UTF-8"))
				//.andDo(print())
				.andExpect(model().attributeExists("articleKeyword"));
	}
}
