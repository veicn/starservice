package com.galaxy.im.business.meeting.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.galaxy.im.bean.Test;
import com.galaxy.im.bean.meeting.MeetingSchedulingBo;
import com.galaxy.im.common.BeanUtils;
import com.galaxy.im.common.CUtils;
import com.galaxy.im.common.db.BaseDaoImpl;
import com.galaxy.im.common.db.QPage;
import com.galaxy.im.common.exception.DaoException;

@Repository
public class MeetingSchedulingDaoImpl extends BaseDaoImpl<Test, Long> implements IMeetingSchedulingDao {

	private Logger log = LoggerFactory.getLogger(MeetingSchedulingDaoImpl.class);
	
	/**
	 * 会议排期列表（搜索，筛选）
	 */
	@Override
	public QPage queryMescheduling(MeetingSchedulingBo query) {
		try{
			List<Map<String,Object>> contentList = null;
			int total = 0;
			if(query!=null){
				Map<String, Object> params = BeanUtils.toMap(query);
				String sqlName = "com.galaxy.im.business.meeting.dao.IMeetingSchedulingDao.queryMescheduling";
				String sqlName1 = "com.galaxy.im.business.meeting.dao.IMeetingSchedulingDao.queryMeschedulingCount";
				contentList = sqlSessionTemplate.selectList(sqlName,getPageMap(params));
				total = CUtils.get().object2Integer(sqlSessionTemplate.selectOne(sqlName1,getPageMap(params)));
			}    
			return new QPage(contentList,total);
		}catch(Exception e){
			log.error(MeetingSchedulingDaoImpl.class.getName() + "queryMescheduling",e);
			throw new DaoException(e);
		}
	}

	/**
	 * 排期列表的排队数目
	 */
	@Override
	public Long selectpdCount(Map<String, Object> ms) {
		try {
			String sqlName = "com.galaxy.im.business.meeting.dao.IMeetingSchedulingDao.selectpdCount";
			Long count= sqlSessionTemplate.selectOne(sqlName,ms);
			return count;
		} catch (Exception e) {
			throw new DaoException(String.format("查询排队数出错！语句：%s", getSqlName("selectpdCount")),e);
		}
	}

	/**
	 * 排期列表的排队数目
	 */
	@Override
	public Long selectltpdCount(Map<String, Object> ms) {
		try {
			String sqlName = "com.galaxy.im.business.meeting.dao.IMeetingSchedulingDao.selectltpdCount";
			Long count= sqlSessionTemplate.selectOne(sqlName,ms);
			return count;
		} catch (Exception e) {
			log.error(MeetingSchedulingDaoImpl.class.getName() + "selectltpdCount",e);
			throw new DaoException(e);
		}
	}
	
	/**
	 * 个数
	 */
	@Override
	public Long queryCount(MeetingSchedulingBo query) {
		long total = 0L;
		try{
			String sqlName = "com.galaxy.im.business.meeting.dao.IMeetingSchedulingDao.queryMeschedulingCount";
			Map<String, Object> params = BeanUtils.toMap(query);
			total = CUtils.get().object2Long(sqlSessionTemplate.selectOne(sqlName,params));
			return total;
		}catch(Exception e){
			log.error(MeetingSchedulingDaoImpl.class.getName() + "queryCount",e);
			throw new DaoException(e);
		}
	}

}
