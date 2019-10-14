## Spring Cloud 集成学习

### 项目介绍
```
目的：
    该项目没有实现任何业务代码，主要为学习微服务架构思想，Spring Sloud框架集成和常见问题的解决方案，微服务项目部署方案，了解vue框架。
技术点：
       核心技术为springcloud+vue两个主流框架，使用consul实现服务注册发现和服务健康监控，使用springcloud gateway 实现服务路由
    同时集成security实现统一认证管理和权限管理， 集成seata处理分布式事物问题，使用 xxl-job 实现分布式任务调度
	
	核心框架：springcloud Greenwich.SR2
	安全框架：Spring Security
	分布式事物: seata 
	分布式任务调度：xxl-job
	持久层框架：MyBatisPlus
	数据库连接池：Alibaba Druid
	日志管理：LFK（Elasticsearch, FileBeat, Kibana） 
	前端框架：Vue
项目部署：
    jenkins+git+docker构建持续化集成环境
    实现思路，通过Jenkins创建Maven项目，通过Git拉取远程分支代码编译打包成docker镜像推送到docker私有仓库， 再给各个服务创建启动项目
    连接到需要部署的服务器，通过docker拉取镜像再运行shell脚本启动容器
    部署环境：
       一台CPU：1核  内存：2GiB和两台CPU：1核  内存：1GiB阿里云服务器 操作系统 Centos7 ,和一台云数据库RDS组成，
    由于服务器器不够就没部署 日志管理：LFK（Elasticsearch, FileBeat, Kibana） 
    
```
### 平台目录结构说明


```
├─paascloud-damo----------------------------父项目，公共依赖
│  │
│  ├─paascloud-config-----------------------微服务配置中心
│  │
│  ├─paascloud-gateway--------------------------微服务网关中心
│  │
│  ├─paascloud-provider-------------------------业务模块
|   |
│  ├─paascloud-provider-api------------------业务模块API
│  │
│  ├─paascloud-common
│  │  │
│  │  ├─paascloud-common-util------------------公共工具包
│  │  │
│  │  ├─springboot-starter-seata------------------分布式事物
│  │
│  ├─consul----------------------------------服务注册发现




```

## 配套项目

```
后端项目代码地址：https://github.com/youjie44340950/paascloud-demo.git     shell 脚本在 doc 目录下

Jenkins地址：http://47.104.150.14:8080/       用户名 root  密码 123456   欢迎大家指出不对的地方和能改进的地方   但是请不要直接修改配置

项目实例访问地址: http://47.104.150.14:80
        
```

### 作者

QQ： 1084931901




