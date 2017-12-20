package com.di.util;


/**
 * Series 维度集合
 * @author jiahh 2015年10月16日
 *
 */
public class Serie2 {

	public String name;
	public String color;
	public float[] center;
	public SeriePoint[] data;
	public static class SeriePoint {

		public String name;
		public String id;
		public float x;
		public float y;
		public float z;
	}

}
