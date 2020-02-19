package com.baturu.zd.config.datasource;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 使用AOP针对{@link Datasource}注解进行动态切换数据源
 *
 */
@Slf4j
@Aspect
@Component
public class DataSourceAdvice {


	@Pointcut("execution(* com.baturu.zd.service..*.*(..))")
	public void aspect() {

	}

	/**
	 * 方法即将执行，记录其数据源标识
	 * 这里采用堆栈记录的方式，将源方法（第一个执行的方法）的数据源标识记录进栈底，并且一直使用这个数据源，直到退出该源方法
	 * 方法内的子方法只记录数据源标识，并且在该方法执行结束的时候清除其数据源标识记录
	 *
	 * @param joinPoint
	 */
	@Before("aspect()")
	public void beforeAdvice(JoinPoint joinPoint) {

		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();

		DataSourceType datasource = null;

		Annotation[] annotations = method.getAnnotations();
		String methodName = method.getName();
		String className = method.getDeclaringClass().getName();
		//方法上没有注解或者注解数组为空时, 返回
		if (annotations == null || annotations.length == 0) {
			DataSourceSwitch.setDbType(datasource);
			return;
		}
		//记录方法上的数据源注解类型
		for (Annotation annotation : annotations) {
			if (Datasource.class.isInstance(annotation)) {
				datasource = ((Datasource) annotation).dataSource();
				DataSourceSwitch.setDbType(datasource);
				if (log.isDebugEnabled()) {
					log.debug("AOP> class: {}, method: {}, current method datasource: {}, final use datasource: {}", className, methodName, datasource, DataSourceType.valueOfDataSource(DataSourceSwitch.getDbType()));
				}
				return;
			}
		}
		//方法上没有标识数据源类型的注解时采用默认数据源类型
		DataSourceSwitch.setDbType(datasource);
	}

	@After("aspect()")
	public void afterAdvice(JoinPoint joinPoint) {
		//方法执行结束，删除其数据源标识
		DataSourceSwitch.popDbType();
	}

}
