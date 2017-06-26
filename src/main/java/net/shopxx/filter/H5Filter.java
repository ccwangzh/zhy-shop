
package net.shopxx.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H5Filter implements Filter {
	/** Logger */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String url = httpRequest.getRequestURI().toString();
		if (url.toLowerCase().endsWith("jhtml")) {
			try {
				request.setAttribute("_H5_", true);
				request.getRequestDispatcher(
						url.replace(httpRequest.getContextPath() + "/h5", ""))
						.forward(request, response);
			} catch (Exception e) {
				logger.error("failed to forward for h5 pages", e);
			}
		}else{
			filterChain.doFilter(httpRequest, response);
		}

	}

}