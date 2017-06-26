package net.shopxx.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import net.shopxx.dao.VerifyCodeDao;
import net.shopxx.entity.Member;
import net.shopxx.entity.VerifyCode;
@Repository("verifyCodeDaoImpl")
public class VerifyCodeDaoImpl extends BaseDaoImpl<VerifyCode, Long> implements VerifyCodeDao {
	 public VerifyCode getVerifyCodeByUserAndVerifyType(String userId,String userType, String sendTarget, String verifyType) {
		 	Date time = new Date();
			String jpql = "select verifyCode from VerifyCode verifyCode where verifyCode.deadTime > :time and verifyCode.verifyType = :verifyType  and verifyCode.sendResult='success'"
					+ "and verifyCode.sendTarget = :sendTarget and verifyCode.userId = :userId "
					+ "and verifyCode.userType = :userType order by verifyCode.genTime DESC";
			try{
			return entityManager.createQuery(jpql, VerifyCode.class).setParameter("time", time).setParameter("verifyType", verifyType).setParameter("sendTarget", sendTarget).setParameter("userId", userId).setParameter("userType", userType).setFirstResult(0).setMaxResults(1).getSingleResult();
			}catch(NoResultException nre){
				return null;
			}
		}
	 
	 
	 public long getSendCodeNum(String userId,String sendTarget,String verifyType){
		 Date time = new Date(new Date().getTime() - 24*3600*1000);
		 String jpql = "select count(*) from VerifyCode verifyCode where verifyCode.genTime > :time and verifyCode.userId = :userId and verifyCode.sendTarget = :sendTarget "
		 		+ "and verifyCode.verifyType = :verifyType";
		 return entityManager.createQuery(jpql,Long.class).setParameter("userId", userId).setParameter("sendTarget", sendTarget).setParameter("verifyType", verifyType).setParameter("time",time).getSingleResult();
	 }
	public long getIpSendNum(String sendIp){
		 Date time = new Date(new Date().getTime() - 24*3600*1000);
		String jpql = "select count(*) from VerifyCode verifyCode where verifyCode.genTime > :time and verifyCode.sendIp = :sendIp";
		return entityManager.createQuery(jpql,Long.class).setParameter("sendIp", sendIp).setParameter("time", time).getSingleResult();
		
	}
	
	public Boolean invalidVerifyCode(String userId){
		String jpql = "update VerifyCode verifyCode set verifyCode.status = \"invalid\" where verifyCode.userId = :userId and verifyCode.status = \"valid\"";
		try{
				entityManager.createQuery(jpql).setParameter("userId", userId).executeUpdate();
				return true;
		}catch(Exception e){
			return false;
		}
	}
}
