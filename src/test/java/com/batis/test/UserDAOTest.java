package com.batis.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.test.model.UserDO;
import com.test.model.UserDOExample;
import com.test.model.mapper.UserDOMapperExt;

public class UserDAOTest {

	private static SqlSessionFactory sqlSessionFactory;
	private static SqlSession session;

	@BeforeClass
	public static void init() {
		System.out.println(UserDAOTest.class.getName() + " init() invoker ...");

		String resource = "mybatis-config.xml";
		InputStream inputStream = null;
		try {
			inputStream = Resources.getResourceAsStream(resource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UserDAOTest.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		UserDAOTest.session = sqlSessionFactory.openSession();
	}

	@AfterClass
	public static void destory() {
		System.out.println(UserDAOTest.class.getName() + " destory() invoker ...");
		if (session != null) {
			session.close();
		}
	}

	@Test
	public void insertUserByBatch() {
		UserDOMapperExt mapper = session.getMapper(UserDOMapperExt.class);

		List<UserDO> userList = new ArrayList<>();
		UserDO user = new UserDO();
		for (int i = 0; i < 5; i++) {
			user = new UserDO();
			// if (i != 2){
			user.setId(UUID.randomUUID().toString().replaceAll("-", ""));
			// }
			user.setName("zhangsan_" + i);
			user.setGender(1);
			user.setBirthday(new Date());
			user.setMobilephone("13700001111");
			user.setEmail("zhangsan" + i + "@123.com");
			userList.add(user);
		}
		int result = mapper.insertByBatch(userList);
		System.out.println("result is :" + result);
		session.commit();
	}

	@Test
	public void insertUser() {
		UserDOMapperExt mapper = session.getMapper(UserDOMapperExt.class);
		UserDO user = new UserDO();
		user.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		user.setName("lisi");
		user.setGender(1);
		int id = mapper.insert(user);
		System.out.println("return id is : " + id);
		session.commit();
	}

	@Test
	public void selectUser() {
		UserDOMapperExt mapper = session.getMapper(UserDOMapperExt.class);
		UserDOExample example = new UserDOExample();
		// example.createCriteria().andCreatorEqualTo("system");
		// example.createCriteria().andIdIn(values)

		List<UserDO> records = mapper.selectByExample(example);
		for (UserDO b : records) {
			System.out.println(b.getName());
		}
	}

	@Test
	public void selectUserExt() {
		UserDOMapperExt mapper = session.getMapper(UserDOMapperExt.class);
		UserDOExample example = new UserDOExample();
		// example.createCriteria().andCreatorEqualTo("system");
		// example.createCriteria().andIdIn(values)

		List<UserDO> records = mapper.selectByExample(example);
		for (UserDO b : records) {
			System.out.println(b.getName());
		}

	}

	@Test
	public void getGoodUsers() {
		UserDOMapperExt mapper = session.getMapper(UserDOMapperExt.class);
		UserDOExample example = new UserDOExample();
		// example.createCriteria().andCreatorEqualTo("system");
		// // example.createCriteria().andIdIn(values)
		//
		// List<UserDO> records = mapper.getGoodUsers();
		// for (UserDO b : records) {
		// System.out.println(b.getName());
		// }

	}

	@Test
	public void selectPaging() {
		UserDOMapperExt mapper = session.getMapper(UserDOMapperExt.class);

		UserDOExample example = new UserDOExample();
		// example.setOrderByClause(""+);
		example.setPageStart(3);
		example.setPageSize(5);
		List<UserDO> records = mapper.selectByExample(example);
		for (UserDO b : records) {
			System.out.println(b.getName());
		}

	}

	@Test
	public void getCountUsers() {
		UserDOMapperExt mapper = session.getMapper(UserDOMapperExt.class);

		// Integer count = mapper.g();
		// System.out.println("all User count is :" + count);

	}

	@Ignore("没有数据可用")
	@Test
	public void deleteUser() {
		UserDOMapperExt mapper = session.getMapper(UserDOMapperExt.class);
		UserDOExample example = new UserDOExample();
		// example.createCriteria().andCreatorEqualTo("system");
		mapper.deleteByExample(example);
		session.commit();
	}
}
