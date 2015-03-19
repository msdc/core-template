package com.isoftstone.crawl.template.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 * 
 * @ClassName: SolrSerach
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lj
 * @date 2014年8月19日 上午11:38:22
 * 
 */
public class SolrSerach {
	private CommonsHttpSolrServer solr = SolrServer.getInstance().getSolrServer();
	private String format = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public long getQueryResultCount(String field, String value) {
		if (field.isEmpty() || field == null || value.isEmpty() || value == null) {
			return -1;
		}
		SolrQuery query = null;
		query = new SolrQuery();
		query.setQuery(field + ":" + value);
		query.setRows(10);
		QueryResponse rsp = null;
		try {
			rsp = solr.query(query);
			if (rsp != null) {
				return rsp.getResults().getNumFound();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return -1;
	}

	public long getQueryResultCount(String field, String value, String filter, Date start, Date end) {
		if (field.isEmpty() || field == null || value.isEmpty() || value == null) {
			return -1;
		}
		SimpleDateFormat df = new SimpleDateFormat(format);
		SolrQuery query = null;
		query = new SolrQuery();
		query.setQuery(field + ":" + value);
		query.addFilterQuery(filter = ":[" + df.format(DateFormatUtils.nHourBefore(start, 8)) + " TO " + df.format(DateFormatUtils.nHourBefore(end, 8)) + "]");
		query.setRows(10);
		QueryResponse rsp = null;
		try {
			rsp = solr.query(query);
			if (rsp != null) {
				return rsp.getResults().getNumFound();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return -1;
	}

	public HashMap<String, Long> getQueryResultCount(List<String> hosts, String filter, Date start, Date end) {
		HashMap<String, Long> hashmap = new HashMap<String, Long>();
		if (hosts != null) {
			for (String host : hosts) {
				hashmap.put(host, getQueryResultCount("host", host, filter, start, end));
			}
		}
		return hashmap;
	}

	public static void main(String[] args) {
		// SolrSerach search = new SolrSerach();

		// System.out.println(df.format(DateFormatUtils.nHourBefore(new Date(),
		// 8)));

		// System.out.println(search.getQueryResultCount("host","www.bidnews.cn"));
	}
}
