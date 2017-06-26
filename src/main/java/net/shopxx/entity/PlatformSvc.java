/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity - 平台服务
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Entity
@Table(name = "xx_platform_svc")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "seq_platform_svc")
public class PlatformSvc extends Svc {

	private static final long serialVersionUID = -8691544373427027535L;

}