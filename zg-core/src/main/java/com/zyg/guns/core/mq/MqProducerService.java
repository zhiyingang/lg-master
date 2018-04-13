package com.zyg.guns.core.mq;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.UUID;

/**
 * @Description：MQ生产者
 * @Author： chenggang
 * @Date:2017/12/11
 * @Copyright:the Corporation of mianshui365
 */
@Configuration
public class MqProducerService {
    private final Logger logger = LoggerFactory.getLogger(MqProducerService.class);

    @Value("${mq.producerGroup}")
    private String producerGroup;
    @Value("${mq.namesrvaddr}")
    private String namesrvAddr;
    @Autowired
    private DefaultMQProducer defaultMQProducer;


    @Bean
    @Scope(value = "singleton")
    public DefaultMQProducer createDefaultMQProducer() {
        defaultMQProducer = new DefaultMQProducer(producerGroup);
        defaultMQProducer.setNamesrvAddr(namesrvAddr);
        defaultMQProducer.setInstanceName(UUID.randomUUID().toString());
        return defaultMQProducer;
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @Scope(value = "singleton")
    public MqProducerService createProductService() {
        // 初始化
        MqProducerService mqProducerService = new MqProducerService();
        return mqProducerService;
    }

    /**
     * 发送消息
     *
     * @param topic
     * @param tag
     * @param keys
     * @param jsonData
     * @return Boolean
     */
    public Boolean sendMessage(String topic, String tag, String keys, String jsonData) {
        try {
            Message msg = new Message(topic, tag, keys, jsonData.getBytes());
            SendResult sendResult = defaultMQProducer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer id = (Integer) arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, 1);
            logger.info(msg.getKeys() + ":", sendResult.toString());
            if (sendResult != null && sendResult.getSendStatus() == SendStatus.SEND_OK) {
                return true;
            }
        } catch (MQClientException e) {
            logger.error(e.getMessage(), e);
        } catch (RemotingException e) {
            logger.error(e.getMessage(), e);
        } catch (MQBrokerException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 消息生产者链接初始化
     */
    public void init() throws MQClientException {
        // 参数信息
        logger.info("DefaultMQProducer initialize!");
        logger.info(producerGroup);
        logger.info(namesrvAddr);
        // 初始化
        defaultMQProducer.start();
    }


    public void destroy() {
        defaultMQProducer.shutdown();
    }
}

