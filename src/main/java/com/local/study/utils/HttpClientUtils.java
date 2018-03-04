package com.local.study.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.Map;

public class HttpClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    /**
     * upload file
     * @param serverUrl
     * @param fileParamName
     * @param file
     * @param params
     * @return
     * @throws Exception
     */
    public static String post(String serverUrl, String fileParamName, File file, Map<String, String> params) throws Exception {

        HttpPost httpPost = new HttpPost(serverUrl);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 上传的文件
        builder.addBinaryBody(fileParamName, file);
        // 设置其他参数 if exist
        if (!CollectionUtils.isEmpty(params)){
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
            }
        }
        HttpEntity httpEntity = builder.build();
        httpPost.setEntity(httpEntity);
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(httpPost);
        if (null == response || response.getStatusLine() == null) {
            logger.info("Post Request For Url[{}] is not ok. Response is null", serverUrl);
            return null;
        } else if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            logger.info("Post Request For Url[{}] is not ok. Response Status Code is {}", serverUrl,
                    response.getStatusLine().getStatusCode());
            return null;
        }
        return EntityUtils.toString(response.getEntity());
    }

    public static void main(String[] args) throws Exception{
        String url = "http://127.0.0.1:8080/upload";
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "config/hystrix/hystrix.properties";
        File file = new File(path);
        String post = HttpClientUtils.post(url, "file", file, null);
        System.out.println("return :" + post);
    }

}
