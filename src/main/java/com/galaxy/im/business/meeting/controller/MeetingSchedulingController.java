package com.galaxy.im.business.meeting.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxy.im.business.meeting.service.IMeetingSchedulingService;
import com.galaxy.im.common.ResultBean;

@Controller
@RequestMapping("/schedule")
public class MeetingSchedulingController {
	private Logger log = LoggerFactory.getLogger(MeetingSchedulingController.class);
	
	@Autowired
	IMeetingSchedulingService service;

	/**
	 * 会议排期列表（搜索，筛选）
	 * @param paramString
	 * @return
	 */
	@RequestMapping("queryMescheduling")
	@ResponseBody
	public Object queryMescheduling(HttpServletRequest request,HttpServletResponse response,@RequestBody String paramString){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		try{
			
		}catch(Exception e){
			log.error(MeetingSchedulingController.class.getName() + "queryMescheduling",e);
		}
		return resultBean;
	}
}
