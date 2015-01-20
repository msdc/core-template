package com.isoftstone.crawl.template.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    	reParseAndIndex(nutch_root,data_folder,solr_index);
    }
    
    public static void reParseAndIndex(String nutch_root,String data_folder,String solr_index){
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
                    	String tpindexPath=sectpFile.getPath();
                    	tpindexPath=tpindexPath.substring(0,tpindexPath.indexOf(new String("segments")));
                    	segIndexList.add(tpindexPath);
                        File[] thirdFile = sectpFile.listFiles();
                        for (File thirdtpFile : thirdFile) {
                            segParseList.add(thirdtpFile.getPath());
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
            excuteCmd(String.format(tpStrParse, segs));
            LOG.info(String.format(tpStrParse, segs));
        }
        for (String segs : segIndexList) {
            String tpStrIndex = nutch_root+" solrindex "+solr_index+" %s/crawldb -linkdb %s/linkdb -dir %s/segments";
            System.out.println(String.format(tpStrIndex, segs, segs, segs));
            excuteCmd(String.format(tpStrIndex, segs, segs, segs));
            LOG.info(String.format(tpStrIndex, segs, segs, segs));
        }
    }
    
    public static void excuteCmd(String cmd){
    	 try {  
             Runtime rt = Runtime.getRuntime(); 
             Process proc = rt.exec(cmd);  
             InputStream stdin = proc.getInputStream();  
             InputStreamReader isr = new InputStreamReader(stdin);  
             BufferedReader br = new BufferedReader(isr);  
             String line = null;  
             System.out.println("<output></output>");  
             while ((line = br.readLine()) != null)  
                 System.out.println(line);  
             System.out.println("");  
             int exitVal = proc.waitFor();  
             System.out.println("Process exitValue: " + exitVal);  
         } catch (Throwable t) {  
             t.printStackTrace();  
         }  
    	
    }

    public static void removeDir(File dir) {

        File[] files = dir.listFiles();

        for (File file : files) {                  //闁秴宸�
            if (file.isDirectory()) {                 //閸掋倖鏌囬弰顖氭儊娑撶儤鏋冩禒璺恒仚閿涳拷
                removeDir(file);                       //闁帒缍�
            } else
                System.out.println(file + ":" + file.delete());  //婵″倹鐏夋稉宥嗘Ц閺傚洣娆㈡径鐧哥礉鐏忓崬鍨归梽銈冿拷锟�
        }
        System.out.println(dir + "----" + dir.delete());    //娴犲孩娓堕柌灞界湴瀵拷婵鍨归梽銈嗘瀮娴犺泛銇欓妴锟�

    }
}
