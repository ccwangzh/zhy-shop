package net.shopxx.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.shopxx.Setting;
import net.shopxx.dao.VerifyCodeDao;
import net.shopxx.entity.VerifyCode;
import net.shopxx.exception.ApplicationException;
import net.shopxx.service.MessageService;
import net.shopxx.service.SmsService;
import net.shopxx.service.VerifyCodeService;
import net.shopxx.util.StringUtil;
import net.shopxx.util.SystemUtils;
@Service("verifyCodeServiceImpl")
public class VerifyCodeServiceImpl implements VerifyCodeService {
	@Resource (name = "verifyCodeDaoImpl")
	private VerifyCodeDao verifyCodeDao;
	@Resource (name = "smsServiceImpl")
	private SmsService smsService;
	@Value ("${maxTargetSend}")
	private int maxTargetSend;
    @Value ("${maxIpSend}")
    private int maxIpSend;	
	
	 private static final Logger logger = LoggerFactory.getLogger(VerifyCode.class);
	 
		@Transactional 
	    public void sendVerifyCode(String userId, String sendTarget, String userType, String codeType, String sendIp,Integer type){
	    	Long lockId=null;
	    	Setting setting = SystemUtils.getSetting();
	    	 if(!StringUtil.isMobile(sendTarget))
			{
				throw new ApplicationException("手机号码异常");
			}
			VerifyCode verifyCode = verifyCodeDao.getVerifyCodeByUserAndVerifyType(userId, userType, sendTarget, codeType);
			if(verifyCode!=null&&(System.currentTimeMillis()-verifyCode.getGenTime().getTime())<60*1000)
			{
			    logger.warn(sendTarget+" 1分钟内发送验证码，被阻止！");
			    throw new ApplicationException("验证码发送太频繁！");
			}
			long num=verifyCodeDao.getSendCodeNum(userId, sendTarget, codeType);
			int maxNum=3;
			try{
				
				maxNum=maxTargetSend;
			}catch (Exception e) {
			}
			if(num>=maxNum){
			    logger.warn(sendTarget+"已经发送"+codeType+"类型的验证码"+num+"次");
			    throw new ApplicationException("验证码已超过"+num+"次，请明日再操作！");
			}
			num = verifyCodeDao.getIpSendNum(sendIp);
			try {
			    maxNum=maxIpSend;
			} catch (Exception e) {
				maxNum=3;
			}
			if(num>=maxNum){
			    logger.warn("(IP:"+sendIp+")已经发送"+codeType+"类型的验证码"+num+"次");
			    throw new ApplicationException("验证码已超过"+num+"次，请明日再操作！");
			}
			Map<String, String> params = new HashMap<String, String>();
			String securityCode = "";
			String sendResult = "success";
			String sendInfo = "";
			try {
			        securityCode = getSecurityCode(6);
			        params.put("verifyCode", securityCode);
			        // 发送
			        smsService.sendRegisterVerifyCode(sendTarget, securityCode);
			        
			} catch (Exception e) {
			    sendResult = "false";
			    sendInfo = e.getMessage();
			}
			verifyCodeDao.invalidVerifyCode(userId);
			VerifyCode verifyCodeNew = new VerifyCode();
			verifyCodeNew.setUserId(userId);
			verifyCodeNew.setSendTarget(sendTarget);
			verifyCodeNew.setVerifyCode(securityCode);
			verifyCodeNew.setVerifyType(codeType);
			verifyCodeNew.setUserType("P");
			verifyCodeNew.setSendResult(sendResult);
			verifyCodeNew.setSendIp(sendIp);
			Date date = new Date();
			verifyCodeNew.setGenTime(date);
			verifyCodeNew.setDeadTime(new Date(date.getTime()+30*60*1000));
			verifyCodeNew.setStatus("valid");
			verifyCodeDao.persist(verifyCodeNew);
	    }
	    
	    public String getVerifyCode(String userId,String sendTarget,String userType,String codeType){
			VerifyCode verifyCode = verifyCodeDao.getVerifyCodeByUserAndVerifyType(userId, userType, sendTarget, codeType);
			if(verifyCode==null||verifyCode.getVerifyCode()==null){
				return "";
			}
			return verifyCode.getVerifyCode();
	    }

	    
	    /**
	     * 生成length长度的验证码
	     * @param length
	     * @return String
	     */
		public String getSecurityCode(int length){
			 // 随机抽取len个字符
	        int len = length;

	        // 字符集合(除去易混淆的数字0、数字1、字母l、字母o、字母O)
	        char[] codes = { '1', '2', '3', '4', '5', '6', '7', '8', '9'};

	        // 字符集合长度
	        int n = codes.length;

	        // 存放抽取出来的字符
	        char[] result = new char[len];

	        for (int i = 0; i < result.length; i++) {
	            // 索引 0 and n-1
	            int r = (int) (Math.random() * n);

	            // 将result中的第i个元素设置为codes[r]存放的数值
	            result[i] = codes[r];

	            // 必须确保不会再次抽取到那个字符，因为所有抽取的字符必须不相同。
	            // 因此，这里用数组中的最后一个字符改写codes[r]，并将n减1
	            codes[r] = codes[n - 1];
	            n--;
	        }
	        String str=String.valueOf(result);
	        logger.info("SecurityCode:{}",str);
	        return str;		
		} 
		
		/**
		 * 验证码无效化
		 */
	    public void invaildCode(String userId){
			verifyCodeDao.invalidVerifyCode(userId);
	    }


}
