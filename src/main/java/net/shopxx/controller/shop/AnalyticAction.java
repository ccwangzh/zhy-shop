package net.shopxx.controller.shop;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author zjw 20170504
 *
 */
@Controller("shopAnalyticAction")
@RequestMapping("/analytic")
public class AnalyticAction extends BaseController {

	@Value("${sensors_server_url}")
	private String sensorServerUrl = null;
	@Value("${sensors_web_url}")
	private String sensorWebUrl = null;

	@RequestMapping(value = "/urlAndProject")
	public @ResponseBody Map<String, Object> getUrlAndProject() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("server", sensorServerUrl != null && !sensorServerUrl.startsWith("$") ? sensorServerUrl : "");
		map.put("web", sensorWebUrl != null && !sensorWebUrl.startsWith("$") ? sensorWebUrl : "");
		return map;
	}

}
