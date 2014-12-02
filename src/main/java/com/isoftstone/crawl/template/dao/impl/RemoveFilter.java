package com.isoftstone.crawl.template.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.isoftstone.crawl.template.dao.TFilterHandler;
import com.isoftstone.crawl.template.vo.SelectorFilter;

/**
 * RemoveFilter类是过滤器的实现类,主要提供替换过滤器
 * 
 * @author Tony Wang
 * @see TFilterHandler
 * @see SelectorFilter
 * 
 */
public class RemoveFilter implements TFilterHandler {
	private static final Log LOG = LogFactory.getLog(RemoveFilter.class);
	private SelectorFilter filter = null;

	public RemoveFilter() {

	}

	public RemoveFilter(SelectorFilter filter) {
		this.filter = filter;
	}

	/**
	 * TFIlterHandler接口方法实现
	 * 
	 * @param str
	 *            需要过滤的字符串
	 * @return 过滤后的字符串，如果为与模板中的匹配项匹配，则返回str的值
	 */
	@Override
	public String filter(String str) {
		String value = this.filter.getValue();
		// value为匹配项，str为需要匹配的内容
		if (!str.isEmpty() && !value.isEmpty()) {
			// value支持正则，可以替换掉匹配的内容
			if (str.contains(value) || str.matches(value)) {
				return str.replace(value, "");
			} else {
				LOG.warn("Don't get the remove data (" + value + ") from "
						+ str + ".");
				return str;
			}
		} else {
			LOG.error("Remove filter defined error.");
			return null;
		}
	}
}