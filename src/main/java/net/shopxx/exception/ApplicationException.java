/* 
 * Copyright (C) 上海比捷网络科技有限公司.
 *
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *
 * ============================================================
 *
 * FileName: IUserDao.java 
 *
 * Created: [2014-12-3 10:44:54] by haolingfeng
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
 * ProjectName: fbd 
 * 
 * Description: 
 * 
 * ==========================================================*/
package net.shopxx.exception;

/**
 * 
 * Copyright (C) 上海比捷网络科技有限公司.
 * 
 * Description: 异常类
 *
 * @author haolingfeng
 * @version 1.0
 *
 */
public class ApplicationException extends RuntimeException {
	/**
	 * 构造方法
	 * @param arg0 信息
	 * @param arg1 原因
	 */
	public ApplicationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * 构造方法
	 * @param arg0 信息
	 */
	public ApplicationException(String arg0) {
		super(arg0);
	}

	/**
	 * 构造方法
	 * @param arg0 原因
	 */
	public ApplicationException(Throwable arg0) {
		super(arg0);
	}

}
