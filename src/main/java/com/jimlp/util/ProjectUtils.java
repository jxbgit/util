package com.jimlp.util;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * JavaEE项目中，项目路径相关工具类
 * 
 * @author JIML
 *
 */
public class ProjectUtils {
	/**
	 * <p>
	 * 获取项目绝对路径
	 *
	 * @return example：[ /D:/tomcat/webapps/projectName/ ]
	 */
	public static String getProjectPath() {
		return ProjectUtils.class.getResource("/").getPath().replace("WEB-INF/classes/", "");
	}

	/**
	 * <p>
	 * 获取项目绝对路径
	 * 
	 * @param request
	 * @return example：[ D:/tomcat/webapps/projectName/ ]
	 */
	public static String getProjectPath(HttpServletRequest request) {
		return request.getServletContext().getRealPath("/").replace("\\", "/");
	}

	/**
	 * <p>
	 * 获取项目 classpath 绝对路径
	 * 
	 * @return example：[ D:/tomcat/webapps/projectName/WEB-INF/classes/ ]
	 */
	public static String getClassPath() {
		return ProjectUtils.class.getResource("/").getPath();
	}

}
