package com.baturu.zd.config.datasource;

import com.alibaba.druid.util.StringUtils;
import lombok.Data;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Data
@Configuration
@ConfigurationProperties("druid.slave")
@EnableTransactionManagement(proxyTargetClass = true)
public class DynamicDataSourceConfig {

	@Autowired
	DruidDataSourceConfig druidDataSourceConfig;

	private String slaveUrl;

	private String slaveUsername;

	private String slavePassword;

	private Properties slaveProperties;

	@Bean
	public DataSource dataSource() throws SQLException {
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		DataSource druidMasterDataSource = druidDataSourceConfig.targetDataSource();
		Map<Object, Object> targetDataSources = new HashMap<>(16);
		targetDataSources.put(DataSourceType.DB_MASTER.getDataSource(), druidMasterDataSource);

		if (!StringUtils.isEmpty(slaveUrl)) {
			DataSource druidSlaveDataSource = druidDataSourceConfig.targetDataSource(slaveUrl, slaveUsername, slavePassword, slaveProperties);
			targetDataSources.put(DataSourceType.DB_SLAVE.getDataSource(), druidSlaveDataSource);
		}

		dynamicDataSource.setTargetDataSources(targetDataSources);
		dynamicDataSource.setDefaultTargetDataSource(druidMasterDataSource);

		return dynamicDataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager() throws SQLException {
		return new DataSourceTransactionManager(dataSource());
	}

	/*@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(this.dataSource());
//		sessionFactory.setTypeAliasesPackage("com.baturu.zd.entity.*");
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		//配置分页插件
		Page pageHelper = new PageHelper();
		Properties properties = new Properties();
		properties.setProperty("reasonable", "true");
		properties.setProperty("supportMethodsArguments", "true");
		properties.setProperty("returnPageInfo", "check");
		properties.setProperty("params", "count=countSql");
		pageHelper.setProperties(properties);

		sessionFactory.setPlugins(new Interceptor[]{pageHelper});
		return sessionFactory.getObject();
	}*/

}
