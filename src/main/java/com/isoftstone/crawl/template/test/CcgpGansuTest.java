package com.isoftstone.crawl.template.test;

import java.util.ArrayList;
import java.util.HashMap;
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

public class CcgpGansuTest {

	public static void main(String[] args) {
		String url = "http://www.ccgp-gansu.gov.cn/votoonadmin/article/classlist.jsp?pn=1&class_id=214";
		String encoding = "gb2312";
		byte[] input = DownloadHtml.getHtml(url);
		TemplateResult templateResult = ccgpGansuTemplate();
		ParseResult parseResult = null;
		// parseResult = TemplateFactory.localProcess(input, encoding,url,
		// templateResult, Constants.TEMPLATE_LIST);
		parseResult = TemplateFactory.process(input, encoding, url);
		System.out.println("templateResult:" + templateResult.toJSON());
		System.out.println(parseResult.toJSON());
		// System.out.println(TemplateFactory.getOutlink(parseResult).toString());
		// System.out.println(TemplateFactory.getPaginationOutlink(parseResult).toString());
		//
		url = "http://www.ccgp-gansu.gov.cn/web/214/224183.html";
		input = DownloadHtml.getHtml(url);
		encoding = "gb2312";
		parseResult = TemplateFactory.process(input, encoding, url);
		// parseResult = TemplateFactory.localProcess(input, encoding,
		// url,templateResult, Constants.TEMPLATE_NEWS);
		System.out.println(parseResult.toJSON());

	}

	public static TemplateResult ccgpGansuTemplate() {
		TemplateResult template = new TemplateResult();
		template.setType(Constants.TEMPLATE_LIST);
		HashMap<String,Object> dictionary = new HashMap<String,Object>(); 
		dictionary.put("行业", "财经");
		dictionary.put("媒体","XXXX");
		String templateUrl = "http://www.ccgp-gansu.gov.cn/votoonadmin/article/classlist.jsp?pn=1&class_id=213";
//		templateUrl = "http://www.ccgp-gansu.gov.cn/votoonadmin/article/classlist.jsp?pn=1&class_id=214";
//		templateUrl = "http://www.ccgp-gansu.gov.cn/votoonadmin/article/classlist.jsp?pn=1&class_id=215";
//		templateUrl = "http://www.ccgp-gansu.gov.cn/votoonadmin/article/classlist.jsp?pn=1&class_id=216";
//		//138、140、141、220
//		templateUrl = "http://www.ccgp-gansu.gov.cn/votoonadmin/article/classlist.jsp?pn=1&class_id=138";
//		templateUrl = "http://www.ccgp-gansu.gov.cn/votoonadmin/article/classlist.jsp?pn=1&class_id=140";
//		templateUrl = "http://www.ccgp-gansu.gov.cn/votoonadmin/article/classlist.jsp?pn=1&class_id=141";
//		templateUrl = "http://www.ccgp-gansu.gov.cn/votoonadmin/article/classlist.jsp?pn=1&class_id=220";

		String templateGuid = MD5Utils.MD5(templateUrl);
		template.setTemplateGuid(templateGuid);
		template.setTags(dictionary);
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

		// tstamp
		Selector label = new Selector();
		label.setType(Constants.SELECTOR_LABEL);
		indexer = new SelectorIndexer();
		indexer.initJsoupIndexer("body > form > table:nth-child(2) > tbody > tr > td:nth-child(2) > table:nth-child(2) > tbody > tr > td > table > tbody > tr > td > table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(2) > table > tbody > tr:nth-child(2n-1) > td:nth-child(3)", Constants.ATTRIBUTE_TEXT);
		filter = new SelectorFilter();
		filter.initMatchFilter(Constants.YYYYMMDD);
		label.initLabelSelector("tstamp", "", indexer, filter, null);
		selector.setLabel(label);
		list.add(selector);
		template.setList(list);

		// pagitation outlink js翻页无法处理
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("body > form > table:nth-child(2) > tbody > tr > td:nth-child(2) > table:nth-child(2) > tbody > tr > td > table > tbody > tr > td > table:nth-child(3) > tbody > tr > td:nth-child(1) > font:nth-child(2)", Constants.ATTRIBUTE_TEXT);
		filter = new SelectorFilter();
		filter.initMatchFilter("\\d+");
		selector.initPagitationSelector(Constants.PAGINATION_TYPE_PAGENUMBER, "##", "", "http://www.ccgp-gansu.gov.cn/votoonadmin/article/classlist.jsp?pn=##&class_id=214", "2", null, indexer, filter, null);
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
		template.setNews(news);

		RedisUtils.setTemplateResult(template, templateGuid);
		return template;

	}
}
