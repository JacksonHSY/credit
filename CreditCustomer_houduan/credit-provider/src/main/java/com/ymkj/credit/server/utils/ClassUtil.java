package com.ymkj.credit.server.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassUtil {

	private static Logger log = Logger.getLogger(ClassUtil.class); 
	
	public static final String ACTION_ITSM_PACKAGE = "com.ymkj.credit.server.handler";
	
	/**
	 * 搜索classpath 下面的类集合
	 * 
	 * @param packageName
	 *            包名
	 * @param recursion
	 *            是否递归搜索
	 * @param parentClass
	 *            指定返回Class的超类或接口
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<Class<? extends T>> getClassPathClasses(String packageName, boolean recursion, Class<? extends T> parentClass) {
		List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
		List<String> classNames = getClassPathClassNames(packageName, recursion);
		for (String className : classNames) {
			try {
				Class<?> clazz = Class.forName(className);
				if (parentClass.isAssignableFrom(clazz) && !clazz.getName().equals(parentClass.getName())) {
					classes.add((Class<T>) clazz);
				}
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("class not found : " + className, e);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return classes;
	}

	/**
	 * 搜索classpath下的类的名称集合(包名+类名)
	 * 
	 * @param packageName
	 *            包名
	 * @param recursion
	 *            是否递归搜索
	 * @return
	 */
	public static List<String> getClassPathClassNames(String packageName, boolean recursion) {
		List<String> classNames = new ArrayList<String>();
		String packageDirName = packageName.replace(".", "/");
		try {
			Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				String protocol = url.getProtocol();
				if ("file".equalsIgnoreCase(protocol)) {
					String absolutePackagePath = URLDecoder.decode(url.getFile(), "UTF-8");
					addClassFile(packageName, absolutePackagePath, recursion, classNames);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("package not found : " + packageName, e);
		}
		return classNames;
	}

	/**
	 * 以文件的形式获取包下面的所有Class
	 * 
	 * @param packageName
	 * @param absolutePackagePath
	 * @param recursion
	 * @param classNames
	 */
	private static void addClassFile(String packageName, String absolutePackagePath, final boolean recursion, List<String> classNames) {
		File dir = new File(absolutePackagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			log.warn("package not found : " + packageName);
			return;
		}
		File[] dirFiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (recursion && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		for (File file : dirFiles) {
			if (file.isDirectory()) {
				addClassFile(getClassOrPackName(packageName, file.getName()), file.getAbsolutePath(), recursion, classNames);
			} else {
				String classFileName = file.getName().substring(0, file.getName().length() - 6);
				classNames.add(getClassOrPackName(packageName, classFileName));
			}
		}
	}

	private static String getClassOrPackName(String packageName, String classOrSubPackageName) {
		return StringUtils.isNotBlank(packageName) ? packageName + "." + classOrSubPackageName : classOrSubPackageName;
	}

}
