package com.sinosoft.doubledatasource.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * @author ASUS
 * @version v1.0.0
 * @description 第一数据源配置类
 */
@Configuration
@EnableTransactionManagement
/**
 * entityManagerFactoryRef:指定实体管理器工厂,transactionManagerRef:指定事务管理器
 * basePackages:指定该数据源的repository所在包路径
 */
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryPrimary",
        transactionManagerRef = "transactionManagerPrimary",
        basePackages = {"com.sinosoft.doubledatasource.repository.primary"})
public class PrimaryConfig {

    @Resource(name = "primaryDataSource")
    private DataSource primaryDataSource;
    @Resource(name = "vendorProperties")
    private Map<String, Object> vendorProperties;


    /**
     * 获取对应的数据库方言
     */
    @Value("${spring.jpa.hibernate.primary-dialect}")
    private String primaryDialect;

    /**
     * 配置第一数据源实体管理工厂的bean
     *
     * @param builder EntityManagerFactoryBuilder
     * @return LocalContainerEntityManagerFactoryBean
     */
    @Bean(name = "entityManagerFactoryPrimary")
    /**
     * //标识为主数据源(主库对应的数据源)
     */
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(primaryDataSource)
                //指定组合jpaProperties和hibernateProperties配置的map对象
                .properties(vendorProperties)
                //指定该数据源的实体类所在包路径
                .packages("com.sinosoft.doubledatasource.model.primary")
                .persistenceUnit("primaryPersistenceUnit")
                .build();
    }


    /**
     * 配置第一数据源实体管理器
     *
     * @param builder EntityManagerFactoryBuilder
     * @return EntityManager
     */
    @Bean(name = "entityManagerPrimary")
    @Primary
    public EntityManager entityManagerPrimary(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryPrimary(builder).getObject().createEntityManager();
    }

    /**
     * 配置第一数据源事务管理器
     *
     * @param builder EntityManagerFactoryBuilder
     * @return PlatformTransactionManager
     */
    @Bean(name = "transactionManagerPrimary")
    @Primary
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryPrimary(builder).getObject());
    }
}
