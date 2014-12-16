package com.isoftstone.crawl.template.test;

import java.util.ArrayList;
import java.util.List;

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

public class Ebnuw {

	public static void main(String[] args) {
		String url = "http://www.ebnew.com/tradingIndex.view?key=&pubDateBegin=&pubDateEnd=&infoType=&fundSourceCodes=&zone=&normIndustry=&bidModel=&timeType=&sortMethod=&currentPage=1&length=40";
		String encoding = "utf-8";
		// String html =DownloadHtml.getHtml(url, encoding);
		// System.out.println(html);
		byte[] input = DownloadHtml.getHtml(url);
		TemplateResult templateResult = ebnuwTemplate();
		ParseResult parseResult = null;
		//parseResult = TemplateFactory.localProcess(input, encoding, url, templateResult, Constants.TEMPLATE_LIST);
		parseResult = TemplateFactory.process(input, encoding, url);
		System.out.println("templateResult:" + templateResult.toJSON());
		System.out.println(parseResult.toJSON());
		// System.out.println(TemplateFactory.getOutlink(parseResult).toString());
		///System.out.println(TemplateFactory.getPaginationOutlink(parseResult).toString());
		url = "http://www.ebnew.com/businessShow-v-id-489730665.html";
			   
		input = DownloadHtml.getHtml(url);
		encoding = "utf-8";
		parseResult = TemplateFactory.process(input, encoding, url);
		//parseResult = TemplateFactory.localProcess(input, encoding, url, templateResult, Constants.TEMPLATE_NEWS);
		System.out.println(parseResult.toJSON());

	}

	public static TemplateResult ebnuwTemplate() {
		TemplateResult template = new TemplateResult();
		template.setType(Constants.TEMPLATE_LIST);
		String templateUrl = "http://www.ebnew.com/tradingIndex.view?key=&pubDateBegin=&pubDateEnd=&infoType=&fundSourceCodes=&zone=&normIndustry=&bidModel=&timeType=&sortMethod=&currentPage=1&length=40";
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
		indexer.initJsoupIndexer("body > div.sj_content > div.left_box.sj-result > div > div > p.data_main > span.tit > a", Constants.ATTRIBUTE_HREF);
		selector.initContentSelector(indexer, null);
		list.add(selector);
		template.setList(list);

		// pagitation outlink js翻页无法处理
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("body > div.sj-search > div.sj-search-jg > div.coun.float_lt > span > strong", Constants.ATTRIBUTE_TEXT);
		filter = new SelectorFilter();
		filter.initMatchFilter("\\d+");
		selector.initPagitationSelector(Constants.PAGINATION_TYPE_PAGERECORD, "##", "", "http://www.ebnew.com/tradingIndex.view?key=&pubDateBegin=&pubDateEnd=&infoType=&fundSourceCodes=&zone=&normIndustry=&bidModel=&timeType=&sortMethod=&currentPage=##&length=40", "2", "40", indexer, filter, null);
		pagination.add(selector);
		template.setPagination(pagination);

		// title
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("#mainarea > div:nth-child(1) > div.titlehead > h1", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("title", "", indexer, null, null);
		news.add(selector);

		// content
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("#mainarea > div:nth-child(1) > div.mbox.bder > div:nth-child(3)", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("content", "", indexer, null, null);
		news.add(selector);

		// tstamp
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("#mainarea > div:nth-child(1) > div.mbox.bder > div:nth-child(2) > table > tbody > tr:nth-child(1) > td:nth-child(2)", Constants.ATTRIBUTE_TEXT);
		filter = new SelectorFilter();
		filter.initMatchFilter(Constants.YYYYMMDD);
		selector.initFieldSelector("tstamp", "", indexer, filter, null);
		news.add(selector);

		template.setNews(news);

		RedisUtils.setTemplateResult(template, templateGuid);
		return template;

	}
}
