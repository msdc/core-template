package com.isoftstone.crawl.template.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.isoftstone.crawl.template.global.Constants;
import com.isoftstone.crawl.template.impl.ParseResult;
import com.isoftstone.crawl.template.impl.Selector;
import com.isoftstone.crawl.template.impl.SelectorFilter;
import com.isoftstone.crawl.template.impl.SelectorFormat;
import com.isoftstone.crawl.template.impl.SelectorIndexer;
import com.isoftstone.crawl.template.impl.TemplateFactory;
import com.isoftstone.crawl.template.impl.TemplateResult;
import com.isoftstone.crawl.template.utils.MD5Utils;
import com.isoftstone.crawl.template.utils.RedisUtils;
import com.lj.util.http.DownloadHtml;

public class BidnewsTest {

	public static void main(String[] args) {
		// 全国招标信息网

		// 1、生成模板
		String templateUrl = "http://www.bidnews.cn/caigou/gonggao-38046.html";
		// http://www.bidnews.cn/news/dianligongsi-38291.html
		TemplateResult templateResult = bidbewsTemplate(templateUrl);
		// 2、测试列表页
		String encoding = "utf-8";
		byte[] input = DownloadHtml.getHtml(templateUrl);
		System.out.println(JSON.toJSONString(templateResult));
		ParseResult parseResult = null;
		// parseResult = TemplateFactory.localProcess(input, encoding,
		// templateUrl, templateResult, Constants.TEMPLATE_LIST);
		parseResult = TemplateFactory.process(input, encoding, templateUrl);
		System.out.println("templateResult:" + templateResult.toJSON());
		System.out.println(parseResult.toJSON());
		// System.out.println(TemplateFactory.getOutlink(parseResult).toString());
		// /System.out.println(TemplateFactory.getPaginationOutlink(parseResult).toString());
		// 3、测试内容页
		templateUrl = "http://www.bidnews.cn/caigou/zhaobiao-1803747.html";
		input = DownloadHtml.getHtml(templateUrl);
		encoding = "utf-8";
		parseResult = TemplateFactory.process(input, encoding, templateUrl);
		// parseResult = TemplateFactory.localProcess(input, encoding,
		// templateUrl, templateResult, Constants.TEMPLATE_NEWS);
		System.out.println(parseResult.toJSON());
	}

	public static TemplateResult bidbewsTemplate(String templateUrl) {
		HashMap<String, String> dictionary = new HashMap<String, String>();
		TemplateResult template = new TemplateResult();

		template.setType(Constants.TEMPLATE_LIST);
		dictionary.put("行业", "财经");
		dictionary.put("媒体", "XXXX");
		template.setTags(dictionary);

		String templateGuid = MD5Utils.MD5(templateUrl);
		template.setTemplateGuid(templateGuid);

		List<Selector> list = new ArrayList<Selector>();
		List<Selector> news = new ArrayList<Selector>();
		List<Selector> pagination = new ArrayList<Selector>();
		SelectorIndexer indexer = null;
		Selector selector = null;
		SelectorFilter filter = null;
		SelectorFormat format = null;

		// content outlink
		selector = new Selector();
		indexer = new SelectorIndexer();
		indexer.initJsoupIndexer("#DataList1 > tbody > tr > td > div > div.sf-listxxbt > ul > li > a", Constants.ATTRIBUTE_HREF);
		selector.initContentSelector(indexer, null);
		list.add(selector);
		template.setList(list);

		// pagitation outlink js翻页无法处理
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("#AspNetPager1 > cite", Constants.ATTRIBUTE_TEXT);
		filter = new SelectorFilter();
		filter.initMatchFilter("(\\d+)页");
		selector.initPagitationSelector(Constants.PAGINATION_TYPE_PAGENUMBER, "##", "--", "http://www.bidnews.cn/caigou/gonggao-38046##.html", "2", "", indexer, filter, null);
		pagination.add(selector);
		template.setPagination(pagination);

		// title
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("#tdTitle > div > font > b", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("title", "", indexer, null, null);
		news.add(selector);

		// content
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("#contact > div.content", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("content", "", indexer, null, null);
		news.add(selector);

		// tstamp
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("body > table > tbody > tr:nth-child(3) > td > table > tbody > tr > td:nth-child(2) > table > tbody > tr:nth-child(1) > td > table > tbody > tr:nth-child(2) > td > font", Constants.ATTRIBUTE_TEXT);
		filter = new SelectorFilter();
		filter.initMatchFilter(Constants.YYYYMMDD);
		selector.initFieldSelector("tstamp", "", indexer, filter, null);
		news.add(selector);

		template.setNews(news);

		RedisUtils.setTemplateResult(template, templateGuid);
		return template;

	}

}
