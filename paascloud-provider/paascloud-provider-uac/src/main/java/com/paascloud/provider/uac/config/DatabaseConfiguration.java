package com.paascloud.provider.uac.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {

	@Bean(initMethod = "init", destroyMethod = "close")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DruidDataSource storageDataSource() {
		DruidDataSource druidDataSource = new DruidDataSource();
		return druidDataSource;
	}

	@Bean
	public DataSourceProxy dataSourceProxy(DruidDataSource druidDataSource) {
		return new DataSourceProxy(druidDataSource);
	}

	@Bean
	public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSourceProxy dataSourceProxy) {
		// 这里用 MybatisSqlSessionFactoryBean 代替了 SqlSessionFactoryBean，否则 MyBatisPlus 不会生效
		MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
		mybatisSqlSessionFactoryBean.setDataSource(dataSourceProxy);
		// 设置主键自增
		GlobalConfig globalConfig = new GlobalConfig();
		GlobalConfig.DbConfig dbConfig =  new GlobalConfig.DbConfig();
		dbConfig.setIdType(IdType.AUTO);
        globalConfig.setDbConfig(dbConfig);
		mybatisSqlSessionFactoryBean.setGlobalConfig(globalConfig);
		return mybatisSqlSessionFactoryBean;
	}
}
