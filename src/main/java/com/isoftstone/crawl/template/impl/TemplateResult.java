package com.isoftstone.crawl.template.impl;

import java.util.List;

import com.isoftstone.crawl.template.utils.JSONUtils;

/**
 * TemplateResult类定义了模板对象，用于存储网站的模板信息
 * 
 * @author Tony Wang
 * 
 */
public class TemplateResult {
	private String templateGuid;
	private String parseResultGuid = "";
	private String type = "";
	private List<Selector> list;
	private List<Selector> pagination;
	private List<Selector> news;
	private long fetchTime;

	/**
	 * 获取模板的GUID
	 * 
	 * @return 模板的GUID
	 */
	public String getTemplateGuid() {
		return templateGuid;
	}

	/**
	 * 设置模板的GUID
	 * 
	 * @param templateGuid
	 *            模板的GUID
	 */
	public void setTemplateGuid(String templateGuid) {
		this.templateGuid = templateGuid;
	}

	/**
	 * 获取页面解析结果的GUID
	 * 
	 * @return 页面解析结果的GUID
	 */
	public String getParseResultGuid() {
		return parseResultGuid;
	}

	/**
	 * 设置页面解析结果的GUID
	 * 
	 * @param parseResultGuid
	 *            页面解析结果的GUID
	 */
	public void setParseResultGuid(String parseResultGuid) {
		this.parseResultGuid = parseResultGuid;
	}

	/**
	 * 获取当前页面的模板类型
	 * 
	 * @return 当前页面的模板类型
	 */
	public String getType() {
		return type;
	}

	/**
	 * 设置当前页面的模板类型
	 * 
	 * @param type
	 *            当前页面的模板类型
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 获取列表页面的模板的所有拾取器
	 * 
	 * @return 拾取器集合
	 * @see Selector
	 */
	public List<Selector> getList() {
		return list;
	}

	/**
	 * 设置列表页面的模板的所有拾取器
	 * 
	 * @param list
	 *            拾取器集合
	 * @see Selector
	 */
	public void setList(List<Selector> list) {
		this.list = list;
	}

	public List<Selector> getPagination() {
		return pagination;
	}

	public void setPagination(List<Selector> pagination) {
		this.pagination = pagination;
	}

	/**
	 * 获取新闻页面的模板的所有拾取器
	 * 
	 * @return 拾取器集合
	 * @see Selector
	 */
	public List<Selector> getNews() {
		return news;
	}

	/**
	 * 设置新闻页面的模板的所有拾取器
	 * 
	 * @param news
	 *            拾取器集合
	 * @see Selector
	 */
	public void setNews(List<Selector> news) {
		this.news = news;
	}

	public TemplateResult() {

	}

	public TemplateResult(String templateGuid, String parseResultGuid,
			String type) {
		this.templateGuid = templateGuid;
		this.parseResultGuid = parseResultGuid;
		this.type = type;
	}

	public long getFetchTime() {
		return fetchTime;
	}

	public void setFetchTime(long fetchTime) {
		this.fetchTime = fetchTime;
	}

	/**
	 * 重写toString()，用户输出TemplateResult所有属性数据
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(super.toString() + "\n");
		if (this.templateGuid != null)
			str.append("TEMPLATEGUID: " + this.templateGuid + "\n");
		if (this.parseResultGuid != null)
			str.append("PARSERESULTGUID: " + this.parseResultGuid + "\n");
		if (this.type != null)
			str.append("TYPE: " + this.type + "\n");
		if (this.list != null) {
			for (Selector l : this.list) {
				str.append(l.toString());
			}
		}
		if (this.news != null) {
			for (Selector n : this.news) {
				str.append(n.toString());
			}
		}
		return str.toString();
	}

	public String toJSON() {
		return JSONUtils.getTemplateResultJSON(this);
	}
}
