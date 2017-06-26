package net.shopxx.dao;

import net.shopxx.entity.VerifyCode;

public interface VerifyCodeDao extends BaseDao<VerifyCode, Long> {
	public VerifyCode getVerifyCodeByUserAndVerifyType(String userId,String userType, String sendTarget, String verifyType);
	public long getSendCodeNum(String userId,String sendTarget,String verifyCode);
	public long getIpSendNum(String sendIp);
	public Boolean invalidVerifyCode(String userId);
}
