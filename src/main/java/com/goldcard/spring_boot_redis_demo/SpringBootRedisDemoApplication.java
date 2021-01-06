package com.goldcard.spring_boot_redis_demo;

import com.goldcard.spring_boot_redis_demo.interceptor.Interceptor1;
import com.goldcard.spring_boot_redis_demo.interceptor.MulitiInterceptor1;
import com.goldcard.spring_boot_redis_demo.interceptor.MulitiInterceptor2;
import com.goldcard.spring_boot_redis_demo.interceptor.MulitiInterceptor3;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Locale;

@SpringBootApplication
@MapperScan(basePackages = "com.goldcard.spring_boot_redis_demo.mapper")
@EnableCaching
//实现 WebMvcConfigurer 接口，覆盖 addInterceptors 方法，注册自定义拦截器
public class SpringBootRedisDemoApplication implements WebMvcConfigurer {
    //Spring boot的自动装配机制会读取application.properties文件里的配置生成有关Redis的操作对象：
    //RedisConnectionFactory,RedisTemplate,StringRedisTemplete等常用对象
    //RedisTemplate默认使用JdkSerializationRedisSerializer进行序列化键值
    @Autowired
    private RedisTemplate redisTemplate;

    // 国际化拦截器
    private LocaleChangeInterceptor lci = null;

    @PostConstruct
    public void init() {
        initRedisTemplete();
    }

    // Redis连接工厂
    @Autowired
    private RedisConnectionFactory connectionFactory;
    // Redis消息监听器
    @Autowired
    private MessageListener redisMsgListener = null;

    // 任务池
    private ThreadPoolTaskScheduler taskScheduler = null;

    /**
     * 创建任务池，运行线程等待处理Redis的消息
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskScheduler initTaskScheduler() {
        if (taskScheduler != null) {
            return taskScheduler;
        }
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(20);
        return taskScheduler;
    }

    /**
     * 定义Redis的监听容器
     *
     * @return 监听容器
     */
    @Bean
    public RedisMessageListenerContainer initRedisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // Redis连接工厂
        container.setConnectionFactory(connectionFactory);
        // 设置运行任务池
        container.setTaskExecutor(initTaskScheduler());
        // 定义监听渠道，名称为topic1
        Topic topic = new ChannelTopic("topic1");
        // 使用监听器监听Redis的消息
        container.addMessageListener(redisMsgListener, topic);
        return container;
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringBootRedisDemoApplication.class, args);
    }

    //修改RedisTemplate的序列化器
    private void initRedisTemplete() {
        RedisSerializer redisSerializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setValueSerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);
        redisTemplate.setHashValueSerializer(redisSerializer);
    }

    /**
     * 自定义redis缓存，注释掉配置文件相关缓存配置
     * @return
     */
    @Bean(name = "redisCacheManager" )
    public RedisCacheManager initRedisCacheManager() {
        // Redis加锁的写入器
        RedisCacheWriter writer= RedisCacheWriter.lockingRedisCacheWriter(connectionFactory);
        // 启动Redis缓存的默认设置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        // 设置JDK序列化器
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer()));
        // 禁用前缀
        config = config.disableKeyPrefix();
        //设置10分钟超时
        config = config.entryTtl(Duration.ofMinutes(10));
        // 创建缓Redis存管理器
        RedisCacheManager redisCacheManager = new RedisCacheManager(writer, config);
        return redisCacheManager;
    }
    // 国际化解析器，请注意这个Bean Name要为localeResolver
    @Bean(name = "localeResolver")
    public LocaleResolver initLocaleResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        // 默认国际化区域
        slr.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return slr;
    }

    // 创建国际化拦截器
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        if (lci != null) {
            return lci;
        }
        lci = new LocaleChangeInterceptor();
        // 设置参数名
        lci.setParamName("language");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器到Spring MVC机制
//        InterceptorRegistration interceptorRegistration = registry.addInterceptor(new Interceptor1());
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(new MulitiInterceptor1());
        //指定拦截匹配模式，限制拦截器拦截请求
        interceptorRegistration.addPathPatterns("/interceptor/*");

        InterceptorRegistration interceptorRegistration2 = registry.addInterceptor(new MulitiInterceptor2());
        //指定拦截匹配模式，限制拦截器拦截请求
        interceptorRegistration2.addPathPatterns("/interceptor/*");

        InterceptorRegistration interceptorRegistration3 = registry.addInterceptor(new MulitiInterceptor3());
        //指定拦截匹配模式，限制拦截器拦截请求
        interceptorRegistration3.addPathPatterns("/interceptor/*");

        //通过国际化拦截器的preHandle方法对请求的国际化区域参数进行修改
        registry.addInterceptor(localeChangeInterceptor());
    }
}
