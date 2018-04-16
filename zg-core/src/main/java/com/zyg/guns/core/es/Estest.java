//package net.jcmob.joyplus.service.impl;
//
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import net.jcmob.joyplus.dao.MaterialImportMapper;
//import net.jcmob.joyplus.dao.MaterialReportMapper;
//import net.jcmob.joyplus.dto.MaterialDTO;
//import net.jcmob.joyplus.entity.MaterialReport;
//import net.jcmob.joyplus.service.MaterialReportService;
//import net.jcmob.joyplus.vo.MaterialImportVo;
//import net.jcmob.joyplus.vo.UserVO;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.search.aggregations.AggregationBuilders;
//import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
//import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
//import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
//import org.elasticsearch.search.aggregations.bucket.terms.Terms;
//import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
//import org.elasticsearch.search.aggregations.metrics.sum.Sum;
//import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeZone;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.elasticsearch.index.query.QueryBuilders.*;
//
//@Service
//public class MaterialReportServiceImpl extends BaseServiceImpl<MaterialReport> implements MaterialReportService {
//
//    private static final Logger logger = LoggerFactory.getLogger(MaterialReportServiceImpl.class);
//
//    @Autowired
//    RestHighLevelClient restHighLevelClient;
//
//    @Autowired
//    private MaterialReportMapper materialReportMapper;
//
//    @Autowired
//    private MaterialImportMapper materialImportMapper;
//
//    @Autowired
//    public void setBaseMapper(){
//        super.setJoyPlusMapper(materialReportMapper);
//    }
//
//    @Override
//    public PageInfo<MaterialReport> selectAll(Integer currentPage, Integer pageSize, MaterialReport materialReport, String type, UserVO userVO) {
//        PageHelper.startPage(currentPage, pageSize);
//        return new PageInfo<>(materialReportMapper.selectAllPage(materialReport,type,userVO));
//    }
//
//
//    /**
//     * 时间范围中的es聚合
//     * @param startTime 起始时间
//     * @param endTime 结束时间
//     * @param dateHistogramInterval 统计维度，按时还是按天
//     * @param logType 日志类型
//     * @param timeField 时间范围字段
//     * @return
//     */
//    @Override
//    public List<MaterialReport> materialReportCounts(Long startTime, Long endTime,DateHistogramInterval dateHistogramInterval,String logType,String timeField) {
//        List<MaterialReport> materialReports = new ArrayList<>();
//
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//
//        TermsAggregationBuilder cidTermsAggregationBuilder = AggregationBuilders.terms("cid").field("cid.keyword");
//
//        TermsAggregationBuilder orderIdTermsAggregationBuilder = AggregationBuilders.terms("orderId").field("orderId");
//
//        orderIdTermsAggregationBuilder.subAggregation(
//                AggregationBuilders
//                        .dateHistogram("by_day_or_hour")
//                        .field(timeField)
//                        .dateHistogramInterval(dateHistogramInterval)
//                        .timeZone(DateTimeZone.forID("Asia/Shanghai"))
//        );
//
//        cidTermsAggregationBuilder.subAggregation(orderIdTermsAggregationBuilder);
//
//        BoolQueryBuilder boolQueryBuilder = boolQuery()
//                .must(termQuery("log_type.keyword", logType))
//                .must(rangeQuery(timeField).gte(startTime).lt(endTime));
//
//        if ("AdvConfirm".equals(logType)) { // 广告主确认 排除重复激活，其状态码为20
//
//            boolQueryBuilder.mustNot(termQuery("status",20));
//
//        }else if("ChannelCallAdv".equals(logType)){ // 通知广告主 1,201,202,204
//
//            boolQueryBuilder.must(termsQuery("status", new int[]{1,201,202,204}));
//
//        }else if("AdvCallChannel".equals(logType)){ // 通知渠道 1,302,303
//
//            boolQueryBuilder.must(termsQuery("status", new int[]{1,302,303}));
//
//        }
//
//        searchSourceBuilder.query(boolQueryBuilder);
//        searchSourceBuilder.aggregation(cidTermsAggregationBuilder);
//        searchSourceBuilder.size(0);
//
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.source(searchSourceBuilder);
//
//        logger.debug("--------materialReportCounts-------->"+searchRequest.toString());
//
//        SearchResponse searchResponse = null;
//        try {
//            searchResponse = restHighLevelClient.search(searchRequest);
//            Terms term = searchResponse.getAggregations().get("cid");
//            term.getBuckets().forEach(bucket->{
//
//                Terms orderIdterm = bucket.getAggregations().get("orderId");
//                orderIdterm.getBuckets().forEach(orderIdBucket->{
//
//                    Histogram dayOrHour = orderIdBucket.getAggregations().get("by_day_or_hour");
//                    dayOrHour.getBuckets().forEach(dayOrHourBucket->{
//
//                        DateTime dateTime = (DateTime) dayOrHourBucket.getKey();
//                        dateTime = dateTime.toDateTime(DateTimeZone.forID("Asia/Shanghai"));
//
//                        MaterialReport materialReport = new MaterialReport();
//                        materialReport.setCreativeId((String) bucket.getKey());
//                        materialReport.setOrderId((Long) orderIdBucket.getKey());
//                        materialReport.setReportDay(dateTime.toString("yyyy-MM-dd"));
//
//                        if(dateHistogramInterval.equals(DateHistogramInterval.HOUR)){
//                            materialReport.setReportHour(dateTime.toString("HH:00"));
//                        }
//
//                        // 广告主确认次数
//                        if("AdvConfirm".equals(logType)){
//                            materialReport.setAdverConfirmTimes(dayOrHourBucket.getDocCount());
//                            // 用户点击数
//                        }else if("ChannelClick".equals(logType)){
//                            materialReport.setUserClickTimes(dayOrHourBucket.getDocCount());
//                            // 通知广告主次数
//                        }else if("ChannelCallAdv".equals(logType)){
//                            materialReport.setNoticeAdverTimes(dayOrHourBucket.getDocCount());
//                        }
//
//                        materialReports.add(materialReport);
//                    });
//
//                });
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return materialReports;
//    }
//
//    @Override
//    public List<MaterialReport> calcCpaConsume(Long startTime, Long endTime) {
//        List<MaterialReport> materialReports = new ArrayList<>();
//
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//
//        // 按cid分组
//        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("cid").field("cid.keyword");
//
//        // 按天聚合
//        DateHistogramAggregationBuilder dateHistogramAggregation = AggregationBuilders
//                .dateHistogram("by_day")
//                .field("confirmTime")
//                .dateHistogramInterval(DateHistogramInterval.DAY)
//                .timeZone(DateTimeZone.forID("Asia/Shanghai"));
//
//        // 聚合广告主应付金额
//        SumAggregationBuilder cpaConsume = AggregationBuilders.sum("cpaConsume").field("inputMoney");
//
//        // 按天聚合金额
//        dateHistogramAggregation.subAggregation(cpaConsume);
//
//        // 根据广告主按天聚合金额
//        termsAggregationBuilder.subAggregation(dateHistogramAggregation);
//
//        BoolQueryBuilder boolQueryBuilder = boolQuery()
//                .must(termQuery("log_type.keyword", "AdvConfirm"))
//                .must(termQuery("channelType.keyword", "CPA"))
//                .must(rangeQuery("confirmTime").gte(startTime).lt(endTime))
//                .mustNot(termQuery("status",20)); // 应排除重复激活其状态为20
//
//        searchSourceBuilder.query(boolQueryBuilder);
//
//        searchSourceBuilder.aggregation(termsAggregationBuilder);
//        searchSourceBuilder.size(0);
//
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.source(searchSourceBuilder);
//
//        logger.debug("--------calcCpaConsume-------->"+searchRequest.toString());
//
//        SearchResponse searchResponse;
//        try {
//            searchResponse = restHighLevelClient.search(searchRequest);
//            Terms term = searchResponse.getAggregations().get("cid");
//            term.getBuckets().forEach(bucket->{
//
//                Histogram dayHistogram = bucket.getAggregations().get("by_day");
//                dayHistogram.getBuckets().forEach(dayBucket->{
//                    MaterialReport materialReport = new MaterialReport();
//
//                    DateTime dateTime = (DateTime) dayBucket.getKey();
//                    dateTime = dateTime.toDateTime(DateTimeZone.forID("Asia/Shanghai"));
//                    Sum money = dayBucket.getAggregations().get("cpaConsume");
//
//                    materialReport.setCreativeId((String) bucket.getKey());
//                    materialReport.setReportDay(dateTime.toString("yyyy-MM-dd"));
//                    if(money!=null){
//                        materialReport.setConsume(money.getValue());
//                    }
//                    materialReports.add(materialReport);
//                });
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return materialReports;
//    }
//
//    @Override
//    public List<MaterialDTO> queryMaterialMetaData() {
//        return materialReportMapper.queryMaterialMetaData();
//    }
//
//    @Override
//    public int createTempTable(String type) {
//        return materialReportMapper.createTempTable(type);
//    }
//
//    @Override
//    public List<MaterialReport> queryTempTable(String type) {
//        return materialReportMapper.queryTempTable(type);
//    }
//
//    @Override
//    public int saveinto(MaterialReport materialReport,String type) {
//        return materialReportMapper.saveinto(materialReport,type);
//    }
//
//    @Override
//    public int dropTempTable(String type) {
//        return materialReportMapper.dropTempTable(type);
//    }
//
//    @Override
//    public List<MaterialReport> charts(MaterialReport materialReport,String type){
//        return materialReportMapper.charts(materialReport,type);
//    }
//
//    @Override
//    public int deleteByMaterialReport(String reportDay,String reportHour,String type) {
//        return materialReportMapper.deleteByMaterialReport(reportDay,reportHour,type);
//    }
//
//    @Transactional
//    @Override
//    public void saveOrUpdate(MaterialImportVo materialImportVo) {
//        materialImportMapper.saveOrUpdate(materialImportVo);
//    }
//
//    @Transactional
//    @Override
//    public int batchUpdate() {
//        return materialImportMapper.batchUpdate();
//    }
//
//}
