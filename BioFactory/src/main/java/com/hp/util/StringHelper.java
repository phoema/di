package com.hp.util;

import java.util.regex.*;

public class StringHelper {
	public static String getInnerText(String sText, int start, String sTagName, Integer lastPos)
	{
		String sBegTxt = String.format("<%s", sTagName);
		String sEndTxt = String.format("</%s>", sTagName);
		
		return getInnerText(sText, start, sBegTxt, sEndTxt, lastPos);
	}
	
	public static String getInnerText(String sText, int start, String sBegTxt, String sEndTxt, Integer lastPos)
	{
		int pos1 = sText.indexOf(sBegTxt, start);
		if(pos1 < 0)
		{
			return null;
		}
		pos1 += sBegTxt.length();
		
		int pos2 = sText.indexOf('>', pos1);
		if(pos2 < 0)
		{
			return null;
		}
		pos1 = pos2 + 1;
		
		pos2 = sText.indexOf(sEndTxt, pos1);
		if(pos2<0)
		{
			return null;
		}
		
		String str = sText.substring(pos1, pos2);
		lastPos = pos2 + sEndTxt.length();
		return str.trim();
	}
	
	public static String getInnerTextWithRegex(String sText, int start, boolean ignoreCase, String sTagName)
	{
		String sBegTxt = String.format("<\\s*+%s.*+>", sTagName);
		String sEndTxt = String.format("<\\s*+/\\s*%s.*+>", sTagName);
		
		String str = getInnerTextWithRegex(sText, start, ignoreCase, sBegTxt, sEndTxt);
		if(str!=null)
		{
			return str;
		}
		
		sEndTxt = " /\\s*+>";
		return getInnerTextWithRegex(sText, start, ignoreCase, sBegTxt, sEndTxt);
	}
	
	public static String getInnerTextWithRegex(String sText, int start, boolean ignoreCase, String sBegTxt, String sEndTxt)
	{
		Pattern p1 = null, p2 = null;
		if( !ignoreCase )
		{
			p1 = Pattern.compile(sBegTxt);
			p2 = Pattern.compile(sEndTxt);
		}
		else 
		{
			p1 = Pattern.compile(sBegTxt, Pattern.CASE_INSENSITIVE);
			p2 = Pattern.compile(sEndTxt, Pattern.CASE_INSENSITIVE);
		}
		
		Matcher mr1 = p1.matcher(sText);
		return null;
	}
}
