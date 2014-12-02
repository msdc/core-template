package com.isoftstone.crawl.template;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.isoftstone.crawl.template.global.Constants;
import com.isoftstone.crawl.template.utils.MD5Utils;
import com.isoftstone.crawl.template.utils.RedisUtils;
import com.isoftstone.crawl.template.vo.ParseResult;
import com.isoftstone.crawl.template.vo.Selector;
import com.isoftstone.crawl.template.vo.SelectorFilter;
import com.isoftstone.crawl.template.vo.SelectorFormat;
import com.isoftstone.crawl.template.vo.SelectorIndexer;
import com.isoftstone.crawl.template.vo.TemplateResult;
import com.lj.util.http.DownloadHtml;

public class GuoyuanTest {
	public static void main(String[] args) {
		String url = "http://www.zhongguoxintuo.com/xtxw/index.html";
		String encoding = "gb2312";
		byte[] input =DownloadHtml.getHtml(url);
		TemplateResult templateResult = GuanyuanTemplate();
//		System.out.println(templateResult.toJSON());
////		ParseResult parseResult = TemplateFactory.localProcess(input, encoding,
////				url, templateResult, Constants.TEMPLATE_LIST);
//		ParseResult parseResult = TemplateFactory.process(input, encoding,
//				url);
//		System.out.println(TemplateFactory.getPaginationOutlink(parseResult));
//		//System.out.println(TemplateFactory.getOutlink(parseResult).toString());

//		url = "http://www.zhongguoxintuo.com/xtxw/5815.html";
//		input =DownloadHtml.getHtml(url);
//		parseResult = TemplateFactory.localProcess(input, encoding, url,
//				templateResult, Constants.TEMPLATE_NEWS);
//		System.out.println(parseResult.toJSON());
	}

	public static TemplateResult GuanyuanTemplate() {
		TemplateResult template = new TemplateResult();
		template.setType(Constants.TEMPLATE_LIST);
		String templateUrl = "http://www.zhongguoxintuo.com/xtxw/index.html";
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
		selector = new Selector();//body > div.top > div.nav > dl:nth-child(4) > dt:nth-child(2) > a
		indexer.initJsoupIndexer("body > div.main > div.main_left > div > div > dl > dt > a", Constants.ATTRIBUTE_HREF);
		selector.initContentSelector(indexer, null);
		list.add(selector);
		template.setList(list);

		// pagitation outlink
		indexer = new SelectorIndexer();
		selector = new Selector();
		filter = new SelectorFilter();
		indexer.initJsoupIndexer("div.page a:nth-child(1) b:nth-child(1)",
				Constants.ATTRIBUTE_TEXT);
		selector.initPagitationSelector(Constants.PAGINATION_TYPE_PAGERECORD,
				"index", "index_",
				"http://www.zhongguoxintuo.com/xtxw/index.html", "2", "23",
				indexer, null, null);
		pagination.add(selector);
		template.setPagination(pagination);

		// title
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("div.title", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("title", "", indexer, null, null);
		news.add(selector);

		// content
		indexer = new SelectorIndexer();
		selector = new Selector();
		indexer.initJsoupIndexer("div.cont_c", Constants.ATTRIBUTE_TEXT);
		selector.initFieldSelector("content", "", indexer, null, null);
		news.add(selector);

		// tstamp
		selector = new Selector();
		indexer = new SelectorIndexer();
		indexer.initJsoupIndexer("div.ly", Constants.ATTRIBUTE_TEXT);
		filter = new SelectorFilter();
		filter.initMatchFilter("\\d{4}-\\d{2}-\\d{2}");
		format = new SelectorFormat();
		format.initDateFormat("yyyy-MM-dd");
		selector.initFieldSelector("tstamp", "", indexer, filter, format);
		news.add(selector);
		template.setNews(news);
		RedisUtils.setTemplateResult(template, templateGuid);
		return template;
	}

	public static byte[] readTextFile(String filePath, String encoding) {
		java.io.InputStreamReader inputReader = null;
		java.io.BufferedReader reader = null;
		File f = new File(filePath);
		try {
			inputReader = new java.io.InputStreamReader(
					new java.io.FileInputStream(f), encoding);
			reader = new java.io.BufferedReader(inputReader);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String s = null;
		try {
			s = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuffer sb = new StringBuffer();
		while (s != null) {
			sb.append(s);
			sb.append("\r\n");
			try {
				s = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		byte[] page = sb.toString().getBytes();
		// System.out.println(sb.toString());
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return page;
	}
}
