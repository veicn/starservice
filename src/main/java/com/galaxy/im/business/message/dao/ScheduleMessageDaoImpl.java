package com.galaxy.im.business.message.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.galaxy.im.bean.message.ScheduleMessageBean;
import com.galaxy.im.common.BeanUtils;
import com.galaxy.im.common.db.BaseDaoImpl;
import com.galaxy.im.common.exception.DaoException;
@Repository
public class ScheduleMessageDaoImpl extends BaseDaoImpl<ScheduleMessageBean, Long>implements IScheduleMessageDao {
private Logger log = LoggerFactory.getLogger(ScheduleMessageDaoImpl.class);
	
	public ScheduleMessageDaoImpl(){
		super.setLogger(log);
		
	}

	/**
	 * 保存消息
	 */
	@Override
	public Long saveMessage(ScheduleMessageBean bean) {
		try {
			sqlSessionTemplate.insert(getSqlName("saveMessage"), bean);
			return bean.getId();
		} catch (Exception e) {
			log.error(getSqlName("saveMessage"),e);
			throw new DaoException(e);
		}
	}

	/**
	 * 查询消息
	 */
	@Override
	public List<ScheduleMessageBean> selectMessageList(ScheduleMessageBean mQ) {
		try {
			Map<String, Object> params = BeanUtils.toMap(mQ);
			List<ScheduleMessageBean> contentList = null;
			if(params!=null){
			 contentList = sqlSessionTemplate.selectList(getSqlName("selectMessageList"), params);
			}
			return contentList;
		} catch (Exception e) {
			log.error(getSqlName("selectMessageList"),e);
			throw new DaoException(e);
		}
	}


}
