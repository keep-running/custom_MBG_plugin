package com.batis.test;

import java.io.IOException;
import java.io.InputStream;
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

import com.test.model.BookDO;
import com.test.model.BookDOExample;
import com.test.model.mapper.BookDOMapper;
import com.test.model.mapper.BookDOMapperExt;

public class BookDaoTest {

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
		BookDaoTest.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		BookDaoTest.session = sqlSessionFactory.openSession();
	}

	@AfterClass
	public static void destory() {
		System.out.println("destory() invoker ...");
		if (session != null) {
			session.close();
		}
	}

	@Test
	public void insertBook() {
		BookDOMapperExt mapper = session.getMapper(BookDOMapperExt.class);
		BookDO book = new BookDO();
		// book.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		book.setAuthor("zhangsan");
		book.setIsbn("4654987");
		book.setName("Thinking in Java");
		book.setCreator("system");
		book.setGmtCreated(new Date());
		book.setIsDeleted("n");
		mapper.insert(book);
		session.commit();
	}

	@Test
	public void selectBook() {
		BookDOMapper mapper = session.getMapper(BookDOMapper.class);
		BookDOExample example = new BookDOExample();
		example.createCriteria().andCreatorEqualTo("system");
		// example.createCriteria().andIdIn(values)

		List<BookDO> records = mapper.selectByExample(example);
		for (BookDO b : records) {
			System.out.println(b.getName());
		}
	}

	@Test
	public void selectBookExt() {
		BookDOMapperExt mapper = session.getMapper(BookDOMapperExt.class);
		BookDOExample example = new BookDOExample();
		example.createCriteria().andCreatorEqualTo("system");
		// example.createCriteria().andIdIn(values)

		List<BookDO> records = mapper.selectByExample(example);
		for (BookDO b : records) {
			System.out.println(b.getName());
		}

	}

	@Test
	public void getGoodBooks() {
		BookDOMapperExt mapper = session.getMapper(BookDOMapperExt.class);
		BookDOExample example = new BookDOExample();
		example.createCriteria().andCreatorEqualTo("system");
		// example.createCriteria().andIdIn(values)

//		List<BookDO> records = mapper.getGoodBooks();
//		for (BookDO b : records) {
//			System.out.println(b.getName());
//		}

	}

	@Test
	public void selectPaging() {
		BookDOMapperExt mapper = session.getMapper(BookDOMapperExt.class);

		BookDOExample example = new BookDOExample();
//		example.setOrderByClause(""+);
		example.setPageStart(3);
		example.setPageSize(5);
		List<BookDO> records = mapper.selectByExample(example);
		for (BookDO b : records) {
			System.out.println(b.getName());
		}

	}

	@Test
	public void getCountBooks() {
		BookDOMapperExt mapper = session.getMapper(BookDOMapperExt.class);

//		Integer count = mapper.getBooksCount();
//		System.out.println("all book count is :" + count);

	}

	@Ignore("没有数据可用")
	@Test
	public void deleteBook() {
		BookDOMapper mapper = session.getMapper(BookDOMapper.class);
		BookDOExample example = new BookDOExample();
		example.createCriteria().andCreatorEqualTo("system");
		mapper.deleteByExample(example);
		session.commit();
	}
}
