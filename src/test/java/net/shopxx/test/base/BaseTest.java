package net.shopxx.test.base;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(value = SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:/applicationContext.xml",
 	"classpath:/applicationContext-shiro.xml",
	"classpath:/applicationContext-mvc.xml"
})
@TestPropertySource(properties = { "TARGET_ENV = dev", "TARGET_EXCHANGE = tcg","webapp.root = WebContent" })
public abstract class BaseTest {
	@Autowired
	protected WebApplicationContext webApplicationContext;
	protected MockMvc mockMvc;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
		
	}

	

	
}
