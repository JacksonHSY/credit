package com.ymkj.credit.web.api.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ymkj.credit.common.entity.Dictionary;
import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.credit.common.ex.JsonException;
import com.ymkj.credit.common.untils.CacherContainer;
import com.ymkj.credit.config.SwitchParamBean;

/**
 * 系统维护检测拦截器
 * 
 * @author 00237071
 * 
 */
@Component
public class SysMaintenceValidationInterceptor extends
		HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object obj) {
		String currentSysSwith = getSysSwitchParameter();
		List<String> errors = (List<String>) obj; // 为了创建filter list
		if (SwitchParamBean.SWITCH_OFF.equals(currentSysSwith)) {
			return true;
		} else {
			validateFailed(JsonException.toJson(BussErrorCode.ERROR_CODE_1111),
					errors);
			return false;
		}
	}

	private void validateFailed(String errorMsg, List<String> errorObj) {
		errorObj.add(errorMsg);
	}

	private String getSysSwitchParameter() {
		String currentSysSwith = SwitchParamBean.SWITCH_OFF;
		List<Dictionary> sysParameterList = CacherContainer.sysSysParameterMap
				.get("sysSwith");
		if (sysParameterList != null && sysParameterList.size() == 1) {
			Dictionary dictionary = sysParameterList.get(0);
			if (dictionary != null) {
				currentSysSwith = dictionary.getDataValue();
			}
		}
		return currentSysSwith;
	}

}
