package com.galaxy.im.business.meeting.service;

import java.util.Map;

import com.galaxy.im.bean.meeting.MeetingRecordBean;
import com.galaxy.im.common.db.QPage;
import com.galaxy.im.common.db.service.IBaseService;

public interface IMeetingRecordService extends IBaseService<MeetingRecordBean>{

	QPage getMeetingRecordList(Map<String, Object> paramMap);


}
