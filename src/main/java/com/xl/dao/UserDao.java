package com.xl.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xl.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<User> {

}
