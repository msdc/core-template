package com.isoftstone.crawl.template.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoftstone.crawl.template.test.MergeNutchData;


public class ExcuteCmd {
	private static final Log LOG = LogFactory.getLog(ExcuteCmd.class);

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
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(cmd);
			InputStream stdin = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(stdin);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			LOG.info(line);
			LOG.info("");
			exitVal = proc.waitFor();
			LOG.info("Process exitValue:"+exitVal);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return exitVal;
	}
}
