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

public class CsTest {

	public static void main(String[] args) {

		// 1、生成模板
		String url = "http://www.cs.com.cn/xwzx/hg/index.html";
		TemplateResult templateResult = cSTemplate(url);
		// 2、测试列表页
		ParseResult parseResult = null;
		String encoding = "gb2312";
		byte[] input = DownloadHtml.getHtml(url);
		// parseResult = TemplateFactory.localProcess(input, encoding,url,
		// templateResult, Constants.TEMPLATE_LIST);
		parseResult = TemplateFactory.process(input, encoding, url);
		System.out.println(parseResult.toJSON());
		// 3、测试内容页
		url = "http://www.cs.com.cn/xwzx/hg/201411/t20141125_4572144.html";
		input = DownloadHtml.getHtml(url);
		encoding = "gb2312";
		parseResult = TemplateFactory.localProcess(input, encoding, url, templateResult, Constants.TEMPLATE_NEWS);
		// parseResult = TemplateFactory.process(input, encoding, url);
		System.out.println(parseResult.toJSON());

	}

	public static TemplateResult cSTemplate(String templateUrl) {
		TemplateResult template = new TemplateResult();
		template.setType(Constants.TEMPLATE_LIST);
		String templateGuid = MD5Utils.MD5(templateUrl);
		template.setTemplateGuid(templateGuid);
		template.setState(Constants.UN_FETCH);
		List<Selector> list = new ArrayList<Selector>();
		List<Selector> news = new ArrayList<Selector>();
		List<Selector> pagination = new ArrayList<Selector>();
		SelectorIndexer indexer = null;
		Selector selector = null;
		SelectorFilter filter = null;
		SelectorFormat format = null;

		// content outlink
		indexer = new SelectorIndexer();
		selector = new Selector();//
		indexer.initJsoupIndexer("body > div.content > div.content_left > div > ul > li > a", Constants.ATTRIBUTE_HREF);
		selector.initContentSelector(indexer, null);
		list.add(selector);
		template.setList(list);

		
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("div.z_list_page a", Constants.ATTRIBUTE_HREF);
		selector.initPagitationSelector(Constants.PAGINATION_TYPE_PAGE, "index", "index_", templateUrl, "1", null, indexer, null, null);
		pagination.add(selector);
		template.setPagination(pagination);

		// title
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("div.content_left > div.column-box > h1", Constants.ATTRIBUTE_TEXT);
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
		indexer = new SelectorIndexer();// body > div:nth-child(10) >
										// div.content_left > div.column-box >
										// div:nth-child(5) > span.ctime
		indexer.initJsoupIndexer("body > div:nth-child(10) > div.content_left > div.column-box > div:nth-child(5) > span.ctime", Constants.ATTRIBUTE_TEXT);
		filter = new SelectorFilter();
		filter.initMatchFilter(Constants.YYYYMMDDHHMM);
		selector.initFieldSelector("tstamp", "", indexer, filter, null);
		news.add(selector);
		template.setNews(news);

		RedisUtils.setTemplateResult(template, templateGuid);
		return template;
	}
}
