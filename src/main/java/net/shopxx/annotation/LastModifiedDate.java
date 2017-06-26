/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation - 最后修改日期
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { FIELD, METHOD })
public @interface LastModifiedDate {

}