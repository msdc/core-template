/**   
* @Title: SolrServer.java 
* @Package com.isoftstone.solr2db.server 
* @Description: TODO(用一句话描述该文件做什么) 
* @author lj
* @date 2014年8月18日 上午11:49:15 
* @version V1.0   
*/
package com.isoftstone.crawl.template.utils;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;



/** 
 * @ClassName: SolrServer 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author lj
 * @date 2014年8月18日 上午11:49:15 
 *  
 */

public class SolrServer {
	static private SolrServer instance;// 唯一实例
	CommonsHttpSolrServer solr = null;

	private static PropertiesUtils propert = PropertiesUtils.getInstance();
	private static String solr_server_url=propert.getValue("solr_server_url");
	/**
	 * 返回唯一实例.如果是第一次调用此方法,则创建实例
	 * @return SolrServer 唯一实例
	 */
	static synchronized public SolrServer getInstance() {
		if (instance == null) {
			instance = new SolrServer();
		}
		return instance;
	}
	
	public CommonsHttpSolrServer getSolrServer() {
		try {
			 solr = new CommonsHttpSolrServer(solr_server_url);
			 solr.setConnectionTimeout(100);
             solr.setDefaultMaxConnectionsPerHost(100);
             solr.setMaxTotalConnections(100);
             return solr;
      } catch (Exception e) {
             System.out.println("check tomcat server port !");
             e.printStackTrace();
      }
		return null;
	}
	 
}
