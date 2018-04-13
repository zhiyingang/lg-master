package com.zyg.guns.core.mq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

/**
 * RocketMQ 消费端
 * Created by chenggang on 2017/12/13.
 */
@Configuration
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private DefaultMQPushConsumer defaultMQPushConsumer;

    //mq服务地址
    @Value("${mq.namesrvAddr}")
    private String namesrvAddr;
    //消费组名称
    @Value("${mq.consumerGroup}")
    private String consumerGroup;
    //topic名称
    @Value("${mq.topicName}")
    private String topicName;

    /**
     * 创建一个单例的consumer Bean
     *
     * @return
     */
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @Scope(value = "singleton")
    public Consumer createConsumer() {
        logger.debug("create Consumer bean start");
        Consumer consumer = new Consumer();
        consumer.setConsumerGroup(consumerGroup);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setTopicName(topicName);
        logger.debug("create Consumer bean end");
        return consumer;
    }

    /**
     * 初始化 DefaultMQPushConsumer
     *
     * @throws InterruptedException
     * @throws com.alibaba.rocketmq.client.exception.MQClientException
     */
    public void init() throws InterruptedException, MQClientException {

        // 参数信息
        logger.debug("Consumer initialize!");
        logger.debug("namesrvAddr: {}", namesrvAddr);
        logger.debug("consumerGroup: {}", consumerGroup);


        // 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br>
        // 注意：ConsumerGroupName需要由应用来保证唯一
        defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
        defaultMQPushConsumer.setNamesrvAddr(namesrvAddr);
        defaultMQPushConsumer.setInstanceName(UUID.randomUUID().toString());
        // 订阅指定Topic下所有Tag
        defaultMQPushConsumer.subscribe(topicName, "*");

        // 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
        // 如果非第一次启动，那么按照上次消费的位置继续消费
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        // 设置为集群消费(区别于广播消费)
        defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
        //最小最大批处理消息次数
        defaultMQPushConsumer.setConsumeThreadMin(1);
        defaultMQPushConsumer.setConsumeThreadMax(1);

        defaultMQPushConsumer.registerMessageListener(new SubscribeService());

        // Consumer对象在使用之前必须要调用start初始化，初始化一次即可
        defaultMQPushConsumer.start();

        logger.debug("GoodsConsumer start success!");
    }

    public void destroy() {
        defaultMQPushConsumer.shutdown();
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

}
