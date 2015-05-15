<<<<<<< HEAD
package com.isoftstone.crawl.template.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.isoftstone.crawl.template.utils.ExcuteCmd;

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
	private final String MV = "mv -f %s %s";
	private final String MKDIR = "mkdir %s";
	
	private final String SUFFIX_MERGED = "_merged";
	private final String SUFFIX_BACKUP = "_backup";
	
	private List<String> data_list = Arrays.asList("crawldb", "linkdb", "segments");
	private String nutch_root = "/nutch_run/local_incremental/bin/nutch";
	private String output_folder = "/home/nutch_final_data/";
	private String data_folder = "/nutch_data/";
	private String temp_folder = "/nutch_temp_data/";
	private String market_folder = "/nutch_data_market/";

	public MergeNutchData(String nutch_root, String output_folder, String data_folder) {
		this.nutch_root = nutch_root;
		this.output_folder = output_folder;
		this.data_folder = data_folder;
	}

	public static void main(String[] args) {
		int length = args.length;
		String nutch_root;
		String output_folder;
		String data_folder;
		String data_domain;
		if (length < 3) {// 参数不全,返回
			System.err.println("Usage: MergeNutchData <nutch_root> <output_folder> <data_folder> [data_domain]");
			System.err.println("nutch_root		nutch shell executable directory ");
			System.err.println("output_folder		merged output_folder");
			System.err.println("data_folder		nutch_data folder");
			System.err.println("data_domain		optional - if data_domain empty, default merge all domain");
			return;
		} else {
			nutch_root = args[0];
			output_folder = args[1];
			data_folder = args[2];
			MergeNutchData merge = new MergeNutchData(nutch_root, output_folder, data_folder);
			switch (length) {
			case 4:// 参数为4个时，直接处理
				data_domain = args[3];
				if (nutch_root != null && output_folder != null && data_folder != null && data_domain != null) {
					LOG.info("MergeNutchData nutch_root: " + nutch_root);
					LOG.info("MergeNutchData output_folder: " + output_folder);
					LOG.info("MergeNutchData data_folder: " + data_folder);
					LOG.info("MergeNutchData data_domain: " + data_domain);
				}
				LOG.info("~~~~~~~~~~~~ MergeNutchData " + data_domain + " start ~~~~~~~~~~~~~~~~~~~~");
				merge.mergeByDomain(data_domain);
				break;
			case 3:// 参数为3个时，处理指定data_folder下所有的domain
				LOG.info("~~~~~~~~~~~~ MergeNutchData all domain start ~~~~~~~~~~~~~~~~~~~~");
				for (String str : merge.getDomainList(data_folder)) {
					// System.out.println(str);
					merge.mergeByDomain(str);
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * @Title: getDomainList
	 * @Description: (获取所有不重复的domain)
	 * @param @param data_folder
	 * @param @return 设定文件
	 * @return List<String> 返回类型
	 * @author lj
	 * @throws
	 */
	public List<String> getDomainList(String data_folder) {
		List<String> ls = new ArrayList<String>();
		try {
			File[] folders = new File(data_folder).listFiles();
			if (folders != null && folders.length > 0) {
				for (File folder : folders) {
					String fname = folder.getName();
					String host = fname.substring(0, fname.indexOf("_"));// 提取host
					if (!ls.contains(host)) {
						ls.add(host);
						// System.out.println(host);
					}
				}
			}
		} catch (Exception e) {
			LOG.info("getDomainList:" + e.getMessage());
		}
		return ls;
	}

	public void mergeByDomain(String domain) {
		try {
			File[] folders = new File(data_folder).listFiles();
			for (String data_name : data_list) {// 分别处理各data目录[crawldb、linkdb、segments]
				for (File folder : folders) {
					try {
						File f = null;
						String fname = folder.getName();
						String host = fname.substring(0, fname.indexOf("_"));// 提取host
						if (host.equals(domain)) {
							f = new File(data_folder + "/" + fname + "/" + data_name);
							if (f.exists()) {
								String path = f.getPath();
								switch (data_name) {// data_name
								case CRAWLDB:// 1、crawldb
									//mergeCrawlDB(path, domain);
									break;
								case LINKDB:// 2、linkdb
									//mergeLinkDB(path, domain);
									break;
								case SEGMENTS:// 3、segments
									 mergeSegments(path, domain);
									 //merge(path, domain);
									break;
								default:
									break;
								}
							} else {
								// System.out.println(fname +
								// " directory not found crawldb!");
								LOG.info(fname + "/" + data_name + " directory not found!");
							}
						}
					} catch (Exception e) {
						LOG.error("mergeByDomain:" + e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			LOG.error("mergeByDomain:" + e.getMessage());
		}
	}

	/**
	 * @Title: mergeCrawlDB
	 * @Description: (合并crawldb)
	 * @param @param crawldb_list
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void mergeCrawlDB(List<String> crawldb_list, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null && crawldb_list != null) {
			String merge_crawldb = nutch_root + MERGE_CRAWLDB + output_folder + domain + "_data/" + CRAWLDB + " %s";
			try {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < crawldb_list.size(); i++) {
					sb.append(crawldb_list.get(i));
					sb.append(" ");
				}
				String folderStr = sb.deleteCharAt(sb.length() - 1).toString();
				String cmd = String.format(merge_crawldb, folderStr);
				LOG.info("mergedb command start:" + cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge crawldb succeed
					LOG.info("merge crawldb succeed:" + cmd);
					cmd = String.format(RM, folderStr);
					if (ExcuteCmd.excuteCmd(cmd) == 0)// rm crawldb
					{
						LOG.info("rm crawldb:" + cmd);
					}
				}
			} catch (Exception e) {
				LOG.error("merge crawldb:" + e.getMessage());
			}
		}
	}

	/**
	 * @Title: mergeCrawlDB
	 * @Description: (合并crawldb,single input CrawlDb is ok)
	 * @param @param crawldb
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void mergeCrawlDB(String crawldb, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null) {
			String merge_crawldb = nutch_root + MERGE_CRAWLDB + output_folder + domain + "_data/" + CRAWLDB + " %s";
			try {
				String cmd = String.format(merge_crawldb, crawldb);
				LOG.info("mergedb command start:" + cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge crawldb succeed
					LOG.info("merge crawldb succeed:" + cmd);
					cmd = String.format(RM, crawldb);
					// if (ExcuteCmd.excuteCmd(cmd) == 0)// rm crawldb
					// {
					// LOG.info("rm crawldb:" + cmd);
					// }
				}
			} catch (Exception e) {
				LOG.error("merge crawldb:" + e.getMessage());
			}
		}
	}

	/**
	 * @Title: mergeLinkDB
	 * @Description: (合并LinkDB)
	 * @param @param linkdb_list
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void mergeLinkDB(List<String> linkdb_list, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null && linkdb_list != null) {
			String merge_linkdb = nutch_root + MERGE_LINKDB + output_folder + domain + "_data/" + LINKDB + " %s";
			try {
				StringBuilder sb = new StringBuilder();
				for (String linkdb_folder : linkdb_list) {
					sb.append(linkdb_folder);
					sb.append(" ");
				}
				String folderStr = sb.deleteCharAt(sb.length() - 1).toString();
				String cmd = String.format(merge_linkdb, folderStr);
				LOG.info("mergelinkdb command start:" + cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge linkdb succeed
					LOG.info("merge linkdb succeed:" + cmd);
					cmd = String.format(RM, folderStr);
					if (ExcuteCmd.excuteCmd(cmd) == 0)// rm linkdb
					{
						LOG.info("rm linkdb:" + cmd);
					}
				}
			} catch (Exception e) {
				LOG.error("merge linkdb:" + e.getMessage());
			}
		}
	}

	/**
	 * @Title: mergeLinkDB
	 * @Description: (合并LinkDB)
	 * @param @param linkdb_list
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void mergeLinkDB(String linkdb, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null) {
			String merge_linkdb = nutch_root + MERGE_LINKDB + output_folder + domain + "_data/" + LINKDB + " %s";
			try {
				String cmd = String.format(merge_linkdb, linkdb);
				LOG.info("mergelinkdb command start:" + cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge linkdb succeed
					LOG.info("merge linkdb succeed:" + cmd);
					// cmd = String.format(RM, linkdb);
					// if (ExcuteCmd.excuteCmd(cmd) == 0)// rm linkdb
					// {
					// LOG.info("rm linkdb:" + cmd);
					// }
				}
			} catch (Exception e) {
				LOG.error("merge linkdb:" + e.getMessage());
			}
		}
	}

	/**
	 * @Title: mergeSegments
	 * @Description: (合并单个segment)
	 * @param @param segments_folder 设定文件
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void merge(String segments_folder, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null && segments_folder != null) {
			String merge_segments = nutch_root + MERGE_SEGMENTS + segments_folder + SUFFIX_MERGED + " %s/*";
			try {
				String cmd = String.format(merge_segments, segments_folder);
				// System.out.println(cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge segment succeed
					LOG.info("mergesegs command:" + cmd);
					cmd = String.format(RM, segments_folder + SUFFIX_BACKUP);
					// System.out.println(cmd);
					if (ExcuteCmd.excuteCmd(cmd) == 0)// 删除备份
					{
						LOG.info(cmd);
						cmd = String.format(MV, segments_folder, segments_folder + SUFFIX_BACKUP);
						// System.out.println(cmd);
						if (ExcuteCmd.excuteCmd(cmd) == 0)// 重命名
						{
							LOG.info(cmd);
							cmd = String.format(MKDIR, segments_folder);
							// System.out.println(cmd);
							if (ExcuteCmd.excuteCmd(cmd) == 0)// 创建segments
							{
								LOG.info(cmd);
								cmd = String.format(MV, segments_folder + SUFFIX_MERGED, segments_folder);
								// System.out.println(cmd);
								if (ExcuteCmd.excuteCmd(cmd) == 0)// meger_segments重命为segments
								{
									LOG.info(cmd);
									cmd = String.format(RM, segments_folder + SUFFIX_MERGED);// 删除合并结果
									// System.out.println(cmd);
									if (ExcuteCmd.excuteCmd(cmd) == 0) {
										LOG.info(cmd);
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				LOG.error("merge segment:" + e.getMessage());
			}
		}
	}

	public void mergeSegments(String segments_folder, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null && segments_folder != null) {
			String out_folder = temp_folder + domain + "_data_" + System.currentTimeMillis();
			String merge_segments = nutch_root + MERGE_SEGMENTS + out_folder + " %s/*";
			try {
				String cmd = String.format(merge_segments, segments_folder);
				LOG.info("merge shell:"+cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {
					LOG.info("merge segment succeed");
					String domain_folder = market_folder + domain + "_data/";
					File f = new File(domain_folder);
					if (!f.exists())// 创建domain folder
					{
						f.mkdir();
						LOG.info("mkdir domain folder: " + f.getPath());
					}
					// 将合并后的目录归档
					cmd = String.format(MV, out_folder + "/*", f.getPath());
					if (ExcuteCmd.excuteCmd(cmd) == 0) {
						LOG.info("mv temp folder:" + cmd);
						cmd = String.format(RM, out_folder);
						if (ExcuteCmd.excuteCmd(cmd) == 0) {
							LOG.info("rm temp folder:" + cmd);
							normalizingSegments(domain);
						}
					}
				}
			} catch (Exception e) {
				LOG.error("merge segment:" + e.getMessage());
			}
		}
	}

	public void normalizingSegments(String domain) {
		String fina_data = data_folder + domain + "/segments/";
		String temp_data = market_folder + domain + "_data/";
		String cmd = "";
		try {
			File[] folders = new File(fina_data).listFiles();
			if (folders != null && folders.length > 0) {
				for (File file : folders) {
					String path = file.getPath();
					cmd = String.format(MV, path, temp_data);
					if (ExcuteCmd.excuteCmd(cmd) == 0) {
						LOG.info("mv data:" + cmd);
					}
				}
			}
			cmd = nutch_root + MERGE_SEGMENTS + fina_data + " " + temp_data + "*";
			if (ExcuteCmd.excuteCmd(cmd) == 0) {
				LOG.info("normalizingSegments:" + cmd);
			}
		} catch (Exception e) {
			LOG.error("normalizingSegments:" + e.getMessage());
		}
	}

	// 将同一域名下所有的segments合并成一个
	// public void mergeSegments(String segments_folder, String domain) {
	// if (nutch_root.length() > 0 && nutch_root != null && segments_folder !=
	// null) {
	// String merge_segments = nutch_root + MERGE_SEGMENTS + temp_folder +
	// domain + "_data_"+System.currentTimeMillis()+"/" + " %s/*";
	// try {
	// String cmd = String.format(merge_segments, segments_folder);
	// LOG.info("mergesegs command start:" + cmd);
	// if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge segment succeed
	// LOG.info("merge segment succeed:" + cmd);
	// }
	// } catch (Exception e) {
	// LOG.error("merge segment:" + e.getMessage());
	// }
	// }
	// }

}
=======
package com.isoftstone.crawl.template.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.isoftstone.crawl.template.utils.ExcuteCmd;

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
	private final String MV = "mv -f %s %s";
	private final String MKDIR = "mkdir %s";
	
	private final String SUFFIX_MERGED = "_merged";
	private final String SUFFIX_BACKUP = "_backup";
	
	private List<String> data_list = Arrays.asList("crawldb", "linkdb", "segments");
	private String nutch_root = "/nutch_run/local_incremental/bin/nutch";
	private String output_folder = "/home/nutch_final_data/";
	private String data_folder = "/nutch_data/";
	private String temp_folder = "/nutch_temp_data/";
	private String market_folder = "/nutch_data_market/";

	public MergeNutchData(String nutch_root, String output_folder, String data_folder) {
		this.nutch_root = nutch_root;
		this.output_folder = output_folder;
		this.data_folder = data_folder;
	}

	public static void main(String[] args) {
		int length = args.length;
		String nutch_root;
		String output_folder;
		String data_folder;
		String data_domain;
		if (length < 3) {// 参数不全,返回
			System.err.println("Usage: MergeNutchData <nutch_root> <output_folder> <data_folder> [data_domain]");
			System.err.println("nutch_root		nutch shell executable directory ");
			System.err.println("output_folder		merged output_folder");
			System.err.println("data_folder		nutch_data folder");
			System.err.println("data_domain		optional - if data_domain empty, default merge all domain");
			return;
		} else {
			nutch_root = args[0];
			output_folder = args[1];
			data_folder = args[2];
			MergeNutchData merge = new MergeNutchData(nutch_root, output_folder, data_folder);
			switch (length) {
			case 4:// 参数为4个时，直接处理
				data_domain = args[3];
				if (nutch_root != null && output_folder != null && data_folder != null && data_domain != null) {
					LOG.info("MergeNutchData nutch_root: " + nutch_root);
					LOG.info("MergeNutchData output_folder: " + output_folder);
					LOG.info("MergeNutchData data_folder: " + data_folder);
					LOG.info("MergeNutchData data_domain: " + data_domain);
				}
				LOG.info("~~~~~~~~~~~~ MergeNutchData " + data_domain + " start ~~~~~~~~~~~~~~~~~~~~");
				merge.mergeByDomain(data_domain);
				break;
			case 3:// 参数为3个时，处理指定data_folder下所有的domain
				LOG.info("~~~~~~~~~~~~ MergeNutchData all domain start ~~~~~~~~~~~~~~~~~~~~");
				for (String str : merge.getDomainList(data_folder)) {
					// System.out.println(str);
					merge.mergeByDomain(str);
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * @Title: getDomainList
	 * @Description: (获取所有不重复的domain)
	 * @param @param data_folder
	 * @param @return 设定文件
	 * @return List<String> 返回类型
	 * @author lj
	 * @throws
	 */
	public List<String> getDomainList(String data_folder) {
		List<String> ls = new ArrayList<String>();
		try {
			File[] folders = new File(data_folder).listFiles();
			if (folders != null && folders.length > 0) {
				for (File folder : folders) {
					String fname = folder.getName();
					String host = fname.substring(0, fname.indexOf("_"));// 提取host
					if (!ls.contains(host)) {
						ls.add(host);
						// System.out.println(host);
					}
				}
			}
		} catch (Exception e) {
			LOG.info("getDomainList:" + e.getMessage());
		}
		return ls;
	}

	public void mergeByDomain(String domain) {
		try {
			File[] folders = new File(data_folder).listFiles();
			for (String data_name : data_list) {// 分别处理各data目录[crawldb、linkdb、segments]
				for (File folder : folders) {
					try {
						File f = null;
						String fname = folder.getName();
						String host = fname.substring(0, fname.indexOf("_"));// 提取host
						if (host.equals(domain)) {
							f = new File(data_folder + "/" + fname + "/" + data_name);
							if (f.exists()) {
								String path = f.getPath();
								switch (data_name) {// data_name
								case CRAWLDB:// 1、crawldb
									//mergeCrawlDB(path, domain);
									break;
								case LINKDB:// 2、linkdb
									//mergeLinkDB(path, domain);
									break;
								case SEGMENTS:// 3、segments
									 mergeSegments(path, domain);
									 //merge(path, domain);
									break;
								default:
									break;
								}
							} else {
								// System.out.println(fname +
								// " directory not found crawldb!");
								LOG.info(fname + "/" + data_name + " directory not found!");
							}
						}
					} catch (Exception e) {
						LOG.error("mergeByDomain:" + e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			LOG.error("mergeByDomain:" + e.getMessage());
		}
	}

	/**
	 * @Title: mergeCrawlDB
	 * @Description: (合并crawldb)
	 * @param @param crawldb_list
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void mergeCrawlDB(List<String> crawldb_list, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null && crawldb_list != null) {
			String merge_crawldb = nutch_root + MERGE_CRAWLDB + output_folder + domain + "_data/" + CRAWLDB + " %s";
			try {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < crawldb_list.size(); i++) {
					sb.append(crawldb_list.get(i));
					sb.append(" ");
				}
				String folderStr = sb.deleteCharAt(sb.length() - 1).toString();
				String cmd = String.format(merge_crawldb, folderStr);
				LOG.info("mergedb command start:" + cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge crawldb succeed
					LOG.info("merge crawldb succeed:" + cmd);
					cmd = String.format(RM, folderStr);
					if (ExcuteCmd.excuteCmd(cmd) == 0)// rm crawldb
					{
						LOG.info("rm crawldb:" + cmd);
					}
				}
			} catch (Exception e) {
				LOG.error("merge crawldb:" + e.getMessage());
			}
		}
	}

	/**
	 * @Title: mergeCrawlDB
	 * @Description: (合并crawldb,single input CrawlDb is ok)
	 * @param @param crawldb
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void mergeCrawlDB(String crawldb, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null) {
			String merge_crawldb = nutch_root + MERGE_CRAWLDB + output_folder + domain + "_data/" + CRAWLDB + " %s";
			try {
				String cmd = String.format(merge_crawldb, crawldb);
				LOG.info("mergedb command start:" + cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge crawldb succeed
					LOG.info("merge crawldb succeed:" + cmd);
					cmd = String.format(RM, crawldb);
					// if (ExcuteCmd.excuteCmd(cmd) == 0)// rm crawldb
					// {
					// LOG.info("rm crawldb:" + cmd);
					// }
				}
			} catch (Exception e) {
				LOG.error("merge crawldb:" + e.getMessage());
			}
		}
	}

	/**
	 * @Title: mergeLinkDB
	 * @Description: (合并LinkDB)
	 * @param @param linkdb_list
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void mergeLinkDB(List<String> linkdb_list, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null && linkdb_list != null) {
			String merge_linkdb = nutch_root + MERGE_LINKDB + output_folder + domain + "_data/" + LINKDB + " %s";
			try {
				StringBuilder sb = new StringBuilder();
				for (String linkdb_folder : linkdb_list) {
					sb.append(linkdb_folder);
					sb.append(" ");
				}
				String folderStr = sb.deleteCharAt(sb.length() - 1).toString();
				String cmd = String.format(merge_linkdb, folderStr);
				LOG.info("mergelinkdb command start:" + cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge linkdb succeed
					LOG.info("merge linkdb succeed:" + cmd);
					cmd = String.format(RM, folderStr);
					if (ExcuteCmd.excuteCmd(cmd) == 0)// rm linkdb
					{
						LOG.info("rm linkdb:" + cmd);
					}
				}
			} catch (Exception e) {
				LOG.error("merge linkdb:" + e.getMessage());
			}
		}
	}

	/**
	 * @Title: mergeLinkDB
	 * @Description: (合并LinkDB)
	 * @param @param linkdb_list
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void mergeLinkDB(String linkdb, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null) {
			String merge_linkdb = nutch_root + MERGE_LINKDB + output_folder + domain + "_data/" + LINKDB + " %s";
			try {
				String cmd = String.format(merge_linkdb, linkdb);
				LOG.info("mergelinkdb command start:" + cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge linkdb succeed
					LOG.info("merge linkdb succeed:" + cmd);
					// cmd = String.format(RM, linkdb);
					// if (ExcuteCmd.excuteCmd(cmd) == 0)// rm linkdb
					// {
					// LOG.info("rm linkdb:" + cmd);
					// }
				}
			} catch (Exception e) {
				LOG.error("merge linkdb:" + e.getMessage());
			}
		}
	}

	/**
	 * @Title: mergeSegments
	 * @Description: (合并单个segment)
	 * @param @param segments_folder 设定文件
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void merge(String segments_folder, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null && segments_folder != null) {
			String merge_segments = nutch_root + MERGE_SEGMENTS + segments_folder + SUFFIX_MERGED + " %s/*";
			try {
				String cmd = String.format(merge_segments, segments_folder);
				// System.out.println(cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge segment succeed
					LOG.info("mergesegs command:" + cmd);
					cmd = String.format(RM, segments_folder + SUFFIX_BACKUP);
					// System.out.println(cmd);
					if (ExcuteCmd.excuteCmd(cmd) == 0)// 删除备份
					{
						LOG.info(cmd);
						cmd = String.format(MV, segments_folder, segments_folder + SUFFIX_BACKUP);
						// System.out.println(cmd);
						if (ExcuteCmd.excuteCmd(cmd) == 0)// 重命名
						{
							LOG.info(cmd);
							cmd = String.format(MKDIR, segments_folder);
							// System.out.println(cmd);
							if (ExcuteCmd.excuteCmd(cmd) == 0)// 创建segments
							{
								LOG.info(cmd);
								cmd = String.format(MV, segments_folder + SUFFIX_MERGED, segments_folder);
								// System.out.println(cmd);
								if (ExcuteCmd.excuteCmd(cmd) == 0)// meger_segments重命为segments
								{
									LOG.info(cmd);
									cmd = String.format(RM, segments_folder + SUFFIX_MERGED);// 删除合并结果
									// System.out.println(cmd);
									if (ExcuteCmd.excuteCmd(cmd) == 0) {
										LOG.info(cmd);
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				LOG.error("merge segment:" + e.getMessage());
			}
		}
	}

	public void mergeSegments(String segments_folder, String domain) {
		if (nutch_root.length() > 0 && nutch_root != null && segments_folder != null) {
			String out_folder = temp_folder + domain + "_data_" + System.currentTimeMillis();
			String merge_segments = nutch_root + MERGE_SEGMENTS + out_folder + " %s/*";
			try {
				String cmd = String.format(merge_segments, segments_folder);
				LOG.info("merge shell:"+cmd);
				if (ExcuteCmd.excuteCmd(cmd) == 0) {
					LOG.info("merge segment succeed");
					String domain_folder = market_folder + domain + "_data/";
					File f = new File(domain_folder);
					if (!f.exists())// 创建domain folder
					{
						f.mkdir();
						LOG.info("mkdir domain folder: " + f.getPath());
					}
					// 将合并后的目录归档
					cmd = String.format(MV, out_folder + "/*", f.getPath());
					if (ExcuteCmd.excuteCmd(cmd) == 0) {
						LOG.info("mv temp folder:" + cmd);
						cmd = String.format(RM, out_folder);
						if (ExcuteCmd.excuteCmd(cmd) == 0) {
							LOG.info("rm temp folder:" + cmd);
							normalizingSegments(domain);
						}
					}
				}
			} catch (Exception e) {
				LOG.error("merge segment:" + e.getMessage());
			}
		}
	}

	public void normalizingSegments(String domain) {
		String fina_data = data_folder + domain + "/segments/";
		String temp_data = market_folder + domain + "_data/";
		String cmd = "";
		try {
			File[] folders = new File(fina_data).listFiles();
			if (folders != null && folders.length > 0) {
				for (File file : folders) {
					String path = file.getPath();
					cmd = String.format(MV, path, temp_data);
					if (ExcuteCmd.excuteCmd(cmd) == 0) {
						LOG.info("mv data:" + cmd);
					}
				}
			}
			cmd = nutch_root + MERGE_SEGMENTS + fina_data + " " + temp_data + "*";
			if (ExcuteCmd.excuteCmd(cmd) == 0) {
				LOG.info("normalizingSegments:" + cmd);
			}
		} catch (Exception e) {
			LOG.error("normalizingSegments:" + e.getMessage());
		}
	}

	// 将同一域名下所有的segments合并成一个
	// public void mergeSegments(String segments_folder, String domain) {
	// if (nutch_root.length() > 0 && nutch_root != null && segments_folder !=
	// null) {
	// String merge_segments = nutch_root + MERGE_SEGMENTS + temp_folder +
	// domain + "_data_"+System.currentTimeMillis()+"/" + " %s/*";
	// try {
	// String cmd = String.format(merge_segments, segments_folder);
	// LOG.info("mergesegs command start:" + cmd);
	// if (ExcuteCmd.excuteCmd(cmd) == 0) {// merge segment succeed
	// LOG.info("merge segment succeed:" + cmd);
	// }
	// } catch (Exception e) {
	// LOG.error("merge segment:" + e.getMessage());
	// }
	// }
	// }

}
>>>>>>> 3f0f16c7e97c1276b53d01ffd369a74c00ddaa37
