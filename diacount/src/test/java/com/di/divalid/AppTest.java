package com.di.divalid;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.springframework.web.client.RestTemplate;

import com.google.common.io.Files;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	
		String info = "登录名：\t%s\t姓名：\t%s\t性别：\t%s\t单位：\t%s\t邮箱：\t%s\t电话：\t%s\t备注：\t%s\t注册日：\t%s";
	    String out = String.format(info, "jiahuihui","贾辉辉","男","出版社","jiahh@cnipr.com","13000000","","20160307");
	    System.out.println(out);
        assertTrue( true );
    }
	/**
	 * 保留cookie，再次请求网站
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void testHttp() throws ClientProtocolException, IOException {

		
		
		// template.postForLocation("http://192.168.0.75//txn999999.do?username=admin&password=gwssiadmin",
		// null);
		// template.getForObject("http://192.168.0.75//txn999999.do?username=admin&password=gwssiadmin",
		// Object.class);
//
//		String url = "http://192.168.0.82:8085/ckmtest/gwssi/order?readColumn=AD;PNO;TIO;ABSO&&strWhere=%s&&sortWhere=%s";
//		// 替换为DI公共接口
//		url = "http://192.168.0.75/txnPatentImgTextListRecord.ajax?select-key:thesaurus=&select-key:cross=&select-key:buttonItem=&select-key:expressCN2=&attribute-node:patent_start-row=1&attribute-node:patent_page-row=10&attribute-node:patent_sort-column=-PD&select-key:expressCN=";
//		url = url + URLEncoder.encode("(((专利权人='专利') OR (标题='专利') OR (申请人='专利') OR (摘要='专利') OR (发明人='专利')))");
//		
		// 创建HttpClient实例
		AbstractHttpClient httpclient = new DefaultHttpClient();
		AbstractHttpClient httpclientget = new DefaultHttpClient();
		// 创建Get方法实例
		HttpGet httpgets = new HttpGet(
				URI.create("http://search.cnipr.com/pages!advSearch.action"));
		httpclient.execute(httpgets);
		((AbstractHttpClient)httpclient).getCookieStore().getCookies();


		HttpPost httppost = new HttpPost();
		httppost.setURI(URI.create("http://search.cnipr.com/search!doOverviewSearch.action"));
		HttpParams params = new BasicHttpParams();
		params.setParameter("strSource", "FMZL,SYXX,WGZL,FMSQ");
		params.setParameter("strWhere", "AD=2011");
		httppost.setParams(params);
		
		HttpResponse response = null;
		response = httpclient.execute(httppost);

		String cookie = this.getUserCookie(httpclient);
		response.getAllHeaders();
		String responseString = EntityUtils.toString(response.getEntity());
		System.out.println(responseString);
	}
    public String getUserCookie(HttpClient httpclient){
        List<Cookie> cookies = ((AbstractHttpClient)httpclient).getCookieStore().getCookies();
        String userCookie = "";
        for(Cookie cookie: cookies){
            String name = cookie.getName();
            String value = cookie.getValue();

            System.out.print(name+":" + value);
        }
        return userCookie;
    }

}
