package com.baturu.zd.config.datasource;

import java.util.Stack;

/**
 * 数据库切换，利用ThreadLocal + Stack 实现
 *
 */
public class DataSourceSwitch {

	private static final ThreadLocal<Stack<DataSourceType>> CONTEXT_HOLDER = new ThreadLocal<Stack<DataSourceType>>();

	public static void setDbType(DataSourceType dbType) {
		Stack<DataSourceType> stack = getStack();
		if (stack == null) {
			stack = new Stack<>();
		}
		stack.push(dbType);
		CONTEXT_HOLDER.set(stack);
	}

	private static Stack<DataSourceType> getStack() {
		return CONTEXT_HOLDER.get();
	}

	public static String getDbType() {
		Stack<DataSourceType> stack = getStack();
		if (stack != null && !stack.isEmpty()) {
			DataSourceType dataSourceType = stack.firstElement();
			if (dataSourceType != null) {
				return dataSourceType.getDataSource();
			}
			return null;
		}
		return null;
	}

	public static DataSourceType popDbType() {
		Stack<DataSourceType> stack = getStack();
		if (stack != null && !stack.isEmpty()) {
			DataSourceType dataSourceType = stack.pop();
			if (stack.isEmpty()) {
				clearDbType();
			}
			return dataSourceType;
		}
		return null;
	}

	private static void clearDbType() {
		CONTEXT_HOLDER.remove();
	}
}