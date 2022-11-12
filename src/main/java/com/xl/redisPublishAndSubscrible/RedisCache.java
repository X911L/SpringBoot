package com.xl.redisPublishAndSubscrible;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

/**
 * @author xl
 * @date 2021年12月04日 13:42
 */
@Component
public class RedisCache {

    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    //@Qualifier("channelTopic")
    public ChannelTopic channelTopic;

    @Autowired
    //@Qualifier("simpleDataTopic")
    public ChannelTopic simpleDataTopic;

    @Autowired
    //@Qualifier("settingTopic")
    public ChannelTopic settingTopic;


    /**
     * redis发布消息 使用对应的主题发送消息，对应的订阅者便会收到消息
     */
    public void convertAndSend(Object message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }

    public void simpleDataTopicSend(Object message) {
        redisTemplate.convertAndSend(simpleDataTopic.getTopic(), message);
    }

    public void settingTopicSend(Object message) {
        redisTemplate.convertAndSend(settingTopic.getTopic(), message);
    }

}
