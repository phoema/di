package com.ipph.bio.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtils {
	   public final static String encoding = "UTF-8";     
	   private static final String ENCODE = "UTF-8";
	   
	    public static void zip(String inputFilePath, String zipFileName) {
	 
	        File inputFile = new File(inputFilePath);
	        if (!inputFile.exists())
	            throw new RuntimeException("原始文件不存在!!!");
	        File basetarZipFile = new File(zipFileName).getParentFile();
	        if (!basetarZipFile.exists() && !basetarZipFile.mkdirs())
	            throw new RuntimeException("目标文件无法创建!!!");
	        zip(inputFile, new File(zipFileName));
	    }
	    public static void zip(File inputFile, File dest) {
	   	 
	        BufferedOutputStream bos = null;
	        FileOutputStream out = null;
	        ZipOutputStream zOut = null;
	        try {
	            // 创建文件输出对象out,提示:注意中文支持
	            out = new FileOutputStream(dest);
	            bos = new BufferedOutputStream(out);
	            // 將文件輸出ZIP输出流接起来
	            zOut = new ZipOutputStream(bos);
	            zip(zOut, inputFile, inputFile.getName());
	            closeAll(zOut, bos, out);
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	 
	    private static void zip(ZipOutputStream zOut, File file, String base) {
	         
	        try {
	            // 如果文件句柄是目录
	            if (file.isDirectory()) {
	                // 获取目录下的文件
	                File[] listFiles = file.listFiles();
	                // 建立ZIP条目
	                zOut.putNextEntry(new ZipEntry(base + "/"));
	                base = (base.length() == 0 ? "" : base + "/");
	                if (listFiles != null && listFiles.length > 0)
	                    // 遍历目录下文件
	                    for (File f : listFiles)
	                        // 递归进入本方法
	                        zip(zOut, f, base + f.getName());
	            }
	            // 如果文件句柄是文件
	            else {
	                if (base == "") {
	                    base = file.getName();
	                }
	                // 填入文件句柄
	                zOut.putNextEntry(new ZipEntry(base));
	                // 开始压缩
	                // 从文件入流读,写入ZIP 出流
	                writeFile(zOut, file);
	            }
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	 
	    private static void writeFile(ZipOutputStream zOut, File file)
	            throws IOException {
	 
	        FileInputStream in = null;
	        BufferedInputStream bis = null;
	        in = new FileInputStream(file);
	        bis = new BufferedInputStream(in);
	        int len = 0;
	        byte[] buff = new byte[2048];
	        while ((len = bis.read(buff)) != -1)
	            zOut.write(buff, 0, len);
	        zOut.flush();
	        closeAll(bis, in);
	    }
	    /***
	     * 关闭IO流
	     * 
	     * @param cls
	     */
	    public static void closeAll(Closeable... cls) {
	 
	        if (cls != null) {
	            for (Closeable cl : cls) {
	                try {
	                    if (cl != null)
	                        cl.close();
	                } catch (Exception e) {
	 
	                } finally {
	                    cl = null;
	                }
	            }
	        }
	    }	 
	    /****
	     * 解压
	     * 
	     * @param zipPath
	     *            zip文件路径
	     * @param destinationPath
	     *            解压的目的地点
	     * @param ecode
	     *            文件名的编码字符集
	     */
	    public static void unZip(String zipPath, String destinationPath) {
	 
	        File zipFile = new File(zipPath);
	        if (!zipFile.exists())
	            throw new RuntimeException("zip file " + zipPath
	                    + " does not exist.");
	 
	        Project proj = new Project();
	        Expand expand = new Expand();
	        expand.setProject(proj);
	        expand.setTaskType("unzip");
	        expand.setTaskName("unzip");
	        expand.setSrc(zipFile);
	        expand.setDest(new File(destinationPath));
	        expand.setEncoding(ENCODE);
	        expand.execute();
	        System.out.println("unzip done!!!");
	    }
	 

	    /** 
	     * 1.可以压缩目录(支持多级)<br> 
	     * 2.可以压缩文件<br> 
	     * 3.如果压缩文件的路径或父路径不存在, 将会自动创建<br> 
	     *  
	     * @param src 
	     *            将要进行压缩的目录 
	     * @param zip 
	     *            最终生成的压缩文件的路径 
	     */  
	    public static void zip2(File src, File dest) throws IOException {  
	    	// 如果没有指定目标目录，则默认使用当前目录
	    	if(dest == null){
	    		dest = new File(src.getParentFile().getPath()+File.separator+src.getName().replaceAll("[.][^.]+$", "")+".zip");
	    	}
	        Project prj = new Project();  
	        File basedir = new File(src.getParentFile().getPath()+File.separator+src.getName());
	        if(!basedir.exists() && basedir.mkdirs()){
	            prj.setBaseDir(basedir);
	        	        	
	        }
	        Zip zip = new Zip();  
	        zip.setProject(prj);  
	        zip.setDestFile(dest);  
	        FileSet fileSet = new FileSet();  
	        fileSet.setProject(prj);  
	        if (src.isFile()) {  
	            fileSet.setFile(src);  
	        } else {  
	            fileSet.setDir(src);  
	        }  
	        zip.addFileset(fileSet);  
	        zip.execute();  
	    }  
	  
	    /** 
	     * 将指定的压缩文件解压到指定的目标目录下. 如果指定的目标目录不存在或其父路径不存在, 将会自动创建. 
	     *  
	     * @param zip 
	     *            将会解压的压缩文件 
	     * @param dest 
	     *            解压操作的目录目录 
	     */  
	    public static void unzip(File src, File dest) throws IOException {  
	        Project proj = new Project();  
	        Expand expand = new Expand();  
	        expand.setProject(proj);  
	        expand.setTaskType("unzip");  
	        expand.setTaskName("unzip");  
	        expand.setSrc(src);  
	        expand.setDest(dest);  
	        expand.setEncoding(encoding);//设置编码不能少，少了文件名会有乱码  
	        expand.execute();  
	    }  
	  
	      
	  	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ZipUtils.zip(new File("D:\\MyWork\\di\\server.txt"), null);
		ZipUtils.zip(new File("D:\\MyWork\\di\\"), null);
		ZipUtils.zip(new File("E:\\biotest.txt"), new File("D:\\MyWork\\di.zip"));

	}

}
