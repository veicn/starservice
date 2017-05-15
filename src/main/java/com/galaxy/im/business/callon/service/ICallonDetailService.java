package com.galaxy.im.business.callon.service;

import java.util.Map;

import com.galaxy.im.bean.schedule.ScheduleDetailBean;
import com.galaxy.im.common.db.service.IBaseService;

public interface ICallonDetailService extends IBaseService<ScheduleDetailBean>{

	//获取字典表信息
	Map<String, Object> getDictInfo();

}