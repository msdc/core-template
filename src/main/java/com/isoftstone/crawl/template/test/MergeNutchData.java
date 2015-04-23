package com.isoftstone.crawl.template.test;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoftstone.crawl.template.utils.ExcuteCmd;

/**
 * Created by Administrator on 2015/4/22.
 */
public class MergeNutchData {
	private final String MERGE_CRAWLDB = " mergedb ";
	private final String MERGE_LINKDB = " mergelinkdb ";
	private final String MERGE_SEGMENTS = " mergesegs -dir ";
	private String nutch_root = "/nutch_run/local_incremental/bin/nutch";
	private String output_folder = " /home/nutch_final_data/";
	private static final Logger LOG = LoggerFactory.getLogger(MergeNutchData.class);

	public MergeNutchData(String nutch_root,String output_folder) {
		this.nutch_root = nutch_root;
		this.output_folder = output_folder;
	}

	public static void main(String[] args) {
		MergeNutchData merge = new MergeNutchData("/nutch_run/local_incremental/bin/nutch","/home/nutch_final_data/");
		List<String> lstOne = new ArrayList<String>();
		lstOne.add("123");
		lstOne.add("456");
		lstOne.add("789");
		lstOne.add("321");
		lstOne.add("121");
		lstOne.add("124");
		lstOne.add("120");
		lstOne.add("141");
		lstOne.add("198");
		lstOne.add("548");
		lstOne.add("654");

		merge.mergeCrawlDB(lstOne);
		merge.mergeLinkDB(lstOne);
		merge.mergeSegments(lstOne);
	}

	/**
	 * @Title: mergeCrawlDB
	 * @Description: TODO(合并crawldb)
	 * @param @param crawldb_list
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void mergeCrawlDB(List<String> crawldb_list) {
		if (nutch_root.length() > 0 && nutch_root != null && crawldb_list != null) {
			String cmd = nutch_root + MERGE_CRAWLDB +output_folder+"crawldb ";
			try {
				String merge_crawldb = cmd + "%s";
				StringBuilder sb = new StringBuilder();
				for (int i = 0;i< crawldb_list.size();i++) {
					sb.append(crawldb_list.get(i));
					sb.append(" ");
				}
				String folderStr = sb.deleteCharAt(sb.length() - 1).toString();
				//System.out.println(String.format(merge_crawldb, folderStr));
				ExcuteCmd.excuteCmd(String.format(merge_crawldb, folderStr));
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
	public void mergeLinkDB(List<String> linkdb_list) {
		if (nutch_root.length() > 0 && nutch_root != null && linkdb_list != null) {
			String cmd = nutch_root + MERGE_LINKDB+output_folder+"linkdb ";
			try {
				StringBuilder sb = new StringBuilder();
				for (String linkdb_folder : linkdb_list) {
					sb.append(linkdb_folder);
					sb.append(" ");
				}
				String merge_linkdb = cmd + "%s";
				String folderStr = sb.deleteCharAt(sb.length() - 1).toString();
				//System.out.println(String.format(merge_linkdb, folderStr));
				ExcuteCmd.excuteCmd(String.format(merge_linkdb, folderStr));
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
	public void mergeSegments(String segments_folder) {
		if (nutch_root.length() > 0 && nutch_root != null && segments_folder != null) {
			String cmd = nutch_root + MERGE_SEGMENTS +output_folder+"segments ";
			try {
				String merge_segments = cmd + "%s";
				// System.out.println(String.format(merge_segments,segments_folder));
				ExcuteCmd.excuteCmd(String.format(merge_segments, segments_folder));
			} catch (Exception e) {
				LOG.info("merge segment:", e.getMessage());
			}
		}
	}

	/**
	 * @Title: mergeSegments
	 * @Description: TODO(合并segments,支持List)
	 * @param @param segments_list
	 * @return void 返回类型
	 * @author lj
	 * @throws
	 */
	public void mergeSegments(List<String> segments_list) {
		if (nutch_root.length() > 0 && nutch_root != null && segments_list != null) {
			String cmd = nutch_root + MERGE_SEGMENTS +output_folder+"segments ";
			try {
				for (String segments_folder : segments_list) {
					String merge_segments = cmd + "%s";
					 //System.out.println(String.format(merge_segments,segments_folder));
					ExcuteCmd.excuteCmd(String.format(merge_segments, segments_folder));
				}
			} catch (Exception e) {
				LOG.info("merge segments:", e.getMessage());
			}
		}
	}
}
