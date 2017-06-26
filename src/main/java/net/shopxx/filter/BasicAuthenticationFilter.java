/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.filter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

public class BasicAuthenticationFilter extends BasicHttpAuthenticationFilter {

    private Set<String> whitelist = Collections.emptySet();

    public void setWhitelist(String list) {
        whitelist = new HashSet<String>();
        Collections.addAll(whitelist, list.split(",")); //make sure there are no spaces in the string!!!!
    }
    @Override
    protected boolean isEnabled (ServletRequest request, ServletResponse response) throws ServletException, IOException
    {
        if (whitelist.contains(request.getRemoteAddr())) {
            return false;
        }
        return super.isEnabled(request, response);
    }
}