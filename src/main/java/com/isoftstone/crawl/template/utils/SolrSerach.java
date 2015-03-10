package com.isoftstone.crawl.template.utils;

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

	public long getQueryResultCount(String host) {
		if (host.isEmpty() || host == null) {
			return -1;
		}
		SolrQuery query = null;
		query = new SolrQuery();
		query.setQuery("host:" + host);
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

	public HashMap<String, Long> getQueryResultCount(List<String> hosts) {
		HashMap<String, Long> hashmap = new HashMap<String, Long>();
		if (hosts != null) {
			for (String host : hosts) {
				hashmap.put(host, getQueryResultCount(host));
			}
		}
		return hashmap;
	}

	public static void main(String[] args) {
		SolrSerach search = new SolrSerach();
		System.out.println(search.getQueryResultCount("www.bidnews.cn"));
	}
}
