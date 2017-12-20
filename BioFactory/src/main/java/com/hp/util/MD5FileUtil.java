package com.hp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;

/**
 * 查看文件MD5值
 * @author linl
 *
 */
public class MD5FileUtil {
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };


	/**
	 * jiahh:关于MappedByteBuffer资源释放问题
	 * 引用：http://blog.csdn.net/zhuyijian135757/article/details/38501121
	 * @param buffer
	 */
	public static void clean(final Object buffer){
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				try {
					Method getCleanerMethod = buffer.getClass().getMethod(
							"cleaner", new Class[0]);
					getCleanerMethod.setAccessible(true);
					sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod
							.invoke(buffer, new Object[0]);
					if (cleaner != null) {
						cleaner.clean();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});

	}


	/**
	 * 获取文件MD5值
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	public static String getFileMD5String(File file) throws IOException, NoSuchAlgorithmException {
		FileInputStream in = new FileInputStream(file);
		FileChannel ch = in.getChannel();
		MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
				file.length());
		MessageDigest messagedigest = MessageDigest.getInstance("MD5");
		messagedigest.update(byteBuffer);
		String md5 =bufferToHex(messagedigest.digest());
		// 关于MappedByteBuffer资源释放问题
		MD5FileUtil.clean(byteBuffer);
		return md5;

		//return bufferToHex(messagedigest.digest());
	}

	public static String getMD5String(String s) throws NoSuchAlgorithmException {
		return getMD5String(s.getBytes());
	}

	public static String getMD5String(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest messagedigest = MessageDigest.getInstance("MD5");
		messagedigest.update(bytes);
		return bufferToHex(messagedigest.digest());
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		char c1 = hexDigits[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	public static boolean checkPassword(String password, String md5PwdStr) throws NoSuchAlgorithmException {
		String s = getMD5String(password);
		return s.equals(md5PwdStr);
	}

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		long begin = System.currentTimeMillis();

		File big = new File("E:/szhzipant.zip");
		String md5 = getFileMD5String(big);

		long end = System.currentTimeMillis();
		System.out.println("md5:" + md5);
		System.out.println("time:" + ((end - begin) / 1000) + "s");
		// 97bcf49b207e8b40a11898d5a0a33d4f
		
		String strMD5 = getMD5String("test");
		System.out.println("strMD5:" + strMD5);
	}

}