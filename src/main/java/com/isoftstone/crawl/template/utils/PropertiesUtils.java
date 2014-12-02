package com.isoftstone.crawl.template.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtils {
	private static Properties prop;

	private static final String properties_fileName = "/template.properties";

	static {
		prop = new Properties();
		InputStream in = Object.class.getResourceAsStream(properties_fileName);
		try {
			// --解决中文乱码问题.
			prop.load(new InputStreamReader(in, "UTF-8"));
		} catch (IOException e) {

		}
	}

	// --禁止外部实例化.
	private PropertiesUtils() {

	}

	public static Properties getInstance() {
		return prop;
	}

	/**
	 * 获取配置文件中的配置.
	 * 
	 * @param key
	 *            配置Key.
	 * @return 配置值.
	 */
	public static String getValue(String key) {
		return prop.getProperty(key);
	}
}
