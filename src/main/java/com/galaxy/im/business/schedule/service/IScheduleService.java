package com.galaxy.im.business.schedule.service;

import java.util.List;
import java.util.Map;

import com.galaxy.im.bean.schedule.ScheduleInfo;
import com.galaxy.im.common.db.service.IBaseService;

public interface IScheduleService  extends IBaseService<ScheduleInfo>{

	//时间是否冲突
	List<Map<String, Object>> ctSchedule(Map<String, Object> map);

}
