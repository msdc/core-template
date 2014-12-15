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

public class CFCPNTest {
	public static void main(String[] args) {
		String url = "http://www.cfcpn.com/front/notice/cggg_list.jsp";
		String encoding = "gb2312";
		byte[] input = DownloadHtml.getHtml(url);
		TemplateResult templateResult = CFCPNTemplate();
		ParseResult parseResult = null;
		
		parseResult = TemplateFactory.process(input, encoding, url);
		System.out.println("templateResult:" + templateResult.toJSON());
		System.out.println(parseResult.toJSON());

//		url = "http://www.cfcpn.com/front/notice/show_news_detail.jsp?rec_id=240707&hyid=0";
//		input = DownloadHtml.getHtml(url);
//		encoding = "gb2312";
//		parseResult = TemplateFactory.process(input, encoding, url);
//		// parseResult = TemplateFactory.localProcess(input, encoding,
//		// url,templateResult, Constants.TEMPLATE_NEWS);
//		System.out.println(parseResult.toJSON());

	}
	
	public static TemplateResult CFCPNTemplate() {
		TemplateResult template = new TemplateResult();
		template.setType(Constants.TEMPLATE_LIST);
		String templateUrl = "http://www.cfcpn.com/front/notice/cggg_list.jsp";
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
		indexer.initJsoupIndexer("#list_table > tbody:nth-child(2)>tr> td:nth-child(2)>a", Constants.ATTRIBUTE_HREF);
		selector.initContentSelector(indexer, null);

		// tstamp
		Selector label = new Selector();
		label.setType(Constants.SELECTOR_LABEL);
		indexer = new SelectorIndexer();
		indexer.initJsoupIndexer("#list_table > tbody:nth-child(2)>tr> td:nth-child(3)", Constants.ATTRIBUTE_TEXT);
		filter = new SelectorFilter();
		filter.initMatchFilter(Constants.YYYYMMDD);
		label.initLabelSelector("tstamp", "", indexer, filter, null);
		selector.setLabel(label);
		list.add(selector);
		template.setList(list);

		// pagitation outlink js翻页无法处理
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer(".pager", Constants.ATTRIBUTE_TEXT);		
		filter = new SelectorFilter();
		filter.initMatchFilter("共(\\d+)页");
		//这里的页面抓取的 页码数加减有问题
		selector.initPagitationSelector(Constants.PAGINATION_TYPE_PAGENUMBER, "##", "20", "http://www.cfcpn.com/front/notice/cggg_list.jsp?offset=##", "0", null, indexer, filter, null);
		pagination.add(selector);
		template.setPagination(pagination);

		// title
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("div.aticle_tit:nth-child(1)", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("title", "", indexer, null, null);
		news.add(selector);

		// content
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("#content", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("content", "", indexer, null, null);
		news.add(selector);
		template.setNews(news);

		RedisUtils.setTemplateResult(template, templateGuid);
		return template;

	}

}
