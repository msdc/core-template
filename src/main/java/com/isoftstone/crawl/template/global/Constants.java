package com.isoftstone.crawl.template.global;

import com.isoftstone.crawl.template.utils.PropertiesUtils;

public class Constants {
	// Redis的设置，如IP及PORT
	public static String REDIS_IP = "127.0.0.1";
	public static int REDIS_PORT = 6379;

	// 用于ParseResult，content_outlink中记录了当前列表页面中内容连接的数量，具体的内容页的URL保存在content_outlink_i中
	public static final String CONTENT_OUTLINK = "content_outlink";
	public static final String LABEL_LIST = "label_list";
	// 用于ParseResult，pagination_outlink中记录了当前列表页面中分页的数量，具体的分页URL保存在pagitation_outlink_i中
	public static final String PAGINATION_OUTLINK = "pagination_outlink";
	// 获取分页URL
	public static final String PAGINATION_TYPE_PAGE = "page";
	// 获取分页的末尾页数
	public static final String PAGINATION_TYPE_PAGENUMBER = "number";
	// 获取分页的记录数
	public static final String PAGINATION_TYPE_PAGENUMBER_INTERVAL = "interval";
	// 获取分页的记录数
	public static final String PAGINATION_TYPE_PAGERECORD = "record";
	// 默认返回最大页数
	public static final int MAX_PAGE_COUNT =  Integer.MAX_VALUE;
	// 用于ParseReuslt，新闻页的URL
	public static final String ARTICLE_URL = "article_url";

	public static final String PARSE_RESULT_PREFIX = "_PS";
	
	public static final String TEMPLATE_PREFIX = "_LIST";
	
	public static final String NEWS_PREFIX = "_NESW";

	// 用于Template，区分模板的使用类型，如当前是列表页则获取list拾取器模板，如果是内容页则获取news拾取器模板
	public static final String TEMPLATE_LIST = "list";
	public static final String TEMPLATE_NEWS = "news";
	public static final String TEMPLATE_PAGITATION = "pagitation";
	public static final String TEMPLATE_STATIC = "tags";
	// 用于Selector，用于描述当前拾取器的功能
	public static final String SELECTOR_FEILD = "field";
	public static final String SELECTOR_LABEL = "label";
	public static final String SELECTOR_CONTENT = "content";
	public static final String SELECTOR_PAGITATION = "pagination";

	// 用于SelectorIndexer，索引器的类别
	public static final String INDEXER_JOUP = "jsoup";

	// 用于SelectorIndexer中的Atrribute属性
	public static final String ATTRIBUTE_TEXT = "text";
	public static final String ATTRIBUTE_HTML = "html";
	public static final String ATTRIBUTE_HREF = "href";
	public static final String ATTRIBUTE_SRC = "src";

	// 默认输出的文本编码规则
	public static final String DEFAULT_ENCODING = "UTF-8";

	// 过滤器类别
	public static final String FILTER_REMOVE = "remove";
	public static final String FILTER_MATCH = "match";
	public static final String FILTER_REPLACE = "replace";

	// 格式化器的类别
	public static final String FORMAT_DATE = "date";
	
	public static final String YYYYMMDDHHMMSS = "(20\\d{2}[-|/|年](?:0[1-9]|1[012])[-|/|月](?:0[1-9]|[12][0-9]|3[01])日?\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})";
	public static final String YYYYMMDDHHMM = "(20\\d{2}[-|/|年](?:0[1-9]|1[012])[-|/|月](?:0[1-9]|[12][0-9]|3[01])日?\\s+\\d{1,2}:\\d{1,2})";
	public static final String YYYYMMDD = "(20\\d{2}[-|/|年](?:0[1-9]|1[012])[-|/|月](?:0[1-9]|[12][0-9]|3[01])日?)";
	
	public static final String INCREMENT = PropertiesUtils
			.getValue("template.config.increment");
}
