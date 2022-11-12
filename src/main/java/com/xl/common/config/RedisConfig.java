package com.xl.common.config;

import com.xl.redisPublishAndSubscrible.SubListener;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @author xl
 * @date 2021年12月04日 13:37
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /** redis发布/订阅模式配置*/

    @Bean
    public ChannelTopic pubTopic() {
        return new ChannelTopic("pub");
    }

    @Bean
    public SubListener subListener() {
        return new SubListener();
    }


    /**
     * redis消息容器
     */
    //@Bean
    //public RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory) {
    //    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    //    // 设置redis连接工厂
    //    container.setConnectionFactory(redisConnectionFactory);
    //    // 绑定订阅主题和监听器
    //    container.addMessageListener((MessageListener) subListener(), pubTopic());
    //    return container;
    //}



    //@Bean
    //RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
    //                                        MessageListenerAdapter listenerAdapter) {
    //
    //    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    //    container.setConnectionFactory(connectionFactory);
    //    // 可以添加多个 messageListener，配置不同的交换机
    //    container.addMessageListener(listenerAdapter, new PatternTopic("channel:redis-test"));
    //    return container;
    //}
    //
    //@Bean
    //MessageListenerAdapter listenerAdapter(MessagePubListener receiver) {
    //    System.out.println("消息适配器1");
    //    return new MessageListenerAdapter(receiver, "onMessage");
    //}


}
