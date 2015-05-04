package com.isoftstone.crawl.template.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.isoftstone.crawl.template.utils.ExcuteCmd;
import com.isoftstone.crawl.template.utils.HdfsUtils;

/**
 * Created by Administrator on 2015/4/22.
 */
public class MergeNutchData {
	private static final Log LOG = LogFactory.getLog(MergeNutchData.class);
	private final String MERGE_CRAWLDB = " mergedb ";
	private final String MERGE_LINKDB = " mergelinkdb ";
	private final String MERGE_SEGMENTS = " mergesegs ";
	private final String CRAWLDB = "crawldb";
	private final String LINKDB = "linkdb";
	private final String SEGMENTS = "segments";
	private final String RM = "rm -rf %s";

	private List<String> data_list = Arrays.asList("crawldb", "linkdb", "segments");
	private String nutch_root = "/nutch_run/local_incremental/bin/nutch";
	private String output_folder = " /home/nutch_final_data/";
	private String data_folder = "/nutch_data/";

	public MergeNutchData(String nutch_root, String output_folder, String data_folder) {
		this.nutch_root = nutch_root;
		this.output_folder = output_folder;
		this.data_folder = data_folder;
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Usage: MergeNutchData <nutch_root> <output_folder> <data_folder> <data_domain>");
			return;
		}
		String nutch_root = args[0];
		String output_folder = args[1];
		String data_folder = args[2];
		String data_domain= args[3];
		
		if (nutch_root != null && data_folder != null) {
			System.out.println("MergeNutchData nutch_root: " + nutch_root);
			System.out.println("MergeNutchData output_folder: " + output_folder);
			System.out.println("MergeNutchData data_folder: " + data_folder);
			System.out.println("MergeNutchData data_domain: " + data_domain);
		}
//		
//		 String nutch_root = "/nutch_run/local_incremental/bin/nutch";
//		 String output_folder = " /home/nutch_final_data/";
//		 String data_folder = "/nutch_data";
//		 String data_domain = "www.jcrb.com";
		MergeNutchData merge = new MergeNutchData(nutch_root, output_folder, data_folder);
		merge.mergeByDomain(data_domain);
		//merge.deployMergeByDomain(data_domain);
	}

	
	public void deployMergeByDomain(String domain) {
		if (domain!=null && domain.length()>0) {
			List<String> ls_folder;
			List<String> ls_data = new ArrayList<String>();//保存与domain匹配的data_folder
			try {
				ls_folder = HdfsUtils.listAll(data_folder);
				for (String data_name : data_list) {// 分别处理各data目录[crawldb、linkdb、segments]
					for (String folder : ls_folder) {
						//判断下面是否有要处理的文件
						//System.out.println(folder);
						String host = folder.substring(folder.lastIndexOf("/")+1, folder.indexOf("_increment"));
						if(host.equals(domain))
						{
							ls_data.add(folder);
							//System.out.println(folder);
							if (ls_data.size() == 5)// 防止一次合并过多,一次最多合并5个
							{
								switch (data_name) {
								case CRAWLDB:// 1、crawldb
									mergeCrawlDB(ls_data, domain);
									break;
								case LINKDB:// 2、linkdb
									mergeLinkDB(ls_data, domain);
									break;
								default:
									break;
								}
								ls_data = new ArrayList<String>();
							}
						}else
						{
							System.out.println("folder not equal "+domain);
						}
					}
					// 不足5个,有多少处理多少
					switch (data_name) {
					case CRAWLDB:// 1、crawldb
						mergeCrawlDB(ls_data, domain);
						ls_data = new ArrayList<String>();
						break;
					case LINKDB:// 2、linkdb
						mergeLinkDB(ls_data, domain);
						ls_data = new ArrayList<String>();
						break;
					default:
						break;
					}
				}
			} catch (IOException e) {
				LOG.info("deployMergeByDomain:"+e.getMessage());
			}
		} else {
			LOG.info("deployMergeByDomain domain not found!");
		}
	}
	
	
	public void mergeByDomain(String domain) {
		List<String> ls = new ArrayList<String>();
		try {
			File[] folders = new File(data_folder).listFiles();
			for (String data_name : data_list) {// 分别处理各data目录[crawldb、linkdb、segments]
				for (File folder : folders) {
					File f = null;
					String fname = folder.getName();
					if (fname.contains(domain)) {
						f = new File(data_folder + "/" + fname + "/" + data_name);
						if (f.exists()) {
							if (data_name.equals(SEGMENTS)) {
								mergeSegments(f.getPath(), domain);// segments比较特殊，需单个合并
							}
							ls.add(f.getPath());
						} else {
							// System.out.println(fname +
							// " directory not found crawldb!");
							LOG.info(fname + " directory not found crawldb or linkdb!");
						}
						if (ls.size() == 5)// 防止一次合并过多,一次最多合并5个
						{
							switch (data_name) {
							case CRAWLDB:// 1、crawldb
								mergeCrawlDB(ls, domain);
								break;
							case LINKDB:// 2、linkdb
								mergeLinkDB(ls, domain);
								break;
							default:
								break;
							}
							ls = new ArrayList<String>();
						}
					}
				}
				// 不足5个,有多少处理多少
				switch (data_name) {
				case CRAWLDB:// 1、crawldb
					mergeCrawlDB(ls, domain);
					ls = new ArrayList<String>();
					break;
				case LINKDB:// 2、linkdb
					mergeLinkDB(ls, domain);
					ls = new ArrayList<String>();
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			LOG.error("mergeByDomain:"+ e.getMessage());
		}
	}

	/**
	 * @Title: mergeCrawlDB
	 * @Description: TODO(合并crawldb)
	 * @param @param crawldb_list
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void mergeCrawlDB(List<String> crawldb_list, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null && crawldb_list != null) {
			String str = nutch_root + MERGE_CRAWLDB + output_folder + domain + "_data/" + CRAWLDB + " ";
			try {
				String merge_crawldb = str + "%s";
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < crawldb_list.size(); i++) {
					sb.append(crawldb_list.get(i));
					sb.append(" ");
				}
				String folderStr = sb.deleteCharAt(sb.length() - 1).toString();
				String cmd = String.format(merge_crawldb, folderStr);
				LOG.info("mergedb command start:"+cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge crawldb succeed
					LOG.info("merge crawldb succeed:"+cmd);
					cmd = String.format(RM, folderStr);
					if(ExcuteCmd.excuteCmd(cmd)==0)//rm crawldb
					{
						LOG.info("rm crawldb:"+cmd);
					}
				}
			} catch (Exception e) {
				LOG.error("merge crawldb:"+e.getMessage());
			}
		}
	}

	/**
	 * @Title: mergeLinkDB
	 * @Description: TODO(合并LinkDB)
	 * @param @param linkdb_list
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void mergeLinkDB(List<String> linkdb_list, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null && linkdb_list != null) {
			String str = nutch_root + MERGE_LINKDB + output_folder + domain + "_data/" + LINKDB + " ";
			try {
				StringBuilder sb = new StringBuilder();
				for (String linkdb_folder : linkdb_list) {
					sb.append(linkdb_folder);
					sb.append(" ");
				}
				String merge_linkdb = str + "%s";
				String folderStr = sb.deleteCharAt(sb.length() - 1).toString();
				String cmd =String.format(merge_linkdb, folderStr);
				LOG.info("mergelinkdb command start:"+cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {//merge linkdb succeed
					LOG.info("merge linkdb succeed:"+cmd);
					cmd = String.format(RM, folderStr);
					if(ExcuteCmd.excuteCmd(cmd) == 0)//rm linkdb
					{
						LOG.info("rm linkdb:"+cmd);
					}
				}
			} catch (Exception e) {
				LOG.error("merge linkdb:"+ e.getMessage());
			}
		}
	}

	/**
	 * @Title: mergeSegments
	 * @Description: TODO(合并单个segment)
	 * @param @param segments_folder 设定文件
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void mergeSegments(String segments_folder, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null && segments_folder != null) {
			String str = nutch_root + MERGE_SEGMENTS + output_folder + domain + "_data/"+SEGMENTS+" -dir ";
			try {
				String merge_segments = str + "%s";
				String cmd =String.format(merge_segments, segments_folder);
				LOG.info("mergesegs command start:"+cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge segment succeed
					LOG.info("merge segment succeed:"+cmd);
					cmd =String.format(RM, segments_folder);
					if(ExcuteCmd.excuteCmd(cmd)==0)//rm segments
					{
						LOG.info("rm segments:"+cmd);
					}
				}
			} catch (Exception e) {
				LOG.error("merge segment:"+ e.getMessage());
			}
		}
	}
}
