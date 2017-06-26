package net.shopxx.util;

import javax.servlet.ServletRequest;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public class HttpRequestHelper {

    public static String extractParams(ServletRequest request) {
        ToStringHelper helper = MoreObjects.toStringHelper(request);
        for(Object key : request.getParameterMap().keySet()) {
            Object values = request.getParameterMap().get(key);
            if(values instanceof Object[])
                helper.add(key.toString(), Joiner.on(",").join((Object[])values));
            else helper.add(key.toString(), values);
        }
        return helper.toString();
    }

}
