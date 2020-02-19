package com.baturu.zd.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @date 2018/8/7 14:48
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties("druid")
@EnableTransactionManagement(proxyTargetClass = true)
public class DruidDataSourceConfig {

	private String url;


	private String username;


	private String password;


	private String driverClass;

	/**
	 * 初始化时建立物理连接的个数
	 */
	private Integer initialSize;

	/**
	 * 最小连接池数量
	 */
	private Integer minIdle;

	/**
	 * 最大连接池数量
	 */
	private Integer maxActive;

	/**
	 * 获取连接时最大等待时间，单位毫秒
	 */
	private Long maxWait;

	/**
	 * Destroy线程检测连接的间隔时间
	 */
	private Long timeBetweenEvictionRunsMillis;

	/**
	 * 连接保持空闲而不被驱逐的最长时间
	 */
	private Long minEvictableIdleTimeMillis;

	/**
	 * 用来检测连接是否有效的sql
	 */
	private String validationQuery;

	/**
	 * 执行validationQuery检测连接是否有效
	 */
	private Boolean testWhileIdle;

	/**
	 * 申请连接时执行validationQuery检测连接是否有效
	 */
	private Boolean testOnBorrow;

	/**
	 * 归还连接时执行validationQuery检测连接是否有效
	 */
	private Boolean testOnReturn;

	/**
	 * 配置filter，例如使用密码加密
	 */
	private String filters;

	/**
	 * 配置连接属性
	 */
	private Properties properties;

	public DataSource targetDataSource(String url, String jdbcUsername, String jdbcPassword, Properties properties)
			throws SQLException {

		DruidDataSource druidDataSource = new DruidDataSource();

		druidDataSource.setUrl(url);
		druidDataSource.setDriverClassName(driverClass);
		druidDataSource.setUsername(jdbcUsername);
		druidDataSource.setPassword(jdbcPassword);
		druidDataSource.setInitialSize(initialSize);
		druidDataSource.setMinIdle(minIdle);
		druidDataSource.setMaxActive(maxActive);
		druidDataSource.setMaxWait(maxWait);
		druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		druidDataSource.setValidationQuery(validationQuery);
		druidDataSource.setTestWhileIdle(testWhileIdle);
		druidDataSource.setTestOnBorrow(testOnBorrow);
		druidDataSource.setTestOnReturn(testOnReturn);
		druidDataSource.setFilters(filters);
		druidDataSource.setConnectProperties(properties);

		return new LazyConnectionDataSourceProxy(druidDataSource);
	}

	public DataSource targetDataSource() throws SQLException {
		return targetDataSource(url, username, password, properties);
	}

}
