package com.isoftstone.crawl.template.test;

import java.util.ArrayList;
import java.util.List;

import com.isoftstone.crawl.template.Component.ParseResult;
import com.isoftstone.crawl.template.Component.Selector;
import com.isoftstone.crawl.template.Component.SelectorFilter;
import com.isoftstone.crawl.template.Component.SelectorFormat;
import com.isoftstone.crawl.template.Component.SelectorIndexer;
import com.isoftstone.crawl.template.Component.TemplateFactory;
import com.isoftstone.crawl.template.Component.TemplateResult;
import com.isoftstone.crawl.template.global.Constants;
import com.isoftstone.crawl.template.utils.MD5Utils;
import com.isoftstone.crawl.template.utils.RedisUtils;
import com.lj.util.http.DownloadHtml;

public class CSTest {

	public static void main(String[] args) {
		String url = "http://www.cs.com.cn/xwzx/hg/index.html";
		String encoding = "gb2312";
		byte[] input = DownloadHtml.getHtml(url);
		TemplateResult templateResult = CSTemplate();
		ParseResult parseResult = null;
		//parseResult = TemplateFactory.localProcess(input, encoding,url, templateResult, Constants.TEMPLATE_LIST);
		parseResult = TemplateFactory.process(input, encoding, url);
		System.out.println(parseResult.toJSON());
//		System.out.println(TemplateFactory.getOutlink(parseResult).toString());
//		//System.out.println(TemplateFactory.getPaginationOutlink(parseResult).toString());
//		
		url = "http://www.cs.com.cn/xwzx/hg/201412/t20141202_4579057.html";
		input = DownloadHtml.getHtml(url);
		encoding = "gb2312";
//		parseResult = TemplateFactory.localProcess(input, encoding, url,templateResult, Constants.TEMPLATE_NEWS);
		parseResult = TemplateFactory.process(input, encoding, url);
		System.out.println(parseResult.toJSON());

	}
	
	public static TemplateResult CSTemplate() {
		TemplateResult template = new TemplateResult();
		template.setType(Constants.TEMPLATE_LIST);
		String templateUrl = "http://www.cs.com.cn/xwzx/hg/index.html";
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
		indexer.initJsoupIndexer("div.column-box ul li a", Constants.ATTRIBUTE_HREF);
		selector.initContentSelector(indexer, null);
		list.add(selector);
		template.setList(list);

		// pagitation outlink  js翻页无法处理
//		indexer = new SelectorIndexer();
//		selector = new Selector();
//		indexer.initJsoupIndexer("div.z_list_page a",
//				Constants.ATTRIBUTE_HREF);
//		selector.initPagitationSelector(Constants.PAGINATION_TYPE_PAGE,
//				"index", "index_",
//				"http://www.cs.com.cn/xwzx/hg/index.html", "1", null,
//				indexer, null, null);
//		pagination.add(selector);
//		template.setPagination(pagination);

		// title
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("div.column-box h1", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("title", "", indexer, null, null);
		news.add(selector);

		// content
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("div#ozoom1", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("content", "", indexer, null, null);
		news.add(selector);

		// tstamp
		selector = new Selector();
		indexer = new SelectorIndexer();
		indexer.initJsoupIndexer("div.column-box div span.ctime", Constants.ATTRIBUTE_TEXT);
		filter = new SelectorFilter();
		filter.initMatchFilter(Constants.YYYYMMDDHHMM);
		selector.initFieldSelector("tstamp", "", indexer, filter, null);
		news.add(selector);
		template.setNews(news);
		
		RedisUtils.setTemplateResult(template, templateGuid);
		return template;
	}
}
