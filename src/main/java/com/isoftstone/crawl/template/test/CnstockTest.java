package com.isoftstone.crawl.template.test;

import java.util.ArrayList;
import java.util.List;

import com.isoftstone.crawl.template.global.Constants;
import com.isoftstone.crawl.template.impl.ParseResult;
import com.isoftstone.crawl.template.impl.Selector;
import com.isoftstone.crawl.template.impl.SelectorFilter;
import com.isoftstone.crawl.template.impl.SelectorFormat;
import com.isoftstone.crawl.template.impl.SelectorIndexer;
import com.isoftstone.crawl.template.impl.TemplateResult;
import com.isoftstone.crawl.template.utils.MD5Utils;
import com.isoftstone.crawl.template.utils.RedisUtils;
import com.lj.util.http.DownloadHtml;

public class CnstockTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "http://irm.cnstock.com/ivlist/index/yqjj/0";
		String encoding = "utf-8";
		byte[] input = DownloadHtml.getHtml(url);
		TemplateResult templateResult = cnstockTemplate();
		ParseResult parseResult = null;
		//parseResult = TemplateFactory.localProcess(input, encoding,url, templateResult, Constants.TEMPLATE_LIST);
//	    parseResult = TemplateFactory.process(input, encoding,url);
//		System.out.println("templateResult:"+templateResult.toJSON());
//		System.out.println(parseResult.toJSON());
//		//System.out.println(TemplateFactory.getOutlink(parseResult).toString());
//		System.out.println(TemplateFactory.getPaginationOutlink(parseResult).toString());
//		
//		url = "http://irm.cnstock.com/company/scp_tzzgx/tgx_yqjj/201411/3250507.htm";
//		input = DownloadHtml.getHtml(url);
//		encoding = "gbk";
//		parseResult = TemplateFactory.process(input, encoding, url);
//		//parseResult = TemplateFactory.localProcess(input, encoding, url,templateResult, Constants.TEMPLATE_NEWS);
//		System.out.println(parseResult.toJSON());
	}
	public static TemplateResult cnstockTemplate() {
		TemplateResult template = new TemplateResult();
		template.setType(Constants.TEMPLATE_LIST);
		String templateUrl = "http://irm.cnstock.com/ivlist/index/yqjj/0";
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
		selector = new Selector();//body > div.container.video-wrap > div.main-wrap > div.publish-list.mt15 > ul > li:nth-child(1) > a
		indexer.initJsoupIndexer("body > div.container.video-wrap > div.main-wrap > div.publish-list.mt15 > ul > li > a", Constants.ATTRIBUTE_HREF);
		selector.initContentSelector(indexer, null);
		list.add(selector);
		template.setList(list);

		// pagitation outlink
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("div.pagination.pagination-centered ul li a:contains(末页)",
				Constants.ATTRIBUTE_HREF);
		filter = new SelectorFilter();
		filter.initMatchFilter("\\d+");
		selector.initPagitationSelector(Constants.PAGINATION_TYPE_PAGENUMBER,
				"0", "",
				"http://irm.cnstock.com/ivlist/index/yqjj/0", "1", null,
				indexer, filter, null);
		pagination.add(selector);
		template.setPagination(pagination);

		// title
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("body > div.container.video-wrap > div.main-wrap.common-detail-blank > div.main-title > div > h1", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("title", "", indexer, null, null);
		news.add(selector);

		// content
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("div#qmt_content_div", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("content", "", indexer, null, null);
		news.add(selector);

		// tstamp
		selector = new Selector();
		indexer = new SelectorIndexer();//body > div.container.video-wrap > div.main-wrap.common-detail-blank > div.main-title > div > div > span.time
		indexer.initJsoupIndexer("body > div.container.video-wrap > div.main-wrap.common-detail-blank > div.main-title > div > div > span.time", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("tstamp", "", indexer, null, null);
		news.add(selector);
		template.setNews(news);
		
		RedisUtils.setTemplateResult(template, templateGuid);
		return template;
	}
}
