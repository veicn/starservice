package com.galaxy.im.business.flow.internalreview.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxy.im.bean.Test;
import com.galaxy.im.business.flow.internalreview.dao.IInternalreviewDao;
import com.galaxy.im.common.CUtils;
import com.galaxy.im.common.db.IBaseDao;
import com.galaxy.im.common.db.service.BaseServiceImpl;
import com.galaxy.im.common.exception.ServiceException;

@Service
public class InternalreviewServiceImpl extends BaseServiceImpl<Test> implements IInternalreviewService{
	
	private Logger log = LoggerFactory.getLogger(InternalreviewServiceImpl.class);
	@Autowired
	private IInternalreviewDao dao;

	@Override
	protected IBaseDao<Test, Long> getBaseDao() {
		return dao;
	}

	@Override
	public Map<String, Object> hasPassMeeting(Map<String, Object> paramMap) {
		try{
			Map<String,Object> result = new HashMap<String,Object>();
			result.put("pass", false);
			result.put("veto", false);
			List<Map<String,Object>> dataList = dao.hasPassMeeting(paramMap);
			
			if(dataList!=null && dataList.size()>0){
				String dictCode;
				Integer pcount = 0;
				for(Map<String,Object> map : dataList){
					dictCode = CUtils.get().object2String(map.get("dictCode"), "");
					pcount = CUtils.get().object2Integer(map.get("pcount"), 0);
					//投资或闪投的会议
					if("meeting1Result:1".equals(dictCode) || "meeting1Result:2".equals(dictCode)){
						if(pcount>0){
							result.put("pass", true);
						
						}
					}else if("meeting1Result:4".equals(dictCode)){//否决的会议
						if(pcount>0){
							result.put("veto", true);
							
						}
					}
				}
			}
			return result;
		}catch(Exception e){
			log.error(InternalreviewServiceImpl.class.getName() + ":projectStatus",e);
			throw new ServiceException(e);
		}
	}
	@Override
	public int saveCeoScheduling(Map<String, Object> paramMap) {
		try {
			return dao.insertCeoScheduling(paramMap);
		} catch (Exception e) {
			log.error(InternalreviewServiceImpl.class.getName() + ":saveCeoScheduling",e);
			throw new ServiceException(e);
		}
		
		
	}


}
	
	


