/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.Maps;
import org.springframework.web.client.RestTemplate;

import net.shopxx.Message;
import net.shopxx.Setting;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.Member;
import net.shopxx.entity.SafeKey;
import net.shopxx.service.CaptchaService;
import net.shopxx.service.MailService;
import net.shopxx.service.MemberService;
import net.shopxx.service.VerifyCodeService;
import net.shopxx.util.SystemUtils;
import net.shopxx.util.WebUtil;

/**
 * Controller - 密码
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopPasswordController")
@RequestMapping("/password")
public class PasswordController extends BaseController {

    @Resource(name = "captchaServiceImpl")
    private CaptchaService captchaService;
    @Resource(name = "memberServiceImpl")
    private MemberService memberService;
    @Resource(name = "mailServiceImpl")
    private MailService mailService;
    @Resource(name = "verifyCodeServiceImpl")
    private VerifyCodeService verifyCodeService;
    @Value("${clearingUrl}")
    private String clearingUrl;
    @Value("${exchangeId}")
    private String exchangeId;
    @Resource
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PasswordController.class);

    /**
     * 重置密码
     */
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public String reset(Model model) {
        model.addAttribute("captchaId", UUID.randomUUID().toString());
        return "/shop/${theme}/password/find";
    }

    /**
     * 重置密码提交
     */
    @RequestMapping(value = "find", method = RequestMethod.POST)
    public @ResponseBody Message reset(String captchaId, String captcha, String username, String newPassword,
            String key) {
        if (!captchaService.isValid(Setting.CaptchaType.resetPassword, captchaId, captcha)) {
            return Message.error("shop.captcha.invalid");
        }
        Map<String, Object> mUser = getUserByMobile(username);
        if (mUser == null || !(boolean) mUser.get(SUCCESS) || mUser.get("userId") == null) {
            return ERROR_MESSAGE;
        }
        if (!checkVerityCode(mUser.get("userId").toString(), username, key)) {
            return Message.warn("shop.register.wrongVerifyCode");
        }
        Member member = memberService.findByUsername(mUser.get("userId").toString());
        if (member == null) {
            return ERROR_MESSAGE;
        }
        Map<String, Object> result = resetPWFromClearing(username, newPassword, mUser.get("userId").toString());
        if (!(boolean) result.get(SUCCESS)) {
            return Message.warn("shop.password.resetFailure");
        }
        return Message.success("shop.password.resetSuccess");
    }

    /**
     * 获取验证码
     * 
     * @param username
     * @param request
     * @return Map
     */
    @RequestMapping(value = "/getVerityCode.jhtml", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> sendVerityCode(String username, HttpServletRequest request) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            Map<String, Object> mUser = getUserByMobile(username);
            if (mUser == null || !(boolean) mUser.get(SUCCESS) || mUser.get("userId") == null) {
                result.put("msg", "该用户不存在");
                return result;
            }
            verifyCodeService.sendVerifyCode(mUser.get("userId").toString(), username, "P", "forgetPWD",
                    request.getRemoteAddr(), 1);
            result.put(SUCCESS, true);
        } catch (Exception e) {
            logger.error("发送验证码失败", e);
            result.put(SUCCESS, false);
            result.put("msg", "发送验证码失败,请稍后再试！");

        }
        return result;
    }

    /**
     * 从清算中心通过手机号获取用户信息
     * 
     * @param userId
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getUserByMobile(String mobile) {
        long start = System.currentTimeMillis();
        Setting setting = SystemUtils.getSetting();
        HashMap<String, Object> resultMap = new HashMap<>();
        // 向清算中心 获得用户的基本信息
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/clearing-web/mgmt/user/checkMobile", clearingUrl))
                .queryParam("mobile", mobile);
        try {
            resultMap = restTemplate.postForObject(builder.build().toUriString(), null, HashMap.class);
            logger.info("从清算系统获取用户资料，耗时：{}", System.currentTimeMillis() - start);
            return resultMap;
        } catch (HttpStatusCodeException e) {
            String responseString = e.getResponseBodyAsString();
            logger.error("从清算系统获取用户资料异常, details: {}", responseString);
        }
        return resultMap;
    }

    /**
     * 向清算重置密码
     * 
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> resetPWFromClearing(String username, String password, String userId) {
        Map<String, Object> resultMap = Maps.newHashMap();
        // 修改密码
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(
                        String.format("%s/clearing-web/mgmt/user/resetPwd", clearingUrl))
                .queryParam("id", username).queryParam("password", password).queryParam("exchangeId", exchangeId);
        try {
            resultMap = restTemplate.postForObject(builder.build().toUriString(), null, HashMap.class);
            if ((boolean) resultMap.get(SUCCESS)) {
                Member member = memberService.findByUsername(userId);
                member.setLastModifiedDate(new Date());
                memberService.update(member);
            } else {
                resultMap.put(SUCCESS, false);
                return resultMap;
            }
        } catch (HttpStatusCodeException e) {
            String responseString = e.getResponseBodyAsString();
            logger.warn("Failed to reset password , details: {}", responseString);
        }
        resultMap.put(SUCCESS, true);

        return resultMap;

    }

    /**
     * 校对验证码
     * 
     * @param username
     * @param verifyCode
     * @return
     */
    private boolean checkVerityCode(String username, String mobile, String verifyCode) {
        if (verifyCode.length() != 6) {
            return false;
        }
        String VCode = verifyCodeService.getVerifyCode(username, mobile, "P", "forgetPWD");
        if (!VCode.equals(verifyCode)) {
            return false;
        }
        verifyCodeService.invaildCode(username);
        return true;
    }
}