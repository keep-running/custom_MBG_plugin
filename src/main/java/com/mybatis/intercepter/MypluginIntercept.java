package com.mybatis.intercepter;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

@Intercepts({ @Signature(type = Executor.class, method = "select", args = { MappedStatement.class, Object.class }) })
public class MypluginIntercept implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		System.out.println("MypluginIntercept intercept ...");
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {

		System.out.println("MypluginIntercept plugin ...");
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

		System.out.println("MypluginIntercept setProperties ...");
	}

}
