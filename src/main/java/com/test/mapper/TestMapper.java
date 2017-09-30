package com.test.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.test.model.BookDO;

public interface TestMapper {

	public BookDO selectBook(@Param("id") String id, @Param("name") String name);

	public Integer check();

	public Date getLastTime();
}
