package com.ipph.bio.file;

import java.io.File;

/** 
 * 该类实现文件夹压缩成zip文件和zip文件解压 
 * @author jiahh 
 * 
 */  
public class FileUtil{   
	  
	  /** 
	   * 删除文件、文件夹 
	   */  
	  public static void deleteFile(String path) {  
	      File file = new File(path);  
	      deleteFile(file);  
	  }  
	  /** 
	   * 删除文件、文件夹 
	   */  
	  public static void deleteFile(File file) {  
	      if (file.isDirectory()) {  
	          File[] ff = file.listFiles();  
	          for (int i = 0; i < ff.length; i++) {  
	              deleteFile(ff[i].getPath());  
	          }  
	      }  
	      file.delete();  
	  }  


}   
  