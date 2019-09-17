package com.springboot.start.seata;


import io.seata.spring.annotation.GlobalTransactionScanner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 
 * @author zcb
 * @date 2019年4月9日 上午10:28:08
 */
@Configuration
@EnableConfigurationProperties(FescarProperties.class)
public class GlobalTransactionAutoConfiguration {

	private final ApplicationContext applicationContext;

	private final FescarProperties fescarProperties;

	public GlobalTransactionAutoConfiguration(ApplicationContext applicationContext,
                                              FescarProperties fescarProperties) {
		this.applicationContext = applicationContext;
		this.fescarProperties = fescarProperties;
	}

	@Bean
	public GlobalTransactionScanner globalTransactionScanner() {

		String applicationName = applicationContext.getEnvironment()
				.getProperty("spring.application.name");

		String txServiceGroup = fescarProperties.getTxServiceGroup();

		if (StringUtils.isEmpty(txServiceGroup)) {
			txServiceGroup = applicationName + "-fescar-service-group";
			fescarProperties.setTxServiceGroup(txServiceGroup);
		}

		return new GlobalTransactionScanner(applicationName, txServiceGroup);
	}
}
