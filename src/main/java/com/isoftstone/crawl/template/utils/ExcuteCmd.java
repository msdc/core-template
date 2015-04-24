package com.isoftstone.crawl.template.utils;

import java.io.BufferedReader;
import java.io.File;
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
	public static int excuteCmd(String cmd) {
		int exitVal =-1;
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
			exitVal = proc.waitFor();
			LOG.info("Process exitValue:",exitVal);
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return exitVal;
	}
	
	public static void removeDir(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				removeDir(file);
			} else
				System.out.println(file + ":" + file.delete());
		}
		System.out.println(dir + "----" + dir.delete());
	}
}
