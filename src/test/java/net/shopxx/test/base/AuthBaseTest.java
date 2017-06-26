package net.shopxx.test.base;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.UUID;

import javax.servlet.http.Cookie;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.LifecycleUtils;
import org.apache.shiro.util.ThreadState;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public abstract class AuthBaseTest extends DbUnitTest {

	private static ThreadState subjectThreadState;
	/*	@Before
	public void initSecurityManager() throws Exception {
		Factory<org.apache.shiro.mgt.SecurityManager> factory = 
				new IniSecurityManagerFactory("classpath:test.shiro.ini");
		org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
	}
	*/
	
  /*  @After
    public void tearDownSubject() {
       clearSubject();
    }*/
	private static final String tokenName= "token";
	private String token;
	private Cookie cookie;
	@Before
	public void setup() throws Exception {
		 super.setup();
		 token = 	DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30));
         cookie=new Cookie(tokenName,token);
         cookie.setMaxAge(60*60*1000);
	}

	protected MockHttpServletRequestBuilder getPrivate(String url) {
		return  get(url).param(tokenName, token).header(tokenName, token).cookie(cookie);
	}
	protected MockHttpServletRequestBuilder postPrivate(String url) {
		return  post(url).param(tokenName, token).header(tokenName, token).cookie(cookie);
	}
	
	
	
	public AuthBaseTest() {
	}

	/**
	 * Allows subclasses to set the currently executing {@link Subject}
	 * instance.
	 *
	 * @param subject
	 *            the Subject instance
	 */
	protected void setSubject(Subject subject) {
		clearSubject();
		subjectThreadState = createThreadState(subject);
		subjectThreadState.bind();
	}

	protected Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	protected ThreadState createThreadState(Subject subject) {
		return new SubjectThreadState(subject);
	}

	/**
	 * Clears Shiro's thread state, ensuring the thread remains clean for future
	 * test execution.
	 */
	protected void clearSubject() {
		doClearSubject();
	}

	private static void doClearSubject() {
		if (subjectThreadState != null) {
			subjectThreadState.clear();
			subjectThreadState = null;
		}
	}

	protected static void setSecurityManager(org.apache.shiro.mgt.SecurityManager securityManager) {
		SecurityUtils.setSecurityManager(securityManager);
	}

	protected static org.apache.shiro.mgt.SecurityManager getSecurityManager() {
		return SecurityUtils.getSecurityManager();
	}

	@AfterClass
	public static void tearDownShiro() {
		doClearSubject();
		try {
			org.apache.shiro.mgt.SecurityManager securityManager = getSecurityManager();
			LifecycleUtils.destroy(securityManager);
		} catch (UnavailableSecurityManagerException e) {
			// we don't care about this when cleaning up the test environment
			// (for example, maybe the subclass is a unit test and it didn't
			// need a SecurityManager instance because it was using only
			// mock Subject instances)
		}
		setSecurityManager(null);
	}
	 @After
    public void tearDownSubject() {
        clearSubject();
    }
}
