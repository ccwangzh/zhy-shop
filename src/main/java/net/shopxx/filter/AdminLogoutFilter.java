
package net.shopxx.filter;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import javax.servlet.*;



public class AdminLogoutFilter extends LogoutFilter {

	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

		Subject subject = SecurityUtils.getSubject();
		String redirectUrl = getRedirectUrl(request, response, subject);
		if (subject.isAuthenticated()) {
			subject.logout(); // session 会销毁，在SessionListener监听session销毁，清理权限缓存
		}

		issueRedirect(request, response, redirectUrl);

		return false;

	}
}