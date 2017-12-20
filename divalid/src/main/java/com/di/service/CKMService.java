package com.di.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.di.TrsHybaseConfig;
import com.trs.ckm.soap.AbsTheme;
import com.trs.ckm.soap.TrsCkmSoapClient;

@Component("ckmService")
public class CKMService {
	
	
	@Autowired
	TrsHybaseConfig hybaseConfig;

	private String dictName = "cnenDict";
	private int ckmExtractKeywordsNum = 5;
	
	public String[] extractKeywords(String text,int num) throws Exception{
		// TODO 
		//hybaseConfig = new TrsHybaseConfig();
		if(num == 0) num = ckmExtractKeywordsNum;
		String[] _retValue = null;
		TrsCkmSoapClient client = new TrsCkmSoapClient(hybaseConfig.ckmurl, hybaseConfig.ckmuser, hybaseConfig.ckmpassword);
		AbsTheme[] _result = client.GetAbsThemeList(text, num,dictName);
		if (_result != null && _result.length > 0) {
			_retValue = new String[_result.length];
			for (int i = 0; i < _result.length; i++) {
				_retValue[i] = _result[i].getWord();
			}
		}
		return _retValue;

	}

	public static void main(String args[]) throws Exception {
		CKMService zhiliao = new CKMService();
		String[] array = zhiliao.extractKeywords("本发明公开了一种基于智能终端的电视节目评论处理方法及系统",0);
		System.out.println(array);
	}

}
