package com.paascloud.provider.pmc.job;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.GetACLBuilder;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;
import java.util.List;

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
     * @param shardingItemParameters
     * @return
     */
    @Bean(initMethod = "init")
    public JobScheduler mailSendJobScheduler(final SimpleJob mailSendJob, @Value("${simpleJob.cron}") final String cron,
                                             @Value("${regCenter.namespace}") final String namespace,
                                             @Value("${simpleJob.shardingItemParameters}") final String shardingItemParameters){
        CuratorFramework client = regCenter.getClient();
        CuratorZookeeperClient zookeeperClient = client.getZookeeperClient();
        int shardingTotalCount = 1;
        try {
            ZooKeeper zooKeeper = zookeeperClient.getZooKeeper();
            List<String> children = zooKeeper.getChildren("/"+namespace+"/"+mailSendJob.getClass().getName()+"/instances", false);
            shardingTotalCount = shardingTotalCount + children.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
