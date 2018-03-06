package com.local.study.utils;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpClientManager {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientManager.class);

    private HttpClientManager(){}

    public CloseableHttpClient getInstance(){
        return HttpClientFactory.getInstance();
    }

    private static class HttpClientFactory{

        private static PoolingHttpClientConnectionManager manager = null;

        private static CloseableHttpClient httpClient = null;

        private static CloseableHttpClient getInstance(){
            if(httpClient == null){
                //注册访问协议相关的socket工厂
                Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                        .register("https", SSLConnectionSocketFactory.getSystemSocketFactory())
                        .register("http", PlainConnectionSocketFactory.getSocketFactory()).build();

                //配置HttpConnection工厂
                ManagedHttpClientConnectionFactory connectionFactory = new ManagedHttpClientConnectionFactory(DefaultHttpRequestWriterFactory.INSTANCE,
                        DefaultHttpResponseParserFactory.INSTANCE);
                SystemDefaultDnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;

                manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry,connectionFactory,dnsResolver);

                SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();

                manager.setDefaultSocketConfig(socketConfig);
                manager.setMaxTotal(300);//整个pool的最大连接数
                manager.setDefaultMaxPerRoute(200);//路由是对maxTotal的细分，设置太小无法支持大并发
                manager.setMaxPerRoute(new HttpRoute(new HttpHost("http://www.baidu.com")),100);//单独设置某个路由最大连接数
                manager.setValidateAfterInactivity(5 * 1000);

                //默认请求配置
                RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(2 * 1000)//连接超时
                        .setSocketTimeout(5 * 1000)//等待数据超时
                        .setConnectionRequestTimeout(2 * 1000)//从pool取连接超时
                        .build();

                httpClient = HttpClients.custom().setConnectionManager(manager)
                        .setConnectionManagerShared(false)//pool不共享
                        .evictExpiredConnections()//定期回收过期连接
                        .setConnectionTimeToLive(60, TimeUnit.SECONDS)//连接存活时间
                        .setDefaultRequestConfig(requestConfig)
                        .setConnectionReuseStrategy(DefaultClientConnectionReuseStrategy.INSTANCE)//连接重用，keepAlive
                        .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)//长连接配置
                        .setRetryHandler(new DefaultHttpRequestRetryHandler(0,false))//关闭重试
                        .build();

                Runtime.getRuntime().addShutdownHook(new Thread(()->{
                    try {
                        httpClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));

            }

            return httpClient;
        }
    }

    public String get(String url){
        HttpResponse response ;
        String result = "" ;
        HttpGet get = new HttpGet(url);
        try {
            response = getInstance().execute(get);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                EntityUtils.consume(response.getEntity());//尽快消费掉释放连接到pool
            }else {
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
