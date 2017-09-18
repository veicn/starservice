package com.galaxy.im.business.common.dict.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxy.im.bean.common.Dict;
import com.galaxy.im.business.common.dict.service.IDictService;
import com.galaxy.im.common.CUtils;
import com.galaxy.im.common.ResultBean;
import com.galaxy.im.common.StaticConst;

@Controller
@RequestMapping("/dict")
public class DictController {
	private Logger log = LoggerFactory.getLogger(DictController.class);
	
	@Autowired
	private IDictService dictService;
	
	/**
	 * 拜访相关 获取拜访进度、重要性、记录缺失 相关字典值
	 * @return
	 */
	@RequestMapping("selectCallonFilter")
	@ResponseBody
	public Object selectCallonFilter(){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		try{
			Map<String,Object> entityMap = dictService.selectCallonFilter();
			if(entityMap!=null && entityMap.size()>0){
				resultBean.setStatus("OK");
				resultBean.setMap(entityMap);
			}
		}catch(Exception e){
			log.error(DictController.class.getName() + "_selectCallonFilter",e);
		}
		return resultBean;
	}
	
	/**
	 * 结论选择
	 * @return
	 * @author liuli
	 */
	@RequestMapping("selectResultFilter")
	@ResponseBody
	public Object selectResultFilter(@RequestBody String paramString){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		try{
			Map<String,Object> paramMap = CUtils.get().jsonString2map(paramString);	
			Map<String,Object> entityMap = dictService.selectResultFilter(paramMap);
			if(entityMap!=null && entityMap.size()>0){
				resultBean.setStatus("OK");
				resultBean.setMap(entityMap);
			}
		}catch(Exception e){
			log.error(DictController.class.getName() + "_selectResultFilter",e);
		}
		return resultBean;
	}
	
	/**
	 * 原因选择
	 * @return
	 * @author liuli
	 */
	@RequestMapping("selectReasonFilter")
	@ResponseBody
	public Object selectReasonFilter(@RequestBody String paramString){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		try{
			Map<String,Object> paramMap = CUtils.get().jsonString2map(paramString);	
			Map<String,Object> entityMap = dictService.selectReasonFilter(paramMap);
			if(entityMap!=null && entityMap.size()>0){
				resultBean.setStatus("OK");
				resultBean.setMap(entityMap);
			}
		}catch(Exception e){
			log.error(DictController.class.getName() + "_selectReasonFilter",e);
		}
		return resultBean;
	}
	
	
	/**
	 * 1.项目类型		projectType
	 * 2.行业归属		industryOwn
	 * 3.本轮融资轮次	FNO1_1（全息报告数据，添加：无尚未获投和不确定，筛选时：多尚未获投和不确定）     
	 * 4.项目进度 		projectProgress
	 * 5.项目来源		projectSource
	 * 6.股东性质		shareholderNature
	 * 7.股东类型		shareholderType
	 * 8.团队成员职位	FNO1_4（全息报告：团队成员职位列表）
	 * 9.投资方币种		currency
	 * @return
	 * @author liuli
	 */
	@RequestMapping("getDictionaryList")
	@ResponseBody
	public Object getDictionaryList(@RequestBody String paramString){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		try{
			Map<String,Object> paramMap = CUtils.get().jsonString2map(paramString);	
			List<Map<String, Object>> entityMap = dictService.getDictionaryList(paramMap);
			if(entityMap!=null && entityMap.size()>0){
				resultBean.setStatus("OK");
				resultBean.setMapList(entityMap);
			}
		}catch(Exception e){
			log.error(DictController.class.getName() + "getDictionaryList",e);
		}
		return resultBean;
	}
	
	/**
	 * 获取融资历史字典信息
	 * 1.融资轮次
	 * 2.币种
	 * 3.新老股
	 * @param paramString
	 * @return
	 */
	@RequestMapping("getDictionaryFinanceList")
	@ResponseBody
	public Object getDictionaryFinanceList(@RequestBody String paramString){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		try{
			Map<String,Object> paramMap = CUtils.get().jsonString2map(paramString);	
			Map<String, Object> entityMap = dictService.getDictionaryFinanceList(paramMap);
			if(entityMap!=null && entityMap.size()>0){
				resultBean.setStatus("OK");
				resultBean.setMap(entityMap);
			}
		}catch(Exception e){
			log.error(DictController.class.getName() + "getDictionaryFinanceList",e);
		}
		return resultBean;
	}
	
	/**
	 * 获取项目文档筛选条件
	 * @param paramString
	 * @return
	 */
	@RequestMapping("getDictByParent")
	@ResponseBody
	public Object getDictByParent(@RequestBody String paramString){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		try{
			List<Map<String, Object>> listMap =new ArrayList<Map<String, Object>>();
			Map<String,Object> paramMap = CUtils.get().jsonString2map(paramString);	
			if (paramMap.containsKey("fileWorktype")) {
				List<Dict> list = dictService.selectByParentCode(CUtils.get().object2String(paramMap.get("fileWorktype")));
				for(Dict dict : list){
					if(!dict.getDictCode().equals(StaticConst.FILE_WORKTYPE_12)){
						Map<String,Object> map =new HashMap<String,Object>();
						map.put("name", dict.getName());
						map.put("code", dict.getDictCode());
						map.put("id", dict.getId());
						map.put("sort", dict.getDictSort());
						map.put("isDelete", dict.getIsDelete());
						map.put("text", dict.getText());
						map.put("value", dict.getDictValue());
						map.put("createdTime", dict.getCreatedTime());
						map.put("updatedTime", dict.getUpdatedTime());
						map.put("parentCode", dict.getParentCode());
						listMap.add(map);
					}
				}
				resultBean.setStatus("OK");
				resultBean.setMapList(listMap);
			}
		}catch(Exception e){
			log.error(DictController.class.getName() + "getDictByParent",e);
		}
		return resultBean;
	}
	

}
