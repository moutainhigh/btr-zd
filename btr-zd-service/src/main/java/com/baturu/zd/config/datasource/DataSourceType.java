package com.baturu.zd.config.datasource;

/**
 * 用于注解切换主从库
 */
public enum DataSourceType {

	/**
	 * 如果方法注解为DB_MASTER, 则覆盖类注解, 使用主库
	 */
	DB_MASTER("db_master"),

	/**
	 * 如果方法注解为DB_SLAVE, 则覆盖类注解, 使用从库
	 */
	DB_SLAVE("db_slave"),

	/**
	 * 如果方法注解为DB_WRITE, 则覆盖类注解，使用主库
	 */
	DB_WRITE("db_master"),

	/**
	 * 如果方法注解为DB_READ, 则随机选择数据源
	 */
	DB_READ("db_read");

	private final String dataSource;

	DataSourceType(String datasource) {
		this.dataSource = datasource;
	}

	public String getDataSource() {
		return dataSource;
	}

	public static DataSourceType valueOfDataSource(String dataSource) {
		for (DataSourceType type : DataSourceType.values()) {
			if (type.getDataSource().equals(dataSource)) {
				return type;
			}
		}
		return null;
	}
}