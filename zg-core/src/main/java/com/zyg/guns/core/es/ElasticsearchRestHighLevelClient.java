package com.zyg.guns.core.es;

import com.google.common.collect.Lists;
import com.zyg.guns.core.utils.StringUtil;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by yg.zhi on 2018/4/16.
 */
@Configuration
public class ElasticsearchRestHighLevelClient {

    @Value("${data.elasticsearch.cluster-rest-nodes}")
    private String clusterRestNodes;

    public RestHighLevelClient restHighLevelClient(){
        List<HttpHost> httpHosts = Lists.newArrayList();
        if(!StringUtil.isEmpty(clusterRestNodes)){
            for(String nodes : clusterRestNodes.split(",")){
                String[] inetSocket = nodes.split(":");
                String address = inetSocket[0];
                Integer port = Integer.parseInt(inetSocket[1]);
                httpHosts.add(new HttpHost(address,port));
            }
        }
        RestClient lowLevelRestClient = RestClient.builder(httpHosts.toArray(new HttpHost[]{})).build();
        RestHighLevelClient client = new RestHighLevelClient(lowLevelRestClient);
        return client;
    }

}
