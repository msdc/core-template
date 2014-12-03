package com.isoftstone.crawl.template.Component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.isoftstone.crawl.template.IComponent.IFilterHandler;

/**
 * ReplaceFilter类是过滤器的实现类,主要提供替换过滤器
 * 
 * @author Tony Wang
 * @see IFilterHandler
 * @see SelectorFilter
 * 
 */
public class ReplaceFilter implements IFilterHandler {
	private static final Log LOG = LogFactory.getLog(RemoveFilter.class);
	private SelectorFilter filter = null;

	public ReplaceFilter() {

	}

	public ReplaceFilter(SelectorFilter filter) {
		this.filter = filter;
	}

	@Override
	public String filter(String str) {
		String value = this.filter.getValue();
		String replaceTo = this.filter.getReplaceTo();
		// value为匹配项，str为需要匹配的内容
		if (!str.isEmpty() && !value.isEmpty()) {
			// value支持正则，可以替换掉匹配的内容
			if (str.contains(value) || str.matches(value)) {
				return str.replace(value, replaceTo);
			} else {
				LOG.warn("Don't get the Replace data (" + value + ") from "
						+ str + ".");
				return str;
			}
		} else {
			LOG.error("Replace filter defined error.");
			return null;
		}
	}
}