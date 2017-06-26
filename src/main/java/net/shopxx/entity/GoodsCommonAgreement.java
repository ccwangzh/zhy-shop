/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;


/**
 * Entity - 商品通用协议
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Entity
@Table(name = "xx_goods_common_agreement")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "seq_common_agreement")
public class GoodsCommonAgreement extends BaseEntity<Long>{
    
	private String title;
	private String content;
	
	@Length(max = 200)
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	@Length(max = 200)
	public void setContent(String content) {
        this.content = content;
    }
	
	@Field(store = Store.YES, index = Index.YES, analyze = Analyze.YES)
    @Lob
    public String getContent() {
        return content;
    }

}