package com.ymkj.credit.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileDownUtils {
    
    private static final int BUFFER = 8192;

    /**
     * 压缩文件
     * @param path
     * @param returnFileName
     * @throws IOException
     */
    public static void zipFiles(String path, String returnFileName) throws IOException {
        File inFile = new File(path);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(returnFileName));
        compress(inFile, zos, "");
        zos.flush();
        zos.close();
    }

    /**
     * 递归压缩目录
     * @param file
     * @param out
     * @param basedir
     */
    public static void compress(File file, ZipOutputStream out, String basedir) {
        /* 判断是目录还是文件 */
        if (file.isDirectory()) {
            compressDirectory(file, out, basedir);
        } else {
            compressFile(file, out, basedir);
        }
    }

    /**
     * 压缩一个目录
     * @param dir
     * @param out
     * @param basedir
     */
    public static void compressDirectory(File dir, ZipOutputStream out,String basedir) {
        if (!dir.exists()){
            return;
        }
        File[] files = dir.listFiles();
        for (File file:files) {
            /* 递归 */
            compress(file, out, basedir + dir.getName() + File.separator);
        }
    }

    /**
     * 压缩一个文件
     * @param file
     * @param out
     * @param basedir
     */
    public static void compressFile(File file, ZipOutputStream out,String basedir) {
        if (!file.exists()) {
            return;
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(basedir + file.getName());
            out.putNextEntry(entry);
            int count;
            byte[] data = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成zip文件
     * @param inFile
     * @param zos
     * @param dir
     * @throws IOException
     */
    public static void zipFile(File inFile, ZipOutputStream zos, String dir) throws IOException {
        if (inFile.isDirectory()) {
            File[] files = inFile.listFiles();
            for (File file : files){
                zipFile(file, zos, inFile.getName());
            }
        } else {
            String entryName = null;
            if (!"".equals(dir)){
                entryName = inFile.getName();
            }else{
                entryName = inFile.getName();
            }
            ZipEntry entry = new ZipEntry(entryName);
            zos.putNextEntry(entry);
            InputStream is = new FileInputStream(inFile);
            int len = 0;
            while ((len = is.read()) != -1){
                zos.write(len);
            }
            is.close();
        }
    }

    /**
     * 创建目录、包含子目录
     * @param path
     */
    public static void ensureDir(String path) {
        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }
    }
    
    /**
     * 递归删除目录下的所有子目录及文件
     * @param dir
     */
    public static void deleteDir(File dir) {
        if(dir.isFile()){
            dir.delete();
        }else{
            File[] childFiles = dir.listFiles();
            for(File file : childFiles){
                deleteDir(file);
            }
        }
    }
}
