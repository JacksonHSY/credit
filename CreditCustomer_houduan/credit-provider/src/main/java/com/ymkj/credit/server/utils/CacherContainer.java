package com.ymkj.credit.server.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ymkj.credit.common.entity.Dictionary;




public class CacherContainer {

	// key = PR_TYPE，value = 对应  PR_TYPE 的 SysParameter 结果集
	public static final Map<String, List<Dictionary>> sysSysParameterMap = new HashMap<String, List<Dictionary>>();
	
	public static String getParamValue(String prType, String prName){
		List<Dictionary> list = CacherContainer.sysSysParameterMap.get(prType);
		
		for(Dictionary sys : list){
			if(sys.getDataName().equals(prName)){
				return sys.getDataValue();
			}
		}
		
		return "";
	}
	
}
