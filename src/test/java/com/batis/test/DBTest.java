package com.batis.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.test.mapper.TestMapper;
import com.test.model.BookDO;
import com.test.model.BookDOExample;
import com.test.model.mapper.BookDOMapper;

public class DBTest {

	private static SqlSessionFactory sqlSessionFactory;
	private static SqlSession session;

	@BeforeClass
	public static void init() {
		System.out.println("init() invoker ...");

		String resource = "mybatis-config.xml";
		InputStream inputStream = null;
		try {
			inputStream = Resources.getResourceAsStream(resource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DBTest.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		DBTest.session = sqlSessionFactory.openSession();
	}

	@AfterClass
	public static void destory() {
		System.out.println("destory() invoker ...");
		if (session != null) {
			session.close();
		}
	}

	@Test
	public void select() {
		TestMapper mapper = session.getMapper(TestMapper.class);
		Integer a = mapper.check();
		Date date = mapper.getLastTime();
		System.out.println(date);
		System.out.println("111 " + (System.currentTimeMillis() - date.getTime()));

		System.out.println(a);
		// System.out.println(book.getName());
	}

	@Test
	public void delete() {
	}
}
