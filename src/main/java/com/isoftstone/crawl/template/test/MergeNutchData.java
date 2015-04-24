package com.isoftstone.crawl.template.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoftstone.crawl.template.utils.ExcuteCmd;

/**
 * Created by Administrator on 2015/4/22.
 */
public class MergeNutchData {
	private static final Logger LOG = LoggerFactory.getLogger(MergeNutchData.class);
	private final String MERGE_CRAWLDB = " mergedb ";
	private final String MERGE_LINKDB = " mergelinkdb ";
	private final String MERGE_SEGMENTS = " mergesegs -dir ";
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
		
		MergeNutchData merge = new MergeNutchData(nutch_root, output_folder, data_folder);
		merge.mergeByDomain(data_domain);
	}

	public void mergeByDomain(String domain) {
		List<String> ls = new ArrayList<String>();
		try {
			File[] folders = new File(data_folder).listFiles();
			for (String data_name : data_list) {// 分别处理各data目录
				for (File folder : folders) {
					File f = null;
					String fname = folder.getName();
					if (fname.contains(domain)) {
						f = new File(data_folder + "\\" + fname + "\\" + data_name);
						if (f.exists()) {
							if (data_name.equals("segments")) {
								mergeSegments(f.getPath(), domain);// segments比较特殊，需单个合并
							}
							ls.add(f.getPath());
						} else {
							// System.out.println(fname +
							// " directory not found crawldb!");
							LOG.info(fname + " directory not found crawldb!");
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
			LOG.info("mergeByDomain:", e.getMessage());
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
			String cmd = nutch_root + MERGE_CRAWLDB + output_folder + domain + "_data/" + CRAWLDB + " ";
			try {
				String merge_crawldb = cmd + "%s";
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < crawldb_list.size(); i++) {
					sb.append(crawldb_list.get(i));
					sb.append(" ");
				}
				String folderStr = sb.deleteCharAt(sb.length() - 1).toString();
				System.out.println(String.format(merge_crawldb, folderStr));
				int code = ExcuteCmd.excuteCmd(String.format(merge_crawldb, folderStr));
				if (code == 0) {// 执行成功,则删除目录
					// System.out.println(String.format(RM, folderStr));
					ExcuteCmd.excuteCmd(String.format(RM, folderStr));
				}
			} catch (Exception e) {
				LOG.info("merge crawldb:", e.getMessage());
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
			String cmd = nutch_root + MERGE_LINKDB + output_folder + domain + "_data/" + LINKDB + " ";
			try {
				StringBuilder sb = new StringBuilder();
				for (String linkdb_folder : linkdb_list) {
					sb.append(linkdb_folder);
					sb.append(" ");
				}
				String merge_linkdb = cmd + "%s";
				String folderStr = sb.deleteCharAt(sb.length() - 1).toString();
				// System.out.println(String.format(merge_linkdb, folderStr));
				int code = ExcuteCmd.excuteCmd(String.format(merge_linkdb, folderStr));
				if (code == 0) {// 执行成功,则删除目录
					// System.out.println(String.format(RM, folderStr));
					ExcuteCmd.excuteCmd(String.format(RM, folderStr));
				}
			} catch (Exception e) {
				LOG.info("merge linkdb:", e.getMessage());
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
			String cmd = nutch_root + MERGE_SEGMENTS + output_folder + domain + "_data/" + SEGMENTS + " ";
			try {
				String merge_segments = cmd + "%s";
				// System.out.println(String.format(merge_segments,
				// segments_folder));
				int code = ExcuteCmd.excuteCmd(String.format(merge_segments, segments_folder));
				if (code == 0) {// 执行成功,则删除目录
					// System.out.println(String.format(RM, segments_folder));
					ExcuteCmd.excuteCmd(String.format(RM, segments_folder));
				}
			} catch (Exception e) {
				LOG.info("merge segment:", e.getMessage());
			}
		}
	}
}
