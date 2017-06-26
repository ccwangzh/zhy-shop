/* 
 * Copyright (C) 上海比捷网络科技有限公司.
 *
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *
 * ============================================================
 *
 * FileName: WebUtil.java 
 *
 * Created: [2014-12-10 下午7:24:24] by haolingfeng
 *
 * $Id$
 * 
 * $Revision$
 *
 * $Author$
 *
 * $Date$
 *
 * ============================================================ 
 * 
 * ProjectName: fbd-core 
 * 
 * Description: 
 * 
 * ==========================================================*/

package net.shopxx.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 上海比捷网络科技有限公司.
 * 
 * Description:
 * 
 * @author haolingfeng
 * @version 1.0
 * 
 */

public class WebUtil {
    private static final Logger logger = LoggerFactory.getLogger(WebUtil.class);
    /**
     * 
     * Description: 获取客户端的ip
     * 
     * @param
     * @return String
     * @throws
     * @Author haolingfeng Create Date: 2014-12-10 下午7:25:03
     */
    public static String getIpAddr(HttpServletRequest request) {
        // 提取用户真实的IP地址
        String ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        logger.debug("IP addr: {}", ipAddress);
        String[] ips = ipAddress.split(",");
        if (ips.length > 1) {
            ipAddress = ips[0].trim();
            logger.debug("Extract real IP addr: {}", ipAddress);
        }
        return ipAddress;
    }
    
	/**
	 * 正则验证手机号
	 * @param str
	 * @return
	 */
	public static boolean regPhoneNum(String str){
		String regEx = "^1[3|4|5|7|8]\\d{9}$";
	    Pattern pattern = Pattern.compile(regEx);
	    Matcher matcher = pattern.matcher(str);
	    return matcher.matches();
	}
	
	/**
	 * 正则验证密码
	 * @param str
	 * @return
	 */
	public static boolean regPwd(String str){
		String regEx = "^(?![\\d]+$)(?![a-zA-Z]+$)(?![^\\da-zA-Z]+$).{6,16}$";
	    Pattern pattern = Pattern.compile(regEx);
	    Matcher matcher = pattern.matcher(str);
	    return matcher.matches();
	}
	
	
	
	/**
	 * 对字符串md5加密
	 *
	 * @param str
	 * @return
	 */
	public static String getMD5(String str) {
		String md5str = null;
        // 生成一个MD5加密计算摘要
        MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			// 计算md5函数
	        md.update(str.getBytes());
	        // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
	        // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
	        md5str = new BigInteger(1, md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			logger.error("生产MD5码失败！", e);
		}
		return md5str;
	}
}
