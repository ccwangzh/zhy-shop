/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.Admin;
import net.shopxx.entity.Member;
import net.shopxx.entity.Operator;
import net.shopxx.entity.PointLog;
import net.shopxx.service.AdminService;
import net.shopxx.service.MemberService;
import net.shopxx.service.PointLogService;

/**
 * Controller - 积分
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("adminPointController")
@RequestMapping("/admin/point")
public class PointController extends BaseController {

	@Resource(name = "pointLogServiceImpl")
	private PointLogService pointLogService;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	/**
	 * 检查会员
	 */
	@RequestMapping(value = "/check_member", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> checkMember(String username) {
		Map<String, Object> data = new HashMap<String, Object>();
		Member member = memberService.findByUsername(username);
		if (member == null) {
			data.put("message", Message.warn("admin.point.memberNotExist"));
			return data;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("point", member.getPoint());
		return data;
	}

	/**
	 * 调整
	 */
	@RequestMapping(value = "/adjust", method = RequestMethod.GET)
	public String adjust() {
		return "/admin/point/adjust";
	}

	/**
	 * 调整
	 */
	@RequestMapping(value = "/adjust", method = RequestMethod.POST)
	public String adjust(String username, long amount, String memo, RedirectAttributes redirectAttributes) {
		Member member = memberService.findByUsername(username);
		if (member == null) {
			return ERROR_VIEW;
		}
		if (amount == 0) {
			return ERROR_VIEW;
		}
		if (member.getPoint() == null || member.getPoint() + amount < 0) {
			return ERROR_VIEW;
		}
		Admin admin = adminService.getCurrent();
		memberService.addPoint(member, amount, PointLog.Type.adjustment, new Operator(admin), memo);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:log.jhtml";
	}
	
	/**
	 * 调整接口
	 */
	@RequestMapping(value = "/mgmt/adjust", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public @ResponseBody Map<String, Object> mgmtAdjust(String username, long amount, String memo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Member member = memberService.findByUsername(username);
		if (member == null) {
			resultMap.put("succcess", false);
			resultMap.put("msg", "用户不存在");
			return resultMap;
		}
		if (amount == 0) {
			resultMap.put("succcess", false);
			resultMap.put("msg", "积分不能为空");
			return resultMap;
		}
		if (member.getPoint() == null || member.getPoint() + amount < 0) {
			resultMap.put("succcess", false);
			resultMap.put("msg", "调整后不能为负值");
			return resultMap;
		}
		Admin admin = adminService.getCurrent();
		memberService.addPoint(member, amount, PointLog.Type.adjustment, new Operator(admin), memo);
		resultMap.put("succcess", true);
		return resultMap;
	}

	/**
	 * 记录
	 */
	@RequestMapping(value = "/log", method = RequestMethod.GET)
	public String log(Long memberId, Pageable pageable, ModelMap model) {
		Member member = memberService.find(memberId);
		if (member != null) {
			model.addAttribute("member", member);
			model.addAttribute("page", pointLogService.findPage(member, pageable));
		} else {
			model.addAttribute("page", pointLogService.findPage(pageable));
		}
		return "/admin/point/log";
	}

}