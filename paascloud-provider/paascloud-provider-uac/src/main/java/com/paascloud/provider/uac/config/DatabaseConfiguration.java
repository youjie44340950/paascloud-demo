package com.paascloud.provider.uac.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import io.seata.rm.datasource.DataSourceProxy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.sql.SQLException;



@Configuration
public class DatabaseConfiguration {

	private final ApplicationContext applicationContext;

	public DatabaseConfiguration(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Bean(initMethod = "init", destroyMethod = "close")
	public DruidDataSource storageDataSource() throws SQLException {
		Environment env = applicationContext.getEnvironment();

		String ip = env.getProperty("mysql.server.ip");
		String port = env.getProperty("mysql.server.port");
		String dbName = env.getProperty("mysql.db.name");
		String userName = env.getProperty("mysql.user.name");
		String password = env.getProperty("mysql.user.password");

		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setUrl("jdbc:mysql://" + ip + ":" + port + "/" + dbName+"?serverTimezone=GMT%2B8");
		druidDataSource.setUsername(userName);
		druidDataSource.setPassword(password);
		druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		druidDataSource.setInitialSize(0);
		druidDataSource.setMaxActive(180);
		druidDataSource.setMaxWait(60000);
		druidDataSource.setMinIdle(0);
		druidDataSource.setTestOnBorrow(false);
		druidDataSource.setTestOnReturn(false);
		druidDataSource.setTestWhileIdle(true);
		druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
		druidDataSource.setMinEvictableIdleTimeMillis(25200000);
		druidDataSource.setRemoveAbandoned(true);
		druidDataSource.setRemoveAbandonedTimeout(1800);
		druidDataSource.setLogAbandoned(true);
		druidDataSource.setFilters("mergeStat");
		return druidDataSource;
	}

	@Bean
	public DataSourceProxy dataSourceProxy(DruidDataSource druidDataSource) {
		return new DataSourceProxy(druidDataSource);
	}

	@Bean
	public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSourceProxy dataSourceProxy) throws Exception {
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
