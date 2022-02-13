### java操作es方式
- http
- 操作es使用的方式为http方式，需要springboot的pom依赖，我使用的版本为：7.4.2，es对应的版本为：7.4.2，springboot的版本为：2.2.1.RELEASE

### java使用方式
- 引入pom依赖

```java
<dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch</artifactId>
</dependency>
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-client</artifactId>
</dependency>
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
</dependency>
```

- es配置

```yaml
elasticsearch.port=9200
elasticsearch.username=elastic
elasticsearch.password=123
elasticsearch.cluster.address=http://p.es.net
elasticsearch.shards=1
elasticsearch.replicas=0
elasticsearch.connect_timeout=5000
elasticsearch.socket_timeout=60000
```

- ElasticSearchConfig.class

```java
package com.dadi01.scrm.service.mot.provider.config;

import com.dadi01.scrm.foundation.model.constant.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lviter
 */
@Slf4j
@Configuration
public class ElasticSearchConfig {

    @Value("${elasticsearch.cluster.address}")
    private String clusterAddress;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;

    @Value("${elasticsearch.shards}")
    private Integer numberOfShards;

    @Value("${elasticsearch.replicas}")
    private Integer numberOfReplicas;

    @Value("${elasticsearch.connect_timeout}")
    private Long connectTimeout;

    @Value("${elasticsearch.socket_timeout}")
    private Long socketTimeout;

    public static RestHighLevelClient client = null;

    public Integer getNumberOfShards() {
        return numberOfShards;
    }

    public Integer getNumberOfReplicas() {
        return numberOfReplicas;
    }

    /**
     * @return 连接es
     */
    @Bean
    public RestHighLevelClient restClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        Header[] defaultHeaders = {new BasicHeader("content-type", "application/json")};
        RestClientBuilder restClientBuilder = RestClient.builder(HttpHost.create(clusterAddress));
        restClientBuilder
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .setDefaultHeaders(defaultHeaders)
                .setRequestConfigCallback(requestConfigBuilder -> {
                    // 连接5秒超时，套接字连接60s超时
                    return requestConfigBuilder.setConnectTimeout(connectTimeout.intValue()).setSocketTimeout(socketTimeout.intValue());
                })
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.disableAuthCaching();
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                });

        client = new RestHighLevelClient(restClientBuilder);
        return client;
    }

    private List<HttpHost> createHttpHostList() {
        List<HttpHost> hostList = new ArrayList<>();
        String[] hostNamesPort;
        if (!clusterAddress.contains(StringPool.COMMA)) {
            hostNamesPort = new String[]{clusterAddress};
        } else {
            hostNamesPort = clusterAddress.split(",");
        }
        for (String host : hostNamesPort) {
            hostList.add(new HttpHost(host.substring(0, host.indexOf(StringPool.COLON)), Integer.parseInt(host.substring(host.indexOf(StringPool.COLON) + 1))));
        }
        return hostList;
    }

}
```

- EsSettingsConstant.class

```java
package com.dadi01.scrm.service.mot.provider.constant;

/**
 * @author lviter
 */
public class EsSettingsConstant {

    /**
     * 数据分片数
     */
    public final static String NUMBER_OF_SHARDS = "index.number_of_shards";

    /**
     * 数据备份数
     */
    public final static String NUMBER_OF_REPLICAS = "index.number_of_replicas";

    /**
     * 分页查询es限制最大条数
     */
    public final static String MAX_RESULT_WINDOW = "index.max_result_window";

    /**
     * 最大一亿
     */
    public final static String MAX_RESULT_WINDOW_VALUE = "100000000";

}
```

- ElasticSearchServiceImpl.class es通用增删改查，分页等

```java
package com.dadi01.scrm.service.mot.provider.impl;

import com.alibaba.fastjson.JSON;
import com.dadi01.scrm.foundation.model.constant.StringPool;
import com.dadi01.scrm.foundation.model.dto.PageData;
import com.dadi01.scrm.foundation.model.dto.ResultDTO;
import com.dadi01.scrm.foundation.model.dto.ResultListDTO;
import com.dadi01.scrm.foundation.model.dto.ResultPageDTO;
import com.dadi01.scrm.foundation.model.error.ErrorEnum;
import com.dadi01.scrm.foundation.model.exception.ScrmException;
import com.dadi01.scrm.service.mot.api.IElasticSearchService;
import com.dadi01.scrm.service.mot.api.common.EsLogActionEnum;
import com.dadi01.scrm.service.mot.api.dto.elasticsearch.CrowdMessageDTO;
import com.dadi01.scrm.service.mot.api.dto.elasticsearch.MemberDTO;
import com.dadi01.scrm.service.mot.api.dto.elasticsearch.OperatingLogDTO;
import com.dadi01.scrm.service.mot.provider.config.ElasticSearchConfig;
import com.dadi01.scrm.service.mot.provider.constant.EsIndexConstant;
import com.dadi01.scrm.service.mot.provider.constant.EsSettingsConstant;
import com.dadi01.scrm.service.mot.provider.util.JsonUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lviter
 */
@Slf4j
@Service
public class ElasticSearchServiceImpl implements IElasticSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private ElasticSearchConfig elasticSearchConfig;

    private static AtomicLong i = new AtomicLong(0);

    @Override
    public ResultDTO<Object> getElasticSearchInfo() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // SearchRequest
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        // 查询ES
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es查询异常{}", JsonUtils.objectToJson(e));
        }
        return ResultDTO.success(searchResponse);
    }

    @Override
    public ResultDTO<Boolean> addCrowdMessage(String index, CrowdMessageDTO crowdMessageDTO) {
        if (StringUtils.isBlank(index)) {
            throw new ScrmException(ErrorEnum.MOT_ES_INDEX_NOT_NULL.build());
        }
        IndexRequest indexRequest = new IndexRequest(index);
        Long createTime = System.currentTimeMillis();
        crowdMessageDTO.setCreateTime(createTime);
        String source = JSON.toJSONString(crowdMessageDTO);
        try {
            indexRequest.source(source, XContentType.JSON);
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es add data filed{}", JsonUtils.objectToJson(e));
            throw new ScrmException(ErrorEnum.MOT_ES_ADD_DATA_FAILED.build());
        }
        addOperatingLog(EsIndexConstant.ES_OPERATING_LOG.getIndex(), EsLogActionEnum.INSERT, index, new Gson().toJson(source));
        return ResultDTO.success(true);
    }

    /**
     * 批量添加es数据
     *
     * @param crowdMessages
     * @return
     */
    @Override
    public ResultDTO addBatchCrowdMessage(String index, List<CrowdMessageDTO> crowdMessages) {
        if (crowdMessages.size() > 100000) {
            log.error("es add batch data too large{}", crowdMessages.size());
            throw new ScrmException(ErrorEnum.MOT_ES_ADD_DATA_FAILED.build());
        }
        BulkRequest request = new BulkRequest();
        crowdMessages.forEach(crowdMessageDTO -> {
            crowdMessageDTO.setCreateTime(System.currentTimeMillis());
            crowdMessageDTO.setSort(i.getAndIncrement());
            String source = JSON.toJSONString(crowdMessageDTO);
            request.add(new IndexRequest(index).source(source, XContentType.JSON));
        });
        esBatchAdd(request, index);
        return ResultDTO.success();
    }

    @Override
    public ResultDTO<Void> addBatchMember(String index, List<MemberDTO> members) {
        if (members.size() > 100000) {
            log.error("es add batch data too large{}", members.size());
            throw new ScrmException(ErrorEnum.MOT_ES_ADD_DATA_FAILED.build());
        }

        BulkRequest request = new BulkRequest();
        members.forEach(member -> {
            member.setCreateTime(System.currentTimeMillis());
            String source = JSON.toJSONString(member);
            request.add(new IndexRequest(index).source(source, XContentType.JSON));
        });
        esBatchAdd(request, index);
        return ResultDTO.success();
    }

    /**
     * 批量插入数据
     *
     * @param bulkRequest
     * @param index
     */
    private void esBatchAdd(BulkRequest bulkRequest, String index) {
        try {
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es add batch data filed{}", JsonUtils.objectToJson(e));
            throw new ScrmException(ErrorEnum.MOT_ES_ADD_DATA_FAILED.build());
        }
        log.info("插入数据----------------------{}", bulkRequest.requests().size());
        addOperatingLog(EsIndexConstant.ES_OPERATING_LOG.getIndex(), EsLogActionEnum.INSERT, index, String.valueOf(bulkRequest.requests().size()));
    }

    /**
     * 创建索引
     *
     * @param index
     */
    @Override
    public ResultDTO<Void> createIndexResponse(String index) {
        //创建索引,如果索引已存在，返回错误信息
        if (checkIndexExists(index)) {
            log.info("索引已存在{}", index);
            throw new ScrmException(ErrorEnum.MOT_ES_INDEX_ALREADY_EXIST.build());
        }
        //创建索引
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
        //设置分片
        createIndexRequest.settings(
                Settings.builder().put(EsSettingsConstant.NUMBER_OF_SHARDS, elasticSearchConfig.getNumberOfShards())
                        .put(EsSettingsConstant.NUMBER_OF_REPLICAS, elasticSearchConfig.getNumberOfReplicas())
                        .put(EsSettingsConstant.MAX_RESULT_WINDOW, EsSettingsConstant.MAX_RESULT_WINDOW_VALUE));
        CreateIndexResponse createIndexResponse = null;
        try {
            createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            boolean acknowledged = createIndexResponse.isAcknowledged();
            boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();

            if (acknowledged && shardsAcknowledged) {
                addOperatingLog(EsIndexConstant.ES_OPERATING_LOG.getIndex(), EsLogActionEnum.CREATE, index, new Gson().toJson(createIndexResponse));
                log.info("索引创建成功{}", index);
            }
        } catch (IOException e) {
            log.error("index create failed{}", JsonUtils.objectToJson(e));
            addOperatingLog(EsIndexConstant.ES_OPERATING_LOG.getIndex(), EsLogActionEnum.CREATE, index, new Gson().toJson(e));
            throw new ScrmException(ErrorEnum.MOT_ES_INDEX_CREATE_FAILED.build());
        }
        return ResultDTO.success();
    }

    /**
     * 判断索引是否存在
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean checkIndexExists(String indexName) {
        GetIndexRequest request = new GetIndexRequest().indices(indexName);
        try {
            return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("判断索引是否存在，操作异常！");
        }
        return false;
    }

    @Override
    public ResultPageDTO<CrowdMessageDTO> pageQuery(Integer page, Integer rows, String index, Integer status) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from((page - 1) * rows)
                .size(rows)
                .sort("sort", SortOrder.DESC)
                .trackTotalHits(true);
        if (status != null) {
            searchSourceBuilder.query(QueryBuilders.termQuery("status", status));
        }
        SearchResponse searchResponse = pageQuerySearchResponse(searchSourceBuilder, index);
        long total = searchResponse.getHits().getTotalHits().value;
        // 遍历封装列表对象
        List<CrowdMessageDTO> crowdMessages = new ArrayList<>();
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        for (SearchHit searchHit : searchHits) {
            crowdMessages.add(JSON.parseObject(searchHit.getSourceAsString(), CrowdMessageDTO.class).setId(searchHit.getId()));
        }
        return ResultPageDTO.success(new PageData<CrowdMessageDTO>().setData(crowdMessages).setPageSize(crowdMessages.size()).setTotal((int) total));
    }

    @Override
    public ResultPageDTO<Map<String, Object>> pageQueryByIndex(Integer page, Integer rows, String index) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from((page - 1) * rows)
                .size(rows)
                .trackTotalHits(true);
        SearchResponse searchResponse = pageQuerySearchResponse(searchSourceBuilder, index);
        long total = searchResponse.getHits().getTotalHits().value;

        List<Map<String, Object>> resultList = new ArrayList<>();
        // 遍历封装列表对象
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        for (SearchHit searchHit : searchHits) {
            searchHit.getSourceAsMap().put("id", searchHit.getId());
            resultList.add(searchHit.getSourceAsMap());
        }
        return ResultPageDTO.success(new PageData<Map<String, Object>>().setData(resultList).setPageSize(resultList.size()).setTotal((int) total));
    }

    /**
     * 分页查询搜索es
     *
     * @param searchSourceBuilder
     * @param index
     * @return
     */
    private SearchResponse pageQuerySearchResponse(SearchSourceBuilder searchSourceBuilder, String index) {
        SearchRequest searchRequest = new SearchRequest()
                .source(searchSourceBuilder)
                .indices(index);
        SearchResponse searchResponse;
        try {
            log.info("查询es入参：{}", new Gson().toJson(searchRequest));
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es page query failed{}", JsonUtils.objectToJson(e));
            throw new ScrmException(ErrorEnum.MOT_ES_PAGE_QUERY_FAILED.build());
        }
        return searchResponse;
    }

    @Override
    public ResultDTO<CrowdMessageDTO> getById(String index, String id) {
        if (StringUtils.isBlank(index)) {
            index = EsIndexConstant.ES_TEST.getIndex();
        }
        GetRequest getRequest = new GetRequest(index, id);
        GetResponse getResponse = null;
        try {
            log.info("根据编号查询数据，rq:{}", new Gson().toJson(getRequest));
            getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es查询异常{}", JsonUtils.objectToJson(e));
            throw new ScrmException(ErrorEnum.MOT_ES_QUERY_FAILED.build());
        }
        CrowdMessageDTO crowdMessageDTO = JSON.parseObject(getResponse.getSourceAsString(), CrowdMessageDTO.class).setId(getResponse.getId());
        return ResultDTO.success(crowdMessageDTO);
    }

    @Override
    public ResultListDTO<MemberDTO> getMemberList(List<String> memberIds) {
        List<MemberDTO> memberList = new ArrayList<>();

        MultiSearchRequest request = new MultiSearchRequest();
        memberIds.forEach(memberId -> {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(QueryBuilders.matchPhraseQuery("memberId", memberId));
            request.add(new SearchRequest()
                    .source(searchSourceBuilder)
                    .indices(EsIndexConstant.ES_MEMBER_INFO.getIndex()));
        });
        try {
            MultiSearchResponse response = restHighLevelClient.msearch(request, RequestOptions.DEFAULT);
            for (MultiSearchResponse.Item item : response.getResponses()) {
                log.info(JsonUtils.objectToJson(item));
                for (SearchHit hit : item.getResponse().getHits().getHits()) {
                    memberList.add(JsonUtils.jsonToPojo(hit.getSourceAsString(), MemberDTO.class));
                }
            }
        } catch (IOException e) {
            log.error("es查询异常{}", JsonUtils.objectToJson(e));
            throw new ScrmException(ErrorEnum.MOT_ES_QUERY_FAILED.build());
        }
        return ResultListDTO.success(memberList);
    }

    @Override
    public ResultDTO update(String index, CrowdMessageDTO crowdMessageDTO) {
        if (StringUtils.isBlank(index)) {
            throw new ScrmException(ErrorEnum.MOT_ES_INDEX_NOT_NULL.build());
        }
        UpdateRequest updateRequest = new UpdateRequest(index, crowdMessageDTO.getId());
        updateRequest.retryOnConflict(3);
        updateRequest.doc(JSON.toJSONString(crowdMessageDTO), XContentType.JSON);
        // 操作ES
        UpdateResponse updateResponse = null;
        try {
            log.info("更新数据,rq:{}", new Gson().toJson(updateRequest));
            updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es update failed{}", JsonUtils.objectToJson(e));
            throw new ScrmException(ErrorEnum.MOT_ES_UPDATE_FAILED.build());
        }
        return ResultDTO.success(updateResponse);
    }

    @Override
    @Async
    public ResultDTO<Void> updateBatch(String index, List<CrowdMessageDTO> crowdMessages) {
        batchUpdate(index, crowdMessages);
        addOperatingLog(EsIndexConstant.ES_OPERATING_LOG.getIndex(), EsLogActionEnum.UPDATE, index, String.valueOf(crowdMessages.size()));
        return ResultDTO.success();
    }

    /**
     * 批量修改
     *
     * @param index
     * @param crowdMessages
     */
    private void batchUpdate(String index, List<CrowdMessageDTO> crowdMessages) {
        BulkRequest bulkRequest = new BulkRequest();
        crowdMessages.forEach(crowdMessageDTO -> bulkRequest.add(new UpdateRequest(index, crowdMessageDTO.getId()).doc(JSON.toJSONString(crowdMessageDTO), XContentType.JSON)));
        try {
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es add batch data filed{}", JsonUtils.objectToJson(e));
            throw new ScrmException(ErrorEnum.MOT_ES_UPDATE_FAILED.build());
        }
    }


    @Override
    public ResultDTO deleteById(String index, String id) {
        if (StringUtils.isBlank(index)) {
            index = EsIndexConstant.ES_TEST.getIndex();
        }
        DeleteRequest deleteRequest = new DeleteRequest(index, id);
        // 操作ES
        DeleteResponse deleteResponse = null;
        try {
            log.info("删除数据根据ID,rq:{}", new Gson().toJson(deleteRequest));
            deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es查询异常{}", JsonUtils.objectToJson(e));
            e.printStackTrace();
        }
        return ResultDTO.success(deleteResponse);
    }

    @Override
    public ResultDTO deleteIndex(String index) {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);

        try {
            AcknowledgedResponse deleteResponse = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            boolean acknowledged = deleteResponse.isAcknowledged();
            if (acknowledged) {
                return ResultDTO.success();
            }
            addOperatingLog(EsIndexConstant.ES_OPERATING_LOG.getIndex(), EsLogActionEnum.DELETE, index, new Gson().toJson(deleteResponse));
        } catch (IOException e) {
            log.error("es delete index failed{}", JsonUtils.objectToJson(e));
            addOperatingLog(EsIndexConstant.ES_OPERATING_LOG.getIndex(), EsLogActionEnum.DELETE, index, new Gson().toJson(e));
            throw new ScrmException(ErrorEnum.MOT_ES_INDEX_DELETE_FAIL.build());
        }
        return ResultDTO.success();
    }

    @Override
    public ResultDTO<Set<String>> getAlias() {
        Set<String> indices;
        GetAliasesRequest request = new GetAliasesRequest();
        try {
            GetAliasesResponse getAliasesResponse = restHighLevelClient.indices().getAlias(request, RequestOptions.DEFAULT);
            Map<String, Set<AliasMetaData>> map = getAliasesResponse.getAliases();
            indices = map.keySet();
            indices.removeIf(str -> str.startsWith(StringPool.DOT));
            return ResultDTO.success(indices);
        } catch (IOException e) {
            log.error("es get indices failed{}", JsonUtils.objectToJson(e));
            throw new ScrmException(ErrorEnum.MOT_ES_QUERY_FAILED.build());
        }
    }

    @Override
    public ResultDTO updateAllByKey(String index, String key, String value) {
//        UpdateRequest updateRequest = new UpdateRequest(index);
//        restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        return null;
    }

    @Override
    @Async
    public ResultDTO<Void> addOperatingLog(String index, EsLogActionEnum esLogActionEnum, String operateIndex, String comment) {
        //创建索引,如果索引不存在，就创建索引
//        String index = EsIndexConstant.ES_OPERATING_LOG.getIndex();
        if (!checkIndexExists(index)) {
            createIndexResponse(index);
        }
        IndexRequest indexRequest = new IndexRequest(index);
        OperatingLogDTO operatingLogDTO = new OperatingLogDTO();
        operatingLogDTO.setCreateTime(System.currentTimeMillis());
        operatingLogDTO.setLogAction(esLogActionEnum.getKey());
        operatingLogDTO.setLogModule(operateIndex);
        operatingLogDTO.setComment(comment);
        String source = JSON.toJSONString(operatingLogDTO);
        indexRequest.source(source, XContentType.JSON);
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("add operating log fail!");
        }
        return ResultDTO.success();
    }
}
```