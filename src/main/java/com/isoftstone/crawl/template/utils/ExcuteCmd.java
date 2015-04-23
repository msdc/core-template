package com.isoftstone.crawl.template.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExcuteCmd {
	private static final Logger LOG = LoggerFactory.getLogger(ExcuteCmd.class);

	/**
	 * @Title: excuteCmd
	 * @Description: TODO(执行系统角本)
	 * @param @param cmd 设定文件
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public static void excuteCmd(String cmd) {
		try {
			LOG.info("command:",cmd);
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(cmd);
			InputStream stdin = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(stdin);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			LOG.info("<output></output>");
			while ((line = br.readLine()) != null)
			LOG.info(line);
			LOG.info("");
			int exitVal = proc.waitFor();
			LOG.info("Process exitValue:",exitVal);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
