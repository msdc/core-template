package com.isoftstone.crawl.template.test;

import java.io.File;
import java.io.FileFilter;
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
        PropertiesUtils property = PropertiesUtils.getInstance();
        List<String> segParseList = new ArrayList<String>();
        List<String> segIndexList = new ArrayList<String>();
        String rootPath = property.getValue("template.root.crawl.data.folder");
        File[] fis = new File(rootPath).listFiles();
        for (File tpFile : fis) {
            //File tpFile = fis[i];
            if (tpFile.isDirectory()) {
                File[] secfis = tpFile.listFiles();
                for (File sectpFile : secfis) {
                    //File sectpFile = secfis[j];
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
                                //removeDir(finalFile[l]);
                            }
                        }
                    }
                }
            }
        }
        for (String segs : segParseList) {
            String tpStrParse = "bin/nutch parse %s";
            System.out.println(String.format(tpStrParse, segs));
            LOG.info(String.format(tpStrParse, segs));
        }
        for (String segs : segIndexList) {
            String tpStrIndex = "bin/nutch solrindex http://192.168.100.236:8983/solr/core0 %s/crawldb -linkdb %s/linkdb -dir %s/segments";
            System.out.println(String.format(tpStrIndex, segs,segs,segs));
            LOG.info(String.format(tpStrIndex, segs,segs,segs));
        }
    }

    public static void removeDir(File dir) {

        File[] files = dir.listFiles();

        for (File file : files) {                  //遍历
            if (file.isDirectory()) {                 //判断是否为文件夹？
                removeDir(file);                       //递归
            } else
                System.out.println(file + ":" + file.delete());  //如果不是文件夹，就删除。
        }
        System.out.println(dir + "----" + dir.delete());    //从最里层开始删除文件夹。

    }
}
