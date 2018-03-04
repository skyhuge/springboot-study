package com.local.study.utils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;

public class IOUtils {

        public static List<String> getFiles(List<String> list, String name){
            File file = new File(name);
            if (file.isFile()){
                list.add(file.getPath());
            }else {
                File[] files = file.listFiles();
                for (File f : files){
                    if (f.isFile()){
                        list.add(f.getPath() + File.separator +  f.getName());
                    }else {
                        getFiles(list,f.getPath());
                    }
                }
            }
            return list;
        }

        public static void copy(String src, String dest, String suffix) throws Exception {
            File file = new File(src);
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    if (file1.isDirectory()) {
                        copy(file1.getPath(),dest,suffix);
                    } else if (file1.getName().endsWith(suffix)) {
                        copy(file1.getPath(), dest + File.separator + file1.getName());
                    }
                }
            }
        }

        public static void copy(String src,String dest){
            try {
                RandomAccessFile srcFile = new RandomAccessFile(new File(src),"rw");
                RandomAccessFile destFile = new RandomAccessFile(new File(dest),"rw");
                FileChannel srcChannel = srcFile.getChannel();
                FileChannel destChannel = destFile.getChannel();
                long count = srcChannel.size();
                destChannel.transferFrom(srcChannel,0,count);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         *
         * @param is
         * @param lineBreaks 是否保留换行符
         * @return
         */
        public static String readFromInputStream(InputStream is, boolean lineBreaks){
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String tmp;
            try {
                while ((tmp = reader.readLine()) != null){
                    if (lineBreaks){
                        builder.append("\n").append(tmp);
                    }else {
                        builder.append(tmp);
                    }
                }
                return builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    }
