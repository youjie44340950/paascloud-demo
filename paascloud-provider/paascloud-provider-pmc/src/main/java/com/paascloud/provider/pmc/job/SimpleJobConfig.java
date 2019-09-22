package com.paascloud.provider.pmc.job;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;

@Configuration
public class SimpleJobConfig {

    @Resource
    private ZookeeperRegistryCenter regCenter;

    @Bean
    public SimpleJob simpleJob() {
        return new MyElasticSmipleJob();
    }

    /**
     * 普通邮件定时配置
     * @param mailSendJob
     * @param cron
     * @param shardingTotalCount
     * @param shardingItemParameters
     * @return
     */
    @Bean(initMethod = "init")
    public JobScheduler mailSendJobScheduler(final SimpleJob mailSendJob, @Value("${simpleJob.cron}") final String cron,
                                             @Value("${simpleJob.shardingTotalCount}") final int shardingTotalCount,
                                             @Value("${simpleJob.shardingItemParameters}") final String shardingItemParameters){
        LiteJobConfiguration jobConfiguration = createJobConfiguration(mailSendJob.getClass(), cron, shardingTotalCount, shardingItemParameters);
        return new JobScheduler(regCenter, jobConfiguration);
    }

    @SuppressWarnings("rawtypes")
    private LiteJobConfiguration createJobConfiguration(Class<? extends SimpleJob> jobClass, String cron, int shardingTotalCount
            , String shardingItemParameters) {
        // 定义作业核心配置
        JobCoreConfiguration coreConfig = JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount).shardingItemParameters(shardingItemParameters).build();
        // 定义SimpleJob类型配置
        SimpleJobConfiguration jobConfiguration = new SimpleJobConfiguration(coreConfig, jobClass.getCanonicalName());
        // 定义Lite作业根配置
        LiteJobConfiguration rootConfig = LiteJobConfiguration.newBuilder(jobConfiguration).overwrite(true).build();
        return rootConfig;
    }

}
