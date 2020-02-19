package com.baturu.zd.config.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Random;

public class DynamicDataSource extends AbstractRoutingDataSource {

	private final static Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

	private final static int DB_COUNT = 2;

	/**
	 * @see AbstractRoutingDataSource#determineCurrentLookupKey()
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		String dataSource = DataSourceSwitch.getDbType();
		//Read模式, 随机分发请求
		if (DataSourceType.DB_READ.getDataSource().equals(dataSource)) {
			if (new Random().nextInt(DB_COUNT) > 0) {
				return DataSourceType.DB_MASTER.getDataSource();
			} else {
				return DataSourceType.DB_SLAVE.getDataSource();
			}
		}
		//没有设置数据源默认使用master
		if (null == dataSource) {
			if (logger.isDebugEnabled()) {
				logger.debug("Use Default datasource : db_master");
			}
			return DataSourceType.DB_MASTER.getDataSource();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("DataSourceSwitch: {}", dataSource);
		}
		return dataSource;
	}

}