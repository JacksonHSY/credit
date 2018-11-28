package com.ymkj.credit.web.api.resolver;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.ymkj.credit.web.api.anno.PageSolver;
import com.ymkj.springside.modules.orm.PageInfo;

/**
 * 分页
 * @author YM10156
 *
 */
public class PageArgumentResolver implements HandlerMethodArgumentResolver{

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return null != parameter.getParameterAnnotation(PageSolver.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
		PageInfo<?> page = new PageInfo<>();
		// 页码
		if (StringUtils.isNotBlank(req.getParameter("page"))) {
			int current = Integer.parseInt(req.getParameter("page"));
			page.setPageNo(current);
		}
		//页数
		if(StringUtils.isNotBlank(req.getParameter("rows"))){
			int length = Integer.parseInt(req.getParameter("rows"));
			page.setPageSize(length);
		}
		return page;
	}

}
