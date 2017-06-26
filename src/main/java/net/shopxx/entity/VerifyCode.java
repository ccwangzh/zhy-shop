package net.shopxx.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "xx_verify_code")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "seq_verify_code")
public class VerifyCode extends BaseEntity<Long> {
	
	 private Long id;
	 	/** 用户ID*/
	    private String userId;
	    /**用户类型*/
	    private String userType;
	    /**验证码*/
	    private String verifyCode;
	    /**验证码类型*/
	    private String verifyType;
	    /**用户类型*/
	    private String sendTarget;
	    /**用户类型*/
	    private Date deadTime;
	    /**用户类型*/
	    private Date genTime;
	    /**用户类型*/
	    private String sendResult;
	    /**用户类型*/
	    private String sendId;
	    /**用户类型*/
	    private String sendIp;//发送的Ip
	    
	    private String status;//已验证/未验证
	    
		@Column(nullable = false, insertable = false, updatable = false)
	    public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getUserType() {
			return userType;
		}

		public void setUserType(String userType) {
			this.userType = userType;
		}
		
		@NotEmpty
		public String getVerifyCode() {
			return verifyCode;
		}

		public void setVerifyCode(String verifyCode) {
			this.verifyCode = verifyCode;
		}
		@NotEmpty
		public String getVerifyType() {
			return verifyType;
		}

		public void setVerifyType(String verifyType) {
			this.verifyType = verifyType;
		}
		@NotEmpty
		public String getSendTarget() {
			return sendTarget;
		}

		public void setSendTarget(String sendTarget) {
			this.sendTarget = sendTarget;
		}

		public Date getDeadTime() {
			return deadTime;
		}

		public void setDeadTime(Date deadTime) {
			this.deadTime = deadTime;
		}

		public Date getGenTime() {
			return genTime;
		}

		public void setGenTime(Date genTime) {
			this.genTime = genTime;
		}

		public String getSendResult() {
			return sendResult;
		}

		public void setSendResult(String sendResult) {
			this.sendResult = sendResult;
		}

		public String getSendId() {
			return sendId;
		}

		public void setSendId(String sendId) {
			this.sendId = sendId;
		}

		public String getSendIp() {
			return sendIp;
		}

		public void setSendIp(String sendIp) {
			this.sendIp = sendIp;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

}
