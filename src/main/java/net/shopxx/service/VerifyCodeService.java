package net.shopxx.service;

public interface VerifyCodeService {
    public void sendVerifyCode(String userId, String sendTarget, String userType, String codeType, String sendIp,Integer type);
    public String getVerifyCode(String userId,String sendTarget,String userType,String codeType);
    public void invaildCode(String userId);
}
