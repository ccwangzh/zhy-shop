package net.shopxx.controller.admin;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import net.shopxx.test.base.AuthBaseTest;


public class GoodsControllerTest extends AuthBaseTest {

	@Resource()
	private org.apache.shiro.mgt.SecurityManager securityManager;
	
	@Before
	public void setup() throws Exception {
		super.setup();
	}
  

	/**
	 * 商品上架
	 * @throws Exception
	 */
	@Test
	public void testShelves() throws Exception {
		this.mockMvc.perform(postPrivate("/admin/goods/shelves").param("ids", "2"))
           .andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andDo(print())
				.andExpect(jsonPath("$.type").value("success"));
	}
 

}
