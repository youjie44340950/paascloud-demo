package com.springboot.start.seata;

import com.springboot.start.seata.rest.FescarRestTemplateAutoConfiguration;
import com.springboot.start.seata.web.FescarHandlerInterceptorConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Configuration
@Import({FescarRestTemplateAutoConfiguration.class,FescarHandlerInterceptorConfiguration.class
,GlobalTransactionAutoConfiguration.class})

public @interface EnableSeata {
}
