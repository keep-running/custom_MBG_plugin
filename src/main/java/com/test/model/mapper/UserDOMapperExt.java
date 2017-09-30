package com.test.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.test.model.UserDO;

public interface UserDOMapperExt extends UserDOMapper {
	
	/*int insertByBatch(@Param("users")List<UserDO> users);*/
}