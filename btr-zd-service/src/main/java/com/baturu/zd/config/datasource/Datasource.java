package com.baturu.zd.config.datasource;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <P>数据源类型</P>
 * 默认使用类注解(没有注解, 默认master)<br/>
 * 如果方法注解为DB_SLAVE, 则覆盖类注解, 使用从库<br/>
 * 如果方法注解为DB_MASTER, 则覆盖类注解, 使用主库<br/>
 * 如果方法注解为DB_READ, 则随机选择数据源 <br/>
 * 如果方法注解为DB_WRITE, 则覆盖类注解，使用主库<br/>
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Inherited
public @interface Datasource {
	DataSourceType dataSource() default DataSourceType.DB_MASTER;
}