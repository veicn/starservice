package com.galaxy.im.business.flow.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxy.im.bean.project.ProjectBean;
import com.galaxy.im.bean.soptask.SopTask;
import com.galaxy.im.business.flow.businessnegotiation.controller.BusinessnegotiationController;
import com.galaxy.im.business.flow.common.dao.IFlowCommonDao;
import com.galaxy.im.common.CUtils;
import com.galaxy.im.common.StaticConst;
import com.galaxy.im.common.db.IBaseDao;
import com.galaxy.im.common.db.service.BaseServiceImpl;
import com.galaxy.im.common.exception.ServiceException;
import com.galaxy.im.common.html.QHtmlClient;

@Service
public class FlowCommonServiceImpl extends BaseServiceImpl<ProjectBean> implements IFlowCommonService{
	private Logger log = LoggerFactory.getLogger(FlowCommonServiceImpl.class);
	
	@Autowired
	private IFlowCommonDao dao;

	@Autowired
	private Environment env;
	
	@Override
	protected IBaseDao<ProjectBean, Long> getBaseDao() {
		return dao;
	}

	/**
	 * 取得项目状态
	 */
	@Override
	public Map<String, Object> projectStatus(Map<String, Object> paramMap) {
		try{
			Map<String,Object> result = new HashMap<String,Object>();
			result.put("flag", 0);
			result.put("message", "未知错误");
			Map<String,Object> daoMap = dao.projectStatus(paramMap);
			
			if(daoMap!=null && !daoMap.isEmpty()){
				String projectStatus = CUtils.get().object2String(daoMap.get("dictCode"),"");
				if("projectStatus:0".equals(projectStatus)){
					result.put("flag",1);
				}else if("projectStatus:1".equals(projectStatus)){
					result.put("message","本项目处于投后运营状态，不能进行操作");
				}else if("projectStatus:2".equals(projectStatus)){
					result.put("message","本项目已经被否决，不能进行操作");
				}else if("projectStatus:3".equals(projectStatus)){
					result.put("message","本项目处于退出状态，不能进行操作");
				}else{
					result.put("message","本项目所处的阶段异常，请联系管理员");
				}
			}
			return result;
		}catch(Exception e){
			log.error(FlowCommonServiceImpl.class.getName() + ":projectStatus",e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 否决项目
	 */
	@Override
	public Boolean vetoProject(Map<String, Object> paramMap) {
		try{
			return dao.vetoProject(paramMap)>0;
		}catch(Exception e){
			log.error(FlowCommonServiceImpl.class.getName() + ":vetoProject",e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 流程进入到下一阶段
	 */
	@Override
	public boolean enterNextFlow(Map<String, Object> paramMap) {
		try{
			return dao.enterNextFlow(paramMap)>0;
		}catch(Exception e){
			log.error(FlowCommonServiceImpl.class.getName() + ":enterNextFlow",e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 创建代办任务
	 */
	@Override
	public Long insertsopTask(SopTask bean) {
		try{
			return dao.insertsopTask(bean);
		}catch(Exception e){
			log.error(FlowCommonServiceImpl.class.getName() + ":insertsopTask",e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取最新会议
	 */
	@Override
	public Map<String, Object> getLatestMeetingRecordInfo(Map<String, Object> paramMap) {
		Map<String,Object> result = dao.getLatestMeetingRecordInfo(paramMap);
		return result;
	}

	/**
	 * 权限---获取用户所在部门id
	 * @param guserid
	 * @param request
	 * @param response
	 * @return
	 */
	@Override
	public long getDeptId(Long guserid, HttpServletRequest request, HttpServletResponse response) {
		//调用客户端
		Map<String,Object> headerMap = QHtmlClient.get().getHeaderMap(request);
		String url = env.getProperty("power.server") + StaticConst.getCreadIdInfo;
		Map<String,Object> qMap = new HashMap<String,Object>();
		qMap.put("createdId",guserid);
		JSONArray valueJson=null;
		List<Map<String, Object>> list = null;
		String result = QHtmlClient.get().post(url, headerMap, qMap);
		if("error".equals(result)){
			log.error(BusinessnegotiationController.class.getName() + "getDeptId：获取创建人信息时出错","此时服务器返回状态码非200");
		}else{
			boolean flag = true;
			JSONObject resultJson = JSONObject.parseObject(result);
			if(resultJson!=null && resultJson.containsKey("value")){
				valueJson = resultJson.getJSONArray("value");
				if(resultJson.containsKey("success") && "true".equals(resultJson.getString("success"))){
					flag = false;
				}
				list=CUtils.get().jsonString2list(valueJson);
			}
			if(flag){
				log.error(BusinessnegotiationController.class.getName() + "getDeptId：获取创建人信息时出错","服务器返回正常，获取数据失败");
			}
		}
		if(list!=null){
			for(Map<String, Object> vMap:list){
				guserid= CUtils.get().object2Long( vMap.get("deptId"));
			}
		}else{
			guserid=0l;
		}
		return guserid;
	}

	/**
	 * 获取最新上传文件的信息
	 */
	@Override
	public Map<String, Object> getLatestSopFileInfo(Map<String, Object> paramMap) {
		Map<String,Object> result = dao.getLatestSopFileInfo(paramMap);
		return result;
	}

	
}
