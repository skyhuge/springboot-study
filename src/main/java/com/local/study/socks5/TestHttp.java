package com.local.study.socks5;

import java.util.HashMap;
import java.util.Map;

public class TestHttp {

	public static void main(String[] args) {
		try {
			String url="http://www.baidu.com/";
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Accept", "text/html, */*; q=0.01");
	        headers.put("Accept-Encoding", "gzip, deflate, sdch");
	        headers.put("Accept-Language", "zh-CN,zh;q=0.8");
	        headers.put("Referer", url);
	        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:46.0) Gecko/20100101 Firefox/46.0");
	        
			String result=HttpClientUtil.getWithProxy(url, headers, "UTF-8");
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
