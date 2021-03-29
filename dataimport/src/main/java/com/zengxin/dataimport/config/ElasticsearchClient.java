package com.zengxin.dataimport.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RestHighLevelClient 客户端
 *
 * @author lenovo
 */
@Configuration
public class ElasticsearchClient {

    /**
     * es集群地址
     */
    @Value("${es.hosts}")
    private String hostlist;
    /**
     * logger.
     */
    private final Logger logger = LoggerFactory.getLogger(ElasticsearchClient.class);

    private static final String URL_REGEX = ".+:\\d{0,5}$";


    /**
     * 获取客户端连接
     *
     * @return RestHighLevelClient
     */
    @Bean(name = "restHighLevelClient")
    public RestHighLevelClient getClient() {
        logger.info("es.hosts{}", hostlist);
        //构造登录凭证
        String[] split = hostlist.split(",");
        //创建HttpHost数组，其中存放es主机和端口的配置信息
        HttpHost[] httpHostArray = new HttpHost[split.length];
        for (int i = 0; i < split.length; i++) {
            String item = split[i];
            Pattern p = Pattern.compile(URL_REGEX);
            Matcher m = p.matcher(item);
            if (m.find()) {
                httpHostArray[i] = new HttpHost(item.split(":")[0], Integer.parseInt(item.split(":")[1]), "http");
            } else {
                httpHostArray[i] = new HttpHost(item);
            }
        }
        RestClientBuilder builder = RestClient.builder(httpHostArray);
        //创建RestHighLevelClient客户端
        return new RestHighLevelClient(builder);
    }

}
