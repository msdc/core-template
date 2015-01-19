package com.isoftstone.crawl.template.test;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.isoftstone.crawl.template.utils.PropertiesUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2015/1/14.
 */

public class ReParseAndIndex {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesUtils.class);

    public static void main(String[] args) {
    	if (args.length < 3) {
			System.err.println("Usage: ParseAndIndex <nutch_root> <data_folder> <solr_index>");
			return ;
		}
    	String nutch_root = args[0];
    	String data_folder = args[1];
    	String solr_index =args[2];
    	if (nutch_root!=null && data_folder!=null) {
    		System.out.println("ParseAndIndex: nutch_root: " + nutch_root);
    		System.out.println("ParseAndIndex: data_folder: " + data_folder);
    		System.out.println("ParseAndIndex: solr_index: " + solr_index);
		}
    	
        //PropertiesUtils property = PropertiesUtils.getInstance();
        List<String> segParseList = new ArrayList<String>();
        List<String> segIndexList = new ArrayList<String>();

       // String rootPath = property.getValue("template.root.crawl.data.folder");
        File[] fis = new File(data_folder).listFiles();

//        List<String> segCrawlList = new ArrayList<String>();
//        String rootPath = property.getValue("template.root.crawl.data.folder");
//        String rootCrawl = property.getValue("template.root.crawl.seeds.folder");
//        File[] fis = new File(rootPath).listFiles();
//
//        File[] seedsFolder = new File(rootCrawl).listFiles();

//        for (File tpFile : seedsFolder) {
//            if (tpFile.isDirectory() == true && tpFile.getName().endsWith("_data") == false) {
//                segCrawlList.add(tpFile.getPath());
//            }
//        }

        for (File tpFile : fis) {
            if (tpFile.isDirectory()) {
                File[] secfis = tpFile.listFiles();
                for (File sectpFile : secfis) {
                    if (sectpFile.isDirectory() && sectpFile.getName().equals(new String("segments"))) {
                        segParseList.add(sectpFile.getPath());
                        File[] thirdFile = sectpFile.listFiles();
                        for (File thirdtpFile : thirdFile) {
                            segIndexList.add(thirdtpFile.getPath());
                            File[] finalFile = thirdtpFile.listFiles(new FileFilter() {
                                @Override
                                public boolean accept(File pathname) {
                                    if (pathname.isDirectory()) {
                                        if (pathname.getName().equals(new String("crawl_parse")) ||
                                                pathname.getName().equals(new String("parse_data")) ||
                                                pathname.getName().equals(new String("parse_text"))) {
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    } else {
                                        return false;
                                    }
                                }
                            });
                            for (int l = 0; l < finalFile.length; l++) {
                                removeDir(finalFile[l]);
                            }
                        }
                    }
                }
            }
        }

//        for (String segs : segCrawlList) {
//            String[] tpStr = segs.split("\\\\");
//            String secStr = rootPath+"/"+tpStr[tpStr.length - 1].split("_")[0] + "_data";
//            String tpStrCrawl = "bin/nutch crawl %s %s http://192.168.100.236:8983/solr/core0 3";
//            System.out.println(String.format(tpStrCrawl, segs, secStr));
//            LOG.info(String.format(tpStrCrawl, segs, secStr));
//        }
        for (String segs : segParseList) {
            String tpStrParse = nutch_root+" parse %s";
            System.out.println(String.format(tpStrParse, segs));
            LOG.info(String.format(tpStrParse, segs));
        }
        for (String segs : segIndexList) {
            String tpStrIndex = nutch_root+" solrindex "+solr_index+" %s/crawldb -linkdb %s/linkdb -dir %s/segments";
            System.out.println(String.format(tpStrIndex, segs, segs, segs));
            LOG.info(String.format(tpStrIndex, segs, segs, segs));
        }
    }

    public static void removeDir(File dir) {

        File[] files = dir.listFiles();

        for (File file : files) {                  //閬嶅巻
            if (file.isDirectory()) {                 //鍒ゆ柇鏄惁涓烘枃浠跺す锛�
                removeDir(file);                       //閫掑綊
            } else
                System.out.println(file + ":" + file.delete());  //濡傛灉涓嶆槸鏂囦欢澶癸紝灏卞垹闄ゃ��
        }
        System.out.println(dir + "----" + dir.delete());    //浠庢渶閲屽眰寮�濮嬪垹闄ゆ枃浠跺す銆�

    }
}
