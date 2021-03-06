package com.galaxy.im.business.flow.stockequity.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxy.im.bean.common.SessionBean;
import com.galaxy.im.bean.project.SopProjectBean;
import com.galaxy.im.bean.soptask.SopTask;
import com.galaxy.im.bean.talk.SopFileBean;
import com.galaxy.im.business.flow.common.service.IFlowCommonService;
import com.galaxy.im.business.flow.investmentintent.service.IInvestmentintentService;
import com.galaxy.im.business.flow.stockequity.service.IStockequityService;
import com.galaxy.im.business.operationLog.controller.ControllerUtils;
import com.galaxy.im.business.sopfile.service.ISopFileService;
import com.galaxy.im.common.CUtils;
import com.galaxy.im.common.ResultBean;
import com.galaxy.im.common.StaticConst;
import com.galaxy.im.common.webconfig.interceptor.operationLog.UrlNumber;

/**
 * 股权交割
 * 
 * @author
 */
@Controller
@RequestMapping("/flow/stockequity")
public class StockequityController {
	private Logger log = LoggerFactory.getLogger(StockequityController.class);
	@Autowired
	private IFlowCommonService fcService;

	@Autowired
	IInvestmentintentService service;

	@Autowired
	IStockequityService isservice;
	@Autowired
	ISopFileService fileService;

	/**
	 * 判断项目操作按钮状态
	 * 依据：需要上传完成工商转让凭证同时判断资金拨付代办任务的状态是"taskStatus:2":待完工或者"taskStatus:3":已完工
	 * 前端处理：满足条件则"进入投后运营"按钮点亮;
	 * 
	 * @param projectId
	 *            项目ID-使用JSON的方式传递
	 * @return entity -> pass:true,
	 * 
	 */

	@RequestMapping("poButonStatus")
	@ResponseBody
	public Object projectOperateStatus(@RequestBody String paramString) {
		ResultBean<Object> result = new ResultBean<Object>();
		try {
			Map<String, Object> paramMap = CUtils.get().jsonString2map(paramString);
			paramMap.put("fileStatus2", StaticConst.FILE_STATUS_2);
			paramMap.put("fileStatus4", StaticConst.FILE_STATUS_4);
			Map<String, Object> m = isservice.operateStatus(paramMap);
			if (CUtils.get().mapIsNotEmpty(m)) {
				result.setEntity(m);
			}
			// 项目最后上传的文件信息
			paramMap.put("fileWorkType", StaticConst.FILE_WORKTYPE_8);
			Map<String, Object> map = fcService.getLatestSopFileInfo(paramMap);
			if (CUtils.get().mapIsNotEmpty(map)) {
				result.setMap(map);
			}
			result.setStatus("OK");
		} catch (Exception e) {
			log.error(StockequityController.class.getName() + "projectOperateStatus",e);
		}
		return result;
	}

	/**
	 * 进入投后运营
	 * 
	 * @param paramString
	 * @return
	 */

	@RequestMapping("startIntervestoperate")
	@ResponseBody
	public Object startIntervestoperate(@RequestBody String paramString,HttpServletRequest request) {
		ResultBean<Object> resultBean = new ResultBean<Object>();
		Map<String, Object> map = new HashMap<>();
		resultBean.setFlag(0);
		try {
			String progressHistory = "";
			Map<String, Object> paramMap = CUtils.get().jsonString2map(paramString);
			if (CUtils.get().mapIsNotEmpty(paramMap)) {
				SopProjectBean sopBean = fcService.getSopProjectInfo(paramMap);
				if (sopBean != null) {
					if (sopBean.getProjectProgress().equals(StaticConst.PROJECT_PROGRESS_9)) {
						// 项目当前所处在股权交割阶段,在流程历史记录拼接要进入的下个阶段
						if (!"".equals(sopBean.getProgressHistory()) && sopBean.getProgressHistory() != null) {
							progressHistory = sopBean.getProgressHistory() + "," + StaticConst.PROJECT_PROGRESS_10;
						} else {
							progressHistory = StaticConst.PROJECT_PROGRESS_10;
						}
						paramMap.put("projectProgress", StaticConst.PROJECT_PROGRESS_10); // 表示进入投后运营阶段
						paramMap.put("projectStatus", "projectStatus:1");// 将项目状态调整为“投后运营”
						paramMap.put("progressHistory", progressHistory); // 流程历史记录
						if (fcService.enterNextFlow(paramMap)) {
							resultBean.setFlag(1);
							map.put("projectProgress", StaticConst.PROJECT_PROGRESS_10);
							resultBean.setStatus("OK");
							resultBean.setMap(map);
							//记录操作日志
							ControllerUtils.setRequestParamsForMessageTip(request, sopBean.getProjectName(), sopBean.getId(),"");
						}else {
							resultBean.setMessage("项目当前状态或进度已被修改，请刷新");
						}
					} else {
						resultBean.setMessage("项目当前状态或进度已被修改，请刷新");
					}
				}
			}
		} catch (Exception e) {
			log.error(StockequityController.class.getName() + "startIntervestoperate",e);
		}
		return resultBean;
	}
	
	/**
	 * 股权交割信息
	 * @param paramString
	 * @return
	 */
	@RequestMapping("stockequityList")
	@ResponseBody
	public Object stockequityList(HttpServletRequest request,HttpServletResponse response,@RequestBody String paramString){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		resultBean.setFlag(0);
		try{
			List<String> fileWorkTypeList = new ArrayList<String>();
			List<Integer> taskFlagList = new ArrayList<Integer>();
			Map<String,Object> paramMap = CUtils.get().jsonString2map(paramString);
			Map<String,Object> resultMap = new HashMap<String,Object>();
			fileWorkTypeList.add(StaticConst.FILE_WORKTYPE_8);
			fileWorkTypeList.add(StaticConst.FILE_WORKTYPE_9);
			
			paramMap.put("fileWorkTypeList", fileWorkTypeList);
			List<Map<String,Object>> list = fcService.getSopFileList(paramMap);
			
			//工商转让凭证和资金调拨凭证的代办任务的状态和认领人
			taskFlagList.add(StaticConst.TASK_FLAG_GSBG);
			taskFlagList.add(StaticConst.TASK_FLAG_ZJBF);
			paramMap.put("taskFlagList", taskFlagList);
			List<Map<String,Object>> taskList = fcService.getSopTaskList(paramMap);
			if(taskList!=null){
				for(Map<String,Object> map:taskList){
					if(map.containsKey("assignUid")){
						//通过用户id获取一些信息
						List<Map<String, Object>> userList = fcService.getDeptId(CUtils.get().object2Long(map.get("assignUid")),request,response);
						if(userList!=null){
							for(Map<String, Object> vMap:userList){
								String userName=CUtils.get().object2String(vMap.get("userName"));
								map.put("assignName", userName);
							}
						}
					}
				}
			}
			
			resultMap.put("fileInfo", list);
			resultMap.put("taskInfo", taskList);
			resultBean.setMap(resultMap);
			resultBean.setStatus("ok");
		}catch(Exception e){
			log.error(StockequityController.class.getName() + "stockequityList",e);
		}
		return resultBean;
	}
	
	/**
	 * 上传/更新工商转让凭证，资金拨付凭证
	 * @param paramString
	 * @return
	 */
	@RequestMapping("uploadStockequity")
	@ResponseBody
	public Object uploadStockequity(HttpServletRequest request,HttpServletResponse response,@RequestBody SopFileBean bean){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		resultBean.setFlag(0);
		long id=0L;
		int prograss=0;
		try{
			Long deptId =0L;
			SessionBean sessionBean = CUtils.get().getBeanBySession(request);
			//通过用户id获取一些信息
			List<Map<String, Object>> list = fcService.getDeptId(sessionBean.getGuserid(),request,response);
			if(list!=null){
				for(Map<String, Object> vMap:list){
					deptId= CUtils.get().object2Long( vMap.get("deptId"));
				}
			}
			
			paramMap.put("projectId", bean.getProjectId());
			paramMap.put("fileWorkType", bean.getFileWorkType());
			
			if(bean!=null){
				SopProjectBean sopBean = fcService.getSopProjectInfo(paramMap);
				if(sopBean!=null && sopBean.getProjectStatus().equals(StaticConst.PROJECT_STATUS_0)
						&& sopBean.getProjectProgress().equals(bean.getProjectProgress())){
					//项目id，当前阶段，所属事业线
					bean.setProjectId(sopBean.getId());
					bean.setProjectProgress(sopBean.getProjectProgress());
					//事业线为用户部门id
					bean.setCareerLine(deptId);
					//文件类型
					String fileType =fcService.getFileType(bean.getFileSuffix());
					bean.setFileType(fileType);
					//文件名称拆分
					Map<String,String> nameMap = fcService.transFileNames(bean.getFileName());
					bean.setFileName(nameMap.get("fileName"));
					//文件状态：已上传
					bean.setFileStatus(StaticConst.FILE_STATUS_2);
					bean.setFileValid(1);
					bean.setCreatedTime(new Date().getTime());
					bean.setFileUid(sessionBean.getGuserid());
					//业务操作
					if(bean.getId()!=null && bean.getId()!=0){
						//更新：添加新的一条记录(文件历史表)
						id=fcService.updateSopFile(bean);
						@SuppressWarnings("unused")
						long vid = fileService.insertHistory(bean.getId());
						prograss=1;
					}else{
						//上传之前:查数据库中是否存在信息，存在更新，否则新增
						Map<String,Object> info = fcService.getLatestSopFileInfo(paramMap);
						if(info!=null && info.get("id")!=null && CUtils.get().object2Long(info.get("id"))!=0){
							bean.setId(CUtils.get().object2Long(info.get("id")));
							bean.setUpdatedTime(new Date().getTime());
							id=fcService.updateSopFile(bean);
						}else{
							id =fcService.addSopFile(bean);
						}
					}
					if(id>0){
						//更新工商转让凭证待办任务
						SopTask taskBean = new SopTask();
						if (bean.getFileWorkType().equals(StaticConst.FILE_WORKTYPE_8)) {
							taskBean.setTaskName(StaticConst.TASK_NAME_GSBG);
							taskBean.setTaskFlag(StaticConst.TASK_FLAG_GSBG);
						}else if (bean.getFileWorkType().equals(StaticConst.FILE_WORKTYPE_9)) {//更新资金调拨凭证
							taskBean.setTaskName(StaticConst.TASK_NAME_ZJBF);
							taskBean.setTaskFlag(StaticConst.TASK_FLAG_ZJBF);
						}
						taskBean.setProjectId(bean.getProjectId());
						taskBean.setTaskStatus(StaticConst.TASK_STATUS_YWG);
						taskBean.setTaskType(StaticConst.TASK_TYPE_XTBG);
						taskBean.setUpdatedTime(new Date().getTime());
						taskBean.setTaskDeadline(new Date());
						@SuppressWarnings("unused")
						Long taskId = fcService.updateSopTask(taskBean);
						//返回信息
						paramMap.clear();
						paramMap.put("fileId", id);
						resultBean.setMap(paramMap);
						resultBean.setStatus("ok");
						resultBean.setFlag(1);
					}
				}else{
					resultBean.setMessage("项目当前状态或进度已被修改，请刷新");	
				}
				if(id!=0L){
					//记录操作日志
					UrlNumber uNum = fcService.setNumForFile(prograss,bean);
					ControllerUtils.setRequestParamsForMessageTip(request, sopBean.getProjectName(), sopBean.getId(), null, uNum);
				}
			}
		}catch(Exception e){
			log.error(StockequityController.class.getName() + "uploadStockequity",e);
		}
		return resultBean;
	}
}
