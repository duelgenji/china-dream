package com.dream.interceptor;

import com.dream.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//		logger.info(" =================== request preHandle =========================");
		
//		System.out.println(request.getRequestURI() + " ----------------------");

//		if (request.getRequestURI().contains("users") || request.getRequestURI().contains("pic") || request.getRequestURI().contains("systemVersions") || request.getRequestURI().contains("notify")
//				|| request.getRequestURI().contains("doctorDir") || request.getRequestURI().contains("breakTalk")) {
//			return true;
//		}

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("currentUser");
		if (null == user) {
//
			user = new User();
			session.setAttribute("currentUser", user);
//			response.setContentType("application/json");
//			response.setCharacterEncoding("UTF-8");
//			PrintWriter writer = response.getWriter();
//			writer.println("{\"success\": \"0\", \"message\": \"err000\"}");
//			writer.close();
//			return false;
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

	}

}
