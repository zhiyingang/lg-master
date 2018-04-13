package com.zyg.guns.core.mq;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 订阅服务
 * Created by chenggang on 2017/12/13.
 */
public class SubscribeService implements MessageListenerConcurrently {
    private static final Logger logger = LoggerFactory.getLogger(SubscribeService.class);
    /**
     * 消息处理
     *
     * @param list
     * @param consumeConcurrentlyContext
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        logger.info("SubscribeService consumeMessage start...");
        // 默认list里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
        MessageExt msg = list.get(0);
        String tags = msg.getTags();
//        NoticeService noticeService = initNoticeServiceBean(tags);
        logger.info("MQ tags is {}", tags);
        String jsonData = new String(msg.getBody());
        logger.info("SubscribeService receive message json data: {}", jsonData);
        try {
            //处理业务
//            noticeService.sendNotice(jsonData);
            logger.info("SubscribeService consumeMessage end...");
            // 如果没有return success ，consumer会重新消费该消息，直到return success
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }

    /**
     * 初始化通知服务bean
     * @param tag
     * @return
     */
//    private NoticeService initNoticeServiceBean(String tag) {
//        switch (tag) {
//            //产品变更通知
//            case "noticeProductChanged":
//                return (ProductChangeNoticeService) SpringContextUtil.getBean("productChangeNoticeService");
//            //退款结果通知
//            case "noticeOrderRefundApproveResult":
//                return (NoticeOrderRefundApproveResultService) SpringContextUtil.getBean("noticeOrderRefundApproveResultService");
//            //供应商发码通知
//            case "noticeOrderEticketSended":
//                return (NoticeOrderEticketSendedService) SpringContextUtil.getBean("noticeOrderEticketSendedService");
//            //用户消费通知
//            case "noticeOrderConsumed":
//                return (NoticeOrderConsumedService) SpringContextUtil.getBean("noticeOrderConsumedService");
//        }
//        return null;
//    }

}
