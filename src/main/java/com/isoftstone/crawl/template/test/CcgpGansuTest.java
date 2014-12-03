package com.isoftstone.crawl.template.test;

import java.util.ArrayList;
import java.util.List;

import com.isoftstone.crawl.template.Component.ParseResult;
import com.isoftstone.crawl.template.Component.Selector;
import com.isoftstone.crawl.template.Component.SelectorFilter;
import com.isoftstone.crawl.template.Component.SelectorFormat;
import com.isoftstone.crawl.template.Component.SelectorIndexer;
import com.isoftstone.crawl.template.Component.TemplateResult;
import com.isoftstone.crawl.template.global.Constants;
import com.isoftstone.crawl.template.utils.MD5Utils;
import com.isoftstone.crawl.template.utils.RedisUtils;
import com.lj.util.http.DownloadHtml;

public class CcgpGansuTest {

	public static void main(String[] args) {
		String url = "http://www.ccgp-gansu.gov.cn/votoonadmin/article/classlist.jsp?pn=1&class_id=214";
		String encoding = "gb2312";
		byte[] input = DownloadHtml.getHtml(url);
		TemplateResult templateResult = CcgpGansuTemplate();
		ParseResult parseResult = null;
		parseResult = TemplateFactory.localProcess(input, encoding,url, templateResult, Constants.TEMPLATE_LIST);
//	    parseResult = TemplateFactory.process(input, encoding,url);
		System.out.println("templateResult:"+templateResult.toJSON());
		System.out.println(parseResult.toJSON());
		//System.out.println(TemplateFactory.getOutlink(parseResult).toString());
//	System.out.println(TemplateFactory.getPaginationOutlink(parseResult).toString());
//		
		url = "http://www.ccgp-shandong.gov.cn/fin_info/site/read.jsp?colcode=0301&id=162302";
		input = DownloadHtml.getHtml(url);
		encoding = "gb2312";
		//parseResult = TemplateFactory.process(input, encoding, url);
		parseResult = TemplateFactory.localProcess(input, encoding, url,templateResult, Constants.TEMPLATE_NEWS);
		System.out.println(parseResult.toJSON());

	}
	public static TemplateResult CcgpGansuTemplate()
	{
		TemplateResult template = new TemplateResult();
		template.setType(Constants.TEMPLATE_LIST);
		String templateUrl = "http://www.ccgp-gansu.gov.cn/votoonadmin/article/classlist.jsp?pn=1&class_id=214";
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
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("body > form > table:nth-child(2) > tbody > tr > td:nth-child(2) > table:nth-child(2) > tbody > tr > td > table > tbody > tr > td > table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(2) > table > tbody > tr:nth-child(2n-1) > td:nth-child(2) > a", Constants.ATTRIBUTE_HREF);
		selector.initContentSelector(indexer, null);
		list.add(selector);
		template.setList(list);
		
		// tstamp
		selector = new Selector();
		indexer = new SelectorIndexer();
		indexer.initJsoupIndexer("body > form > table:nth-child(2) > tbody > tr > td:nth-child(2) > table:nth-child(2) > tbody > tr > td > table > tbody > tr > td > table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(2) > table > tbody > tr:nth-child(2n-1) > td:nth-child(3)", Constants.ATTRIBUTE_TEXT);
		filter = new SelectorFilter();
		filter.initMatchFilter(Constants.YYYYMMDD);
		selector.initFieldSelector("tstamp", "", indexer, filter, null);
		list.add(selector);
		template.setList(list);

		// pagitation outlink  js翻页无法处理
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("body > form > table:nth-child(2) > tbody > tr > td:nth-child(2) > table:nth-child(2) > tbody > tr > td > table > tbody > tr > td > table:nth-child(3) > tbody > tr > td:nth-child(1) > font:nth-child(2)",
				Constants.ATTRIBUTE_TEXT);
		filter = new SelectorFilter();
		filter.initMatchFilter("\\d+");
		selector.initPagitationSelector(Constants.PAGINATION_TYPE_PAGENUMBER,
				"##", "",
				"http://www.ccgp-gansu.gov.cn/votoonadmin/article/classlist.jsp?pn=##&class_id=214", "2", null,
				indexer, filter, null);
		pagination.add(selector);
		template.setPagination(pagination);
		
		
		// title
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("body > table:nth-child(2) > tbody > tr > td > table > tbody > tr > td > table > tbody > tr > td > table > tbody > tr > td > table:nth-child(1) > tbody > tr > td", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("title", "", indexer, null, null);
		news.add(selector);

		// content
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("body > table:nth-child(2) > tbody > tr > td > table > tbody > tr > td > table > tbody > tr > td > table > tbody > tr > td > table:nth-child(2) > tbody > tr > td", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("content", "", indexer, null, null);
		news.add(selector);

		
		RedisUtils.setTemplateResult(template, templateGuid);
		return template;

		}
}
